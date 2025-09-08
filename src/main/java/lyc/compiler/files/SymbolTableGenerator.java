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

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        for (Symbol symbol : symbolTable.values()) {
            fileWriter.write(symbol.name + " " + symbol.type + " " + symbol.value + "\n");
        }
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

    public static void insertVariables(String list, String type) {
        if (list == null || list.isEmpty()) return;
        String[] variables = list.split(",");
        for (String var : variables) {
            insertVariable(var.trim(), "", type);
        }
    }

    // Devuelve el símbolo completo por nombre
    public static Symbol getSymbol(String name) {
        return symbolTable.get(name);
    }
}

