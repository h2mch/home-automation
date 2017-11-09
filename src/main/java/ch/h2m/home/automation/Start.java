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

        Observable<String> hueSwitchPressed = HueService.hueObservable();
        Observable<BigDecimal> smartMeTemperatureA = SmartMeService.smartMeObservable("A");
        Observable<BigDecimal> smartMeTemperatureB = SmartMeService.smartMeObservable("B");

        Disposable smartMeDisposableA = smartMeTemperatureA.subscribe(
                messages -> System.out.println("Raumtemperatur für A ist " + messages + "°C")
        );

        Disposable smartMeDisposableB = smartMeTemperatureB.subscribe(
                messages -> System.out.println("Raumtemperatur für A ist " + messages + "°C")
        );


        Disposable hueSwitchSDisposable = hueSwitchPressed.subscribe(
                message -> callTelegram("Nächste Busse fähren : " + message));

        System.out.println("System is running.");

        // Do not stop Application
        Thread.currentThread().join();

        System.out.println("Shutdown");
        hueSwitchSDisposable.dispose();
        smartMeDisposableA.dispose();
        smartMeDisposableB.dispose();
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
