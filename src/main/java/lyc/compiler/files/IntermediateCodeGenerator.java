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
}
