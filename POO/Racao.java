import java.io.Serializable;

public class Racao implements Serializable {
    private static final long serialVersionUID = 1L;

    private String marca, especie;
    private int idadeMin, idadeMax;
    private double precoPorKg;

    public Racao(String marca, String especie, int idadeMin, int idadeMax, double precoPorKg) {
        this.marca = marca;
        this.especie = especie;
        this.idadeMin = idadeMin;
        this.idadeMax = idadeMax;
        this.precoPorKg = precoPorKg;
    }

    public String getMarca() { return marca; }
    public String getEspecie() { return especie; }
    public int getIdadeMin() { return idadeMin; }
    public int getIdadeMax() { return idadeMax; }
    public double getPrecoPorKg() { return precoPorKg; }
}