package com.project.engine;

import com.project.engine.utils.Resources;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.libc.LibCStdlib;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.List;

public class AudioManager {
    /**
     * Handle to the OpenAL audio device used for audio playback. This represents
     * the physical or virtual audio device that OpenAL uses to output sound.
     */
    private long device;
    /**
     * Handle to the OpenAL audio context. The context manages all OpenAL state and
     * is associated with a specific device.
     */
    private long context;

    /**
     * Maps sound names to their corresponding OpenAL buffer IDs.
     * Each buffer contains the audio data for a loaded sound file.
     */
    private HashMap<String, Integer> soundBuffers = new HashMap<>();

    /**
     * Maps file paths to their corresponding OpenAL buffer IDs.
     * This prevents loading the same file multiple times when multiple
     * sound names reference the same file path.
     */
    private HashMap<String, Integer> pathToBuffer = new HashMap<>();

    /**
     * Maps sound names to lists of active source IDs currently playing that sound.
     * Multiple sources can play the same sound simultaneously, and each source
     * represents an independent playback instance with its own state (position,
     * volume, etc.).
     */
    private HashMap<String, List<Integer>> activeSources = new HashMap<>();

    public AudioManager() {
        device = ALC10.alcOpenDevice((ByteBuffer) null);
        if (device == 0) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }

        context = ALC10.alcCreateContext(device, (int[]) null);
        if (context == 0) {
            ALC10.alcCloseDevice(device);
            throw new IllegalStateException("Failed to create OpenAL context.");
        }

        ALC10.alcMakeContextCurrent(context);
        AL.createCapabilities(ALC.createCapabilities(device));
    }

    /**
     * Loads an OGG file into an OpenAL buffer and stores it under "name". If the
     * file path has already been loaded, reuses the existing buffer instead of
     * loading it again.
     *
     * @param name     The name to reference the sound by
     * @param filePath Path to OGG file to load
     */
    public void loadSound(String name, String filePath) {
        // Check if this file path has already been loaded
        Integer existingBufferId = pathToBuffer.get(filePath);

        if (existingBufferId != null) {
            // Reuse the existing buffer for this name
            soundBuffers.put(name, existingBufferId);
            return;
        }

        // Load the file since it hasn't been loaded before
        ByteBuffer vorbisData = Resources.loadResourceToByteBuffer(filePath);

        IntBuffer channels = BufferUtils.createIntBuffer(1);
        IntBuffer sampleRate = BufferUtils.createIntBuffer(1);

        ShortBuffer pcm = STBVorbis.stb_vorbis_decode_memory(vorbisData, channels, sampleRate);
        if (pcm == null) {
            throw new RuntimeException("Failed to decode OGG file: " + filePath);
        }

        int format;
        int channelCount = channels.get(0);
        if (channelCount == 1) {
            format = AL10.AL_FORMAT_MONO16;
        } else if (channelCount == 2) {
            format = AL10.AL_FORMAT_STEREO16;
        } else {
            LibCStdlib.free(pcm);
            throw new RuntimeException("Unsupported number of channels: " + channelCount);
        }

        int bufferId = AL10.alGenBuffers();
        AL10.alBufferData(bufferId, format, pcm, sampleRate.get(0));
        LibCStdlib.free(pcm);

        // Store the buffer for both the name and the file path
        soundBuffers.put(name, bufferId);
        pathToBuffer.put(filePath, bufferId);
    }

    /**
     * Plays the sound identified by "name". If that same named sound is already
     * active, it is restarted.
     */
    public void playSound(String name) {
        Integer bufferId = soundBuffers.get(name);
        if (bufferId == null) {
            throw new IllegalArgumentException("Sound not loaded: " + name);
        }

        // Stop existing source if already playing
        stopSound(name);

        int sourceId = AL10.alGenSources();
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
        AL10.alSourcef(sourceId, AL10.AL_GAIN, 1.0f);
        AL10.alSourcef(sourceId, AL10.AL_PITCH, 1.0f);
        AL10.alSourcei(sourceId, AL10.AL_LOOPING, AL10.AL_FALSE);
        AL10.alSourcePlay(sourceId);

        // Look up active sources for this sound, and add this new source to the list
        // If no list exists yet, create a new one and add it to the map
        activeSources.computeIfAbsent(name, k -> new java.util.ArrayList<>())
                .add(sourceId);
    }

    public void stopSound(String name) {
        List<Integer> sources = activeSources.remove(name);
        if (sources != null) {
            for (int sourceId : sources) {
                AL10.alSourceStop(sourceId);
                AL10.alDeleteSources(sourceId);
            }
        }
    }

    public void cleanup() {
        // Delete active sources
        for (List<Integer> sources : activeSources.values()) {
            for (int sourceId : sources) {
                AL10.alSourceStop(sourceId);
                AL10.alDeleteSources(sourceId);
            }
        }
        activeSources.clear();

        // Delete buffers
        for (int bufferId : soundBuffers.values()) {
            AL10.alDeleteBuffers(bufferId);
        }
        soundBuffers.clear();

        ALC10.alcMakeContextCurrent(0L);
        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(device);
    }
}
