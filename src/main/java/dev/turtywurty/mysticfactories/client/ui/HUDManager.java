package dev.turtywurty.mysticfactories.client.ui;

import dev.turtywurty.mysticfactories.util.Identifier;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages HUD elements in a fixed draw order (insertion order = back-to-front).
 */
public class HUDManager {
    private static final List<Pair<Identifier, UIElement>> ELEMENTS = new ArrayList<>();

    public static void addLastElement(Identifier id, UIElement element) {
        if (isElementPresent(id))
            throw new IllegalArgumentException("Element with ID " + id + " is already present in the HUDManager.");

        ELEMENTS.addLast(new Pair<>(id, element));
    }

    public static void addFirstElement(Identifier id, UIElement element) {
        if (isElementPresent(id))
            throw new IllegalArgumentException("Element with ID " + id + " is already present in the HUDManager.");

        ELEMENTS.addFirst(new Pair<>(id, element));
    }

    public static void addBefore(Identifier beforeId, Identifier id, UIElement element) {
        if (isElementPresent(id))
            throw new IllegalArgumentException("Element with ID " + id + " is already present in the HUDManager.");

        for (int i = 0; i < ELEMENTS.size(); i++) {
            if (ELEMENTS.get(i).getFirst().equals(beforeId)) {
                ELEMENTS.add(i, new Pair<>(id, element));
                return;
            }
        }

        throw new IllegalArgumentException("Element with ID " + beforeId + " not found in the HUDManager.");
    }

    public static void addAfter(Identifier afterId, Identifier id, UIElement element) {
        if (isElementPresent(id))
            throw new IllegalArgumentException("Element with ID " + id + " is already present in the HUDManager.");

        for (int i = 0; i < ELEMENTS.size(); i++) {
            if (ELEMENTS.get(i).getFirst().equals(afterId)) {
                ELEMENTS.add(i + 1, new Pair<>(id, element));
                return;
            }
        }

        throw new IllegalArgumentException("Element with ID " + afterId + " not found in the HUDManager.");
    }

    public static void removeElement(Identifier id) {
        ELEMENTS.removeIf(pair -> pair.getFirst().equals(id));
    }

    public static boolean isElementPresent(Identifier id) {
        for (Pair<Identifier, UIElement> pair : ELEMENTS) {
            if (pair.getFirst().equals(id))
                return true;
        }

        return false;
    }

    public static void render(DrawContext context) {
        for (UIElement element : ELEMENTS.stream().map(Pair::getSecond).toList()) {
            element.render(context);
        }
    }

    public static void cleanup() {
        for (UIElement element : ELEMENTS.stream().map(Pair::getSecond).toList()) {
            element.cleanup();
        }

        ELEMENTS.clear();
    }
}
