package ru.geekbrains.lines.game;

import ru.geekbrains.lines.common_classes.GameCanvas;
import ru.geekbrains.lines.common_classes.Sprite;

import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

class Ball extends Sprite {

    private float vx = 150f + (float) (Math.random() * 200f);
    private float vy = 150f + (float) (Math.random() * 200f);
    private Color color;

    private static final float BALL_RADIUS = 38.5f;
    private static final Color[] colors = {
            Color.GREEN,
            Color.BLUE,
            Color.CYAN,
            Color.RED,
            Color.YELLOW,
            Color.MAGENTA};
    private static ArrayList<Ball> balls = new ArrayList<>();

    Ball(int x, int y) {
        halfWidth = BALL_RADIUS;
        halfHeight = halfWidth;
        this.x = x;//вместо х и у что то что будет хранить направление вектора
        this.y = y;
        setColor();
        balls.add(this);
    }

    Color getColor() {
        return color;
    }

    void setColor() {
        color = colors[(int) (Math.random() * colors.length)];
    }

    @Override
    public void update(GameCanvas gameCanvas, float deltaTime) {
        x = x + vx * deltaTime;
        y = y + vy * deltaTime;
//        for (int i = 0; i < balls.size(); i++) {
//            for (int j = 0; j < balls.size(); j++) {
//                Ball ball1 = balls.get(i);
//                Ball ball2 = balls.get(j);
//                if (ball1 == ball2) continue;
//                if (Math.sqrt(Math.pow(ball1.x - ball2.x, 2) + Math.pow(ball1.y - ball2.y, 2)) <= (ball1.halfWidth + ball2.halfWidth))
//                    ;
//                ball1.vx -= ball1.vx;
//                ball1.vy -= ball1.vy;
//            }
//        }
//        для этого должен знать координаты центров и радиусы.
//
//        if sqrt(sqr(x1-x2)+sqr(y1-y2)) > R1 + R2
//        все нормально, никто ни с кем не сталкивался
//        if sqrt(sqr(x1-x2)+sqr(y1-y2)) = R1 + R2
//        столкнулись!!! но точное равентсво уловить трудно, так что лучше пользуй следующий вариант:
//        if sqrt(sqr(x1-x2)+sqr(y1-y2)) <= R1 + R2
//        либо столкнулись, либо пересеклись
        if (getLeft() < gameCanvas.getLeft()) {
            setLeft(gameCanvas.getLeft());
            vx = -vx;
        }
        if (getRight() > gameCanvas.getRight()) {
            setRight(gameCanvas.getRight());
            vx = -vx;
        }
        if (getBottom() > gameCanvas.getBottom()) {
            setBottom(gameCanvas.getBottom());
            vy = -vy;
        }
        if (getTop() < gameCanvas.getTop()) {
            setTop(gameCanvas.getTop());
            vy = -vy;
        }
    }

    @Override
    public void render(GameCanvas gameCanvas, Graphics g) {
        g.setColor(color);
        g.fillOval((int) getLeft(), (int) getTop(), (int) (getWidth()), (int) getHeight());
    }
}