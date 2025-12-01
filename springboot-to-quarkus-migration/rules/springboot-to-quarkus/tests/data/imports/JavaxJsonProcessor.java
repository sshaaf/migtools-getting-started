package com.example.json;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonWriter;

public class JavaxJsonProcessor {
    
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







