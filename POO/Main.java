import java.util.Scanner;

// Classe principal com o ponto de entrada do programa (main)
public class Main {
    public static void main(String[] args) {
        Abrigo abrigo = Abrigo.carregarDeFicheiro("abrigo.dat"); // Carrega dados do ficheiro ou inicia novo abrigo
        Scanner sc = new Scanner(System.in);

        // Ciclo principal do menu
        while (true) {
            System.out.println("\n--- MENU ABRIGO ---");
            System.out.println("1. Adicionar animal");
            System.out.println("2. Adoção de animal");
            System.out.println("3. Registrar óbito");
            System.out.println("4. Registrar ato veterinário");
            System.out.println("5. Listagens/Consultas");
            System.out.println("6. Exportar relatórios texto");
            System.out.println("0. Sair");
            System.out.print("Opção: ");
            String op = sc.nextLine();

            switch (op) {
                case "1": abrigo.adicionarAnimal(sc); break;
                case "2": abrigo.adoptarAnimal(sc); break;
                case "3": abrigo.animalMorreu(sc); break;
                case "4": abrigo.registrarAtoVeterinario(sc); break;
                case "5": abrigo.menuConsultas(sc); break;
                case "6": abrigo.gerarRelatoriosTxt(); break;
                case "0": 
                    abrigo.guardarEmFicheiro("abrigo.dat");
                    System.out.println("A sair...");
                    return;
                default: System.out.println("Opção inválida!");
            }
        }
    }
}