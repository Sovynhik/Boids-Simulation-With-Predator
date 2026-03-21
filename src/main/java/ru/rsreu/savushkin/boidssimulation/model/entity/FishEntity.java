package ru.rsreu.savushkin.boidssimulation.model.entity;

import ru.rsreu.savushkin.boidssimulation.config.Settings;
import ru.rsreu.savushkin.boidssimulation.dto.SimulationSnapshot;
import ru.rsreu.savushkin.boidssimulation.model.SimulationModel;

import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;

public class FishEntity extends Entity {

    public FishEntity(int id, Point position, SimulationModel model) {
        super(id, position, Settings.FISH_SPEED, model);
    }

    @Override
    protected void calculateBehavior(SimulationSnapshot snapshot) {
        List<FishEntity> neighbors = snapshot.getEntities().stream()
                .filter(e -> e instanceof FishEntity && e != this)
                .map(e -> (FishEntity) e)
                .collect(Collectors.toList());

        PredatorEntity predator = snapshot.getPredator();

        if (predator != null && distanceTo(predator) < Settings.PANIC_RADIUS) {
            double dx = position.x - predator.position.x;
            double dy = position.y - predator.position.y;
            normalizeAndSetVelocity(dx * 3, dy * 3);
            return;
        }

        double sepX = 0, sepY = 0;
        double alignX = 0, alignY = 0;
        double cohX = 0, cohY = 0;
        int sepCount = 0, alignCount = 0, cohCount = 0;

        for (FishEntity other : neighbors) {
            double d = distanceTo(other);
            if (d < Settings.SEPARATION_RADIUS) {
                sepX += position.x - other.position.x;
                sepY += position.y - other.position.y;
                sepCount++;
            }
            if (d < Settings.ALIGNMENT_RADIUS) {
                alignX += other.vx;
                alignY += other.vy;
                alignCount++;
            }
            if (d < Settings.COHESION_RADIUS) {
                cohX += other.position.x;
                cohY += other.position.y;
                cohCount++;
            }
        }

        double totalVx = 0, totalVy = 0;

        if (sepCount > 0) {
            totalVx += sepX * Settings.SEPARATION_WEIGHT;
            totalVy += sepY * Settings.SEPARATION_WEIGHT;
        }
        if (alignCount > 0) {
            totalVx += alignX * Settings.ALIGNMENT_WEIGHT;
            totalVy += alignY * Settings.ALIGNMENT_WEIGHT;
        }
        if (cohCount > 0) {
            totalVx += ((cohX / cohCount) - position.x) * Settings.COHESION_WEIGHT;
            totalVy += ((cohY / cohCount) - position.y) * Settings.COHESION_WEIGHT;
        }

        totalVx += (Math.random() - 0.5) * Settings.JITTER_STRENGTH;
        totalVy += (Math.random() - 0.5) * Settings.JITTER_STRENGTH;

        normalizeAndSetVelocity(totalVx, totalVy);
    }
}