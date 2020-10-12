package ru.geekbrains.lines.game;

import ru.geekbrains.lines.common_classes.CanvasPaintListener;
import ru.geekbrains.lines.common_classes.GameCanvas;
import ru.geekbrains.lines.common_classes.GameObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class GameWindow extends JFrame implements Thread.UncaughtExceptionHandler, CanvasPaintListener {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameWindow();
            }
        });
    }

    private static final int WINDOW_WIDTH = 835;
    private static final int WINDOW_HEIGHT = 1007;

    private static final int NEW_BALLS_NUMBER = 3;
    private final ArrayList<GameObject> gameObjects = new ArrayList<>(NEW_BALLS_NUMBER);

    //проверить все for циклы
    //надо сделать чтобы шарик сначала отрисовывался в линию из 5 а потом только пропадал, а не пропадал не успев отрисоваться
    //подумать насчет размера поля, может универсальное написать
    //убрать эррейлист из передачи в метод последовательным вызовом методом из мэп или просто передать экземпляр this. для столкновений передать еще куда нибудь
    //возможен вариант когда на поле не останется ни одного шарика
    //ударения и деформации в канве
    //прыгающий шарик придется убрать наверное прямоугольник
    //возможно придется запускать отрисовку в 2 разных потоках поскольку томозим потоки и там и там
    //добавить наверху панель, поля с количеством шаров, очками. Добавить таблицу рекордов
    //поставить чтобы при выходе запрашивал хотите выйти да... нет
    //сли унас шарики пропадают надо чтобы новые не рисовались
    private GameWindow() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Lines");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setAlwaysOnTop(true);

        GameCanvas gameCanvas = new GameCanvas(this);
        gameCanvas.setBackground(Color.BLACK);
        gameCanvas.setPreferredSize(new Dimension(0, 150));

        initBalls();
        Map map = new Map(this);//отдельный поток скорее всего
        add(gameCanvas, BorderLayout.NORTH);
        add(map, BorderLayout.CENTER);
        setVisible(true);
    }

    private void initBalls() {
        for (int i = 0; i < NEW_BALLS_NUMBER; i++)
            gameObjects.add(new Ball((int) (Math.random() * (getWidth() - 18)), 0));//рандом посимпатичнее либо вообще убрать и сделать нулевой конструкор в болл если будут столкновения рандом не нужен
    }

    @Override
    public void onDrawFrame(GameCanvas gameCanvas, float deltaTime, Graphics g) {
        update(gameCanvas, deltaTime);
        render(gameCanvas, g);
    }

    private void update(GameCanvas gameCanvas, float deltaTime) {
        for (int i = 0; i < gameObjects.size(); i++) gameObjects.get(i).update(gameCanvas, deltaTime);
    }

    private void render(GameCanvas gameCanvas, Graphics g) {
        for (int i = 0; i < gameObjects.size(); i++) gameObjects.get(i).render(gameCanvas, g);
    }

    int getSizeGameObjects() {
        return gameObjects.size();
    }

    Color getBallColor(int index) {
        return ((Ball) gameObjects.get(index)).getColor();
    }

    void setBallColor(int index) {
        ((Ball)gameObjects.get(index)).setColor();
    }

//    private void bounceBalls(){
//        for (int i = 0; i <balls.size() ; i++) {
//            for (int j = 0; j <balls.size() ; j++) {
//
//            }
//        }
//    }

//    Чтобы обнаружить столкновение каждого шара друг с другом, простой способ будет проходить через набор шаров с парой вложенных петель. Таким образом, каждый шар будет проверен на все остальные шары для столкновения:
//    //From your list of balls
//    ArrayList<BouncingBall> bList = new ArrayList<BouncingBall>();
//
//for(BouncingBall b1 : bList)
//            for(BouncingBall b2 : bList)
//            if(b1.intersects(b2)){
//        //do whatever (such as bouncing off) when the balls collide
//        b1.flipDirection();
//        b2.flipDirection();
//    }

//    Чтобы использовать метод intersects(), ваш класс BouncingBall может распространяться на класс Rectangle с Java. Кроме того, если вы не можете позволить классу BouncingBall быть расширенным до другого класса.
//
//    Вы можете позволить им вернуть границы:
//class BouncingBall{
//    public Rectangle getBounds(){
//        return new Rectangle(x, y, width ,height);
//    }
//}
//Тогда вы все равно сможете использовать метод intersects():
//
////to check for collision using intersects() method
//            for(BouncingBall b1 : bList)
//            for(BouncingBall b2 : bList)
//            if(b1.getBounds().intersects(b2.getBounds())){
//        //do whatever (such as bouncing off) when the balls collide
//        b1.flipDirection();
//        b2.flipDirection();
//    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        String msg;
        if (stackTraceElements.length == 0) {
            msg = "Пустой StackTrace";
        } else {
            msg = e.getClass().getCanonicalName() + ": " + e.getMessage() + "\n" + stackTraceElements[0];
        }
        JOptionPane.showMessageDialog(this, msg, "Exception: ", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}