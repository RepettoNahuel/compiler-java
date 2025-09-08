package lyc.compiler.files;

public class GeometryUtils {
    // Calcula el área de un triángulo dado como Object[]{p1, p2, p3}, donde cada punto es Object[]{x, y}
    public static double calculateTriangleArea(Object triangleObj) {
        if (!(triangleObj instanceof Object[])) return 0.0;
        Object[] triangle = (Object[]) triangleObj;
        if (triangle.length != 3) return 0.0;
        double[][] points = new double[3][2];
        for (int i = 0; i < 3; i++) {
            Object[] point = (Object[]) triangle[i];
            points[i][0] = parseToDouble(point[0]);
            points[i][1] = parseToDouble(point[1]);
        }
        // Fórmula del área de triángulo dada por coordenadas
        double area = Math.abs(
            points[0][0]*(points[1][1]-points[2][1]) +
            points[1][0]*(points[2][1]-points[0][1]) +
            points[2][0]*(points[0][1]-points[1][1])
        ) / 2.0;
        return area;
    }

    // Convierte variable/constante a double
    private static double parseToDouble(Object val) {
        if (val == null) return 0.0;
        try {
            if (val instanceof String) {
                String s = (String) val;
                // Si es variable, busca su valor en la tabla de símbolos
                lyc.compiler.files.SymbolTableGenerator.Symbol symbol = lyc.compiler.files.SymbolTableGenerator.getSymbol(s);
                if (symbol != null && symbol.getValue() != null && !symbol.getValue().isEmpty()) {
                    s = symbol.getValue();
                }
                return Double.parseDouble(s);
            }
            if (val instanceof Number) {
                return ((Number) val).doubleValue();
            }
        } catch (Exception e) {
            return 0.0;
        }
        return 0.0;
    }
}
