package algorithms;

import model.Graph;
import java.util.*;

public class SearchAlgorithms {

    // BFS algorithm
    public List<Integer> bfs(Graph graph, int start, int goal) {
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> parent = new HashMap<>();
        List<Integer> path = new ArrayList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            int node = queue.poll();

            if (node == goal) {
                return buildPath(parent, start, goal);
            }

            for (int neighbor : graph.getNeighbors(node)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    parent.put(neighbor, node);
                }
            }
        }

        return path; // Return empty list if no path is found
    }

    // DFS algorithm
    public List<Integer> dfs(Graph graph, int start, int goal) {
        Stack<Integer> stack = new Stack<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> parent = new HashMap<>();
        List<Integer> path = new ArrayList<>();

        stack.push(start);

        while (!stack.isEmpty()) {
            int node = stack.pop();

            if (node == goal) {
                return buildPath(parent, start, goal);
            }

            if (!visited.contains(node)) {
                visited.add(node);
                for (int neighbor : graph.getNeighbors(node)) {
                    if (!visited.contains(neighbor)) {
                        stack.push(neighbor);
                        parent.put(neighbor, node);
                    }
                }
            }
        }

        return path; // Return empty list if no path is found
    }

    // British Museum Search (brute-force all paths)
    public List<Integer> britishMuseumSearch(Graph graph, int start, int goal) {
        List<List<Integer>> allPaths = new ArrayList<>();
        findAllPaths(graph, start, goal, new HashSet<>(), new ArrayList<>(), allPaths);

        if (!allPaths.isEmpty()) {
            return allPaths.get(0); // Return first path found for demonstration
        }
        return new ArrayList<>();
    }

    // Hill Climbing algorithm
    public List<Integer> hillClimbing(Graph graph, int start, int goal,
                                      Map<Integer, Integer> heuristics,
                                      Map<String, Integer> edgeWeights) {
        List<Integer> path = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        int current = start;

        path.add(current);

        while (current != goal) {
            visited.add(current);
            List<Integer> neighbors = graph.getNeighbors(current);

            // Sort neighbors by their heuristic cost
            neighbors.sort(Comparator.comparingInt(heuristics::get));

            // Select the best neighbor based on the heuristic value
            int bestNeighbor = -1;
            for (int neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    bestNeighbor = neighbor;
                    break;
                }
            }

            // If we can't find an unvisited neighbor, the algorithm is stuck
            if (bestNeighbor == -1) {
                break; // No valid path, exit
            }

            path.add(bestNeighbor);
            current = bestNeighbor;
        }

        return current == goal ? path : new ArrayList<>(); // Successfully reached the goal or failed
    }

    // Helper method to build path from the parent map
    private List<Integer> buildPath(Map<Integer, Integer> parent, int start, int goal) {
        List<Integer> path = new ArrayList<>();
        for (int at = goal; at != start; at = parent.get(at)) {
            path.add(at);
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }

    // Helper method to find all paths (for British Museum Search)
    private void findAllPaths(Graph graph, int current, int goal, Set<Integer> visited, List<Integer> currentPath, List<List<Integer>> allPaths) {
        visited.add(current);
        currentPath.add(current);

        if (current == goal) {
            allPaths.add(new ArrayList<>(currentPath));
        } else {
            for (int neighbor : graph.getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    findAllPaths(graph, neighbor, goal, visited, currentPath, allPaths);
                }
            }
        }

        visited.remove(current);
        currentPath.remove(currentPath.size() - 1);
    }

    // Beam Search implementation
    public List<Integer> beamSearch(Graph graph, int start, int goal, Map<Integer, Integer> heuristics, Map<String, Integer> edgeWeights) {
        int beamWidth = 3; // You can adjust this based on your needs
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(n -> n.heuristic));
        Set<Integer> explored = new HashSet<>(); // To track explored nodes
        List<Integer> path = new ArrayList<>(); // To store the final path

        // Initialize the search with the starting node
        Node startNode = new Node(start, 0, heuristics.getOrDefault(start, 0), null);
        priorityQueue.add(startNode);

        while (!priorityQueue.isEmpty()) {
            // Get the nodes up to the beam width
            List<Node> currentLevelNodes = new ArrayList<>();
            for (int i = 0; i < beamWidth && !priorityQueue.isEmpty(); i++) {
                currentLevelNodes.add(priorityQueue.poll());
            }

            for (Node currentNode : currentLevelNodes) {
                int current = currentNode.id;

                // Check if we reached the goal
                if (current == goal) {
                    path = reconstructPath(currentNode);
                    return path;
                }

                // Expand the current node
                for (int neighbor : graph.getNeighbors(current)) {
                    if (!explored.contains(neighbor)) {
                        // Calculate the new cost and heuristic for the neighbor
                        int newCost = currentNode.costSoFar + edgeWeights.getOrDefault(current + "," + neighbor, Integer.MAX_VALUE);
                        int newHeuristic = heuristics.getOrDefault(neighbor, Integer.MAX_VALUE);
                        Node neighborNode = new Node(neighbor, newCost, newHeuristic, currentNode);

                        // Add the neighbor to the priority queue
                        priorityQueue.add(neighborNode);
                    }
                }

                // Mark the current node as explored
                explored.add(current);
            }
        }

        return null; // Return null if no path is found
    }

    // Oracle Search implementation
    public List<List<Integer>> oracleSearch(Graph graph, int start, int goal, Map<String, Integer> edgeWeights, int oracleValue) {
        List<List<Integer>> allPaths = new ArrayList<>(); // To store all valid paths
        List<Integer> currentPath = new ArrayList<>(); // To track the current path
        Set<Integer> visited = new HashSet<>(); // To track visited nodes
        backtrack(graph, start, goal, currentPath, visited, allPaths, 0, oracleValue, edgeWeights);
        return allPaths;
    }

    private void backtrack(Graph graph, int current, int goal, List<Integer> currentPath, Set<Integer> visited,
                           List<List<Integer>> allPaths, int currentCost, int oracleValue, Map<String, Integer> edgeWeights) {
        currentPath.add(current); // Add current node to the path
        visited.add(current); // Mark current node as visited

        // Check if we have reached the goal
        if (current == goal) {
            allPaths.add(new ArrayList<>(currentPath)); // Add a copy of the current path to all paths
        }

        // Explore neighbors
        for (int neighbor : graph.getNeighbors(current)) {
            if (!visited.contains(neighbor)) { // If neighbor is not visited
                String edgeKey = current + "," + neighbor;
                int edgeCost = edgeWeights.getOrDefault(edgeKey, Integer.MAX_VALUE); // Get edge cost, or max if edge doesn't exist
                int newCost = currentCost + edgeCost;

                // Continue exploring if the new cost does not exceed the Oracle value
                if (newCost <= oracleValue) {
                    backtrack(graph, neighbor, goal, currentPath, visited, allPaths, newCost, oracleValue, edgeWeights);
                }
            }
        }

        // Backtrack
        currentPath.remove(currentPath.size() - 1); // Remove current node from the path
        visited.remove(current); // Mark current node as unvisited
    }

    // Branch and Bound implementation
    public List<List<Integer>> branchAndBound(Graph graph, int start, int goal,
                                              Map<Integer, Integer> heuristics,
                                              Map<String, Integer> edgeWeights,
                                              int oracleValue) {
        List<List<Integer>> validPaths = new ArrayList<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.costSoFar));
        Set<Integer> visited = new HashSet<>();

        // Initialize with the start node
        pq.add(new Node(start, 0, heuristics.getOrDefault(start, 0), null));

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            // If we reach the goal
            if (current.id == goal) {
                validPaths.add(reconstructPath(current));
                continue; // We found a valid path, but we need to keep exploring
            }

            // Mark the current node as visited
            visited.add(current.id);

            for (int neighbor : graph.getNeighbors(current.id)) {
                if (!visited.contains(neighbor)) {
                    int edgeCost = edgeWeights.getOrDefault(current.id + "," + neighbor, Integer.MAX_VALUE);
                    int newCost = current.costSoFar + edgeCost;

                    // Add to queue if within the oracle limit
                    if (newCost <= oracleValue) {
                        pq.add(new Node(neighbor, newCost, heuristics.getOrDefault(neighbor, Integer.MAX_VALUE), current));
                    }
                }
            }
        }
        return validPaths;
    }



    // Inner class for node representation
    private static class Node {
        int id; // Node identifier
        int costSoFar; // Cost to reach this node
        int heuristic; // Heuristic cost
        Node parent; // Parent node to reconstruct the path

        Node(int id, int costSoFar, int heuristic, Node parent) {
            this.id = id;
            this.costSoFar = costSoFar;
            this.heuristic = heuristic;
            this.parent = parent;
        }
    }
    private List<Integer> reconstructPath(Node node) {
        List<Integer> path = new ArrayList<>();
        while (node != null) {
            path.add(0, node.id); // Insert at the beginning to maintain the order
            node = node.parent; // Move to the parent
        }
        return path; // This should return List<Integer>
    }

    // Branch and Bound with Dead Horse/Ext List

    // Branch and Bound with Dead Horse/Ext List
    public List<Integer> branchAndBoundDeadHorse(Graph graph, int start, int goal,
                                                 Map<Integer, Integer> heuristics,
                                                 Map<String, Integer> edgeWeights) {
        Set<Integer> deadHorses = new HashSet<>(); // To keep track of dead-end nodes
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.costSoFar));

        // Initialize with the start node
        pq.add(new Node(start, 0, heuristics.getOrDefault(start, 0), null));

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            // If we reach the goal, return the reconstructed path immediately
            if (current.id == goal) {
                return reconstructPath(current); // Return the first valid path found
            }

            // Check if the current node is a dead horse (dead-end)
            if (deadHorses.contains(current.id)) {
                continue; // Skip dead horses
            }

            // Expand the current node
            for (int neighbor : graph.getNeighbors(current.id)) {
                int edgeCost = edgeWeights.getOrDefault(current.id + "," + neighbor, Integer.MAX_VALUE);
                int newCost = current.costSoFar + edgeCost;

                // Add to queue if the new cost does not exceed any limit
                if (!deadHorses.contains(neighbor)) {
                    pq.add(new Node(neighbor, newCost, heuristics.getOrDefault(neighbor, Integer.MAX_VALUE), current));
                }
            }

            // Mark the current node as a dead horse if it has no valid children
            if (pq.stream().noneMatch(node -> node.id != current.id)) {
                deadHorses.add(current.id);
            }
        }

        // Return an empty list if no valid path is found
        return new ArrayList<>();
    }

    // Branch and Bound with Heuristics
    public List<Integer> branchAndBoundWithHeuristics(Graph graph, int start, int goal,
                                                      Map<Integer, Integer> heuristics,
                                                      Map<String, Integer> edgeWeights,
                                                      int oracleValue) {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.costSoFar + n.heuristic));
        Set<Integer> visited = new HashSet<>();

        // Initialize with the start node
        pq.add(new Node(start, 0, heuristics.getOrDefault(start, 0), null));

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            // If we reach the goal, return the reconstructed path
            if (current.id == goal) {
                return reconstructPath(current); // Return the first valid path found
            }

            // Mark the current node as visited
            visited.add(current.id);

            for (int neighbor : graph.getNeighbors(current.id)) {
                if (!visited.contains(neighbor)) {
                    int edgeCost = edgeWeights.getOrDefault(current.id + "," + neighbor, Integer.MAX_VALUE);
                    int newCost = current.costSoFar + edgeCost;

                    // Add to queue only if the new cost is within the oracle limit
                    if (newCost <= oracleValue) {
                        pq.add(new Node(neighbor, newCost, heuristics.getOrDefault(neighbor, Integer.MAX_VALUE), current));
                    }
                }
            }
        }

        // Return an empty list if no valid path is found within the oracleValue limit
        return new ArrayList<>();
    }
    public List<Integer> aStarAlgorithm(Graph graph, int start, int goal,
                                        Map<Integer, Integer> heuristics,
                                        Map<String, Integer> edgeWeights,
                                        int oracleValue) {
        // PriorityQueue to store nodes to explore, ordered by (costSoFar + heuristic)
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.costSoFar + n.heuristic));

        // Set to store dead-end nodes (dead horses)
        Set<Integer> deadHorses = new HashSet<>();

        // Set to track visited nodes
        Set<Integer> visited = new HashSet<>();

        // Initialize the queue with the start node
        pq.add(new Node(start, 0, heuristics.getOrDefault(start, 0), null));

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            // If we reach the goal, return the path
            if (current.id == goal) {
                return reconstructPath(current); // Return the path to the goal
            }

            // Mark the current node as visited
            visited.add(current.id);

            // If current node is a dead horse (no valid children), skip further exploration
            if (deadHorses.contains(current.id)) {
                continue;
            }

            // Expand the current node's neighbors
            for (int neighbor : graph.getNeighbors(current.id)) {
                if (!visited.contains(neighbor)) {
                    int edgeCost = edgeWeights.getOrDefault(current.id + "," + neighbor, Integer.MAX_VALUE);
                    int newCost = current.costSoFar + edgeCost;
                    int heuristicValue = heuristics.getOrDefault(neighbor, Integer.MAX_VALUE);
                    int totalCost = newCost + heuristicValue;

                    // Oracle-based pruning: skip this branch if total cost exceeds the oracleValue
                    if (totalCost > oracleValue) {
                        continue; // Stop exploring this branch
                    }

                    // Add the neighbor to the priority queue if not a dead horse
                    if (!deadHorses.contains(neighbor)) {
                        pq.add(new Node(neighbor, newCost, heuristicValue, current));
                    }
                }
            }

            // If none of the neighbors are valid (i.e., dead horse), mark the current node
            if (pq.stream().noneMatch(node -> node.id != current.id)) {
                deadHorses.add(current.id);
            }
        }

        // Return an empty list if no valid path is found
        return new ArrayList<>();
    }

}


