package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Hashtable;
import java.util.List;

public class AsmCodeGenerator implements FileGenerator {

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write(generateHeader());
        fileWriter.write(generateDataSection());
        fileWriter.write(generateCode());
    }

    private String generateHeader() {
        StringBuilder header = new StringBuilder();
        header.append("include macros.asm\n")
              .append("include macros2.asm\n")
              .append("include number.asm\n")
              .append("include numbers.asm\n\n")
              .append(".MODEL LARGE\n") //Modelo de Memoria 
              .append(".386\n") //Tipo de Procesador 
              .append(".STACK 200h\n\n") //Bytes en el Stack
              .append("MAXTEXTSIZE equ 50\n\n");
        return header.toString();
    }

    private String generateDataSection() {
        StringBuilder dataSection = new StringBuilder();
        dataSection.append(".DATA\n");

        for (SymbolTableGenerator.Symbol symbol : SymbolTableGenerator.getSymbolTable().values()) {        
            String name = symbol.getName();
            String type = symbol.getType();
            String value = symbol.getValue();
            int size = symbol.getLength();

            if (type.equals("String") || type.equals("CTE_STRING")) {
                if (value == null || value.equals("")){
                    dataSection.append(String.format("\t%s\tdb\tMAXTEXTSIZE dup (?), '$'\n", name));
                }
                else{
                    dataSection.append(String.format("\t%s\tdb\t%s, '$', %d dup (?)\n", name, value, 50-size));
                }
            } else if (type.equals("Float") || type.equals("CTE_FLOAT")) {
                if (value != null && !value.equals("")) {
                    dataSection.append(String.format("\t%s\tdd\t%s\n", name, value));
                } else {
                    dataSection.append(String.format("\t%s\tdd\t?\n", name));
                }
            } else if (type.equals("Integer") || type.equals("CTE_INTEGER")) {
                if (value != null && !value.equals("")) {
                    dataSection.append(String.format("\t%s\tdd\t%s\n", name, value));
                } else {
                    dataSection.append(String.format("\t%s\tdd\t?\n", name));
                }
            } else if (type.equals("Boolean") || type.equals("CTE_BOOLEAN")) {
                if (value != null && !value.equals("")) {
                    dataSection.append(String.format("\t%s\tdb\t%s\n", name, value.equals("True") ? 1 : 0));
                } else {
                    dataSection.append(String.format("\t%s\tdb\t?\n", name));
                }
            }
        }

        dataSection.append("\n\n");
        return dataSection.toString();
    }

     private String generateCode() {
        StringBuilder code = new StringBuilder();

        code.append(".CODE\n\n")
            .append("START:\n")
            .append("\tMOV AX,@DATA\n")
            .append("\tMOV DS,AX\n")
            .append("\tMOV ES,AX\n\n");
        
        code.append("\tacá meter toda la logica para generar el código assembles correspondiente a cada terceto \n\n");

        code.append("\tMOV EAX, 4C00h\n")
            .append("\tINT 21h\n\n")
            .append("END START\n");

        return code.toString();
    }
}
