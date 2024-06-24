package br.com.agrotis.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CircularDependencyChecker {
    private Map<String, List<String>> dependencyMap;

    public CircularDependencyChecker() {
        this.dependencyMap = new HashMap<>();
    }

    // Method to add a dependency between two classes
    public void addDependency(String classA, String classB) {
        dependencyMap.computeIfAbsent(classA, k -> new ArrayList<>()).add(classB);
    }

    // Method to detect all circular dependencies
    public List<List<String>> getAllCircularDependencies() {
        List<List<String>> cycles = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        List<String> recursionStack = new ArrayList<>();

        for (String clazz : dependencyMap.keySet()) {
            detectCycles(clazz, visited, recursionStack, cycles);
        }
        return cycles;
    }

    private void detectCycles(String clazz, Set<String> visited, List<String> recursionStack, List<List<String>> cycles) {
        if (recursionStack.contains(clazz)) {
            // Cycle detected, extract the cycle path
            int cycleStartIndex = recursionStack.indexOf(clazz);
            List<String> cycle = new ArrayList<>(recursionStack.subList(cycleStartIndex, recursionStack.size()));
            cycles.add(cycle);
            return;
        }
        if (visited.contains(clazz)) {
            return; // Already visited this node
        }


        visited.add(clazz);
        recursionStack.add(clazz);

        List<String> dependencies = dependencyMap.get(clazz);
        if (dependencies != null) {
            for (String dependency : dependencies) {
                detectCycles(dependency, visited, recursionStack, cycles);
            }
        }

        recursionStack.remove(clazz);
    }
}

