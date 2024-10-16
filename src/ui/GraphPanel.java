package ui;

import model.Graph;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class GraphPanel extends JPanel {
    private Graph graph;
    private Map<String, Integer> edgeCosts; // Map to hold the cost of each edge
    private Map<Integer, Integer> heuristics; // Map to hold the heuristic values for nodes

    public GraphPanel(Graph graph, Map<String, Integer> edgeCosts, Map<Integer, Integer> heuristics) {
        this.graph = graph;
        this.edgeCosts = edgeCosts;
        this.heuristics = heuristics; // Initialize heuristics
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw edges and costs
        for (Map.Entry<Integer, List<Integer>> entry : graph.getAdjacencyList().entrySet()) {
            int src = entry.getKey();
            for (int dest : entry.getValue()) {
                Point srcPoint = getNodePosition(src);
                Point destPoint = getNodePosition(dest);
                g.drawLine(srcPoint.x, srcPoint.y, destPoint.x, destPoint.y);

                // Draw the cost at the midpoint between src and dest
                String edgeKey = src + "," + dest;
                if (edgeCosts.containsKey(edgeKey)) {
                    int cost = edgeCosts.get(edgeKey);
                    Point midPoint = getMidpoint(srcPoint, destPoint);
                    g.setColor(Color.RED);
                    g.drawString(String.valueOf(cost), midPoint.x, midPoint.y);
                }
            }
        }

        // Draw nodes and heuristic values
        for (Integer node : graph.getAdjacencyList().keySet()) {
            Point point = getNodePosition(node);

            // Draw the node (as a larger filled circle)
            g.setColor(Color.BLUE);
            int nodeRadius = 50;  // Increase the radius of the node circle
            g.fillOval(point.x - nodeRadius / 2, point.y - nodeRadius / 2, nodeRadius, nodeRadius);

            // Label the node (node number)
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(node), point.x - 5, point.y + 5);

            // Draw the heuristic value inside a smaller circle within the node
            if (heuristics.containsKey(node)) {
                int heuristicValue = heuristics.get(node);
                g.setColor(Color.YELLOW);

                // Position the small circle inside the node
                int smallCircleRadius = 20;
                int smallCircleX = point.x - smallCircleRadius / 2;
                int smallCircleY = point.y - smallCircleRadius / 2;

                g.fillOval(smallCircleX, smallCircleY, smallCircleRadius, smallCircleRadius); // Small circle inside the node
                g.setColor(Color.BLACK);
                g.drawString(String.valueOf(heuristicValue), smallCircleX + 5, smallCircleY + 15); // Heuristic value inside the small circle
            }
        }

    }

    // Calculate position for each node
    private Point getNodePosition(int node) {
        // This is a simple example of placing nodes in a circular layout.
        int radius = 100;
        int x = (int) (getWidth() / 2 + radius * Math.cos(Math.toRadians(node * 60)));
        int y = (int) (getHeight() / 2 + radius * Math.sin(Math.toRadians(node * 60)));
        return new Point(x, y);
    }

    // Calculate the midpoint between two points
    private Point getMidpoint(Point p1, Point p2) {
        int midX = (p1.x + p2.x) / 2;
        int midY = (p1.y + p2.y) / 2;
        return new Point(midX, midY);
    }
}
