package ru.geekbrains.lines.common_classes;

import java.awt.*;

public interface CanvasPaintListener {

    void onDrawFrame(GameCanvas gameCanvas, float deltaTime, Graphics g);
}