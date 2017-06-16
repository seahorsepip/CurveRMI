package com.seapip.thomas.curve_rmi;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Date;

public class Snake {
    private final float SPEED = 0.05f; //Movement speed in px/ms

    private Point start;
    private Direction direction;
    private ArrayList<Curve> curves;

    public Snake(Point start) {
        this.start = start;
        this.direction = Direction.UP;
        this.curves = new ArrayList<>();
    }

    public Point getStart() {
        return start;
    }

    public ArrayList<Curve> getCurves() {
        return curves;
    }

    public void turn(Direction direction) {
        Direction current = (curves.size() > 0 ? curves.get(curves.size() - 1).getDirection() : this.direction);
        if (direction != current) {
            boolean valid;
            switch (direction) {
                default:
                case UP:
                    valid = current != Direction.DOWN;
                    break;
                case LEFT:
                    valid = current != Direction.RIGHT;
                    break;
                case DOWN:
                    valid = current != Direction.UP;
                    break;
                case RIGHT:
                    valid = current != Direction.LEFT;
                    break;
            }
            if (valid) {
                System.out.println("Direction changed!");
                this.curves.add(new Curve(direction, new Date()));
            }
        }
    }

    private Point movement(Point start, long time, Direction direction) {
        int x = start.getX();
        int y = start.getY();
        float distance = time * SPEED;
        switch (direction) {
            case UP:
                y -= distance;
                break;
            case LEFT:
                x -= distance;
                break;
            case DOWN:
                y += distance;
                break;
            case RIGHT:
                x += distance;
                break;
        }
        return new Point(x, y);
    }

    public void draw(GraphicsContext context, Date begin) {
        Point position = start;
        context.setStroke(Color.CYAN);
        context.setLineWidth(4);
        for (int i = 0; i < curves.size(); i++) {
            long time = curves.get(i).getDate().getTime() - (i == 0 ? begin : curves.get(i - 1).getDate()).getTime();
            Direction direction = i == 0 ? this.direction : curves.get(i - 1).getDirection();
            Point destination = movement(position, time, direction);
            context.strokeLine(position.getX(), position.getY(), destination.getX(), destination.getY());
            position = destination;
        }
        long time = new Date().getTime() - (curves.size() > 0 ? curves.get(curves.size() - 1).getDate() : begin).getTime();
        Direction direction = curves.size() > 0 ? curves.get(curves.size() - 1).getDirection() : this.direction;
        Point destination = movement(position, time, direction);
        context.strokeLine(position.getX(), position.getY(), destination.getX(), destination.getY());
    }
}
