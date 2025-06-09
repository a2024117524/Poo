import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// Classe utilitária para funções auxiliares
public class Util {
    // Converte uma string para LocalDate no formato ISO (YYYY-MM-DD)
    public static LocalDate strToDate(String s) {
        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return LocalDate.now(); // Se falhar, retorna a data atual
        }
    }
    // Formata um valor double como moeda (com símbolo do euro)
    public static String formatMoeda(double v) {
        return String.format(Locale.US, "%.2f €", v);
    }
}