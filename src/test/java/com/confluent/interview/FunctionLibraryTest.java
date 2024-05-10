package com.confluent.interview;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.junit.Test;

/**
 * Unit test for FunctionLibraryTest.
 */
public class FunctionLibraryTest {

	@Test
	public void shouldAnswerWithTrue() {
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
