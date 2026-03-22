package ru.rsreu.savushkin.boidssimulation.model;

import ru.rsreu.savushkin.boidssimulation.config.Settings;
import ru.rsreu.savushkin.boidssimulation.dto.EntityDTO;
import ru.rsreu.savushkin.boidssimulation.dto.SimulationSnapshot;
import ru.rsreu.savushkin.boidssimulation.dto.SimulationState;
import ru.rsreu.savushkin.boidssimulation.model.entity.*;
import ru.rsreu.savushkin.boidssimulation.view.Subscriber;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SimulationModel {
    private final Set<Subscriber> subscribers = Collections.synchronizedSet(new HashSet<>());
    private final CopyOnWriteArrayList<Entity> entities = new CopyOnWriteArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(0);
    private final List<Point> eatEffects = new CopyOnWriteArrayList<>();
    private long lastEatEffectClear = System.currentTimeMillis();

    private volatile boolean simulationOver = true;
    private volatile boolean paused = false;

    private ExecutorService executor;
    private final AtomicReference<SimulationSnapshot> currentSnapshot = new AtomicReference<>();

    public synchronized void startNewSimulation() {
        finishSimulation();
        entities.clear();
        eatEffects.clear();

        executor = Executors.newCachedThreadPool();

        PredatorEntity predator = createPredator();
        entities.add(predator);
        executor.submit(predator);

        for (int i = 0; i < Settings.INITIAL_FISH_COUNT; i++) {
            FishEntity fish = createFish();
            entities.add(fish);
            executor.submit(fish);
        }

        simulationOver = false;
        paused = false;

        updateSnapshot();
        notifySubscribers();
    }

    public synchronized void loadSimulation(SimulationState state) {
        finishSimulation();
        entities.clear();
        eatEffects.clear();

        executor = Executors.newCachedThreadPool();

        if (state.getPredator() != null) {
            var dto = state.getPredator();
            PredatorEntity p = new PredatorEntity(dto.id(), new Point(dto.position()), this);
            p.setVelocity(dto.vx(), dto.vy());
            entities.add(p);
            executor.submit(p);
        }

        for (EntityDTO.FishDTO dto : state.getFishes()) {
            FishEntity fish = new FishEntity(dto.id(), new Point(dto.position()), this);
            fish.setVelocity(dto.vx(), dto.vy());
            entities.add(fish);
            executor.submit(fish);
        }

        simulationOver = state.isSimulationOver();
        paused = false;
        updateSnapshot();
        notifySubscribers();
    }

    public void finishSimulation() {
        simulationOver = true;
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        entities.forEach(Entity::stop);
        eatEffects.clear();
        notifySubscribers();
    }

    public void switchPause() {
        paused = !paused;
        notifySubscribers();
    }

    public void update() {
        if (simulationOver || paused) return;

        updateSnapshot();
        applyCollisions();
        checkAndRespawnFish();
        clearOldEatEffects();
        notifySubscribers();
    }

    private void updateSnapshot() {
        PredatorEntity predator = entities.stream()
                .filter(e -> e instanceof PredatorEntity)
                .map(e -> (PredatorEntity) e)
                .findFirst()
                .orElse(null);

        List<Entity> copy = new ArrayList<>(entities);
        currentSnapshot.set(new SimulationSnapshot(
                copy,
                predator,
                Settings.GAME_FIELD_WIDTH,
                Settings.GAME_FIELD_HEIGHT
        ));
    }

    private void applyCollisions() {
        PredatorEntity predator = entities.stream()
                .filter(e -> e instanceof PredatorEntity)
                .map(e -> (PredatorEntity) e)
                .findFirst()
                .orElse(null);

        if (predator == null) return;

        List<Entity> toRemove = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof FishEntity fish && predator.distanceTo(e) < Settings.EAT_RADIUS) {
                toRemove.add(e);
                eatEffects.add(new Point(fish.getPosition()));
            }
        }

        for (Entity e : toRemove) {
            e.stop();
            entities.remove(e);
        }
    }

    private void checkAndRespawnFish() {
        long fishCount = entities.stream().filter(e -> e instanceof FishEntity).count();
        if (fishCount < Settings.FISH_RESPAWN_THRESHOLD) {
            int toAdd = Settings.FISH_RESPAWN_AMOUNT;
            for (int i = 0; i < toAdd; i++) {
                FishEntity fish = createFish();
                entities.add(fish);
                executor.submit(fish);
            }
        }
    }

    private void clearOldEatEffects() {
        long now = System.currentTimeMillis();
        if (now - lastEatEffectClear > 800) {
            eatEffects.clear();
            lastEatEffectClear = now;
        }
    }

    private FishEntity createFish() {
        return new FishEntity(idCounter.incrementAndGet(), randomPointOnField(), this);
    }

    private PredatorEntity createPredator() {
        return new PredatorEntity(idCounter.incrementAndGet(), randomPointOnField(), this);
    }

    private Point randomPointOnField() {
        Random r = new Random();
        return new Point(
                50 + r.nextInt(Settings.GAME_FIELD_WIDTH - 100),
                50 + r.nextInt(Settings.GAME_FIELD_HEIGHT - 100)
        );
    }

    public SimulationSnapshot getCurrentSnapshot() {
        return currentSnapshot.get();
    }

    public SimulationState getSimulationState() {
        List<EntityDTO.FishDTO> fishDTOs = entities.stream()
                .filter(e -> e instanceof FishEntity)
                .map(e -> {
                    FishEntity f = (FishEntity) e;
                    return new EntityDTO.FishDTO(f.getId(), f.getPosition(), f.getVelocityX(), f.getVelocityY());
                })
                .collect(Collectors.toList());

        EntityDTO.PredatorDTO predatorDTO = null;
        PredatorEntity predator = entities.stream()
                .filter(e -> e instanceof PredatorEntity)
                .map(e -> (PredatorEntity) e)
                .findFirst()
                .orElse(null);

        if (predator != null) {
            predatorDTO = new EntityDTO.PredatorDTO(
                    predator.getId(),
                    predator.getPosition(),
                    predator.getVelocityX(),
                    predator.getVelocityY()
            );
        }

        return SimulationState.Builder.create()
                .fishes(fishDTOs)
                .predator(predatorDTO)
                .simulationOver(simulationOver)
                .build();
    }

    public List<Point> getEatEffects() {
        return Collections.unmodifiableList(eatEffects);
    }

    public void subscribe(Subscriber s) { subscribers.add(s); }
    private void notifySubscribers() { subscribers.forEach(Subscriber::notifySubscriber); }

    public boolean isSimulationOver() { return simulationOver; }
}