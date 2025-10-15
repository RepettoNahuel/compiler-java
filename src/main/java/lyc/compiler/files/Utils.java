package lyc.compiler.files;

public class Utils {

    //constructor
    public Utils() {}

    public static boolean isBoolean (Object obj) {
        if (obj == null) return false;
        String s = obj.toString();
        return "true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s);
    }
}
