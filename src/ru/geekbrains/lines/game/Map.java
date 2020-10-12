package ru.geekbrains.lines.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

class Map extends JPanel {

    private static final int FIELD_SIZE_X = 9;
    private static final int FIELD_SIZE_Y = 9;
    private static final int WIN_LEN = 5;
    private static final int DOTS_MARGIN = 7;
    //прочекать лишние вызовы методов
    //добавить окно начала игры

    private int count;
    private boolean isMoved;
    private boolean isLineDisappear;
    private int cellWidth;
    private int cellHeight;
    private int cellRectX;
    private int cellRectY;
    private Color colorPath;
    private final Color[][] field = new Color[FIELD_SIZE_Y][FIELD_SIZE_X];
    private final int[][] matrixDisappear = new int[FIELD_SIZE_Y][FIELD_SIZE_X];
    private final int[][] matrixPath = new int[FIELD_SIZE_Y][FIELD_SIZE_X];
    private final GameWindow gameWindow;
    private final ArrayList<Point> points = new ArrayList<>();

    private final BasicStroke strokeMap = new BasicStroke(2f);
//    private final BasicStroke strokeBalls = new BasicStroke(3f);

    Map(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        aiTurn();
        setBackground(Color.LIGHT_GRAY);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                update(e);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    private void update(MouseEvent e) {
        int cellX = e.getX() / cellWidth;
        int cellY = e.getY() / cellHeight;
        if (!isValidCell(cellX, cellY)) return;
        count++;
        if (count % 2 == 0) {
            if (isEmptyCell(cellX, cellY)) {
                if (!isPathFree(cellRectX, cellRectY, cellX, cellY)) {
                    count--;
                    //какое-нибудь сообщение прозрачное что не пройти+repaint()
                    return;
                }
                getBackPath(cellX, cellY);
                colorPath = field[cellRectY][cellRectX];
                repaint();//
                field[cellY][cellX] = field[cellRectY][cellRectX];//возможно это надо убрать в отрисовку тобы не было разночтений
                field[cellRectY][cellRectX] = null;//возможно это надо убрать в отрисовку
//                repaint();
            } else {
                cellRectX = cellX;
                cellRectY = cellY;
                count--;
                repaint();
                return;
            }
        } else if (isEmptyCell(cellX, cellY)) {
            count--;
            return;
        } else {
            cellRectX = cellX;
            cellRectY = cellY;
            repaint();
            return;
        }
        checkLineAndDisappear();
        repaint();
        if (isLineDisappear) {
            isLineDisappear = false;
            return;
        }
        aiTurn();
        checkLineAndDisappear();
        if (isMapFull()) {
//            repaint();//добавить кейсы и гейм овер стейты
        }
    }

    private void getBackPath(int x, int y) {
        points.add(new Point(x, y));
        for (int value = matrixPath[y][x] - 1; value > 0; value--) {
            Point lastPoint = points.get(points.size() - 1);
            x = lastPoint.x;
            y = lastPoint.y;
            if (checkNearCell(x, y, 0, -1, value)) continue;
            if (checkNearCell(x, y, 0, 1, value)) continue;
            if (checkNearCell(x, y, -1, 0, value)) continue;
            checkNearCell(x, y, 1, 0, value);
        }
        Collections.reverse(points);
    }

    private boolean checkNearCell(int x, int y, int vx, int vy, int value) {
        final int farX = x + vx;
        final int farY = y + vy;
        if (!isValidCell(farX, farY)) return false;
        if (matrixPath[farY][farX] == value) {
            points.add(new Point(farX, farY));
            return true;
        }
        return false;
    }

    private boolean isPathFree(int x1, int y1, int x2, int y2) {
        clearMatrixPath();
        for (int i = 0; i < FIELD_SIZE_Y; i++) {
            for (int j = 0; j < FIELD_SIZE_X; j++) {
                if (!isEmptyCell(j, i)) matrixPath[i][j] = -1;
            }
        }
        int beginValue = 1;
        matrixPath[y1][x1] = beginValue;
        while (matrixPath[y2][x2] == 0) {
            isMoved = false;
            for (int i = 0; i < FIELD_SIZE_Y; i++) {
                for (int j = 0; j < FIELD_SIZE_X; j++) {
                    if (matrixPath[i][j] == beginValue) {
                        checkDirection(j, i, 0, -1, beginValue);
                        checkDirection(j, i, 0, 1, beginValue);
                        checkDirection(j, i, -1, 0, beginValue);
                        checkDirection(j, i, 1, 0, beginValue);
                    }
                }
            }
            if (!isMoved) return false;
            beginValue++;
        }
        return true;
    }

    private void checkDirection(int x, int y, int vx, int vy, int value) {
        final int farX = x + vx;
        final int farY = y + vy;
        if (!isValidCell(farX, farY)) return;
        if (matrixPath[farY][farX] == 0) {
            matrixPath[farY][farX] = value + 1;
            isMoved = true;
        }
    }

    private void clearMatrixPath() {
        for (int i = 0; i < matrixPath.length; i++) {
            for (int j = 0; j < matrixPath[i].length; j++) {
                matrixPath[i][j] = 0;
            }
        }
    }

    private void checkLineAndDisappear() {
        Color color;
        for (int i = 0; i < FIELD_SIZE_X; i++) {
            for (int j = 0; j < FIELD_SIZE_Y; j++) {
                if (isEmptyCell(i, j)) continue;
                color = field[j][i];
                for (int k = FIELD_SIZE_X; k >= WIN_LEN; k--) {
                    checkLine(i, j, 1, -1, k, color);
                    checkLine(i, j, 1, 0, k, color);
                    checkLine(i, j, 1, 1, k, color);
                    checkLine(i, j, 0, 1, k, color);
                }
            }
        }
        if (isLineDisappear) {
            for (int i = 0; i < FIELD_SIZE_Y; i++) {
                for (int j = 0; j < FIELD_SIZE_X; j++) {
                    if (matrixDisappear[i][j] == 1) {
                        field[i][j] = null;
                        matrixDisappear[i][j] = 0;
                    }
                }
            }
        }
    }

    private void checkLine(int x, int y, int vx, int vy, int len, Color color) {
        final int farX = x + (len - 1) * vx;
        final int farY = y + (len - 1) * vy;
        if (!isValidCell(farX, farY)) return;
        for (int i = 0; i < len; i++) {
            if (field[y + i * vy][x + i * vx] != color) return;
        }
        isLineDisappear = true;
        for (int i = 0; i < len; i++) {
            matrixDisappear[y + i * vy][x + i * vx] = 1;
        }
    }

    private void aiTurn() {
        int x, y;
        for (int i = 0; i < gameWindow.getSizeGameObjects(); i++) {
            do {
                x = (int) (Math.random() * FIELD_SIZE_X);
                y = (int) (Math.random() * FIELD_SIZE_Y);
            } while (!isEmptyCell(x, y));//может измэпфулл сюда как-то закинуть
            field[y][x] = gameWindow.getBallColor(i);
            gameWindow.setBallColor(i);
            if (isMapFull())
                return;//здесь может возникнуть ситуация когда новый шарик отрисовался но поле исчезло либо сразу гейм овер либо еще что то подумать
        }
    }

    private boolean isMapFull() {
        for (int i = 0; i < FIELD_SIZE_Y; i++) {
            for (int j = 0; j < FIELD_SIZE_X; j++) {
                if (field[i][j] == null) return false;
            }
        }
        return true;
    }

    //попробовать Emptycell valid cell сделать универсаьным для 3 методов и матриц
    private boolean isEmptyCell(int x, int y) {
        if (isValidCell(x, y)) return field[y][x] == null;
        return false;
    }

    private boolean isValidCell(int x, int y) {
        return x >= 0 && y >= 0 && x < FIELD_SIZE_X && y < FIELD_SIZE_Y;
    }

    private void render(Graphics g) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        cellWidth = panelWidth / FIELD_SIZE_X;
        cellHeight = panelHeight / FIELD_SIZE_Y;
        g.setColor(Color.WHITE);
        ((Graphics2D) g).setStroke(strokeMap);
        for (int i = 0; i <= FIELD_SIZE_X; i++) {
            int x = i * cellWidth;
            g.drawLine(x, 0, x, panelHeight);
        }
        for (int i = 0; i <= FIELD_SIZE_Y; i++) {
            int y = i * cellHeight;
            g.drawLine(0, y, panelWidth, y);
        }
        if (count % 2 != 0) {
            g.setColor(Color.RED);
            g.drawRect(cellRectX * cellWidth, cellRectY * cellHeight, cellWidth, cellHeight);
        }
        for (int i = 0; i < FIELD_SIZE_Y; i++) {
            for (int j = 0; j < FIELD_SIZE_X; j++) {
                if (isEmptyCell(j, i)) continue;
                g.setColor(field[i][j]);
                g.fillOval(j * cellWidth + DOTS_MARGIN, i * cellHeight + DOTS_MARGIN, cellWidth - 2 * DOTS_MARGIN, cellHeight - 2 * DOTS_MARGIN);
//                g.setStroke(strokeBalls);
//                g.setColor(Color.BLACK);
//                g.drawOval(j * cellWidth + DOTS_MARGIN, i * cellHeight + DOTS_MARGIN, cellWidth - 2 * DOTS_MARGIN, cellHeight - 2 * DOTS_MARGIN);
            }
        }
        if (points.size() != 0) {
            g.setColor(colorPath);
            for (int i = 0; i < points.size(); i++) {
                Point point = points.get(i);
                g.fillOval(point.x * cellWidth + DOTS_MARGIN, point.y * cellHeight + DOTS_MARGIN, cellWidth - 2 * DOTS_MARGIN, cellHeight - 2 * DOTS_MARGIN);
            }
            points.remove(0);
            repaint();
        }
    }
}