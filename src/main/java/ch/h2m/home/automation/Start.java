package ch.h2m.home.automation;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class Start {

    public static void main(String[] args) throws InterruptedException {

        String command;

/*
        Observable<BigDecimal> smartMeTemperatureA = SmartMeService.smartMeObservable("A");
        Disposable smartMeDisposableA = smartMeTemperatureA.subscribe(
                messages -> System.out.println("Raumtemperatur für A ist " + messages + "°C")
        );

        Observable<BigDecimal> smartMeTemperatureB = SmartMeService.smartMeObservable("B");
        Disposable smartMeDisposableB = smartMeTemperatureB.subscribe(
                messages -> System.out.println("Raumtemperatur für A ist " + messages + "°C")
        );
*/
        Observable<String> hueSwitchPressed = hueObservable();
        Disposable hueSwitchSDisposable = hueSwitchPressed.subscribe(
                message -> callTelegram("Nächste Busse fähren : " + message));

        System.out.println("System is running.");
       /*
        Scanner scanner = new Scanner(System.in);
        System.out.println("System is running. type exit for exit ;-)");
        do {
            command = scanner.nextLine();
        } while (!"exit".equalsIgnoreCase(command));
*/
        Thread.currentThread().join();
        System.out.println("Shutdown");
//        smartMeDisposableA.dispose();
//        smartMeDisposableB.dispose();
        hueSwitchSDisposable.dispose();

    }

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

    private static String callTelegram(String message) {
        String telegramUri = PropertyStore.getInstance().getValue("telegram.uri");

        Client client = ClientBuilder.newClient();
        Response response = client
                .target(telegramUri)
                .path("sendMessage")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("cache-control", "no-cache")
                .post(Entity.json("{\n    \"chat_id\": \"-1001114676560\",\n    \"text\": \"" + message + "\"\n}"));
        return response.readEntity(String.class);

    }


}
