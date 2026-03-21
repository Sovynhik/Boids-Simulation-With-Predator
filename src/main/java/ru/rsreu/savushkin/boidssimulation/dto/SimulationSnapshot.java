package ru.rsreu.savushkin.boidssimulation.dto;

import ru.rsreu.savushkin.boidssimulation.model.entity.PredatorEntity;
import ru.rsreu.savushkin.boidssimulation.model.entity.Entity;

import java.util.List;

public class SimulationSnapshot {
    private final List<Entity> entities;
    private final PredatorEntity predator;

    public SimulationSnapshot(List<Entity> entities, PredatorEntity predator, int w, int h) {
        this.entities = List.copyOf(entities);
        this.predator = predator;
    }

    public List<Entity> getEntities() { return entities; }
    public PredatorEntity getPredator() { return predator; }
}