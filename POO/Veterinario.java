import java.io.Serializable;
import java.time.LocalDate;

public class Veterinario implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate data;
    private String descricao;

    public Veterinario(LocalDate data, String descricao) {
        this.data = data;
        this.descricao = descricao;
    }
    public LocalDate getData() { return data; }
    public String getDescricao() { return descricao; }

    @Override
    public String toString() {
        return descricao + " (" + data + ")";
    }
}