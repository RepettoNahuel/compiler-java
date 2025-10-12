package lyc.compiler.files;

import java.util.ArrayList;
import java.util.List;

public class TriplesGenerator {
    private static List<Triple> triples = new ArrayList<>();

    public static int createTriple(String op, Object operand1, Object operand2) {
        triples.add(new Triple(op, operand1, operand2));
        return triples.size() - 1;
    }
}
   