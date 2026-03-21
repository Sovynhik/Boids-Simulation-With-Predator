package ru.rsreu.savushkin.boidssimulation.dto;

import ru.rsreu.savushkin.boidssimulation.model.entity.PredatorEntity;
import ru.rsreu.savushkin.boidssimulation.model.entity.RunnableEntity;

import java.util.List;

public class SimulationSnapshot {
    private final List<RunnableEntity> entities;
    private final PredatorEntity predator;

    public SimulationSnapshot(List<RunnableEntity> entities, PredatorEntity predator, int w, int h) {
        this.entities = List.copyOf(entities);
        this.predator = predator;
    }

    public List<RunnableEntity> getEntities() { return entities; }
    public PredatorEntity getPredator() { return predator; }
}