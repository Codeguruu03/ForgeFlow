package com.forgeflow.backend.workflow.validator;

import com.forgeflow.backend.workflow.model.EdgeDefinition;
import com.forgeflow.backend.workflow.model.NodeDefinition;
import com.forgeflow.backend.workflow.model.WorkflowValidationResult;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WorkflowDagValidator {

    public WorkflowValidationResult validate(List<NodeDefinition> nodes, List<EdgeDefinition> edges) {
        WorkflowValidationResult result = new WorkflowValidationResult();

        if (nodes == null || nodes.isEmpty()) {
            result.addError("Workflow must contain at least one node.");
            return result;
        }

        Set<String> nodeIds = new HashSet<>();
        for (NodeDefinition node : nodes) {
            if (!nodeIds.add(node.getId())) {
                result.addError("Duplicate node ID found: " + node.getId());
            }
        }

        Map<String, List<String>> adjacencyList = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, Integer> outDegree = new HashMap<>();

        for (String id : nodeIds) {
            adjacencyList.put(id, new ArrayList<>());
            inDegree.put(id, 0);
            outDegree.put(id, 0);
        }

        if (edges != null) {
            for (EdgeDefinition edge : edges) {
                if (!nodeIds.contains(edge.getFrom())) {
                    result.addError("Edge references non-existent source node: " + edge.getFrom());
                    continue;
                }
                if (!nodeIds.contains(edge.getTo())) {
                    result.addError("Edge references non-existent target node: " + edge.getTo());
                    continue;
                }
                adjacencyList.get(edge.getFrom()).add(edge.getTo());
                inDegree.put(edge.getTo(), inDegree.get(edge.getTo()) + 1);
                outDegree.put(edge.getFrom(), outDegree.get(edge.getFrom()) + 1);
            }
        }

        // Cycle Detection via DFS
        Map<String, Integer> state = new HashMap<>(); // 0: unvisited, 1: visiting (gray), 2: visited (black)
        for (String id : nodeIds) {
            state.put(id, 0);
        }

        for (String id : nodeIds) {
            if (state.get(id) == 0) {
                if (hasCycleDFS(id, adjacencyList, state)) {
                    result.addError("Workflow contains a cycle/loop, which is prohibited in DAG workflows.");
                    break;
                }
            }
        }

        // Check for isolated / unconnected nodes if multiple nodes exist
        if (nodes.size() > 1) {
            for (String id : nodeIds) {
                if (inDegree.get(id) == 0 && outDegree.get(id) == 0) {
                    result.addWarning("Node '" + id + "' is completely disconnected from the rest of the workflow.");
                }
            }
        }

        return result;
    }

    private boolean hasCycleDFS(String current, Map<String, List<String>> adj, Map<String, Integer> state) {
        state.put(current, 1);
        for (String neighbor : adj.get(current)) {
            if (state.get(neighbor) == 1) {
                return true; // Cycle detected
            }
            if (state.get(neighbor) == 0 && hasCycleDFS(neighbor, adj, state)) {
                return true;
            }
        }
        state.put(current, 2);
        return false;
    }
}
