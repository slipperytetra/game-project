package main;

public class GameUtils {

    public static String capatilize(String str) {
        str = str.toLowerCase();
        String first = str.substring(0, 1).toUpperCase();
        String remaining = str.substring(1);

        return first + remaining;
    }

}
