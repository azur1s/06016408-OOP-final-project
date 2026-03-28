# Project Architecture

This project uses a [custom-built 2D engine](./src/main/java/engine) written in Java 17 and using LWJGL 3 for hardware-accelerated rendering and GLFW window management. The engine is designed to be modular and extensible, allowing for easy addition of new features and components.

# Engine Architecture

## 1. Core Loop & Window Management
The entry point [`Lwjgl3Main.java`](./src/main/java/game/Lwjgl3Main.java) which initializes GLFW window and configures OpenGL 3.3 context. It maintains the primary game loop, which calculates delta time, polls input events, update callbacks, and renders the game scene.

## 2. Engine globals
The [`Engine.java`](./src/main/java/engine/Engine.java) class serves as a central class for managing global states. It provides access to these core subsystems:
- Input: Tracks keyboard and mouse input states, updated via GLFW callbacks hooked in the main initialization.
- Graphics & Audio: Manages rendering and audio playback.
- Task Scheduling: Allows delayed logic execution (via `runAfter` method) which queue tasks to be executed on the main game thread during `tickScheduleTasks`.

## 3. Scene Management
The engine utilizes a state-based design where discrete game views (like main menus or gameplay levels) inherit from the abstract `Scene` class. Each scene is responsible for managing two distinct rendering contexts:

- World Rendering: Controlled by a centered `OrthoCamera` where (0, 0) is the middle of the screen, designed to natively draw game entities.

- UI Rendering: Controlled by a separate `OrthoCamera` anchored to the bottom-left corner for rendering screen-space elements via the UIManager.

## 4. Textures
The [`Texture`](./src/main/java/engine/graphics/Texture.java) class acts as an OpenGL 2D texture wrapper designed to optimize performance by managing GPU memory efficiently and parallelizing expensive operations. It achieves this by splitting the loading process, utilizing asynchronous threads for IO operations, and implementing strict caching and reference counting.

### Two-Phase Loading and Threading Model
Loading textures from a file path is intentionally split into two distinct phases to ensure strict OpenGL thread compliance while maximizing multi-core performance.

- Phase 1: Asynchronous IO & Image Decoding: File reading and image decoding (via STB image) are offloaded to background threads.

    The system uses a dedicated, fixed-size thread pool called `TEXTURE_IO_EXECUTOR`. The pool size scales dynamically based on hardware, utilizing half of the available CPU processors (with a minimum of 2 threads).

   The actual file reading is delegated to the `Resources` utility, which securely loads the classpath resource into a direct `ByteBuffer`.

   Developers can explicitly trigger this asynchronous phase ahead of time using the `preloadAsync()` method, which is highly useful for masking load times during scene transitions.

- Phase 2: Synchronous GPU Upload: Once the image bytes are decoded into raw pixels, the actual upload to the GPU must occur on the main render thread, as it requires an active OpenGL context.

   The Texture constructor blocks and waits for the asynchronous decode task (`CompletableFuture`) to finish.

   It then uploads the pixels to the GPU using standard OpenGL bindings (like `glGenTextures` and `glTexImage2D`).

   To prevent RAM leaks, the raw STB-allocated pixel buffer is immediately freed as soon as the GPU upload is complete.

### Multi-Tier Caching System
To prevent duplicate file reads and save GPU memory, the architecture employs a thread-safe caching system governed by a central `CACHE_LOCK`. It utilizes two separate caches:

The Decode Cache (`FILE_DECODE_CACHE`): This maps file paths to their in-flight asynchronous decode tasks (`CompletableFuture<DecodedTexture>`). If multiple entities request the exact same texture at the same time, the system reuses the ongoing decode task rather than triggering redundant file reads.

The GPU Texture Cache (`FILE_TEXTURE_CACHE`): This maps file paths to fully loaded CachedTexture objects, which hold the final OpenGL texture ID and dimensions. If a texture is already present on the GPU, new `Texture` instances will instantly inherit the cached OpenGL ID, entirely bypassing IO, decoding, and GPU uploading.

### Memory Management & Reference Counting
Since GPU memory is limited, the caching system is paired with a strict reference counting mechanism to safely garbage-collect textures.

- Reference Tracking: Every shared `CachedTexture` starts with a `refCount` of 1. Whenever a new `Texture` wrapper is created using an already cached file path, this reference count increments.

- Disposal (`cleanup()`): When a system is done using a texture, it calls `cleanup()`, which decrements the reference count.

- Eviction: If the `refCount` drops to 0, it means no active objects are using the texture. The system will automatically remove the entry from the cache and call `glDeleteTextures` to permanently free up the VRAM.

<!-- - Graceful Shutdown: The architecture includes a shutdownPreloader() utility that can be called during application exit to safely clear in-flight decodes, release dangling STB pixel buffers in RAM, and shut down the background worker threads. -->

### Coordinate System & Origins
To simplify entity rendering, the `Texture` class automatically calculates an internal origin point (`originX`, `originY`) set to the exact mathematical center of the image (`width / 2.0f`, `height / 2.0f`) upon instantiation.

# Game Architecture

TODO