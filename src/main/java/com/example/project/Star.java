package com.example.project;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Star {
    private final double centerX;
    private final double centerY;
    private double outerRadius;
    private double innerRadius;
    private final double radiusRatio;
    private final List<Line> edges;
    private final List<Color> starColors;
    private static final List<Color> COLORS = new ArrayList<>();
    private static final double GROWTH_RATE = 1.0;

    static {
        // High contrast colors
        COLORS.add(Color.RED); // Pure Red
        COLORS.add(Color.GREEN); // Pure Green
        COLORS.add(Color.BLUE); // Pure Blue
        COLORS.add(Color.YELLOW); // Yellow
        COLORS.add(Color.MAGENTA); // Magenta
        COLORS.add(Color.CYAN); // Cyan
        COLORS.add(Color.ORANGE); // Orange
        COLORS.add(Color.PURPLE); // Purple
        COLORS.add(Color.GRAY); // Gray
        COLORS.add(Color.BLACK); // Black
    }

    // Constructor
    public Star(double centerX, double centerY, double outerRadius, double innerRadius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.radiusRatio = innerRadius / outerRadius;
        this.edges = new ArrayList<>();
        this.starColors = new ArrayList<>(COLORS);
        Collections.shuffle(this.starColors); // Randomize the colors (changing the order of colors)
        createStarEdges();
    }

    // This method creates the edges of the star (the shape)
    private void createStarEdges() {
        edges.clear();
        int numEdges = 10;

        for (int i = 1; i <= numEdges; i++) {
            double angle1 = Math.toRadians(-90) + (i - 1) * (2 * Math.PI / numEdges);
            double angle2 = Math.toRadians(-90) + i * (2 * Math.PI / numEdges);

            double radius1 = (i % 2 == 0) ? innerRadius : outerRadius;
            double radius2 = ((i + 1) % 2 == 0) ? innerRadius : outerRadius;

            double startX = centerX + radius1 * Math.cos(angle1);
            double startY = centerY + radius1 * Math.sin(angle1);
            double endX = centerX + radius2 * Math.cos(angle2);
            double endY = centerY + radius2 * Math.sin(angle2);

            Line line = new Line(startX, startY, endX, endY);
            line.setStroke(starColors.get(i - 1));
            line.setStrokeWidth(2);
            edges.add(line);
        }
    }

    public int checkCollisionWithBall(Bounds ballBounds, Color ballColor) {
        for (int i = 0; i < edges.size(); i++) {
            Line edge = edges.get(i);
            Bounds edgeBounds = edge.getBoundsInParent();
            if (ballBounds.intersects(edgeBounds)) {
                if (edge.getStroke().equals(ballColor)) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
        return -1;
    }

    // get Random color for Ball after collision
    public static Color getRandomColor() {
        return COLORS.get((int) (Math.random() * COLORS.size()));
    }

    // Control the Growth Rate
    public void grow() {
        outerRadius += GROWTH_RATE;
        innerRadius = outerRadius * radiusRatio;
        createStarEdges();
    }

    // if Star touches the border
    public boolean touchesBorder(double canvasWidth, double canvasHeight) {
        for (Line line : edges) {
            if (line.getStartX() <= 0 || line.getStartX() >= canvasWidth ||
                    line.getStartY() <= 0 || line.getStartY() >= canvasHeight ||
                    line.getEndX() <= 0 || line.getEndX() >= canvasWidth ||
                    line.getEndY() <= 0 || line.getEndY() >= canvasHeight) {
                return true;
            }
        }
        return false;
    }

    public void draw(GraphicsContext gc) {
        for (Line line : edges) {
            gc.setStroke(line.getStroke());
            gc.setLineWidth(line.getStrokeWidth());
            gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        }
    }

    public List<Line> getEdges() {
        return edges;
    }
}
