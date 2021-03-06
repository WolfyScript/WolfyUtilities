package me.wolfyscript.utilities.util;


import org.bukkit.util.Vector;

import java.util.Random;

public class RandomUtils {

    private RandomUtils() {
    }

    public static final Random random = new Random(System.currentTimeMillis());

    public static Vector getRandomVector() {
        double x = random.nextDouble() * 2 - 1;
        double y = random.nextDouble() * 2 - 1;
        double z = random.nextDouble() * 2 - 1;
        return (new Vector(x, y, z)).normalize();
    }

    public static Vector getRandomCircleVector() {
        double rnd = random.nextDouble() * 2.0D * Math.PI;
        double x = Math.cos(rnd);
        double z = Math.sin(rnd);
        return new Vector(x, 0.0D, z);
    }
}
