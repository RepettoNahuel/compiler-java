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
    public static Object resolverTerceto(String terc) {

        int index = Integer.parseInt(terc.replaceAll("[\\[\\]]", ""));

        String[] terceto = tercetosMap.get(index);
        if (terceto == null) {
            throw new IllegalArgumentException("No existe el terceto [" + index + "]");
        }

        String op = terceto[0];
        String arg1 = terceto[1];
        String arg2 = terceto[2];

        // Resolver operandos recursivamente
        Object val1 = obtenerValor(arg1);
        Object val2 = obtenerValor(arg2);

        switch (op) {
            case "ADD": return toNumber(val1) + toNumber(val2);
            case "SUB": return toNumber(val1) - toNumber(val2);
            case "MULT": return toNumber(val1) * toNumber(val2);
            case "DIV": return toNumber(val1) / toNumber(val2);
            default:
                throw new UnsupportedOperationException("Operador no soportado: " + op);
        }
    }

    private static Object obtenerValor(String arg) {
        if (arg == null) return 0;

        // Caso: referencia a otro terceto -> "[23]"
        if (arg.matches("\\[\\d+\\]")) {
            return resolverTerceto(arg);
        }

        // Caso: número literal
        if (arg.matches("-?\\d+(\\.\\d+)?")) {
            return arg.contains(".") ? Float.parseFloat(arg) : Integer.parseInt(arg);
        }

        // Caso: variable -> buscar en la tabla de símbolos
        SymbolTableGenerator.Symbol sym = SymbolTableGenerator.getSymbol(arg);
        if (sym != null && sym.getValue() != null) {
            return sym.getValue();
        }

        // Si no se encontró valor, retornar el nombre literal
        return arg;
    }

    private static float toNumber(Object val) {
        if (val instanceof Number) return ((Number) val).floatValue();
        try {
            return Float.parseFloat(val.toString());
        } catch (Exception e) {
            throw new RuntimeException("No se puede convertir a número: " + val);
        }
    }
}
