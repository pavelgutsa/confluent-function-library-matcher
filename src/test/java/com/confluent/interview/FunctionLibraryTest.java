package com.confluent.interview;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Unit test for FunctionLibraryTest.
 */
public class FunctionLibraryTest {

    @Test
    public void testNoArguments() {
        FunctionLibrary lib = new FunctionLibrary();

        Function funA = new Function("funA", Arrays.asList(), false);

        lib.register(Set.of(funA));

        assertTrue(lib.findMatches(Arrays.asList()).contains(funA));
    }
    
    @Test
    public void testOneArgument() {
        FunctionLibrary lib = new FunctionLibrary();

        Function funA = new Function("funA", Arrays.asList("Integer"), false);

        lib.register(Set.of(funA));

        assertTrue(lib.findMatches(Arrays.asList("Integer")).contains(funA));
    }
    
    @Test
    public void testOneVariaticArgument() {
        FunctionLibrary lib = new FunctionLibrary();

        Function funA = new Function("funA", Arrays.asList("Integer"), true);

        lib.register(Set.of(funA));

        assertTrue(lib.findMatches(Arrays.asList("Integer")).contains(funA));
    }
    
    @Test
    public void testTwoOneVariaticSameLevel() {
        FunctionLibrary lib = new FunctionLibrary();

        Function funA = new Function("funA", Arrays.asList("Integer"), false);
        Function funB = new Function("funB", Arrays.asList("Integer"), true);

        lib.register(Set.of(funA, funB));

        assertTrue(lib.findMatches(Arrays.asList("Integer")).containsAll(Arrays.asList(funA, funB)));
    }
    
    @Test
    public void testTwoOneVariaticNextLevel() {
        FunctionLibrary lib = new FunctionLibrary();

        Function funA = new Function("funA", Arrays.asList("Integer", "Integer"), false);
        Function funB = new Function("funB", Arrays.asList("Integer"), true);

        lib.register(Set.of(funA, funB));

        assertTrue(lib.findMatches(Arrays.asList("Integer", "Integer")).containsAll(Arrays.asList(funA, funB)));
    }
    
	@Test
	public void testAll() {
		FunctionLibrary lib = new FunctionLibrary();

		Function funA = new Function("funA", Arrays.asList("Boolean", "Integer"), false);
		Function funB = new Function("funB", Arrays.asList("Integer"), false);
		Function funC = new Function("funC", Arrays.asList("Integer"), true);
		Function funD = new Function("funD", Arrays.asList("Integer", "Integer"), true);

		lib.register(Set.of(funA, funB, funC, funD));

		assertTrue(lib.findMatches(Arrays.asList("Boolean", "Integer")).contains(funA));
		assertTrue(lib.findMatches(Arrays.asList("Integer")).containsAll(Arrays.asList(funB, funC)));
		assertTrue(lib.findMatches(Arrays.asList("Integer", "Integer")).containsAll(Arrays.asList(funC, funD)));
	}
}
