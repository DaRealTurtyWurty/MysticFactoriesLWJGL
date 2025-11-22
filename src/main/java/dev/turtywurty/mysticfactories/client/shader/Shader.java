package dev.turtywurty.mysticfactories.client.shader;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class Shader {
    private final int programId;
    private final Map<String, Integer> uniformLocationCache = new HashMap<>();

    public Shader(String vertexPath, String fragmentPath) {
        int vertexId = compileShader(vertexPath, GL20.GL_VERTEX_SHADER);
        int fragmentId = compileShader(fragmentPath, GL20.GL_FRAGMENT_SHADER);

        this.programId = GL20.glCreateProgram();
        GL20.glAttachShader(this.programId, vertexId);
        GL20.glAttachShader(this.programId, fragmentId);
        GL20.glLinkProgram(this.programId);

        if (GL20.glGetProgrami(this.programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(this.programId);
            GL20.glDeleteShader(vertexId);
            GL20.glDeleteShader(fragmentId);
            throw new IllegalStateException("Failed to link shader program: " + log);
        }

        GL20.glDetachShader(this.programId, vertexId);
        GL20.glDetachShader(this.programId, fragmentId);
        GL20.glDeleteShader(vertexId);
        GL20.glDeleteShader(fragmentId);
    }

    private int compileShader(String resourcePath, int shaderType) {
        int shaderId = GL20.glCreateShader(shaderType);
        GL20.glShaderSource(shaderId, loadResource(resourcePath));
        GL20.glCompileShader(shaderId);

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shaderId);
            GL20.glDeleteShader(shaderId);
            throw new IllegalStateException("Failed to compile shader [" + resourcePath + "]: " + log);
        }

        return shaderId;
    }

    private String loadResource(String resourcePath) {
        InputStream stream = Shader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (stream == null)
            throw new IllegalArgumentException("Shader resource not found: " + resourcePath);

        try (var reader = new BufferedReader(new InputStreamReader(stream))) {
            var builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            return builder.toString();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to read shader resource: " + resourcePath, exception);
        }
    }

    public void bind() {
        GL20.glUseProgram(this.programId);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        GL20.glDeleteProgram(this.programId);
        this.uniformLocationCache.clear();
    }

    public void setUniform(String name, int value) {
        GL20.glUniform1i(getUniformLocation(name), value);
    }

    public void setUniform(String name, float value) {
        GL20.glUniform1f(getUniformLocation(name), value);
    }

    public void setUniform(String name, boolean value) {
        GL20.glUniform1i(getUniformLocation(name), value ? 1 : 0);
    }

    public void setUniform(String name, Vector2f value) {
        GL20.glUniform2f(getUniformLocation(name), value.x, value.y);
    }

    public void setUniform(String name, Vector3f value) {
        GL20.glUniform3f(getUniformLocation(name), value.x, value.y, value.z);
    }

    public void setUniform(String name, Vector4f value) {
        GL20.glUniform4f(getUniformLocation(name), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);
            GL20.glUniformMatrix4fv(getUniformLocation(name), false, buffer);
        }
    }

    private int getUniformLocation(String name) {
        if (this.uniformLocationCache.containsKey(name))
            return this.uniformLocationCache.get(name);

        int location = GL20.glGetUniformLocation(this.programId, name);
        if (location == -1)
            throw new IllegalStateException("Uniform '" + name + "' does not exist in shader!");

        this.uniformLocationCache.put(name, location);
        return location;
    }
}
