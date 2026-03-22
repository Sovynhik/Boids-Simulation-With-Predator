package ru.rsreu.savushkin.boidssimulation.view;

import ru.rsreu.savushkin.boidssimulation.config.Settings;
import ru.rsreu.savushkin.boidssimulation.dto.EntityDTO;
import ru.rsreu.savushkin.boidssimulation.dto.SimulationState;
import ru.rsreu.savushkin.boidssimulation.model.SimulationModel;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class Renderer {
    public void render(Graphics2D g, SimulationState state, SimulationModel model) {
        long time = System.currentTimeMillis();

        drawWaterBackground(g, time);

        for (EntityDTO.FishDTO fish : state.getFishes()) {
            drawFishShadow(g, fish);
        }
        if (state.getPredator() != null) {
            drawPredatorShadow(g, state.getPredator());
        }

        for (EntityDTO.FishDTO fish : state.getFishes()) {
            drawFish(g, fish, time);
        }

        if (state.getPredator() != null) {
            drawPredator(g, state.getPredator(), time);
        }

        drawEatEffects(g, model);

        drawStats(g, state.getFishes().size());
    }

    private void drawWaterBackground(Graphics2D g, long time) {
        g.setColor(new Color(10, 40, 100));
        g.fillRect(0, 0, Settings.GAME_FIELD_WIDTH, Settings.GAME_FIELD_HEIGHT);

        g.setColor(new Color(30, 100, 180, 80));
        for (int y = 0; y < Settings.GAME_FIELD_HEIGHT + 100; y += 40) {
            int offset = (int) (Math.sin((y + time * 0.06) * 0.015) * 30);
            g.fillRect(-100 + offset, y, Settings.GAME_FIELD_WIDTH + 200, 20);
        }
    }

    private void drawFishShadow(Graphics2D g, EntityDTO.FishDTO fish) {
        Point p = fish.position();
        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(p.x - 20, p.y + 8, 40, 16);
    }

    private void drawPredatorShadow(Graphics2D g, EntityDTO.PredatorDTO predator) {
        Point p = predator.position();
        g.setColor(new Color(100, 0, 0, 60));
        g.fillOval(p.x - 35, p.y + 15, 70, 25);
    }

    private void drawFish(Graphics2D g, EntityDTO.FishDTO fish, long time) {
        Point p = fish.position();
        double vx = fish.vx();
        double vy = fish.vy();
        double angle = Math.atan2(vy, vx);

        g.translate(p.x, p.y);
        g.rotate(angle);

        GradientPaint gp = new GradientPaint(-15, 0, new Color(100, 200, 255),
                15, 0, new Color(0, 120, 255));
        g.setPaint(gp);
        g.fill(new Ellipse2D.Double(-18, -10, 36, 20));

        double tailWave = Math.sin(time * 0.012 + fish.id() * 0.5) * 5;
        Polygon tail = new Polygon();
        tail.addPoint(-18, 0);
        tail.addPoint((int)(-32 - tailWave), -12);
        tail.addPoint((int)(-32 - tailWave), 12);
        g.setColor(new Color(0, 150, 255));
        g.fill(tail);

        g.setColor(Color.WHITE);
        g.fillOval(10, -5, 8, 10);
        g.setColor(Color.BLACK);
        g.fillOval(13, -3, 4, 6);

        g.rotate(-angle);
        g.translate(-p.x, -p.y);
    }

    private void drawPredator(Graphics2D g, EntityDTO.PredatorDTO predator, long time) {
        Point p = predator.position();
        double vx = predator.vx();
        double vy = predator.vy();
        double angle = Math.atan2(vy, vx);

        g.translate(p.x, p.y);
        g.rotate(angle);

        GradientPaint gp = new GradientPaint(0, -18, new Color(220, 20, 60),
                0, 18, new Color(139, 0, 0));
        g.setPaint(gp);
        g.fill(new RoundRectangle2D.Double(-30, -18, 60, 36, 25, 25));

        g.setColor(new Color(180, 0, 0));
        Polygon dorsal = new Polygon();
        dorsal.addPoint(0, -18);
        dorsal.addPoint(20, -35);
        dorsal.addPoint(15, -15);
        g.fill(dorsal);

        g.setColor(Color.WHITE);
        g.fillOval(22, -10, 12, 12);
        g.fillOval(22, 2, 12, 12);
        g.setColor(Color.RED.darker().darker());
        g.fillOval(26, -7, 6, 6);
        g.fillOval(26, 5, 6, 6);

        g.setColor(Color.WHITE);
        for (int i = 0; i < 10; i++) {
            int x = 28 + i * 3;
            int offset = (i % 2 == 0) ? -10 : -6;
            g.fill(new Polygon(new int[]{x, x + 4, x + 8}, new int[]{0, offset, 0}, 3));
        }

        g.rotate(-angle);
        g.translate(-p.x, -p.y);
    }

    private void drawEatEffects(Graphics2D g, SimulationModel model) {
        for (Point p : model.getEatEffects()) {
            int alpha = 255;
            for (int i = 1; i <= 5; i++) {
                g.setColor(new Color(255, 180, 0, alpha));
                g.fillOval(p.x - i*15, p.y - i*15, i*30, i*30);
                alpha -= 50;
            }
        }
    }

    private void drawStats(Graphics2D g, int fishCount) {
        g.setColor(new Color(255, 255, 255, 200));
        g.setFont(new Font("Consolas", Font.BOLD, 18));
        g.drawString("Рыб в стае: " + fishCount, 15, 35);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(new Color(200, 200, 255));
        g.drawString("SPACE — пауза | ESC — меню", 15, Settings.GAME_FIELD_HEIGHT - 15);
    }
}