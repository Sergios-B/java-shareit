package ru.practicum.shareit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class MockGeneratorTest {

    protected static Random rnd = new Random();

    public static String generatorText(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(rnd.nextInt(characters.length())));
        }

        return sb.toString();
    }

    public static String generatorEmail() {

        String[] postfix = {"@mail.ru","@yandex.ru","gmail.com"};

        return generatorText(12) + postfix[rnd.nextInt(postfix.length)];
    }
}
