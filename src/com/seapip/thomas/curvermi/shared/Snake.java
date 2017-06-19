package com.seapip.thomas.curvermi.shared;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Snake implements Serializable {
    private static final double SPEED = 0.05f; //Movement speed in px/ms
    private static final double RADIUS = 20; //Radius in px of a curve

    private Point start;
    private ArrayList<Curve> curves;
    private int hue;
    private long lost;

    public Snake(Point start) {
        this.start = start;
        this.start = new Point(200, 350);
        curves = new ArrayList<>();
        hue = ThreadLocalRandom.current().nextInt(0, 361);
    }

    public void start(Date date) {
        curves.add(new Curve(Direction.FORWARD, date));
    }

    public Curve turn(Direction direction) {
        return turn(direction, new Date());
    }

    public Curve turn(Direction direction, Date date) {
        Curve curve = new Curve(direction, date);
        this.curves.add(curve);
        return curve;
    }

    public void draw(GraphicsContext context) {
        double rotation = 0;
        Point position = start;
        context.setStroke(Color.hsb(hue, 1, 1));
        context.setLineWidth(4);
        context.beginPath();
        context.moveTo(position.getX(), position.getY());
        for (int i = 1; i < curves.size() + 1; i++) {
            Curve previous = curves.get(i - 1);
            Curve current = i == curves.size() ? new Curve(Direction.FORWARD, new Date()) : curves.get(i);
            long time = current.getDate().getTime() - previous.getDate().getTime();
            Direction direction = previous.getDirection();
            Point destination;
            context.moveTo(position.getX(), position.getY());
            if (direction == Direction.FORWARD) {
                destination = new Point(
                        position.getX() + Math.sin(rotation) * time * SPEED,
                        position.getY() - Math.cos(rotation) * time * SPEED);
                context.lineTo(destination.getX(), destination.getY());
            } else {
                double rotate = time * SPEED / RADIUS * (direction == Direction.LEFT ? -1 : 1);
                destination = new Point(
                        position.getX()
                                - Math.sin(rotation + Math.PI / 2 * (direction == Direction.RIGHT ? -1 : 1)) * RADIUS
                                + Math.sin(rotation + rotate + Math.PI / 2 * (direction == Direction.RIGHT ? -1 : 1))
                                * RADIUS,
                        position.getY()
                                + Math.cos(rotation + Math.PI / 2 * (direction == Direction.RIGHT ? -1 : 1)) * RADIUS
                                - Math.cos(rotation + rotate + Math.PI / 2 * (direction == Direction.RIGHT ? -1 : 1))
                                * RADIUS);
                context.arc(
                        position.getX()
                                - Math.sin(rotation + Math.PI / 2 * (direction == Direction.RIGHT ? -1 : 1)) * RADIUS,
                        position.getY()
                                + Math.cos(rotation + Math.PI / 2 * (direction == Direction.RIGHT ? -1 : 1)) * RADIUS,
                        RADIUS, RADIUS, -rotation / Math.PI * 180 + (direction == Direction.RIGHT ? 180 : 0),
                        -rotate / Math.PI * 180);
                rotation += rotate;
            }
            position = destination;
        }
        context.stroke();
    }

    /*
    private long moment(Line line, Point point, Direction direction) {
        double offset = 0;
        switch (direction) {
            case LEFT:
                offset = line.getStartX() - point.getX();
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
    }*/

    public long getLost() {
        return lost;
    }

    public void setLost(long lost) {
        this.lost = lost;
    }
}
