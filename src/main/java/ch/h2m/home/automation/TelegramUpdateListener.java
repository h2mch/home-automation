package ch.h2m.home.automation;

import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.Optional;

import io.reactivex.Observable;

public class TelegramUpdateListener {


    public Observable<Update> getUdateObservable(TelegramBotsApi botsApi) {
        return Observable.create(emitter -> {

            botsApi.registerBot(new TelegramLongPollingBot() {

                @Override
                public String getBotToken() {
                    return BotConfig.telegramToken();
                }

                @Override
                public String getBotUsername() {
                    return BotConfig.telegramBotName();
                }

                @Override
                public void onUpdateReceived(Update update) {
                    emitter.onNext(update);
                }
            });
        });
    }
}