package engine.graphics;

import static org.lwjgl.opengl.GL20.*;

import engine.math.Matrix4f;

public class Shader {
    private final int programId;

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

    public void setUniform1i(String name, int value) {
        int location = glGetUniformLocation(programId, name);
        glUniform1i(location, value);
    }

    public void setUniform4f(String name, float r, float g, float b, float a) {
        int location = glGetUniformLocation(programId, name);
        glUniform4f(location, r, g, b, a);
    }

    public void setUniformMat4f(String name, Matrix4f matrix) {
        int location = glGetUniformLocation(programId, name);
        glUniformMatrix4fv(location, false, matrix.elements);
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void dispose() {
        glDeleteProgram(programId);
    }
}
