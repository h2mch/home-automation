package ch.h2m.home.automation;

import java.math.BigDecimal;
import java.time.Instant;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;


public class Start {

    public static void main(String[] args) throws InterruptedException {

        System.out.println(Instant.now().toString() + ": System is running");

        Observable<String> hueSwitchPressed = HueService.hueObservable();
        Observable<Boolean> hueLightIsOn = HueService.hueLightObservable();
        Observable<BigDecimal> smartMeTemperatureA = SmartMeService.smartMeObservable("A");
        Observable<BigDecimal> smartMeTemperatureB = SmartMeService.smartMeObservable("B");


        Disposable smartMeDisposableA = smartMeTemperatureA.subscribe(
                messages -> System.out.println("A:" + messages + "°C" + ":" + Instant.now().toString())
        );
        Disposable smartMeDisposableB = smartMeTemperatureB.subscribe(
                messages -> System.out.println("B:" + messages + "°C" + ":" + Instant.now().toString())
        );


        Disposable hueLightIsOnDisposable = hueLightIsOn.subscribe(

                stateChange -> System.out.println("offf!!!!")
                // get the time from the statechange
                // compare with the current time
                // if difference > 1h switch of the light
        );

        // use key press duration to switch off the lights in f.e. 1min.


        Disposable hueSwitchSDisposable = hueSwitchPressed.subscribe(
                message -> callTelegram("Nächste Busse fähren : " + message));


        // Do not stop Application
        Thread.currentThread().join();

        System.out.println(Instant.now().toString() + ": Shutdown");
        hueSwitchSDisposable.dispose();
        smartMeDisposableA.dispose();
        smartMeDisposableB.dispose();
        //      hueLightIsOnDisposable.dispose();
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
