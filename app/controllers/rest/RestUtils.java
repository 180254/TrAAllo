package controllers.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import play.libs.Json;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

public class RestUtils {

    public static String asJson(Object object) {
        Json.mapper().registerModule(new JSR310Module());
        Json.mapper().configure(WRITE_DATES_AS_TIMESTAMPS, false);
        JsonNode jsonNode = Json.toJson(object);
        return Json.prettyPrint(jsonNode);
    }
}
