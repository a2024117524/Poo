import java.io.Serializable;
import java.time.LocalDate;

// Classe que regista atos veterinários feitos a um animal
public class Veterinario implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate data;      // Data do ato veterinário
    private String descricao;    // Descrição do ato

    // Construtor principal
    public Veterinario(LocalDate data, String descricao) {
        this.data = data;
        this.descricao = descricao;
    }

    // Métodos de acesso
    public LocalDate getData() { return data; }
    public String getDescricao() { return descricao; }

    // Representação textual do ato veterinário
    @Override
    public String toString() {
        return descricao + " (" + data + ")";
    }
}