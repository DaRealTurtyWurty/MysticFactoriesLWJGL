package dev.turtywurty.mysticfactories.world.feature;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registerable;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Feature implements Registerable {
    @Setter
    private Identifier id;

    public abstract boolean place(FeaturePlacementContext context);

    public static Feature of(PlacementFunction fn) {
        return new Feature() {
            @Override
            public boolean place(FeaturePlacementContext context) {
                return fn.place(context);
            }
        };
    }

    @FunctionalInterface
    public interface PlacementFunction {
        boolean place(FeaturePlacementContext context);
    }
}
