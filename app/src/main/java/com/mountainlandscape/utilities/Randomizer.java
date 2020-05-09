package com.mountainlandscape.utilities;

import java.util.Random;

public class Randomizer {
    static Random random = new Random();
    public static int randomInt(int min,int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    public static int randomSignum() {
        return random.nextBoolean() ? 1 : -1;
    }

    public static double randomDouble(int max) {
        return random.nextDouble() * (max + 1);
    }
}
