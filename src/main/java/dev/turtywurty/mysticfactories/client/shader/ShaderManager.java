package dev.turtywurty.mysticfactories.client.shader;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private final Map<String, Shader> shaders = new HashMap<>();

    public Shader create(String name, String vertexPath, String fragmentPath) {
        Shader shader = new Shader(vertexPath, fragmentPath);
        this.shaders.put(name, shader);
        return shader;
    }

    public Shader get(String name) {
        return this.shaders.get(name);
    }

    public void cleanup() {
        this.shaders.values().forEach(Shader::cleanup);
        this.shaders.clear();
    }
}
