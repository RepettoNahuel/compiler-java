package lyc.compiler.files;

import java.util.*;

public class ExpressionUtils {
    // Parses expression strings and compares evaluated numeric values or string literals.
    // Input is an Object expected to be List<Object> where elements are expression representations
    // consistent with parser RESULTs (identifiers, numeric strings, combined via +,-,*,/ as infix).
    public static String equalExpressions(Object listObj) {
        if (!(listObj instanceof List)) return "false";
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) listObj;
        if (list.size() < 2) return "false";

        // Evaluate each expression to a canonical value (Double for numbers, String for strings)
        Map<Object, Integer> seen = new HashMap<>();
        for (Object o : list) {
            Object val = evaluate(o);
            Object key = canonical(val);
            int count = seen.getOrDefault(key, 0) + 1;
            if (count >= 2) return "true";
            seen.put(key, count);
        }
        return "false";
    }

    private static Object evaluate(Object expr) {
        if (expr == null) return null;
        String s = String.valueOf(expr).trim();
        // Try numeric evaluation of infix expression using a simple shunting-yard
        try {
            Double d = evalNumeric(s);
            return d;
        } catch (Exception ignore) {
        }
        // If string literal with quotes, compare as-is without quotes
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            return s.substring(1, s.length()-1);
        }
        // Identifier: try to resolve from symbol table
        lyc.compiler.files.SymbolTableGenerator.Symbol sym = lyc.compiler.files.SymbolTableGenerator.getSymbol(s);
        if (sym != null && sym.getValue() != null && !sym.getValue().isEmpty()) {
            // Try to parse symbol value as number
            try {
                return Double.valueOf(sym.getValue());
            } catch (NumberFormatException nfe) {
                return sym.getValue();
            }
        }
        // Fallback: return the string itself
        return s;
    }

    private static Object canonical(Object val) {
        if (val instanceof Double) {
            double d = (Double) val;
            // Normalize -0.0 and small eps
            if (Math.abs(d) < 1e-9) d = 0.0;
            return Double.valueOf(d);
        }
        return val;
    }

    // Very small numeric evaluator for +,-,*,/, and parentheses
    private static Double evalNumeric(String s) {
        // Replace identifiers by their values if present
        s = substituteIdentifiers(s);
        return parseExpression(new Tokenizer(s));
    }

    private static String substituteIdentifiers(String s) {
        // Split by non-identifier characters and replace tokens that are identifiers and have numeric values
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (Character.isLetter(c) || c == '_') {
                int j = i + 1;
                while (j < s.length()) {
                    char cj = s.charAt(j);
                    if (Character.isLetterOrDigit(cj) || cj == '_') j++; else break;
                }
                String id = s.substring(i, j);
                lyc.compiler.files.SymbolTableGenerator.Symbol sym = lyc.compiler.files.SymbolTableGenerator.getSymbol(id);
                if (sym != null && sym.getValue() != null && !sym.getValue().isEmpty()) {
                    out.append(sym.getValue());
                } else {
                    out.append(id);
                }
                i = j;
            } else {
                out.append(c);
                i++;
            }
        }
        return out.toString();
    }

    // Recursive descent over tokenizer
    private static Double parseExpression(Tokenizer tz) {
        double v = parseTerm(tz);
        while (tz.has() && (tz.peek() == '+' || tz.peek() == '-')) {
            char op = tz.next();
            double rhs = parseTerm(tz);
            v = (op == '+') ? v + rhs : v - rhs;
        }
        return v;
    }

    private static Double parseTerm(Tokenizer tz) {
        double v = parseFactor(tz);
        while (tz.has() && (tz.peek() == '*' || tz.peek() == '/')) {
            char op = tz.next();
            double rhs = parseFactor(tz);
            v = (op == '*') ? v * rhs : v / rhs;
        }
        return v;
    }

    private static Double parseFactor(Tokenizer tz) {
        if (!tz.has()) throw new IllegalArgumentException("Unexpected end");
        char c = tz.peek();
        if (c == '(') {
            tz.next();
            double v = parseExpression(tz);
            if (!tz.has() || tz.next() != ')') throw new IllegalArgumentException("Missing )");
            return v;
        } else if (c == '+' || c == '-') {
            char op = tz.next();
            double v = parseFactor(tz);
            return op == '-' ? -v : v;
        } else {
            String num = tz.readNumber();
            if (num.isEmpty()) throw new IllegalArgumentException("Expected number");
            return Double.valueOf(num);
        }
    }

    private static class Tokenizer {
        private final String s;
        private int i = 0;
        Tokenizer(String s) { this.s = s.replaceAll("\\s+", ""); }
        boolean has() { return i < s.length(); }
        char peek() { return s.charAt(i); }
        char next() { return s.charAt(i++); }
        String readNumber() {
            int j = i;
            if (j < s.length() && (s.charAt(j) == '+' || s.charAt(j) == '-')) j++;
            while (j < s.length()) {
                char c = s.charAt(j);
                if (Character.isDigit(c) || c == '.') j++; else break;
            }
            String sub = s.substring(i, j);
            i = j;
            if (sub.isEmpty()) return "";
            // validate
            Double.parseDouble(sub);
            return sub;
        }
    }
}
