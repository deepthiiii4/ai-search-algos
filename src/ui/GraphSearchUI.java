package ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Graph;
import algorithms.SearchAlgorithms;

public class GraphSearchUI {
    private Graph graph;
    private JTextArea resultArea;
    private SearchAlgorithms searchAlgorithms;
    private GraphPanel graphPanel; // Panel for drawing the graph
    private Map<String, Integer> edgeWeights; // Store edge weights
    private Map<Integer, Integer> heuristics; // Store heuristic costs
    private JTextField oracleInput; // Oracle value input field

    public GraphSearchUI() {
        graph = new Graph();
        searchAlgorithms = new SearchAlgorithms();
        edgeWeights = new HashMap<>();
        heuristics = new HashMap<>();
        initComponents();
    }

    private void initComponents() {
        JFrame frame = new JFrame("Graph Search Algorithms");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Increase frame size to accommodate graph visualization

        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        // Graph visualization panel
        graphPanel = new GraphPanel(graph, edgeWeights, heuristics); // Pass edgeWeights and heuristics for drawing
        graphPanel.setBounds(350, 10, 400, 500); // Positioned on the right side
        panel.add(graphPanel);

        // Add the input components on the left
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        JLabel instructions = new JLabel("Enter edges (src, dest, weight): ");
        instructions.setBounds(10, 10, 300, 25);
        panel.add(instructions);

        JTextField edgeInput = new JTextField(20);
        edgeInput.setBounds(10, 40, 160, 25);
        panel.add(edgeInput);

        JButton addEdgeButton = new JButton("Add Edge");
        addEdgeButton.setBounds(180, 40, 100, 25);
        panel.add(addEdgeButton);

        JLabel algorithmLabel = new JLabel("Select Algorithm: ");
        algorithmLabel.setBounds(10, 80, 200, 25);
        panel.add(algorithmLabel);

        String[] algorithms = {
                "BFS", "DFS", "British Museum", "Hill Climbing",
                "Beam search", "Oracle", "Branch and bound",
                "Branch and bound with dead horse/ext list",
                "Branch and bound with heuristics", "A* algorithm"
        };
        JComboBox<String> algorithmList = new JComboBox<>(algorithms);
        algorithmList.setBounds(10, 110, 160, 25);
        panel.add(algorithmList);

        JLabel startLabel = new JLabel("Start Node: ");
        startLabel.setBounds(10, 150, 80, 25);
        panel.add(startLabel);

        JTextField startInput = new JTextField(20);
        startInput.setBounds(100, 150, 50, 25);
        panel.add(startInput);

        JLabel goalLabel = new JLabel("Goal Node: ");
        goalLabel.setBounds(10, 180, 80, 25);
        panel.add(goalLabel);

        JTextField goalInput = new JTextField(20);
        goalInput.setBounds(100, 180, 50, 25);
        panel.add(goalInput);

        JLabel heuristicLabel = new JLabel("Heuristic Costs (node, heuristic): ");
        heuristicLabel.setBounds(10, 210, 250, 25);
        panel.add(heuristicLabel);

        JTextField heuristicInput = new JTextField(20);
        heuristicInput.setBounds(10, 240, 160, 25);
        panel.add(heuristicInput);

        JButton addHeuristicButton = new JButton("Add Heuristic");
        addHeuristicButton.setBounds(180, 240, 120, 25);
        panel.add(addHeuristicButton);

        // Oracle input field (only used for certain algorithms)
        JLabel oracleLabel = new JLabel("Oracle Value: ");
        oracleLabel.setBounds(10, 270, 100, 25);
        panel.add(oracleLabel);

        oracleInput = new JTextField(20);
        oracleInput.setBounds(120, 270, 100, 25);
        panel.add(oracleInput);

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(10, 320, 100, 25);
        panel.add(searchButton);

        resultArea = new JTextArea();
        resultArea.setBounds(10, 360, 260, 80);
        panel.add(resultArea);

        // Action listeners
        addEdgeButton.addActionListener(e -> {
            String[] edgeData = edgeInput.getText().split(",");
            if (edgeData.length == 3) {
                try {
                    int src = Integer.parseInt(edgeData[0].trim());
                    int dest = Integer.parseInt(edgeData[1].trim());
                    int weight = Integer.parseInt(edgeData[2].trim());
                    graph.addEdge(src, dest);
                    edgeWeights.put(src + "," + dest, weight); // Store the edge weight
                    edgeInput.setText("");
                    graphPanel.repaint(); // Repaint graph panel after adding an edge
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Invalid edge input: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please enter valid edge format: src, dest, weight");
            }
        });

        addHeuristicButton.addActionListener(e -> {
            String[] nodeData = heuristicInput.getText().split(",");
            if (nodeData.length == 2) {
                try {
                    int node = Integer.parseInt(nodeData[0].trim());
                    int heuristicCost = Integer.parseInt(nodeData[1].trim());
                    heuristics.put(node, heuristicCost); // Store heuristic cost
                    heuristicInput.setText("");

                    // Check if all heuristics are added



                        JOptionPane.showMessageDialog(panel, "Heuristic added! Enter the next one.");
                    JOptionPane.showMessageDialog(panel, "All heuristic values entered: " + heuristics.toString());

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Invalid heuristic input: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please enter valid heuristic format: node, heuristic");
            }
        });

        searchButton.addActionListener(e -> {

            try {
                int start = Integer.parseInt(startInput.getText());
                int goal = Integer.parseInt(goalInput.getText());
                String selectedAlgorithm = (String) algorithmList.getSelectedItem();
                int oracleValue = oracleInput.getText().isEmpty() ? 0 : Integer.parseInt(oracleInput.getText().trim()); // Read oracle value if applicable

                List<Integer> path = null;
                List<List<Integer>> allPaths = null;

                switch (selectedAlgorithm) {
                    case "BFS":
                        path = searchAlgorithms.bfs(graph, start, goal);
                        break;
                    case "DFS":
                        path = searchAlgorithms.dfs(graph, start, goal);
                        break;
                    case "British Museum":
                        path = searchAlgorithms.britishMuseumSearch(graph, start, goal);
                        break;
                    case "Hill Climbing":
                        path = searchAlgorithms.hillClimbing(graph, start, goal, heuristics, edgeWeights);
                        break;
                    case "Beam search":
                        path = searchAlgorithms.beamSearch(graph, start, goal, heuristics, edgeWeights);
                        break;
                    case "Oracle":
                        allPaths = searchAlgorithms.oracleSearch(graph, start, goal, edgeWeights, oracleValue);
                        break;
                    case "Branch and bound":
                        allPaths = searchAlgorithms.branchAndBound(graph, start, goal, heuristics, edgeWeights, oracleValue);
                        break;
                    case "Branch and bound with dead horse/ext list":
                        path = searchAlgorithms.branchAndBoundDeadHorse(graph, start, goal, heuristics, edgeWeights);
                        break;
                    case "Branch and bound with heuristics":
                        path = searchAlgorithms.branchAndBoundWithHeuristics(graph, start, goal, heuristics, edgeWeights, oracleValue);
                        break;
                    case "A* algorithm":
                        path = searchAlgorithms.aStarAlgorithm(graph, start, goal, heuristics, edgeWeights, oracleValue);
                        break;
                    default:
                        JOptionPane.showMessageDialog(panel, "Invalid algorithm selected.");
                }

                // Display single path result
                if (path != null) {
                    resultArea.setText("Path: " + path);
                }

                // Display all paths for Oracle and Branch & Bound
                if (allPaths != null) {
                    StringBuilder result = new StringBuilder("Paths:\n");
                    for (List<Integer> p : allPaths) {
                        result.append(p.toString()).append("\n");
                    }
                    resultArea.setText(result.toString());
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input: " + ex.getMessage());
            }

        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GraphSearchUI::new);
    }
}
