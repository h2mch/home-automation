package ch.h2m.home.automation;

import java.util.Optional;

public class BotConfig {

    private static Optional<String> telegramBotName;
    private static Optional<String> telegramToken;


    public static String telegramBotName(){
        return telegramToken.orElseGet(() -> {
            String value = PropertyStore.getInstance().getValue("telegram.token");
            telegramToken = Optional.ofNullable(value);
            return value;
        });
    }

    public static String telegramToken(){
        return telegramBotName.orElseGet(() -> {
            String value = PropertyStore.getInstance().getValue("telegram.bot.name");
            telegramBotName = Optional.ofNullable(value);
            return value;
        });
    }


}
