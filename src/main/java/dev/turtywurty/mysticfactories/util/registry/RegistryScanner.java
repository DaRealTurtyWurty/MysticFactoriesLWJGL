package dev.turtywurty.mysticfactories.util.registry;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class RegistryScanner {
    public static void scanForRegistryHolders() {
        var reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath()));
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(RegistryHolder.class)) {
            try {
                // Trigger static initializers
                Class.forName(clazz.getName());
            } catch (ClassNotFoundException exception) {
                throw new RuntimeException("Failed to load registry holder class: " + clazz.getName(), exception);
            }
        }
    }
}
