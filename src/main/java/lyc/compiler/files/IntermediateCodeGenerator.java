package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class IntermediateCodeGenerator implements FileGenerator {

    private static final Map<Integer, String[]> tercetosMap = new HashMap<>();
    private static int tercNumber = 0;
    private static final Stack<Integer> pilaSaltos = new Stack<>();

    public static String addTerceto(String op, Object arg1, Object arg2) {
        String sArg1 = arg1 != null ? arg1.toString() : null;
        String sArg2 = arg2 != null ? arg2.toString() : null;
        Integer key = tercNumber;
        tercetosMap.put(key, new String[]{op, sArg1, sArg2});
        tercNumber++;
        return "[" + key.toString() + "]";
    }

    public static int getLastIndex() {
        return tercNumber - 1;
    }

    public static void pushSalto(int index) {
        pilaSaltos.push(index);
    }

    public static int popSalto() {
        return pilaSaltos.pop();
    }

    public static void completarSalto(int index, int destino) {
        String[] terceto = tercetosMap.get(index);
        if (terceto != null) {
            terceto[2] = "[" + destino + "]";
        }
    }

    public static int getNextIndex() {
        return tercNumber;
    }

    public static boolean isSaltoEmpty() {
        return pilaSaltos.isEmpty();
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        for (Map.Entry<Integer, String[]> entry : tercetosMap.entrySet()) {
            Integer key = entry.getKey();
            String[] t = entry.getValue();
            String line = String.format("[%d] = (%s, %s, %s)\n", key, t[0], t[1], t[2]);
            fileWriter.write(line);
        }
    }

    ////Para obetenr valor de tercetos////

    public static class ResultadoTerceto {
        public String expresion;
        public double valor;

        public ResultadoTerceto(String expresion, double valor) {
            this.expresion = expresion;
            this.valor = valor;
        }
    }

    public static ResultadoTerceto resolverTerceto(String terc) {

        int index = Integer.parseInt(terc.substring(1, terc.length() - 1));

        String[] terceto = tercetosMap.get(index);
        if (terceto == null) {
            throw new IllegalArgumentException("No existe el terceto [" + index + "]");
        }

        String op = terceto[0];
        String a1 = terceto[1];
        String a2 = terceto[2];

        // Resolver operandos recursivamente
        ResultadoTerceto v1 = argComoTexto(a1);
        ResultadoTerceto v2 = argComoTexto(a2);

        double valor;
        String expresion;

        switch (op) {
            case "ADD":
                valor = v1.valor + v2.valor;
                expresion = "(" + v1.expresion + " + " + v2.expresion + ")";
                break;
            case "SUB":
                valor = v1.valor - v2.valor;
                expresion = "(" + v1.expresion + " - " + v2.expresion + ")";
                break;
            case "MULT":
                valor = v1.valor * v2.valor;
                expresion = "(" + v1.expresion + " * " + v2.expresion + ")";
                break;
            case "DIV":
                valor = v1.valor / v2.valor;
                expresion = "(" + v1.expresion + " / " + v2.expresion + ")";
                break;
            default:
                throw new IllegalArgumentException("Operador no soportado: " + op);
        }

        return new ResultadoTerceto(expresion, valor);
    }

    private static ResultadoTerceto argComoTexto(String arg) {
        if (arg == null || arg.isEmpty()) {
            return new ResultadoTerceto("", 0.0);
        }

        // Caso: referencia a otro terceto -> "[23]"
        if (arg.length() >= 2 && arg.charAt(0) == '[' && arg.charAt(arg.length() - 1) == ']') { 
            return resolverTerceto(arg);
        }

        // Caso: valor existente en la tabla de símbolos
        SymbolTableGenerator.Symbol sym_arg = SymbolTableGenerator.getSymbol(arg);
        if (sym_arg != null) {
            String argStr = (String) sym_arg.getValue(); 
            double num = Double.parseDouble(argStr);
            return new ResultadoTerceto(arg, num);
        }

        // Caso: constante numérica directa (int o float)
        try {
            double num = Double.parseDouble(arg);
            return new ResultadoTerceto(arg, num);
        } catch (NumberFormatException e) {
            // Si no es número y tampoco está en tabla, lo devolvemos como literal sin valor numérico
            return new ResultadoTerceto(arg, 0.0);
        }
    }
}
