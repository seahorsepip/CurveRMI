package com.seapip.thomas.curvermi.shared;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class Snake implements Serializable {
    private static final double SPEED = 0.01f; //Movement speed in px/ms

    private Point start;
    private ArrayList<Curve> curves;
    private int hue;
    private long lost;

    public Snake(Point start) {
        this.start = start;
        curves = new ArrayList<>();
        hue = ThreadLocalRandom.current().nextInt(0, 361);
    }

    public void start(Date date) {
        curves.add(new Curve(Direction.UP, date));
    }

    public Curve turn(Direction direction) {
        Direction current = curves.get(curves.size() - 1).getDirection();
        if (direction != current && lost == 0) {
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
        double x = start.getX();
        double y = start.getY();
        double distance = time * SPEED;
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
        long time = (lost == 0 ? new Date().getTime() : lost) - curves.get(curves.size() - 1).getDate().getTime();
        Direction direction = curves.get(curves.size() - 1).getDirection();
        Point destination = movement(position, time, direction);
        context.strokeLine(position.getX(), position.getY(), destination.getX(), destination.getY());
    }

    private long moment(Line line, Point point, Direction direction) {
        double offset = 0;
        switch (direction) {
            case UP:
                offset = line.getStartY() - point.getY();
                break;
            case LEFT:
                offset = line.getStartX() - point.getX();
                break;
            case DOWN:
                offset = point.getY() - line.getStartY();
                break;
            case RIGHT:
                offset = point.getX() - line.getStartX();
                break;
        }
        return (long) (offset * SPEED);
    }

    private Point intersects(Line a, Line b) {
        BigDecimal denominator = BigDecimal.valueOf((a.getStartX() - a.getEndX()) * (b.getStartY() - b.getEndY())
                - (a.getStartY() - a.getEndY()) * (b.getStartX() - b.getEndX()));
        if (denominator.compareTo(BigDecimal.ZERO) != 0) {
            double x = ((a.getStartX() * a.getEndY() - a.getStartY() * a.getEndX())
                    * (b.getStartX() - b.getEndX()) - (a.getStartX() - a.getEndX())
                    * (b.getStartX() * b.getEndY() - b.getStartY() * b.getEndX()))
                    / denominator.doubleValue();
            double y = ((a.getStartX() * a.getEndY() - a.getStartY() * a.getEndX())
                    * (b.getStartY() - b.getEndY()) - (a.getStartY() - a.getEndY())
                    * (b.getStartX() * b.getEndY() - b.getStartY() * b.getEndX()))
                    / denominator.doubleValue();
            return new Point(x, y);
        }
        return null;
    }

    public Point intersectsInFuture(Line line) {
        Point intersection = null;
        Point position = start;
        for (int i = 1; i < curves.size(); i++) {
            long time = curves.get(i).getDate().getTime() - curves.get(i - 1).getDate().getTime();
            Direction direction = curves.get(i - 1).getDirection();
            Point destination = movement(position, time, direction);
            Point point = intersects(line, new Line(position.getX(), position.getY(), destination.getX(), destination.getY()));
            if (point != null) {
                return point;
                //intersection = point;
            }
            position = destination;
        }
        Direction direction = curves.get(curves.size() - 1).getDirection();
        double x = position.getX();
        double y = position.getY();
        switch (direction) {
            case UP:
                y = 0;
                break;
            case LEFT:
                x = 0;
                break;
            case DOWN:
                y = 400;
                break;
            case RIGHT:
                x = 400;
                break;
        }
        Point point = intersects(line, new Line(position.getX(), position.getY(), x, y));
        if (point != null) {
            intersection = point;
        }
        return intersection;
    }

    public long intersectsInFuture(Snake snake) {
        long time = -1;
        Point position = start;
        if (curves.size() > 1) {
            long duration = curves.get(curves.size() - 1).getDate().getTime() - curves.get(curves.size() - 2).getDate().getTime();
            Direction direction = curves.get(curves.size() - 2).getDirection();
            position = movement(position, duration, direction);
        }
        Direction direction = curves.get(curves.size() - 1).getDirection();
        double x = position.getX();
        double y = position.getY();
        switch (direction) {
            case UP:
                y = 0;
                break;
            case LEFT:
                x = 0;
                break;
            case DOWN:
                y = 400;
                break;
            case RIGHT:
                x = 400;
                break;
        }
        Line line = new Line(position.getX(), position.getY(), x, y);
        Point intersection = snake.intersectsInFuture(line);
        if (intersection != null) {
            time = moment(line, intersection, direction) + curves.get(curves.size() - 1).getDate().getTime();
        }
        return time;
    }

    public long getLost() {
        return lost;
    }

    public void setLost(long lost) {
        this.lost = lost;
    }
}
