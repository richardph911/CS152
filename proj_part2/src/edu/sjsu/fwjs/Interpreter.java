package edu.sjsu.fwjs;

public class Interpreter {

    public static void main(String[] args) throws Exception {
        Expression prog = new BinOpExpr(Op.ADD,
                new ValueExpr(new IntVal(3)),
                new ValueExpr(new IntVal(4)));
        System.out.println("'3 + 4;' evaluates to " + prog.evaluate(new Environment()));
    }
}
