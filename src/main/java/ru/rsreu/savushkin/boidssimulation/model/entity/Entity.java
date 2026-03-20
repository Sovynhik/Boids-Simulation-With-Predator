package ru.rsreu.savushkin.boidssimulation.model.entity;

import ru.rsreu.savushkin.boidssimulation.config.Settings;
import ru.rsreu.savushkin.boidssimulation.dto.SimulationSnapshot;
import ru.rsreu.savushkin.boidssimulation.model.SimulationModel;

import java.awt.Point;

public abstract class Entity implements Runnable {
    protected final int id;
    protected final Point position;
    protected double vx, vy;
    protected final double speed;
    protected final SimulationModel model;
    private volatile boolean running = true;

    protected Entity(int id, Point position, double speed, SimulationModel model) {
        this.id = id;
        this.position = new Point(position);
        this.speed = speed;
        this.model = model;

        double angle = Math.random() * Math.PI * 2;
        this.vx = Math.cos(angle) * speed;
        this.vy = Math.sin(angle) * speed;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                SimulationSnapshot snapshot = model.getCurrentSnapshot();
                if (snapshot != null) {
                    calculateBehavior(snapshot);
                    position.x += vx;
                    position.y += vy;
                    applyBoundaryAvoidance();
                    clipToBounds();
                }
                Thread.sleep(Settings.ENTITY_TICK_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    protected void applyBoundaryAvoidance() {
        double avoidStrength = speed * 0.8;
        if (position.x < 50) vx += avoidStrength;
        if (position.x > Settings.GAME_FIELD_WIDTH - 50) vx -= avoidStrength;
        if (position.y < 50) vy += avoidStrength;
        if (position.y > Settings.GAME_FIELD_HEIGHT - 50) vy -= avoidStrength;
    }

    protected void clipToBounds() {
        position.x = Math.max(20, Math.min(position.x, Settings.GAME_FIELD_WIDTH - 20));
        position.y = Math.max(20, Math.min(position.y, Settings.GAME_FIELD_HEIGHT - 20));
    }

    protected void normalizeAndSetVelocity(double desiredVx, double desiredVy) {
        double magnitude = Math.hypot(desiredVx, desiredVy);
        if (magnitude > 0.001) {
            vx = (desiredVx / magnitude) * speed;
            vy = (desiredVy / magnitude) * speed;
        } else {
            vx = 0;
            vy = 0;
        }
    }

    public double distanceTo(Entity other) {
        return position.distance(other.position);
    }

    public void stop() {
        running = false;
    }

    public int getId() { return id; }
    public Point getPosition() { return new Point(position); }
    public double getVelocityX() { return vx; }
    public double getVelocityY() { return vy; }
    public void setVelocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    protected abstract void calculateBehavior(SimulationSnapshot snapshot);
}