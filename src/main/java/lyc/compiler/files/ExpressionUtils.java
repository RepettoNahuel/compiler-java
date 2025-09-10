package lyc.compiler.files;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExpressionUtils {
    // Determina si en una lista de expresiones matemáticas existe más de una expresión que sea equivalente a otra
    public static boolean equalExpressions(List<String> expressions) {
        Set<Double> evaluated = new HashSet<>();

        for (String expr : expressions) {
        String valueStr = expr;

        // Buscar si es variable
        var symbol = lyc.compiler.files.SymbolTableGenerator.getSymbol(expr);
        if (symbol != null && symbol.getValue() != null) {
            valueStr = symbol.getValue();
        }

        // Evaluar numéricamente (ejemplo: convertir directamente si es número)
        try {
            Double value = Double.parseDouble(valueStr);

            if (evaluated.contains(value)) {
                return true;
            }
            evaluated.add(value);
        } catch (NumberFormatException e) {
            System.err.println("No se pudo evaluar: " + valueStr);
        }
    }

        return false;
    }
}
