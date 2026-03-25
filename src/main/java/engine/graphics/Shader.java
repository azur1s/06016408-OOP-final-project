package engine.graphics;

import static org.lwjgl.opengl.GL20.*;

import engine.math.Matrix4f;

/**
 * OpenGL shader program wrapper for compiling, linking, binding, and setting
 * uniforms.
 */
public class Shader {
    private final int programId;

    /**
     * Creates and links a shader program from vertex and fragment source code.
     *
     * @param vertexSrc   vertex shader source
     * @param fragmentSrc fragment shader source
     */
    public Shader(String vertexSrc, String fragmentSrc) {
        // Compile and link them into a shader program
        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexSrc);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentSrc);

        programId = glCreateProgram();
        glAttachShader(programId, vertexShader);
        glAttachShader(programId, fragmentShader);
        glLinkProgram(programId);

        // Check for linking errors
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader program linking failed: " + glGetProgramInfoLog(programId));
        }

        // Cleanup the raw shaders after linking
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private int compileShader(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader compilation failed: " + glGetShaderInfoLog(shader));
        }
        return shader;
    }

    /**
     * Sets an integer uniform value.
     *
     * @param name  uniform name
     * @param value uniform value
     */
    public void setUniform1i(String name, int value) {
        int location = glGetUniformLocation(programId, name);
        glUniform1i(location, value);
    }

    /**
     * Sets a vec4 float uniform value.
     *
     * @param name uniform name
     * @param r    red component
     * @param g    green component
     * @param b    blue component
     * @param a    alpha component
     */
    public void setUniform4f(String name, float r, float g, float b, float a) {
        int location = glGetUniformLocation(programId, name);
        glUniform4f(location, r, g, b, a);
    }

    /**
     * Sets a 4x4 matrix uniform value.
     *
     * @param name   uniform name
     * @param matrix matrix value
     */
    public void setUniformMat4f(String name, Matrix4f matrix) {
        int location = glGetUniformLocation(programId, name);
        glUniformMatrix4fv(location, false, matrix.elements);
    }

    /** Binds this shader program for subsequent draw calls. */
    public void bind() {
        glUseProgram(programId);
    }

    /** Deletes the underlying OpenGL program. */
    public void dispose() {
        glDeleteProgram(programId);
    }
}
