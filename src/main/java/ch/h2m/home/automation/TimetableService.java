package ch.h2m.home.automation;

import sun.misc.Cleaner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class TimetableService {

    private final static String TIMETABLE_URI = "http://transport.opendata.ch/v1";

    private static final Client client = ClientBuilder.newClient();

    public static List<LocalDateTime> getNextTwoDepartureToBahnhof() {
        String timetableFrom = PropertyStore.getInstance().getValue("timetable.from");
        String timetableTo = PropertyStore.getInstance().getValue("timetable.to");

        JsonObject connections = getNextTwoConnectionBetweenSternmattBahnhof(timetableFrom, timetableTo, 2);
        List<LocalDateTime> localDateTimes = new ArrayList<>();

        for (JsonValue connection : connections.getJsonArray("connections")) {
            String datetimeString = ((JsonObject) connection).getJsonObject("from").getString("departure");
            Optional<Calendar> calendar = Converter.parseDate(datetimeString);

            LocalDateTime ldt = LocalDateTime.ofInstant(calendar.get().toInstant(), ZoneId.systemDefault());
            localDateTimes.add(ldt);
        }

        return localDateTimes;
    }

    private static JsonObject getNextTwoConnectionBetweenSternmattBahnhof(String from, String to, int limit) {
        Response response = client
                .target(TIMETABLE_URI)
                .path("connections")
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("fields[]", "connections/from/departure")
                .queryParam("fields[]", "connections/from/station/name")
                .queryParam("fields[]", "connections/to/arrival")
                .queryParam("fields[]", "connections/to/station/name")
                .queryParam("limit", limit)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("cache-control", "no-cache")
                .get();

        if (response.getStatus() != 200) {
            String errorEntity = null;
            if (response.hasEntity()) {
                errorEntity = response.readEntity(String.class);
            }
            throw new RuntimeException("Request to Twitter was not successful. Response code: "
                    + response.getStatus() + ", reason: " + response.getStatusInfo().getReasonPhrase()
                    + ", entity: " + errorEntity);
        }

        String responseBody = response.readEntity(String.class);

        return Converter.getJsonObject(responseBody);
    }
}
