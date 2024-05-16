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
* 
* The idea is to build a search tree to store and lookup functions based on the arguments list.
* Each search tree node holds a list of matching functions.  
* Search tree root holds references to functions with no arguments.
* Each search tree node also contains a map of argument type to a list children search tree nodes matching this argument type.
* E.g. search root node has a map of "Integer" to a list of functions where the first argument is "Integer", etc.
* Second level child nodes have a map of lists of search tree nodes matching the second argument to functions, etc.
* 
* This way, when we are adding a function to the library, we are iterating through the list of function arguments. 
* If the list is empty, then this function is added to the search tree root. 
* Otherwise, we check the first argument of the function. 
* If there is not yet an entry in the child nodes map matching this argument, we create this search node. 
* Then we delegate inserting the function in the search tree to the corresponding node.
* When we reach the last argument in the list, we add the function to the list of matching functions of this node.
* 
* We repeat this iterative process when we are looking up the matches in the search tree using the arguments list.
* If the list is empty, then we return the functions list from the search tree root.
* Otherwise, we delegate the search to the next child node in the hierarchy, argument by argument, deeper and deeper in the search tree.
* When we reach the last argument in the list, the node returns it's list of matching functions.
* At any time, when we cannot find a child node corresponding to the current argument in the argument list, 
* we conclude that the search was unsuccessful and return the empty list.   
*  
* The exception to this process are the vararg functions.
* When the function is marked as variatic, it means that the last argument of the function can be repeated unlimited number of times.
* Under this condition, we insert this function in the special variatic list of matching functions of the search nodes.
* For example, if the function has only one argument "Integer", and it is variatic, it will be inserted in the variatic matches list
* of the child node of the search root node, matching "Integer" argument.
* 
* When we perform the search, on the current recursive iteration of checking the arguments in the list, we take a look ath the rest of the 
* arguments of the list, to see if they are repeating the current argument. If they do, then we add the functions from the variatic
* matches list to the result, before delegating search to the next child node.
* 
* For example, in addition to the previous example function which has only one variatic "Integer" argument and is variatic, we might have another function
* with two arguments, "Integer" and "Boolean". The variatic function will be added to the results list by the first level child
* search node, matching the first "Integer" argument, then the search will be delegated to the second level child search node, 
* matching the second "Boolean" argument.  
* 
*  
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
	List<Function> matchingVariatic = new ArrayList<>();

	void append(List<String> arguments, int position, Function f) {

		String currentArgument = arguments.get(position);

		if (!children.containsKey(currentArgument)) {
			children.put(currentArgument, new FunctionSearchNode());
		}
		
		FunctionSearchNode n = children.get(currentArgument);

		if (position < arguments.size() - 1) {
			n.append(arguments, position + 1, f);
		} else {
		    if(f.isVariadic) {
		        n.matchingVariatic.add(f);
		    } else {
		        n.matchingFunctions.add(f);
		    }
		}
	}

	List<Function> findMatches(List<String> argumentTypes, int position) {
		
		List<Function> result = new ArrayList<>(); 
		
		String currentArgument = argumentTypes.get(position);
		
		FunctionSearchNode n = children.get(currentArgument);
		if (n != null) {

		    // If this is the last argument in the list
    		if (position == argumentTypes.size() - 1) {
    		    result.addAll(n.matchingFunctions);
    		    result.addAll(n.matchingVariatic);
    		} else {
    		    
    		    // Check for variadic    		     
    		    boolean isVariadic = false;
    		    for( int i = position + 1; i < argumentTypes.size(); i++) {
    		        if(argumentTypes.get(i).equals(currentArgument)) {
    		            isVariadic = true;
    		        } else {
    		            isVariadic = false;
    		            break;
    		        }
    		    }
    		    
    		    if(isVariadic) {
    		        result.addAll(n.matchingVariatic);
    		    }
    		    
    			result.addAll(n.findMatches(argumentTypes, position + 1));
    		}
		}
		
		return result;
	}
}

public class FunctionLibrary {

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

