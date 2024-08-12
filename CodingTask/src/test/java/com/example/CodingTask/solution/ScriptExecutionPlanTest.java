package com.example.CodingTask.solution;

import com.example.CodingTask.model.VulnerabilityScript;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScriptExecutionPlanTest {

    @Test
    public void testExecutionPlanNoDependencies() {
        VulnerabilityScript script1 = new VulnerabilityScript(1, Collections.emptyList());
        VulnerabilityScript script2 = new VulnerabilityScript(2, Collections.emptyList());

        List<VulnerabilityScript> scripts = Arrays.asList(script1, script2);
        ScriptExecutionPlan plan = new ScriptExecutionPlan();

        List<Integer> expected = Arrays.asList(1, 2);
        List<Integer> actual = plan.getExecutionPlan(scripts);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecutionPlanWithDependencies() {
        VulnerabilityScript script1 = new VulnerabilityScript(1, Collections.emptyList());
        VulnerabilityScript script2 = new VulnerabilityScript(2, Arrays.asList(1));
        VulnerabilityScript script3 = new VulnerabilityScript(3, Arrays.asList(1, 2));

        List<VulnerabilityScript> scripts = Arrays.asList(script1, script2, script3);
        ScriptExecutionPlan plan = new ScriptExecutionPlan();

        List<Integer> actual = plan.getExecutionPlan(scripts);
        assertEquals(3, actual.size());
        assertEquals(true, actual.containsAll(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void testCyclicDependency() {
        VulnerabilityScript script1 = new VulnerabilityScript(1, Arrays.asList(2));
        VulnerabilityScript script2 = new VulnerabilityScript(2, Arrays.asList(1));

        List<VulnerabilityScript> scripts = Arrays.asList(script1, script2);
        ScriptExecutionPlan plan = new ScriptExecutionPlan();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> plan.getExecutionPlan(scripts));
        assertEquals("There exists a cycle or unresolved dependency in the graph.", exception.getMessage());
    }

    @Test
    public void testSelfDependency() {
        VulnerabilityScript script1 = new VulnerabilityScript(1, Arrays.asList(1));

        List<VulnerabilityScript> scripts = Collections.singletonList(script1);
        ScriptExecutionPlan plan = new ScriptExecutionPlan();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> plan.getExecutionPlan(scripts));
        assertEquals("Script 1 cannot depend on itself.", exception.getMessage());
    }

    @Test
    public void testMissingDependency() {
        VulnerabilityScript script1 = new VulnerabilityScript(1, Collections.emptyList());
        VulnerabilityScript script2 = new VulnerabilityScript(2, Arrays.asList(1));
        VulnerabilityScript script3 = new VulnerabilityScript(3, Arrays.asList(1, 4));

        List<VulnerabilityScript> scripts = Arrays.asList(script1, script2, script3);
        ScriptExecutionPlan plan = new ScriptExecutionPlan();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> plan.getExecutionPlan(scripts));
        assertEquals("Dependency 4 for script 3 is missing from the list of scripts.", exception.getMessage());
    }

    @Test
    public void testDuplicateScriptId() {
        VulnerabilityScript script1 = new VulnerabilityScript(1, Collections.emptyList());
        VulnerabilityScript script2 = new VulnerabilityScript(1, Collections.emptyList());

        List<VulnerabilityScript> scripts = Arrays.asList(script1, script2);
        ScriptExecutionPlan plan = new ScriptExecutionPlan();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> plan.getExecutionPlan(scripts));
        assertEquals("Duplicate script ID found: 1", exception.getMessage());
    }

    @Test
    public void testComplexExecutionPlan() {
        VulnerabilityScript script1 = new VulnerabilityScript(1, Collections.emptyList());
        VulnerabilityScript script2 = new VulnerabilityScript(2, Arrays.asList(1));
        VulnerabilityScript script3 = new VulnerabilityScript(3, Arrays.asList(1, 2));
        VulnerabilityScript script4 = new VulnerabilityScript(4, Arrays.asList(2));
        VulnerabilityScript script5 = new VulnerabilityScript(5, Arrays.asList(4));

        List<VulnerabilityScript> scripts = Arrays.asList(script1, script2, script3, script4, script5);
        ScriptExecutionPlan plan = new ScriptExecutionPlan();

        List<Integer> expected = Arrays.asList(4, 2, 1, 5, 3);
        List<Integer> actual = plan.getExecutionPlan(scripts);

        assertEquals(expected, actual);
    }
}
