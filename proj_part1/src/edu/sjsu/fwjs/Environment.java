package edu.sjsu.fwjs;

import java.util.Map;
import java.util.HashMap;

public class Environment {
    private Map<String,Value> env = new HashMap<String,Value>();
    private Environment outerEnv;

    /**
     * Constructor for global environment
     */
    public Environment() {}

    /**
     * Constructor for local environment of a function
     */
    public Environment(Environment outerEnv) {
        this.outerEnv = outerEnv;
    }

    /**
     * Handles the logic of resolving a variable.
     * If the variable name is in the current scope, it is returned.
     * Otherwise, search for the variable in the outer scope.
     * If we are at the outermost scope (AKA the global scope)
     * null is returned (similar to how JS returns undefined.
     */
    public Value resolveVar(String varName) {

        // YOUR CODE HERE

        // look for var at current scope
        if (env.containsKey(varName))
            return env.get(varName);

        Environment tempOuterEnv = this.outerEnv;
        while (tempOuterEnv != null) {
             // look for var at the outer scope
            if (tempOuterEnv.env.containsKey(varName))
                return tempOuterEnv.env.get(varName);
            
            tempOuterEnv = tempOuterEnv.outerEnv;
        }
        
        return null;
    }

    /**
     * Used for updating existing variables.
     * If a variable has not been defined previously in the current scope,
     * or any of the function's outer scopes, the var is stored in the global scope.
     */
    public void updateVar(String key, Value v) {

        // YOUR CODE HERE

        if (env.containsKey(key)) {
            env.put(key, v);
            return;
        }

        Environment tempOuterEnv = outerEnv;
        while (tempOuterEnv != null) {
            // look for var at the outer scope
            if (tempOuterEnv.env.containsKey(key)) {
                tempOuterEnv.env.put(key, v);
                return;
            }
            
            // create new var at the global scope when var is not define in any scope
            if (tempOuterEnv.outerEnv == null) {
                tempOuterEnv.env.put(key, v);
                return;
            }

            tempOuterEnv = tempOuterEnv.outerEnv;
        }
        
    }

    /**
     * Creates a new variable in the local scope.
     * If the variable has been defined in the current scope previously,
     * a RuntimeException is thrown.
     */
    public void createVar(String key, Value v) {

        // YOUR CODE HERE
        
        if(env.containsKey(key))
            throw new RuntimeException("Error: Variable already exist in the scope!");
        else
            env.put(key, v);
    }

}
