package com.seapip.thomas.curvermi.shared;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class Snake implements Serializable {
    private static final float SPEED = 0.01f; //Movement speed in px/ms

    private String sessionId;
    private Point start;
    private ArrayList<Curve> curves;
    private int hue;

    public Snake(String sessionId, Point start) {
        this.sessionId = sessionId;
        this.start = start;
        curves = new ArrayList<>();
        curves.add(new Curve(Direction.UP, new Date()));
        hue = ThreadLocalRandom.current().nextInt(0, 361);
    }

    public boolean ofSession(String id) {
        return sessionId.equals(id);
    }

    public Curve turn(Direction direction) {
        Direction current = curves.get(curves.size() - 1).getDirection();
        if (direction != current) {
            boolean valid;
            switch (direction) {
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
                default:
                    return null;
            }
            if (valid) {
                Curve curve = new Curve(direction, new Date());
                this.curves.add(curve);
                return curve;
            }
        }
        return null;
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
            default:
                //Do nothing
                break;
        }
        return new Point(x, y);
    }

    public void draw(GraphicsContext context) {
        Point position = start;
        context.setStroke(Color.hsb(hue, 1, 1));
        context.setLineWidth(4);
        for (int i = 1; i < curves.size(); i++) {
            long time = curves.get(i).getDate().getTime() - curves.get(i - 1).getDate().getTime();
            Direction direction = curves.get(i - 1).getDirection();
            Point destination = movement(position, time, direction);
            context.strokeLine(position.getX(), position.getY(), destination.getX(), destination.getY());
            position = destination;
        }
        long time = new Date().getTime() - curves.get(curves.size() - 1).getDate().getTime();
        Direction direction = curves.get(curves.size() - 1).getDirection();
        Point destination = movement(position, time, direction);
        context.strokeLine(position.getX(), position.getY(), destination.getX(), destination.getY());
    }
}
