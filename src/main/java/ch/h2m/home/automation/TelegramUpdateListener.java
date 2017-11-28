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

                private Optional<String> telegramBotName;
                private Optional<String> telegramToken;

                @Override
                public String getBotToken() {
                    return telegramToken.orElseGet(() -> {
                        String value = PropertyStore.getInstance().getValue("telegram.token");
                        telegramToken = Optional.ofNullable(value);
                        return value;
                    });
                }

                @Override
                public String getBotUsername() {
                    return telegramBotName.orElseGet(() -> {
                        String value = PropertyStore.getInstance().getValue("telegram.bot.name");
                        telegramBotName = Optional.ofNullable(value);
                        return value;
                    });
                }

                @Override
                public void onUpdateReceived(Update update) {
                    System.out.println("update:" + update);
                    emitter.onNext(update);
                }
            });
        });
    }
}