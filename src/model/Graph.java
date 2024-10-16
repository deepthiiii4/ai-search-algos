package model;

import java.util.*;

public class Graph {
    private Map<Integer, List<Integer>> adjacencyList; // To store the adjacency list of the graph
    private Map<Integer, Integer> heuristics; // To store the heuristic values of the nodes

    public Graph() {
        adjacencyList = new HashMap<>();
        heuristics = new HashMap<>(); // Initialize the heuristic map
    }

    // Method to add an edge between two nodes
    public void addEdge(int src, int dest) {
        adjacencyList.computeIfAbsent(src, k -> new ArrayList<>()).add(dest);
        adjacencyList.computeIfAbsent(dest, k -> new ArrayList<>()).add(src); // For an undirected graph
    }

    // Method to get neighbors of a node
    public List<Integer> getNeighbors(int node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    // Method to get the adjacency list
    public Map<Integer, List<Integer>> getAdjacencyList() {
        return adjacencyList;
    }

    // Method to set the heuristic value for a node
    public void setHeuristic(int node, int heuristicValue) {
        heuristics.put(node, heuristicValue);
    }

    // Method to get the heuristic value of a node
    public int getHeuristic(int node) {
        return heuristics.getOrDefault(node, Integer.MAX_VALUE); // Return a large number if heuristic is missing
    }

    // Method to get all heuristic values
    public Map<Integer, Integer> getHeuristics() {
        return heuristics;
    }
}
