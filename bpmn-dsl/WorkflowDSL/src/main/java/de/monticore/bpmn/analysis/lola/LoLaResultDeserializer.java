package de.monticore.bpmn.analysis.lola;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Deserializes the result produced by LoLA.
 *
 * @see LoLaResult
 */
public class LoLaResultDeserializer implements JsonDeserializer<LoLaResult> {

    @Override
    public LoLaResult deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();

        final Gson gson = new Gson();//new GsonBuilder().setPrettyPrinting().create();

        final LoLaResult result = gson.fromJson(jsonObject, LoLaResult.class);
        // add raw result (formatted json)
        result.rawResult = jsonObject;

        return result;
    }
}
