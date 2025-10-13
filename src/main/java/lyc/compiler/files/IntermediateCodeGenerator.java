package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IntermediateCodeGenerator implements FileGenerator {

    private List<String[]> tercetos = new ArrayList<>();
    private int tempCount = 0;

    public String newTemp() {
        return "T" + (tempCount++);
    }

    public void addTerceto(String op, String arg1, String arg2) {
        tercetos.add(new String[]{op, arg1, arg2});
    }

    public void addTerceto(String op, String arg1, String arg2, String result) {
        tercetos.add(new String[]{op, arg1, arg2, result});
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        for(String[] t : tercetos) {
            fileWriter.write(String.join(", ", t) + "\n");
        }
    }
}
