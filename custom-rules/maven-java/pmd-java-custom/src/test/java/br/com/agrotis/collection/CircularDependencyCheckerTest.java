package br.com.agrotis.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;


public class CircularDependencyCheckerTest {
    @Test
    void test() {
        CircularDependencyChecker checker = new CircularDependencyChecker();
        checker.addDependency("Class1", "Class2");
        checker.addDependency("Class1", "Class7");
        checker.addDependency("Class2", "Class3");
        checker.addDependency("Class3", "Class1");
        checker.addDependency("Class4", "Class5");
        checker.addDependency("Class5", "Class6");
        checker.addDependency("Class6", "Class4");
        checker.addDependency("Class7", "Class8"); // No cycle
        checker.addDependency("Class7", "Class1");
        checker.addDependency("Class10", "Class1");
        checker.addDependency("Class1", "Class10");

        List<List<String>> cycles = checker.getAllCircularDependencies();
        List<String> stringCycleList = new ArrayList<String>();
        for (List<String> cycle : cycles) {
            stringCycleList.add(String.join("->", cycle));
        }
        Collections.sort(stringCycleList);

        assertEquals(4, cycles.size());
        assertEquals("Class1->Class10", stringCycleList.get(0));
        assertEquals("Class1->Class2->Class3", stringCycleList.get(1));
        assertEquals("Class4->Class5->Class6", stringCycleList.get(2));
        assertEquals("Class7->Class1", stringCycleList.get(3));
    }
}

