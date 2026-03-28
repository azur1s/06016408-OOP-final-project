package game;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import engine.Engine;
import engine.graphics.Texture;
import game.menu.Main;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Lwjgl3Main {
    public static final String TITLE = "project";
    public static final int INITIAL_WIDTH = 1280;
    public static final int INITIAL_HEIGHT = 720;

    private long window;

    public void run() {
        System.out.println("LWJGL version " + Version.getVersion());

        init();
        loop();
        cleanup();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        // Request OpenGL 3.3 core profile
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        // For MacOS compatibility
        if (Platform.get() == Platform.MACOSX) {
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        }

        window = glfwCreateWindow(INITIAL_WIDTH, INITIAL_HEIGHT, TITLE, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        Engine.setWindow(window);

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            Engine.input.updateKeyState(key, action);
        });
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            Engine.input.updateMousePosition((float) xpos, (float) ypos);
        });
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            Engine.input.updateMouseButtonState(button, action);
        });
        glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
            Engine.input.updateMouseScroll((float) yoffset);
        });

        // glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        glfwShowWindow(window);
    }

    private void loop() {
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Engine.width = INITIAL_WIDTH;
        Engine.height = INITIAL_HEIGHT;

        Engine.setScene(new Main());

        // resize callback
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            glViewport(0, 0, width, height);
            if (Engine.getCurrentScene() != null)
                Engine.getCurrentScene().resize(width, height);
        });

        double lastTime = glfwGetTime();

        while (!glfwWindowShouldClose(window)) {
            // Calculate delta time and update FPS
            double currentTime = glfwGetTime();
            float deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;
            Engine.graphics.updateTime((float) currentTime, deltaTime);
            Engine.tickScheduledTasks();

            // Update game state and render
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            if (Engine.getCurrentScene() != null)
                Engine.getCurrentScene().internalTick(deltaTime);

            glfwSwapBuffers(window);

            Engine.input.endFrame();

            glfwPollEvents();
        }
    }

    private void cleanup() {
        if (Engine.getCurrentScene() != null)
            Engine.getCurrentScene().internalCleanup();

        Texture.shutdownPreloader();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        new Lwjgl3Main().run();
    }
}
