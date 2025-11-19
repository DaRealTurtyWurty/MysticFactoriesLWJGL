package dev.turtywurty.mysticfactories.renderer;

import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Shader {
    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    public Shader(String vertexPath, String fragmentPath) {
        programId = GL20.glCreateProgram();
        if (programId == 0)
            throw new RuntimeException("Could not create Shader Program");

        vertexShaderId = createShader(vertexPath, GL20.GL_VERTEX_SHADER);
        fragmentShaderId = createShader(fragmentPath, GL20.GL_FRAGMENT_SHADER);

        link();
    }

    private int createShader(String shaderPath, int shaderType) {
        int shaderId = GL20.glCreateShader(shaderType);
        if (shaderId == 0)
            throw new RuntimeException("Error creating shader. Type: " + shaderType);

        try {
            String shaderSource = new String(Files.readAllBytes(Paths.get(shaderPath)));
            GL20.glShaderSource(shaderId, shaderSource);
            GL20.glCompileShader(shaderId);

            if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0)
                throw new RuntimeException("Error compiling shader " + shaderPath + ": " + GL20.glGetShaderInfoLog(shaderId, 1024));
        } catch (IOException e) {
            throw new RuntimeException("Error loading shader " + shaderPath, e);
        }

        GL20.glAttachShader(programId, shaderId);
        return shaderId;
    }

    private void link() {
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0)
            throw new RuntimeException("Error linking Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));

        if (vertexShaderId != 0) {
            GL20.glDetachShader(programId, vertexShaderId);
        }

        if (fragmentShaderId != 0) {
            GL20.glDetachShader(programId, fragmentShaderId);
        }

        GL20.glValidateProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
        }
    }

    public void bind() {
        GL20.glUseProgram(programId);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            GL20.glDeleteProgram(programId);
        }
    }

    public int getUniformLocation(String uniformName) {
        int uniformLocation = GL20.glGetUniformLocation(programId, uniformName);
        if (uniformLocation == -1) {
            System.err.println("WARNING: Could not find uniform '" + uniformName + "'");
        }

        return uniformLocation;
    }

    public void setUniformMat4(String uniformName, org.joml.Matrix4f value) {
        GL20.glUniformMatrix4fv(getUniformLocation(uniformName), false, value.get(new float[16]));
    }

    public void setUniformInt(String uniformName, int value) {
        GL20.glUniform1i(getUniformLocation(uniformName), value);
    }
}
