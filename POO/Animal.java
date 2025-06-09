import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Animal implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome, especie, genero, raca, observacoes, adoptante;
    private double peso;
    private int idade;
    private LocalDate dataEntrada, dataAdopcao, dataObito;
    private boolean esterilizado = false;
    private List<Veterinario> atosVeterinarios = new ArrayList<>();

    public Animal(String nome, String especie, String genero, double peso, String raca, int idade, String observacoes) {
        this.nome = nome; this.especie = especie; this.genero = genero;
        this.peso = peso; this.raca = raca; this.idade = idade;
        this.dataEntrada = LocalDate.now();
        this.observacoes = observacoes;
    }

    public String getNome() { return nome; }
    public String getEspecie() { return especie; }
    public int getIdade() { return idade; }
    public double getPeso() { return peso; }
    public String getRaca() { return raca; }
    public String getObservacoes() { return observacoes; }
    public String getGenero() { return genero; }
    public String getAdoptante() { return adoptante; }
    public LocalDate getDataAdopcao() { return dataAdopcao; }
    public LocalDate getDataObito() { return dataObito; }
    public boolean isEsterilizado() { return esterilizado; }
    public void setEsterilizado(boolean e) { this.esterilizado = e; }
    public void setAdoptante(String nome) { this.adoptante = nome; }
    public void setDataAdopcao(LocalDate data) { this.dataAdopcao = data; }
    public void setDataObito(LocalDate data) { this.dataObito = data; }
    public List<Veterinario> getAtosVeterinarios() { return atosVeterinarios; }
    public void adicionarAtoVeterinario(Veterinario v) { atosVeterinarios.add(v); observacoes += "\n" + v.getDescricao() + " (" + v.getData() + ")"; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nome).append(" - ").append(especie).append(" - ").append(genero)
                .append(" - ").append(peso).append("kg - ").append(raca)
                .append(" - ").append(idade).append(" anos\n")
                .append("Entrada: ").append(dataEntrada).append("\n")
                .append("Obs: ").append(observacoes).append("\n");
        if (adoptante != null) sb.append("Adoptado por: ").append(adoptante).append(" em ").append(dataAdopcao).append("\n");
        if (dataObito != null) sb.append("Óbito: ").append(dataObito).append("\n");
        sb.append("Esterilizado: ").append(esterilizado ? "Sim" : "Não").append("\n");
        if (!atosVeterinarios.isEmpty()) {
            sb.append("Intervenções veterinárias:\n");
            atosVeterinarios.forEach(v -> sb.append("  - ").append(v).append("\n"));
        }
        return sb.toString();
    }
}