package ru.rsreu.savushkin.boidssimulation.view;

import ru.rsreu.savushkin.boidssimulation.config.Settings;
import ru.rsreu.savushkin.boidssimulation.controller.SimulationController;
import ru.rsreu.savushkin.boidssimulation.dto.SimulationState;

import javax.swing.*;
import java.awt.*;

public class SimulationView extends JPanel implements Subscriber {
    private final SimulationController controller;
    private final MenuManager menuManager;
    private final InputManager inputManager;
    private final Renderer renderer;
    private final FileDialogManager fileDialog;

    private volatile SimulationState state;
    private volatile boolean showMainMenu = true;
    private volatile boolean showPauseMenu = false;

    public SimulationView(SimulationController controller) {
        this.controller = controller;
        this.menuManager = new MenuManager(this);
        this.inputManager = new InputManager(this);
        this.renderer = new Renderer();
        this.fileDialog = new FileDialogManager(this);
        initUI();
        updateMenu();
    }

    private void initUI() {
        setPreferredSize(Settings.GAME_FIELD_DIMENSION);
        setBackground(Settings.BACKGROUND_COLOR);
        setLayout(null);
        setFocusable(true);

        menuManager.addTo(this);
        inputManager.setup();

        JFrame frame = new JFrame("Boids Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        controller.subscribeOnModel(this);

        startUpdateTimer();

        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void startUpdateTimer() {
        new javax.swing.Timer(Settings.ENTITY_TICK_DELAY, e -> {
            if (!showMainMenu && !showPauseMenu && !controller.getModel().isSimulationOver()) {
                controller.getModel().update();
            }
        }).start();
    }

    public void startNew() {
        showMainMenu = false;
        showPauseMenu = false;
        updateMenu();
        controller.startSimulation();
        repaint();
    }

    public void loadGame() {
        fileDialog.showLoadDialog();
    }

    public void saveGame() {
        fileDialog.showSaveDialog(controller.getSimulationState());
    }

    public void returnToMainMenu() {
        showMainMenu = true;
        showPauseMenu = false;
        updateMenu();
        controller.stopSimulation();
        repaint();
    }

    public void togglePause() {
        if (!showMainMenu) {
            controller.pauseSimulation();
            showPauseMenu = !showPauseMenu;
            updateMenu();
            repaint();
        }
    }

    protected void updateMenu() {
        menuManager.update(showMainMenu, showPauseMenu);
    }

    @Override
    public void notifySubscriber() {
        state = controller.getSimulationState();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!showMainMenu && !showPauseMenu && state != null) {
            renderer.render(g2d, controller.getModel().getSimulationState(), controller.getModel());        }

        g2d.dispose();
    }

    public SimulationController getController() { return controller; }
    public boolean isShowMainMenu() { return showMainMenu; }
    public boolean isShowPauseMenu() { return showPauseMenu; }
    public void setShowMainMenu(boolean b) { showMainMenu = b; }
    public void setShowPauseMenu(boolean b) { showPauseMenu = b; }
}