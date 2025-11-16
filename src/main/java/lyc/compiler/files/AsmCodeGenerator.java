package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        Map<Integer, String> mapaAux = new HashMap<>();
        Map<Integer, String> mapaEtiAux = new HashMap<>();
        int nroAux = 0;
        int nroAux2 = 0;
        String varAux;
        String etiAux;

        String aux1;
        String aux2;
        int key;
        String value;
        String nuevaEti;

                 
        code.append(".CODE\n\n")
            .append("START:\n")
            .append("\tMOV AX,@DATA\n")
            .append("\tMOV DS,AX\n")
            .append("\tMOV ES,AX\n\n");       
        
        for (Map.Entry<Integer, String[]> entry : IntermediateCodeGenerator.getTercetosMap().entrySet()) {
            int index = entry.getKey();
            String[] t = entry.getValue();

            String op = t[0];
            String a1 = t[1];
            String a2 = t[2];          

            if (a1 != null && a1.length() >= 2 && a1.charAt(0) == '[' && a1.charAt(a1.length() - 1) == ']') { 
                key = Integer.parseInt(a1.substring(1, a1.length() - 1));
                value = mapaAux.get(key);

                if (value != null) {          
                    aux1 = value;
                } else {
                    aux1 = a1;              
                }
            }
            else{
                aux1 = a1;
            }

            if (a2 != null && a2.length() >= 2 && a2.charAt(0) == '[' && a2.charAt(a2.length() - 1) == ']') { 
                key = Integer.parseInt(a2.substring(1, a2.length() - 1));
                value = mapaAux.get(key);

                if (value != null) {          
                    aux2 = value;
                } else {
                    aux2 = a2;              
                }
            }
            else{
                aux2 = a2;
            }

            //code.append("\tTERCETO ->[" + index + "]\n\n");  

            nuevaEti = mapaEtiAux.get(index);
            if (nuevaEti != null) {          
                code.append(nuevaEti + ":\n\n"); 
            } 

            switch (op) {
                case "ADD":       
                    nroAux++;                    
                    varAux = "@aux" + nroAux;        
                    code.append("\tMOV R1, " + aux1 + "\n");
                    code.append("\tADD R1, " + aux2 + "\n");
                    code.append("\tMOV " + varAux + ", R1\n\n");       
                    mapaAux.put(index, varAux);           
                    break;

                case "SUB":
                    nroAux++;                    
                    varAux = "@aux" + nroAux;        
                    code.append("\tMOV R1, " + aux1 + "\n");
                    code.append("\tSUB R1, " + aux2 + "\n");
                    code.append("\tMOV " + varAux + ", R1\n\n");    
                    mapaAux.put(index, varAux);               
                    break;

                case "MULT":
                    nroAux++;                    
                    varAux = "@aux" + nroAux;        
                    code.append("\tMOV R1, " + aux1 + "\n");
                    code.append("\tMUL R1, " + aux2 + "\n");
                    code.append("\tMOV " + varAux + ", R1\n\n");  
                    mapaAux.put(index, varAux);                 
                    break;

                case "DIV":
                    nroAux++;                    
                    varAux = "@aux" + nroAux;        
                    code.append("\tMOV R1, " + aux1 + "\n");
                    code.append("\tDIV R1, " + aux2 + "\n");
                    code.append("\tMOV " + varAux + ", R1\n\n");  
                    mapaAux.put(index, varAux);                 
                    break;

                case "ASSIGN":    
                    code.append("\tMOV R1, " + aux2 + "\n");
                    code.append("\tMOV " + aux1 + ", R1\n\n"); 
                    break;

                case "CMP":
                    code.append("\tCMP " + aux1 + ", " + aux2 + "\n");      
                    break;

                case "BGE":
                    if (aux1.length() >= 2 && aux1.charAt(0) == '[' && aux1.charAt(aux1.length() - 1) == ']') {
                        
                        etiAux = mapaEtiAux.get(Integer.parseInt(aux1.substring(1, aux1.length() - 1)));
                        if (etiAux != null) {  
                            nroAux2++;
                            etiAux = "ETIQUETA" + nroAux2;
                            mapaEtiAux.put(Integer.parseInt(aux1.substring(1, aux1.length() - 1)), etiAux);  
                        } 
                        aux1 = etiAux;                      
                    }
                    code.append("\tJNB " + aux1 + "\n\n");
                    break;

                case "BLE":
                    if (aux1.length() >= 2 && aux1.charAt(0) == '[' && aux1.charAt(aux1.length() - 1) == ']') {
                        
                        etiAux = mapaEtiAux.get(Integer.parseInt(aux1.substring(1, aux1.length() - 1)));
                        if (etiAux == null) {  
                            nroAux2++;
                            etiAux = "ETIQUETA" + nroAux2;
                            mapaEtiAux.put(Integer.parseInt(aux1.substring(1, aux1.length() - 1)), etiAux);  
                        } 
                        aux1 = etiAux;                      
                    }
                    code.append("\tJBE " + aux1 + "\n\n");
                    break;

                case "BGT":
                    if (aux1.length() >= 2 && aux1.charAt(0) == '[' && aux1.charAt(aux1.length() - 1) == ']') {
                        
                        etiAux = mapaEtiAux.get(Integer.parseInt(aux1.substring(1, aux1.length() - 1)));
                        if (etiAux == null) {  
                            nroAux2++;
                            etiAux = "ETIQUETA" + nroAux2;
                            mapaEtiAux.put(Integer.parseInt(aux1.substring(1, aux1.length() - 1)), etiAux);  
                        } 
                        aux1 = etiAux;                      
                    }
                    code.append("\tJNBE " + aux1 + "\n\n");
                    break;

                case "BLT":
                   if (aux1.length() >= 2 && aux1.charAt(0) == '[' && aux1.charAt(aux1.length() - 1) == ']') {
                        
                        etiAux = mapaEtiAux.get(Integer.parseInt(aux1.substring(1, aux1.length() - 1)));
                        if (etiAux == null) {  
                            nroAux2++;
                            etiAux = "ETIQUETA" + nroAux2;
                            mapaEtiAux.put(Integer.parseInt(aux1.substring(1, aux1.length() - 1)), etiAux);  
                        } 
                        aux1 = etiAux;                      
                    }
                    code.append("\tJNAE " + aux1 + "\n\n");
                    break;

                case "BNE":
                    if (aux1.length() >= 2 && aux1.charAt(0) == '[' && aux1.charAt(aux1.length() - 1) == ']') {
                        
                        etiAux = mapaEtiAux.get(Integer.parseInt(aux1.substring(1, aux1.length() - 1)));
                        if (etiAux == null) {  
                            nroAux2++;
                            etiAux = "ETIQUETA" + nroAux2;
                            mapaEtiAux.put(Integer.parseInt(aux1.substring(1, aux1.length() - 1)), etiAux);  
                        } 
                        aux1 = etiAux;                      
                    }
                    code.append("\tJNE " + aux1 + "\n\n");
                    break;

                case "BEQ":
                    if (aux1.length() >= 2 && aux1.charAt(0) == '[' && aux1.charAt(aux1.length() - 1) == ']') {
                        
                        etiAux = mapaEtiAux.get(Integer.parseInt(aux1.substring(1, aux1.length() - 1)));
                        if (etiAux == null) {  
                            nroAux2++;
                            etiAux = "ETIQUETA" + nroAux2;
                            mapaEtiAux.put(Integer.parseInt(aux1.substring(1, aux1.length() - 1)), etiAux);  
                        } 
                        aux1 = etiAux;                      
                    }
                    code.append("\tJE " + aux1 + "\n\n");
                    break;

                case "BI":  
                    if (aux1.length() >= 2 && aux1.charAt(0) == '[' && aux1.charAt(aux1.length() - 1) == ']') {
                        
                        etiAux = mapaEtiAux.get(Integer.parseInt(aux1.substring(1, aux1.length() - 1)));
                        if (etiAux == null) {  
                            nroAux2++;
                            etiAux = "ETIQUETA" + nroAux2;
                            mapaEtiAux.put(Integer.parseInt(aux1.substring(1, aux1.length() - 1)), etiAux);  
                        } 
                        aux1 = etiAux;                      
                    }
                    code.append("\tJMP " + aux1 + "\n\n");
                    break;

                case "ET":
                    nroAux2++;
                    etiAux = "ETIQUETA" + nroAux2;
                    code.append("\n" + etiAux + ":\n\n"); 
                    mapaAux.put(index, etiAux);
                    break;

                case "WRITE":
                    SymbolTableGenerator.Symbol sym_aux = SymbolTableGenerator.getSymbol(aux1);
                   
                    if (sym_aux == null) {
                        code.append("\t; WARNING: " + aux1 + " no está en la tabla de símbolos\n");
                        break;
                    }

                    switch (sym_aux.getType()) {
                        case "String" -> code.append("\tDisplayString " + aux1 + "\n\n"); 
                        case "CTE_STRING" -> code.append("\tDisplayString " + aux1 + "\n\n");  
                        case "Integer" -> code.append("\tDisplayFloat " + aux1 + "\n\n");  
                        case "CTE_INTEGER" -> code.append("\tDisplayFloat " + aux1 + "\n\n");  
                        case "Float" -> code.append("\tDisplayFloat " + aux1 + "\n\n"); 
                        case "CTE_FLOAT" -> code.append("\tDisplayFloat " + aux1 + "\n\n");  
                    }

                    break;

                case "READ":
                    SymbolTableGenerator.Symbol sym_aux2 = SymbolTableGenerator.getSymbol(aux1);
                   
                    if (sym_aux2 == null) {
                        code.append("\t; WARNING: " + aux1 + " no está en la tabla de símbolos\n");
                        break;
                    }

                    switch (sym_aux2.getType()) {
                        case "String" -> code.append("\tGetString " + aux1 + "\n\n"); 
                        case "CTE_STRING" -> code.append("\tGetString " + aux1 + "\n\n");  
                        case "Integer" -> code.append("\tGetFloat " + aux1 + "\n\n");  
                        case "CTE_INTEGER" -> code.append("\tGetFloat " + aux1 + "\n\n");  
                        case "Float" -> code.append("\tGetFloat " + aux1 + "\n\n"); 
                        case "CTE_FLOAT" -> code.append("\tGetFloat " + aux1 + "\n\n");  
                    }

                    break;
                
                case "POINT":                   
                    // No genera código ensamblador
                    break;

                default:
                    code.append("\t[")
                        .append(index)
                        .append("] = (")
                        .append(op).append(", ")
                        .append(a1).append(", ")
                        .append(a2).append(")\n\n"); 
                    break;
            }
        }
        
        code.append("\n\tMOV EAX, 4C00h\n")
            .append("\tINT 21h\n\n")
            .append("END START\n");

        return code.toString();
    }
}
