package ch.h2m.home.automation;

import java.io.StringReader;
import java.util.Calendar;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.h2m.home.automation.entity.HueDimmerState;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class HueService {

    public static Observable<String> hueObservable() {
        Observable<String> sourceObsevable = Observable.interval(5, TimeUnit.SECONDS, Schedulers.io())
                .map(tick -> HueService.currentState())
                .doOnError(err -> System.err.println("Error retrieving hue messages"))
                .retry()
                .distinctUntilChanged()
                .map(currentState -> currentState.getButtonEvent())
                .filter(buttonEvent -> buttonEvent.equals(2002))
                .map(switchIsPressed -> TimetableService.getNextTwoDepartureToBahnhof())
                .map(calendar -> calendar
                        .stream()
                        .map(cal -> cal.toLocalTime().toString())
                        .collect(Collectors.joining(", "))
                );
        return sourceObsevable;
    }

    public static HueDimmerState currentState() {
        JsonObject dimmerObject = callHueDimmer();
        int buttonEvent = dimmerObject.getJsonObject("state").getInt("buttonevent");
        Optional<Calendar> calendar = Converter.parseDate(dimmerObject.getJsonObject("state").getString("lastupdated"));
        return new HueDimmerState(buttonEvent, calendar.get());
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
