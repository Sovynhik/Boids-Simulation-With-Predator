package ru.rsreu.savushkin.boidssimulation.dto;

import java.io.Serializable;
import java.util.List;

public class SimulationState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<EntityDTO.FishDTO> fishes;
    private final EntityDTO.PredatorDTO predator;
    private final boolean simulationOver;

    private SimulationState(List<EntityDTO.FishDTO> fishes,
                            EntityDTO.PredatorDTO predator,
                            boolean simulationOver) {
        this.fishes = fishes;
        this.predator = predator;
        this.simulationOver = simulationOver;
    }

    public List<EntityDTO.FishDTO> getFishes() { return fishes; }
    public EntityDTO.PredatorDTO getPredator() { return predator; }
    public boolean isSimulationOver() { return simulationOver; }

    public static class Builder {
        private List<EntityDTO.FishDTO> fishes;
        private EntityDTO.PredatorDTO predator;
        private boolean simulationOver;

        public static Builder create() { return new Builder(); }

        public Builder fishes(List<EntityDTO.FishDTO> fishes) { this.fishes = fishes; return this; }
        public Builder predator(EntityDTO.PredatorDTO predator) { this.predator = predator; return this; }
        public Builder simulationOver(boolean over) { this.simulationOver = over; return this; }

        public SimulationState build() {
            return new SimulationState(
                    fishes != null ? List.copyOf(fishes) : List.of(),
                    predator,
                    simulationOver
            );
        }
    }
}