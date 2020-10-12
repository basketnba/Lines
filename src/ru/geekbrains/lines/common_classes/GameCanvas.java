package ru.geekbrains.lines.common_classes;

import javax.swing.*;
import java.awt.*;

public class GameCanvas extends JPanel {

    private final CanvasPaintListener listener;
    private long lastFrameTime;

    public GameCanvas(CanvasPaintListener listener) {
        this.listener = listener;
        lastFrameTime = System.nanoTime();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - lastFrameTime) * 0.000000001f;
        lastFrameTime = currentTime;

        listener.onDrawFrame(this, deltaTime, g);
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        repaint();
    }

    public int getLeft() {
        return 0;
    }

    public int getRight() {
        return getWidth() - 1;
    }

    public int getBottom() {
        return getHeight() - 1;
    }

    public int getTop() {
        return 0;
    }
}