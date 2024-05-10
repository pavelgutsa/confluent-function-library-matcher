package com.confluent.interview;

/*
* register[{ 
*    funA:{["Boolean", "Integer"], isVariadic:false},
*    funB:{["Integer"], isVariadic:false},
*    funC:{["Integer"], isVariadic:true}
     funD: ["Integer", "Integer", Variadic=true
* })
*
* findMatches(["Boolean", "Integer"])            -> [funA]
* findMatches(["Integer"])                       -> [funB, funC]
* findMatches(["Integer", "Integer", "Integer"]) -> [funC, funD]
*/

/*
* More Examples:
*
*    funD:{["String", "Integer", "Integer", "Integer"], isVariadic:true},
*    funE:{["String", "Integer", "Integer"], isVariadic:false}
*
* findMatches(["String", "Integer"])             -> []
* findMatches(["String", "Integer", "Integer"])  -> [funE, funD] # funD due to the supporting 0 variadics
* findMatches(["String", "Integer", "Integer", "Integer", "Integer"])  -> [funD]
*/
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Function {
	public final List<String> argumentTypes; // e.g. ["Integer", "String", "PersonClass"]
	public final String name;
	public final boolean isVariadic;

	Function(String name, List<String> argumentTypes, boolean isVariadic) {
		this.name = name;
		this.argumentTypes = argumentTypes;
		this.isVariadic = isVariadic;
	}

	public String toString() {
		return this.name;
	}
}

class FunctionSearchNode {
	Map<String, FunctionSearchNode> children = new HashMap<>();
	List<Function> matchingFunctions = new ArrayList<>();

	void append(List<String> arguments, int position, Function f) {

		String currentArgument = arguments.get(position);

		if (!children.containsKey(currentArgument)) {
			children.put(currentArgument, new FunctionSearchNode());
		}

		if (position < arguments.size() - 1) {
			children.get(currentArgument).append(arguments, position + 1, f);
		} else {
			matchingFunctions.add(f);
		}
	}

	List<Function> findMatches(List<String> argumentTypes, int position) {
		String currentArgument = argumentTypes.get(position);
		FunctionSearchNode n = children.get(currentArgument);

		if (n == null) {
			return new ArrayList<>();
		}

		if (position < argumentTypes.size() - 1) {
			return n.matchingFunctions;
		} else {
			return n.findMatches(argumentTypes, position + 1);
		}
	}
}

class FunctionLibrary {

	FunctionSearchNode searchRoot = new FunctionSearchNode();

	void register(Set<Function> functions) {
		for (Function f : functions) {

			if (f.argumentTypes.isEmpty()) {
				searchRoot.matchingFunctions.add(f);
			} else {
				searchRoot.append(f.argumentTypes, 0, f);
			}
		}
	}

	List<Function> findMatches(List<String> argumentTypes) {
		if (argumentTypes.isEmpty()) {
			return searchRoot.matchingFunctions;
		} else {
			return searchRoot.findMatches(argumentTypes, 0);
		}
	}
}

class Solution {
	public static void main(String[] args) {
		System.out.println("it works!");
	}
}
