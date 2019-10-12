package edu.sjsu.fwjs;

import java.util.ArrayList;
import java.util.List;

/**
 * FWJS expressions.
 */
public interface Expression {
	/**
	 * Evaluate the expression in the context of the specified environment.
	 */
	public Value evaluate(Environment env);
}

// NOTE: Using package access so that all implementations of Expression
// can be included in the same file.

/**
 * FWJS constants.
 */
class ValueExpr implements Expression {
	private Value val;

	public ValueExpr(Value v) {
		this.val = v;
	}

	public Value evaluate(Environment env) {
		return this.val;
	}
}

/**
 * Expressions that are a FWJS variable.
 */
class VarExpr implements Expression {
	private String varName;

	public VarExpr(String varName) {
		this.varName = varName;
	}

	public Value evaluate(Environment env) {
		return env.resolveVar(varName);
	}
}

/**
 * A print expression.
 */
class PrintExpr implements Expression {
	private Expression exp;

	public PrintExpr(Expression exp) {
		this.exp = exp;
	}

	public Value evaluate(Environment env) {
		Value v = exp.evaluate(env);
		System.out.println(v.toString());
		return v;
	}
}

/**
 * Binary operators (+, -, *, etc). Currently only numbers are supported.
 */
class BinOpExpr implements Expression {
	private Op op;
	private Expression e1;
	private Expression e2;

	public BinOpExpr(Op op, Expression e1, Expression e2) {
		this.op = op;
		this.e1 = e1;
		this.e2 = e2;
	}

	@SuppressWarnings("incomplete-switch")
	public Value evaluate(Environment env) {
		// YOUR CODE HERE
		Value v1 = e1.evaluate(env);
		Value v2 = e2.evaluate(env);
		IntVal r;
		switch (op) {
		case ADD:
			r = new IntVal(Integer.parseInt(v1.toString()) + Integer.parseInt(v2.toString()));
			return (Value) r;

		case SUBTRACT:
			r = new IntVal(Integer.parseInt(v1.toString()) - Integer.parseInt(v2.toString()));
			return (Value) r;

		case MULTIPLY:
			r = new IntVal(Integer.parseInt(v1.toString()) * Integer.parseInt(v2.toString()));
			return (Value) r;

		case DIVIDE:
			r = new IntVal(Integer.parseInt(v1.toString()) / Integer.parseInt(v2.toString()));
			return (Value) r;

		case EQ:
			return new BoolVal(v1.equals(v2));

		case GE:
			return new BoolVal(Integer.parseInt(v1.toString()) >= Integer.parseInt(v2.toString()));

		case GT:
			return new BoolVal(Integer.parseInt(v1.toString()) > Integer.parseInt(v2.toString()));
		case LE:
			return new BoolVal(Integer.parseInt(v1.toString()) <= Integer.parseInt(v2.toString()));
		case LT:
			return new BoolVal(Integer.parseInt(v1.toString()) < Integer.parseInt(v2.toString()));
		case MOD:
			r = new IntVal(Integer.parseInt(v1.toString()) % Integer.parseInt(v2.toString()));
			return (Value) r;
		}
		return v1;

	}
}

/**
 * If-then-else expressions. Unlike JS, if expressions return a value.
 */
class IfExpr implements Expression {
	private Expression cond;
	private Expression thn;
	private Expression els;

	public IfExpr(Expression cond, Expression thn, Expression els) {
		this.cond = cond;
		this.thn = thn;
		this.els = els;
	}

	// Evaluate condition, if equals a BoolVal(true) then evaluate thn, if equals BoolVal(false) evaluates els,
	// if it equals some other type of Value, throws a RuntimeException
	public Value evaluate(Environment env) {
		Value cond_result = cond.evaluate(env);
		if(cond_result.equals(new BoolVal(true))) {
			return thn.evaluate(env);
		}else if(cond_result.equals(new BoolVal(false))){
			return els.evaluate(env);
		}else {
			throw new RuntimeException("Error: Invalid condition value!");
		}
	}
}

/**
 * While statements (treated as expressions in FWJS, unlike JS).
 */
class WhileExpr implements Expression {
	private Expression cond;
	private Expression body;

	public WhileExpr(Expression cond, Expression body) {
		this.cond = cond;
		this.body = body;
	}

	// Evaluate condition, checks if it is an instance of BoolVal, otherwise throws a runtime exception
	public Value evaluate(Environment env) {
		Value result = new NullVal();
		Value condition = cond.evaluate(env);
		if(condition instanceof BoolVal) {
			
			// Evaluates body until condition returns a BoolVal(true)
			while(condition.equals(new BoolVal(true))) {
				result = body.evaluate(env);
				condition = cond.evaluate(env);
			}
			return result;
		}else {
			throw new RuntimeException("Invalid condition value!");
		}

	}
}

/**
 * Sequence expressions (i.e. 2 back-to-back expressions).
 */
class SeqExpr implements Expression {
	private Expression e1;
	private Expression e2;

	public SeqExpr(Expression e1, Expression e2) {
		this.e1 = e1;
		this.e2 = e2;
	}

	// Evaluates both expressions in order, return Value of 2nd expression
	public Value evaluate(Environment env) {
		e1.evaluate(env);
		return e2.evaluate(env);

	}
}

/**
 * Declaring a variable in the local scope.
 */
class VarDeclExpr implements Expression {
	private String varName;
	private Expression exp;

	public VarDeclExpr(String varName, Expression exp) {
		this.varName = varName;
		this.exp = exp;
	}

	// Evaluates the expression and create a var in local env with the new value
	// Return the value
	public Value evaluate(Environment env) {
		Value v = exp.evaluate(env);
		env.createVar(varName, v);
		return v;
	}
}

/**
 * Updating an existing variable. If the variable is not set already, it is
 * added to the global scope.
 */
class AssignExpr implements Expression {
	private String varName;
	private Expression e;

	public AssignExpr(String varName, Expression e) {
		this.varName = varName;
		this.e = e;
	}
	
	// Updates the var with result from evaluating expression
	public Value evaluate(Environment env) {
		Value result = e.evaluate(env);
		env.updateVar(varName, result);
		return result;
	}
}

/**
 * A function declaration, which evaluates to a closure.
 */
class FunctionDeclExpr implements Expression {
	private List<String> params;
	private Expression body;

	public FunctionDeclExpr(List<String> params, Expression body) {
		this.params = params;
		this.body = body;
	}

	// Returns a ClosureVal value containing function definition body, parameters, 
	// and the env passed into the function is the environment outside the function definition
	public Value evaluate(Environment env) {
		
		return new ClosureVal(params, body, env);
	}
}

/**
 * Function application.
 */
class FunctionAppExpr implements Expression {
	private Expression f;
	private List<Expression> args;

	public FunctionAppExpr(Expression f, List<Expression> args) {
		this.f = f;
		this.args = args;
	}
	
	// Evaluates function definition f to get a ClosureVal value
	// Construct a List<Value> containings the values obtained from evaluating each expression 
	// in List<Expression> args
	// Use apply method from ClosureVal to apply the values to each parameters in the function
	// and return the value obtain from apply
	public Value evaluate(Environment env) {
		ClosureVal result = (ClosureVal) f.evaluate(env);
		List<Value> vals = new ArrayList<Value>();
		for(int i = 0; i < args.size(); i++) {
			vals.add(args.get(i).evaluate(env));
		}
		return result.apply(vals);

	}
}
