package dev.turtywurty.mysticfactories.world.entity.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

/**
 * Concrete {@link EntityDataWriter} that targets JSON via {@link JsonOps}.
 */
public final class JsonEntityDataWriter extends EntityDataWriter<JsonElement> {
    public JsonEntityDataWriter() {
        super(JsonOps.INSTANCE);
    }

    public JsonElement toJson() {
        return buildOrThrow();
    }

    public JsonObject toJsonObject() {
        JsonElement element = toJson();
        if (!element.isJsonObject())
            throw new IllegalStateException("Entity data did not encode to a JSON object");

        return element.getAsJsonObject();
    }
}
