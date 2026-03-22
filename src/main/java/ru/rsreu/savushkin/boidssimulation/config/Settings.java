package ru.rsreu.savushkin.boidssimulation.config;

import java.awt.*;

public final class Settings {
    public static final int INITIAL_FISH_COUNT = 35;
    public static final int FISH_RESPAWN_THRESHOLD = 20;
    public static final int FISH_RESPAWN_AMOUNT = 5;
    public static final double FISH_SPEED = 2.5;
    public static final double PREDATOR_SPEED = 3.8;

    public static final int COHESION_RADIUS = 100;
    public static final int SEPARATION_RADIUS = 15;
    public static final int ALIGNMENT_RADIUS = 60;
    public static final int PANIC_RADIUS = 110;
    public static final int EAT_RADIUS = 12;

    public static final int GAME_FIELD_WIDTH = 600;
    public static final int GAME_FIELD_HEIGHT = 500;
    public static final Dimension GAME_FIELD_DIMENSION = new Dimension(GAME_FIELD_WIDTH, GAME_FIELD_HEIGHT);

    public static final int ENTITY_TICK_DELAY = 16;

    public static final Color BACKGROUND_COLOR = new Color(10, 25, 50);

    public static final double SEPARATION_WEIGHT = 2.2;
    public static final double ALIGNMENT_WEIGHT = 1.2;
    public static final double COHESION_WEIGHT = 0.35;
    public static final double JITTER_STRENGTH = 0.8;

    public static final String SAVE_EXTENSION = "sav";
    public static final String DOT_SAVE_EXTENSION = ".sav";
    public static final String SAVE_FILE_PREFIX = "save_";
    public static final String SAVE_FILE_DATE_PATTERN = "yyyy-MM-dd_HH-mm-ss";

    private Settings() {}
}