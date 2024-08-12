package com.example.CodingTask.solution;

import com.example.CodingTask.model.VulnerabilityScript;

import java.util.*;

public class ScriptExecutionPlan {

    public static List<Integer> getExecutionPlan(List<VulnerabilityScript> scripts) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        Map<Integer, Integer> inDegree = new HashMap<>();
        Set<Integer> allScripts = new HashSet<>();
        Set<Integer> addedScriptIds = new HashSet<>();

        for (VulnerabilityScript script : scripts) {
            int scriptId = script.getScriptId();

            if (addedScriptIds.contains(scriptId)) {
                throw new IllegalArgumentException("Duplicate script ID found: " + scriptId);
            }

            addedScriptIds.add(scriptId);
            allScripts.add(scriptId);
            graph.putIfAbsent(scriptId, new ArrayList<>());
            inDegree.putIfAbsent(scriptId, 0);
        }

        for (VulnerabilityScript script : scripts) {
            int scriptId = script.getScriptId();
            for (Integer dep : script.getDependencies()) {

                if (scriptId == dep) {
                    throw new IllegalArgumentException("Script " + scriptId + " cannot depend on itself.");
                }

                if (!allScripts.contains(dep)) {
                    throw new IllegalArgumentException("Dependency " + dep + " for script " + scriptId + " is missing from the list of scripts.");
                }

                graph.get(dep).add(scriptId);
                inDegree.put(scriptId, inDegree.getOrDefault(scriptId, 0) + 1);
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (Map.Entry<Integer, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int current = queue.poll();
            order.add(current);
            for (Integer neighbor : graph.getOrDefault(current, Collections.emptyList())) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (order.size() != scripts.size()) {
            throw new IllegalArgumentException("There exists a cycle or unresolved dependency in the graph.");
        }

        return order;
    }

    public static void main(String[] args) {
        List<VulnerabilityScript> scripts = Arrays.asList(
                new VulnerabilityScript(1, Arrays.asList()),
                new VulnerabilityScript(2, Arrays.asList(1)),
                new VulnerabilityScript(3, Arrays.asList(1, 2)),
                new VulnerabilityScript(4, Arrays.asList(2)),
                new VulnerabilityScript(5, Arrays.asList(3, 4))
        );

        try {
            System.out.println(getExecutionPlan(scripts)); // Output: [1, 2, 4, 3, 5]
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}
