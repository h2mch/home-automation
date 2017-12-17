package ch.h2m.home.automation;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

class TelegramBotTest {

    @Test
    void updateRx() throws InterruptedException {

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();


        TelegramUpdateListener bot = new TelegramUpdateListener();
        bot.getUdateObservable(telegramBotsApi).forEach(update -> {


            telegramBotsApi.registerBot(new TelegramLongPollingBot() {



                @Override
                public String getBotToken() {
                    return BotConfig.telegramToken();
                }

                @Override
                public void onUpdateReceived(Update update) {
                    //

                }

                @Override
                public String getBotUsername() {
                    return BotConfig.telegramBotName();
                }
            });

            System.out.println(update);
        });

        Thread.sleep(12000);
    }
}