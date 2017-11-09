package ch.h2m.home.automation;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.bind.DatatypeConverter;

public class Converter {


    /**
     * According to the cimi spec 5.5.2. Date has to be in the format ISO8601. (ISO 8601
     * YYYY-MM-DDThh:mm:ss.sTZD, example: 2016-01-13T11:00:00.000-08:00)
     *
     * @param iso8601Date iso8601 formatted value
     */
    public static Optional<Calendar> parseDate(String iso8601Date) {
        //datatype converter in JAXB, since JAXB must be able to parse ISO8601
        Calendar calendar = null;
        try {
            calendar = DatatypeConverter.parseDateTime(iso8601Date);
        } catch (IllegalArgumentException iae) {
            try {
                //DatetypeConerter can not handle 2016-06-28T11:10:17.000+0000 format (no colon in
                // the timezone) According to https://en.wikipedia.org/wiki/ISO_8601 it should be
                // possible.
                Date parse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault()).parse(iso8601Date);
                calendar = Calendar.getInstance();
                calendar.setTime(parse);
            } catch (ParseException e) {
                calendar = null;
            }
        }
        return Optional.ofNullable(calendar);
    }


    public static JsonArray getJsonArray(String responseBody) {
        try (JsonReader reader = Json.createReader(new StringReader(responseBody))) {
            return reader.readArray();
        }
    }

    public static JsonObject getJsonObject(String responseBody) {
        try (JsonReader reader = Json.createReader(new StringReader(responseBody))) {
            return reader.readObject();
        }
    }
}
