import java.io.Serializable;

// Classe que representa uma ração disponível no abrigo
public class Racao implements Serializable {
    private static final long serialVersionUID = 1L;

    private String marca, especie; // Marca da ração e espécie a que se destina
    private int idadeMin, idadeMax; // Faixa etária recomendada
    private double precoPorKg; // Preço por quilo

    // Construtor principal
    public Racao(String marca, String especie, int idadeMin, int idadeMax, double precoPorKg) {
        this.marca = marca;
        this.especie = especie;
        this.idadeMin = idadeMin;
        this.idadeMax = idadeMax;
        this.precoPorKg = precoPorKg;
    }

    // Métodos de acesso (getters)
    public String getMarca() { return marca; }
    public String getEspecie() { return especie; }
    public int getIdadeMin() { return idadeMin; }
    public int getIdadeMax() { return idadeMax; }
    public double getPrecoPorKg() { return precoPorKg; }
}