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

    public void addTerceto(String op, Object arg1, Object arg2) {
        if(arg1 != null)
            arg1 = arg1.toString();
        if(arg2 != null)
            arg2 = arg2.toString();
        tercetos.add(new String[]{op, (String)arg1, (String)arg2});
    }

    public void addTerceto(String op, Object arg1, Object arg2, Object result) {
        if(arg1 != null)
            arg1 = arg1.toString();
        if(arg2 != null)
            arg2 = arg2.toString();
        if(result != null)
            result = result.toString();
        tercetos.add(new String[]{op, (String)arg1, (String)arg2, (String)result});
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        for(String[] t : tercetos) {
            fileWriter.write(String.join(", ", t) + "\n");
        }
    }
}
