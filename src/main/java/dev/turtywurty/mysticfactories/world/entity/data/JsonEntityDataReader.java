package dev.turtywurty.mysticfactories.world.entity.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

/**
 * Concrete {@link EntityDataReader} that reads JSON-encoded entity data.
 */
public final class JsonEntityDataReader extends EntityDataReader<JsonElement> {
    public JsonEntityDataReader(JsonElement root) {
        super(JsonOps.INSTANCE, root);
    }

    public static JsonEntityDataReader fromString(String json) {
        return new JsonEntityDataReader(JsonParser.parseString(json));
    }

    public JsonObject asJsonObject() {
        JsonElement root = root();
        if (!root.isJsonObject())
            throw new IllegalStateException("Entity data is not a JSON object");

        return root.getAsJsonObject();
    }
}
