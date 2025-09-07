package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SymbolTableGenerator implements FileGenerator{

    // Inner class to represent a symbol
    private static class Symbol {
        String name;
        String value;
        String type;
        int length; // Only used for strings

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
}

