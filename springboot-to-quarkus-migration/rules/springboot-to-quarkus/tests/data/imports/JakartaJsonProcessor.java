package com.example.json;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonArray;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;

public class JakartaJsonProcessor {
    
    public JsonObject processJson(String jsonString) {
        JsonReader reader = Json.createReader(new StringReader(jsonString));
        JsonObject jsonObject = reader.readObject();
        reader.close();
        return jsonObject;
    }
    
    public String writeJson(JsonObject jsonObject) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = Json.createWriter(stringWriter);
        writer.writeObject(jsonObject);
        writer.close();
        return stringWriter.toString();
    }
}







