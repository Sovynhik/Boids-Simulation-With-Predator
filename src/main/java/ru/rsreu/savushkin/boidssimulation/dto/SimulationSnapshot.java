package ru.rsreu.savushkin.boidssimulation.dto;

import ru.rsreu.savushkin.boidssimulation.model.entity.PredatorEntity;
import ru.rsreu.savushkin.boidssimulation.model.entity.Entity;

import java.util.List;

public class SimulationSnapshot {
    private final List<Entity> entities;
    private final PredatorEntity predator;
    private final int width, height;

    public SimulationSnapshot(List<Entity> entities, PredatorEntity predator, int w, int h) {
        this.entities = List.copyOf(entities);
        this.predator = predator;
        this.width = w;
        this.height = h;
    }

    public List<Entity> getEntities() { return entities; }
    public PredatorEntity getPredator() { return predator; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}