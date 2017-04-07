package net.cerberus.queueBot.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.JSONObject;

public class JsonBeautifier {

    public static String beatifyJsonObject(JSONObject sourceObject){

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jsonParser = new JsonParser();
        JsonElement object = jsonParser.parse(sourceObject.toString());

        return gson.toJson(object);
    }
}
