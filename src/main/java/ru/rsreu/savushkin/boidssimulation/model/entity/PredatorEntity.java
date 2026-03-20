package ru.rsreu.savushkin.boidssimulation.model.entity;

import ru.rsreu.savushkin.boidssimulation.config.Settings;
import ru.rsreu.savushkin.boidssimulation.dto.SimulationSnapshot;
import ru.rsreu.savushkin.boidssimulation.model.SimulationModel;

import java.awt.Point;

public class PredatorEntity extends Entity {

    public PredatorEntity(int id, Point position, SimulationModel model) {
        super(id, position, Settings.PREDATOR_SPEED, model);
    }

    @Override
    protected void calculateBehavior(SimulationSnapshot snapshot) {
        snapshot.getEntities().stream()
                .filter(e -> e instanceof FishEntity)
                .min(java.util.Comparator.comparingDouble(this::distanceTo))
                .ifPresentOrElse(
                        fish -> {
                            double dx = fish.position.x - position.x;
                            double dy = fish.position.y - position.y;
                            normalizeAndSetVelocity(dx, dy);
                        },
                        () -> {
                            double wander = 0.3;
                            vx += (Math.random() - 0.5) * wander;
                            vy += (Math.random() - 0.5) * wander;
                            normalizeAndSetVelocity(vx, vy);
                        }
                );
    }
}