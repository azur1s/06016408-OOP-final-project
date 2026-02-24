package com.project.engine;

import static org.lwjgl.opengl.GL20.*;

import com.project.math.Matrix4f;

public class Shader {
    private final int programId;

    public Shader() {
        String vertexShaderSource = "#version 330 core\n" +
                "layout (location = 0) in vec2 aPos;\n" +
                "layout (location = 1) in vec2 aTexCoords;\n" +
                "out vec2 TexCoords;\n" +
                "uniform mat4 u_projection;\n" +
                "void main() {\n" +
                "    TexCoords = aTexCoords;\n" +
                "    gl_Position = u_projection * vec4(aPos, 0.0, 1.0);\n" +
                "}";

        String fragmentShaderSource = "#version 330 core\n" +
                "in vec2 TexCoords;\n" +
                "out vec4 color;\n" +
                "uniform sampler2D u_texture;\n" +
                "uniform vec4 u_color;\n" +
                "void main() {\n" +
                "    color = texture(u_texture, TexCoords) * u_color;\n" +
                "}";

        // Compile and link them into a shader program
        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);

        programId = glCreateProgram();
        glAttachShader(programId, vertexShader);
        glAttachShader(programId, fragmentShader);
        glLinkProgram(programId);

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
