package ch.h2m.home.automation;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

class TelegramBotTest {

    @Test
    void updateRx() throws InterruptedException {

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();


        TelegramUpdateListener bot = new TelegramUpdateListener();
        bot.getUdateObservable(telegramBotsApi).forEach(update -> {
            System.out.println(update);
        });

        Thread.sleep(12000);
    }
}