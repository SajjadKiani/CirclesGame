
// with java 1.8
// SajadKianiMoghadam

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {

    static Group group = new Group();

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("Game");

        Button addBtn = new Button("Add");
        addBtn.setLayoutX(320);
        addBtn.setLayoutY(650);

        Button startBtn = new Button("Start");
        startBtn.setLayoutX(260);
        startBtn.setLayoutY(650);

        Button stopBtn = new Button("Stop");
        stopBtn.setLayoutX(380);
        stopBtn.setLayoutY(650);

        Line line = new Line(0,600,800,600);

        group.getChildren().addAll(addBtn,startBtn,stopBtn,line);

        Pane pane = new Pane(group);
        pane.setStyle("-fx-background-color: #fff5b7");

        Random rnd = new Random();

        // Actions
        addBtn.setOnAction(e -> new CircleThread(rnd.nextInt(40)+10,0,0,rnd.nextInt(1000)+500,10, Color.RED) );
        startBtn.setOnAction(e -> startThreads());
        stopBtn.setOnAction(e -> stopThreads());

        Scene scene = new Scene(pane, 800 , 700);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void startThreads () {
        for (int i = 0; i < CircleThread.circleThreads.size(); i++) {

            if ( ! CircleThread.circleThreads.get(i).isStart ) {
                CircleThread.circleThreads.get(i).start();
            }

        }
    }

    public void stopThreads () {
        for (int i = 0; i < CircleThread.circleThreads.size(); i++) {
            CircleThread.circleThreads.get(i).stopThread();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}

class CircleThread extends Thread {

    static ArrayList<CircleThread> circleThreads = new ArrayList<>();

    MyCircle c;
    TranslateTransition transition;
    boolean isStart = false;

    public CircleThread (int r, int x, int y, int speed, int angle, Color color) {
        // new Random color
        Random rnd = new Random();
        int R = rnd.nextInt(256);
        int G = rnd.nextInt(256);
        int B = rnd.nextInt(256);
        color = Color.rgb(R,G,B);

        // create circle
        c = new MyCircle( r, x, y,speed, angle,color);

        // show Circle in window
        Main.group.getChildren().add(c);

        // add to CircleThreads ArrayList
        circleThreads.add(this);
    }

    @Override
    public void start () {
        isStart = true;
        moveCircle();
    }

    public void stopThread () {
        isStart = false;
        transition.stop();
        stop();
    }

    public void moveCircle () {
        Duration duration = Duration.millis(c.speed);
        transition = new TranslateTransition(duration, c);

        // find new location
        Point p = findLocation();

        final double[] x = {p.x};
        final double[] y = {p.y};

        // set Destination coordinates
        transition.setToX(x[0]);
        transition.setToY(y[0]);

        transition.play();

        // when the transition has end
        transition.setOnFinished(event -> {
            // using again this function
            moveCircle();
        });

    }

    // Find a random location on the window regardless of angle
    public Point findLocation () {
         Point goodP = new Point();
         Random rnd = new Random();

         int r = (int) c.getRadius();

         // Circle coordinates
         int x = (int) c.getLocalToSceneTransform().getTx();
         int y = (int) c.getLocalToSceneTransform().getTy();

        int side;

         do {
             side = rnd.nextInt(4);
         } while ( (side == 0 && x == r) || (side == 1 && y == 600-r) || (side == 2 && x == 800-r) || (side == 3 && y == r));

         switch (side) {
             case 0:
                 goodP.x = r;
                 goodP.y = rnd.nextInt(600-2*r);
                 break;
             case 1:
                 goodP.x = rnd.nextInt(800-2*r);
                 goodP.y = 600-r;
                 break;
             case 2:
                 goodP.x = 800-r;
                 goodP.y = rnd.nextInt(600-2*r);
                 break;
             case 3:
                 goodP.x = rnd.nextInt(800-2*r);
                 goodP.y = r;
                 break;
         }

        return goodP;
    }
}

class Point {
    double x;
    double y;
}

class MyCircle extends Circle {
    int speed;
    int angle;

    public MyCircle(int r, int x, int y, int speed, int angle, Color color) {
        super(x, y, r, color);
        this.angle = angle;
        this.speed = speed;
    }
}