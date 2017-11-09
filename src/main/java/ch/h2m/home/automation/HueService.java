package ch.h2m.home.automation;

import java.io.StringReader;
import java.util.Calendar;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class HueService {

    private Calendar prevLastUpdated;
    private int lastButtonEvent;

    public static HueDimmerState currentState(){
        JsonObject dimmerObject = callHueDimmer();
        int buttonEvent = dimmerObject.getJsonObject("state").getInt("buttonevent");
        Optional<Calendar> calendar = Converter.parseDate(dimmerObject.getJsonObject("state").getString("lastupdated"));
        return new HueDimmerState(buttonEvent, calendar.get());
    }

    public Optional<Integer> dimmerButtonPressed() {

        JsonObject dimmerObject = callHueDimmer();

        int buttonEvent = dimmerObject.getJsonObject("state").getInt("buttonevent");
        Optional<Calendar> calendar = Converter.parseDate(dimmerObject.getJsonObject("state").getString("lastupdated"));

        if (prevLastUpdated == null || (calendar.isPresent() && prevLastUpdated.before(calendar.get()))) {
            lastButtonEvent = buttonEvent;
            prevLastUpdated = calendar.get();
            return Optional.of(buttonEvent);
        }

        return Optional.empty();
    }

    private static JsonObject callHueDimmer() {
        String hueBridgeUri = PropertyStore.getInstance().getValue("hue.bridge.uri");
        Client client = ClientBuilder.newClient();
        Response response = client
                .target(hueBridgeUri)
                .path("sensors/11")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("cache-control", "no-cache")
                .get();

        String responseBody = response.readEntity(String.class);

        try (JsonReader reader = Json.createReader(new StringReader(responseBody))) {
            return reader.readObject();
        }
    }


}
