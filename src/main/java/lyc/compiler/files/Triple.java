package lyc.compiler.files;

public class Triple {
    private String operator;
    private Object operand1;
    private Object operand2;

    public Triple(String operator, Object operand1, Object operand2) {
        this.operator = operator;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public String toString() {
        return "[" + operator + ", " + operandToString(operand1) + ", " + operandToString(operand2) + "]";
    }

    private String operandToString(Object operand) {
        return operand == null ? "_" : operand.toString();
    }
}
