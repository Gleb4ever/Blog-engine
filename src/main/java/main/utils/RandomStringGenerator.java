package main.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import java.util.Locale;
import java.util.Random;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class RandomStringGenerator
{
    static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final String lower = upper.toLowerCase(Locale.ROOT);
    static final String digits = "0123456789";
    static final String alphanum = upper + lower + digits;
    static StringBuilder stringBuilder;

    public static String randomString(int length)
    {
        stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(alphanum.charAt(new Random().nextInt(alphanum.length())));
        }
        return stringBuilder.toString();
    }
}
