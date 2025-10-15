package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SymbolTableGenerator implements FileGenerator{

    // Inner class to represent a symbol
    public static class Symbol {
        private String name;
        private String value;
        private String type;
        private int length; // Only used for strings

        public String getName() { return name; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public String getType() { return type; }
        public int getLength() { return length; }

        // Strings
        Symbol(String name, String value, String type, int length) {
            this.name = name;
            this.value = value;
            this.type = type;
            this.length = length;
        }
        // Non-strings
        Symbol(String name, String value, String type) {
            this.name = name;
            this.value = value;
            this.type = type;
        }
    }

    // The symbol table
    private static final Map<String, Symbol> symbolTable = new HashMap<>();
    private static final Map<String, Symbol> symbolTableDynamic = new HashMap<>();

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write("Nombre | Tipo | Valor | Longitud\n");
        for (Symbol symbol : symbolTable.values()) {
            String name = symbol.name != null ? symbol.name : " ";
            String type = symbol.type != null ? symbol.type : " ";
            String value = symbol.value != null ? symbol.value : " ";
            String length = (symbol.type != null && symbol.type.equals("CTE_STRING")) ? String.valueOf(symbol.length) : " ";
            fileWriter.write(name + " | " + type + " | " + value + " | " + length + "\n");
        }
    }

    public static void insertStringConstantDynamic(String name, String value, String type, int length) {
        symbolTableDynamic.put(name, new Symbol(name, value, type, length));
    }

    public static void insertNonStringConstantDynamic(String name, String value, String type) {
        symbolTableDynamic.put(name, new Symbol(name, value, type));
    }

    public static void insertVariableDynamic(String name, String value, String type) {
        symbolTableDynamic.put(name, new Symbol(name, value, type));
    }

    public static void insertVariablesDynamic(String list, Object type) {
        if (type == null) return;
        type = type.toString();
        if (list == null || list.isEmpty()) return;
        String[] variables = list.split(",");
        for (String var : variables) {
            insertVariableDynamic(var.trim(), "", (String)type);
        }
    }

    // Devuelve el símbolo completo por nombre
    public static Symbol getSymbolDynamic(String name) {
        return symbolTableDynamic.get(name);
    }

    public static boolean existsDynamic(String name) {
        return symbolTableDynamic.containsKey(name);
    }

    public static void insertStringConstant(String name, String value, String type, int length) {
        symbolTable.put(name, new Symbol(name, value, type, length));
    }

    public static void insertNonStringConstant(String name, String value, String type) {
        symbolTable.put(name, new Symbol(name, value, type));
    }

    public static void insertVariable(String name, String value, String type) {
        symbolTable.put(name, new Symbol(name, value, type));
    }

    public static void insertVariables(String list, Object type) {
        if (type == null) return;
        type = type.toString();
        if (list == null || list.isEmpty()) return;
        String[] variables = list.split(",");
        for (String var : variables) {
            insertVariable(var.trim(), "", (String)type);
        }
    }

    // Devuelve el símbolo completo por nombre
    public static Symbol getSymbol(String name) {
        return symbolTable.get(name);
    }

    public static boolean exists(String name) {
        return symbolTable.containsKey(name);
    }
}

