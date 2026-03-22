package ru.rsreu.savushkin.boidssimulation.view;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputManager {
    private final SimulationView view;

    public InputManager(SimulationView view) {
        this.view = view;
    }

    public void setup() {
        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    view.togglePause();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !view.isShowMainMenu()) {
                    view.returnToMainMenu();
                }
            }
        });
    }
}