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

        Symbol(String name, String value, String type, int length) {
            this.name = name;
            this.value = value;
            this.type = type;
            this.length = length;
        }

        Symbol(String name, String value, String type) {
            this(name, value, type, -1);
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

    static public void insertStringConstant(String name, String value, String type, int length) {
        symbolTable.put(name, new Symbol(name, value, type, length));
        System.out.println("Inserted String Constant: " + name + ", value: " + value + ", type: " + type + ", length: " + length);
    }

    static public void insertNonStringConstant(String name, String value, String type) {
        symbolTable.put(name, new Symbol(name, value, type));
        System.out.println("Inserted Non-String Constant: " + name + ", value: " + value + ", type: " + type);
    }
}
