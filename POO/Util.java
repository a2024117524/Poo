import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Util {
    public static LocalDate strToDate(String s) {
        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
    public static String formatMoeda(double v) {
        return String.format(Locale.US, "%.2f €", v);
    }
}