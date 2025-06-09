import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Abrigo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> capacidadePorEspecie = new HashMap<>();
    private List<Animal> animais = new ArrayList<>();
    private List<Animal> adoptados = new ArrayList<>();
    private List<Animal> mortos = new ArrayList<>();
    private List<Racao> racoes = new ArrayList<>();
    private Map<String, Double> racaoPorKgPorEspecie = new HashMap<>();
    private Map<String, String> cuidadosEspeciais = new HashMap<>();

    // --- Métodos de persistência ---
    public void guardarEmFicheiro(String nome) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nome))) {
            oos.writeObject(this);
        } catch (Exception e) {
            System.out.println("Erro ao guardar: " + e.getMessage());
        }
    }
    public static Abrigo carregarDeFicheiro(String nome) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nome))) {
            return (Abrigo) ois.readObject();
        } catch (Exception e) {
            System.out.println("Novo abrigo criado.");
            Abrigo ab = new Abrigo();
            ab.dadosExemplo();
            return ab;
        }
    }

    // --- Dados de exemplo para arranque fácil ---
    private void dadosExemplo() {
        capacidadePorEspecie.put("Cão", 10);
        capacidadePorEspecie.put("Gato", 10);
        capacidadePorEspecie.put("Pássaro", 5);

        racaoPorKgPorEspecie.put("Cão", 0.025);
        racaoPorKgPorEspecie.put("Gato", 0.030);
        racaoPorKgPorEspecie.put("Pássaro", 0.005);

        cuidadosEspeciais.put("Cão", "Passeio diário e atenção ao pelo.");
        cuidadosEspeciais.put("Gato", "Limpeza de caixa de areia.");
        cuidadosEspeciais.put("Pássaro", "Limpar gaiola e trocar água.");

        racoes.add(new Racao("RacaoBarataCao", "Cão", 0, 20, 2.5));
        racoes.add(new Racao("RacaoPremiumCao", "Cão", 2, 20, 5.0));
        racoes.add(new Racao("RacaoBarataGato", "Gato", 0, 20, 3.0));
        racoes.add(new Racao("RacaoBarataPassaro", "Pássaro", 0, 5, 1.0));
    }

    // --- Menu de consultas ---
    public void menuConsultas(Scanner sc) {
        System.out.println("1. Nomes dos animais de cada espécie");
        System.out.println("2. Número de animais de uma espécie");
        System.out.println("3. Nome e idade dos animais de uma espécie");
        System.out.println("4. Nome, idade e observações dos animais de uma raça");
        System.out.println("5. Número de animais de cada espécie adoptados");
        System.out.println("6. Quantidade diária de ração por espécie");
        System.out.println("7. Custo diário da alimentação (todos)");
        System.out.println("8. Custo diário da alimentação de uma espécie");
        System.out.println("9. Dados completos de um animal");
        System.out.print("Opção: ");
        String op = sc.nextLine();

        switch (op) {
            case "1": listarNomesPorEspecie(); break;
            case "2": listarNumeroPorEspecie(sc); break;
            case "3": listarNomeIdadePorEspecie(sc); break;
            case "4": listarNomeIdadeObsPorRaca(sc); break;
            case "5": listarAdoptadosPorEspecie(); break;
            case "6": listarRacaoPorEspecie(); break;
            case "7": estimativaCustoDiarioTodos(); break;
            case "8": estimativaCustoDiarioPorEspecie(sc); break;
            case "9": detalhesAnimal(sc); break;
            default: System.out.println("Opção inválida!");
        }
    }

    // --- Métodos de gestão do abrigo ---
    public void adicionarAnimal(Scanner sc) {
        System.out.print("Nome: "); String nome = sc.nextLine();
        System.out.print("Espécie: "); String especie = sc.nextLine();
        System.out.print("Género (M/F): "); String genero = sc.nextLine();
        System.out.print("Peso (kg): "); double peso = Double.parseDouble(sc.nextLine());
        System.out.print("Raça: "); String raca = sc.nextLine();
        System.out.print("Idade (anos): "); int idade = Integer.parseInt(sc.nextLine());
        System.out.print("Observações: "); String obs = sc.nextLine();
        Animal a = new Animal(nome, especie, genero, peso, raca, idade, obs);

        // Capacidade
        int cap = capacidadePorEspecie.getOrDefault(especie, 10);
        long count = animais.stream().filter(an -> an.getEspecie().equalsIgnoreCase(especie)).count();
        if (count >= cap) {
            System.out.println("ALERTA: Capacidade de " + especie + " ultrapassada! Existem " + (count+1-cap) + " a mais.");
        } else if (count == cap-1) {
            System.out.println("ALERTA: Só resta um lugar para " + especie);
        }

        animais.add(a);
        System.out.println("Animal adicionado!");
    }

    public void adoptarAnimal(Scanner sc) {
        System.out.print("Nome do animal: ");
        String nome = sc.nextLine();
        Animal a = getAnimalNoAbrigoPorNome(nome);
        if (a != null) {
            System.out.print("Nome do adoptante: ");
            String adoptante = sc.nextLine();
            a.setAdoptante(adoptante);
            a.setDataAdopcao(LocalDate.now());
            animais.remove(a);
            adoptados.add(a);
            System.out.println("Animal adoptado!");
        } else {
            System.out.println("Animal não encontrado no abrigo.");
        }
    }

    public void animalMorreu(Scanner sc) {
        System.out.print("Nome do animal: ");
        String nome = sc.nextLine();
        Animal a = getAnimalNoAbrigoPorNome(nome);
        if (a != null) {
            a.setDataObito(LocalDate.now());
            animais.remove(a);
            mortos.add(a);
            System.out.println("Animal marcado como falecido.");
        } else {
            System.out.println("Animal não encontrado no abrigo.");
        }
    }

    public void registrarAtoVeterinario(Scanner sc) {
        System.out.print("Data (YYYY-MM-DD): ");
        String dataStr = sc.nextLine();
        LocalDate data = Util.strToDate(dataStr);
        System.out.println("1. Desparasitação");
        System.out.println("2. Vacinação");
        System.out.println("3. Esterilização");
        System.out.println("4. Outro");
        System.out.print("Escolha a intervenção: ");
        String tipo = sc.nextLine();
        String descricao = "";
        switch (tipo) {
            case "1": descricao = "Desparasitação"; break;
            case "2": descricao = "Vacinação"; break;
            case "3": descricao = "Esterilização"; break;
            case "4": System.out.print("Descrição: "); descricao = sc.nextLine(); break;
        }
        for (Animal a : new ArrayList<>(animais)) {
            // Esterilização obrigatória para fêmeas não esterilizadas
            if (tipo.equals("3")) {
                if (a.getGenero().equalsIgnoreCase("F") && !a.isEsterilizado()) {
                    a.setEsterilizado(true);
                    a.adicionarAtoVeterinario(new Veterinario(data, "Esterilização"));
                }
            } else {
                a.adicionarAtoVeterinario(new Veterinario(data, descricao));
            }
        }
        System.out.println("Atos registados.");
    }

    // --- Métodos de consulta e relatórios ---
    private Animal getAnimalNoAbrigoPorNome(String nome) {
        return animais.stream().filter(a -> a.getNome().equalsIgnoreCase(nome)).findFirst().orElse(null);
    }

    public void listarNomesPorEspecie() {
        Set<String> especies = animais.stream().map(Animal::getEspecie).collect(Collectors.toSet());
        for (String esp : especies) {
            System.out.println("Espécie: " + esp);
            animais.stream().filter(a -> a.getEspecie().equalsIgnoreCase(esp))
                    .forEach(a -> System.out.println("  " + a.getNome()));
        }
    }

    public void listarNumeroPorEspecie(Scanner sc) {
        System.out.print("Espécie: ");
        String esp = sc.nextLine();
        long n = animais.stream().filter(a -> a.getEspecie().equalsIgnoreCase(esp)).count();
        System.out.println("Total de " + esp + ": " + n);
    }

    public void listarNomeIdadePorEspecie(Scanner sc) {
        System.out.print("Espécie: ");
        String esp = sc.nextLine();
        animais.stream().filter(a -> a.getEspecie().equalsIgnoreCase(esp))
                .forEach(a -> System.out.println(a.getNome() + " (" + a.getIdade() + " anos)"));
    }

    public void listarNomeIdadeObsPorRaca(Scanner sc) {
        System.out.print("Raça: ");
        String raca = sc.nextLine();
        animais.stream().filter(a -> a.getRaca().equalsIgnoreCase(raca))
                .forEach(a -> System.out.println(a.getNome() + ", " + a.getIdade() + " anos, Obs: " + a.getObservacoes()));
    }

    public void listarAdoptadosPorEspecie() {
        Map<String, Long> adotadosPorEspecie = adoptados.stream()
                .collect(Collectors.groupingBy(Animal::getEspecie, Collectors.counting()));
        adotadosPorEspecie.forEach((esp, n) -> System.out.println(esp + ": " + n));
    }

    public void listarRacaoPorEspecie() {
        racaoPorKgPorEspecie.forEach((esp, q) -> System.out.println(esp + ": " + q + " kg por kg/peso por dia"));
    }

    public void estimativaCustoDiarioTodos() {
        double total = 0;
        for (String esp : racaoPorKgPorEspecie.keySet()) {
            total += estimativaCustoDiario(esp);
        }
        System.out.println("Estimativa custo diário (todos): " + Util.formatMoeda(total));
    }

    public void estimativaCustoDiarioPorEspecie(Scanner sc) {
        System.out.print("Espécie: ");
        String esp = sc.nextLine();
        System.out.println("Estimativa custo diário (" + esp + "): " + Util.formatMoeda(estimativaCustoDiario(esp)));
    }

    private double estimativaCustoDiario(String esp) {
        // Soma peso total dos animais da espécie
        double pesoTotal = animais.stream().filter(a -> a.getEspecie().equalsIgnoreCase(esp)).mapToDouble(Animal::getPeso).sum();
        double racaoKg = racaoPorKgPorEspecie.getOrDefault(esp, 0.03) * pesoTotal;
        // Rações disponíveis
        OptionalDouble precoMin = racoes.stream().filter(r -> r.getEspecie().equalsIgnoreCase(esp))
                .mapToDouble(Racao::getPrecoPorKg).min();
        if (precoMin.isPresent()) {
            return racaoKg * precoMin.getAsDouble();
        } else {
            return 0;
        }
    }

    public void detalhesAnimal(Scanner sc) {
        System.out.print("Nome do animal: ");
        String nome = sc.nextLine();
        Animal a = Stream.concat(Stream.concat(animais.stream(), adoptados.stream()), mortos.stream())
                .filter(an -> an.getNome().equalsIgnoreCase(nome)).findFirst().orElse(null);
        if (a != null) System.out.println(a);
        else System.out.println("Animal não encontrado.");
    }

    // --- Exportação relatórios TXT ---
    public void gerarRelatoriosTxt() {
        // 1. Animais (nome, peso, idade) de cada espécie existentes no abrigo – nome_da_espécie.txt
        Set<String> especies = animais.stream().map(Animal::getEspecie).collect(Collectors.toSet());
        for (String esp : especies) {
            try (PrintWriter pw = new PrintWriter(esp + ".txt")) {
                for (Animal a : animais)
                    if (a.getEspecie().equalsIgnoreCase(esp))
                        pw.println(a.getNome() + ", " + a.getPeso() + "kg, " + a.getIdade() + " anos");
            } catch (Exception e) { }
        }

        // 2. Animais (nome, idade, raça) sem raça indefinida para cada espécie
        for (String esp : especies) {
            try (PrintWriter pw = new PrintWriter(esp + "_raca.txt")) {
                for (Animal a : animais)
                    if (a.getEspecie().equalsIgnoreCase(esp) && !a.getRaca().equalsIgnoreCase("indefinida"))
                        pw.println(a.getNome() + ", " + a.getIdade() + " anos, " + a.getRaca());
            } catch (Exception e) { }
        }

        // 3. Animais adoptados (nome e espécie) e nome do novo dono
        try (PrintWriter pw = new PrintWriter("adoptados.txt")) {
            for (Animal a : adoptados)
                pw.println(a.getNome() + ", " + a.getEspecie() + ", Adoptante: " + a.getAdoptante());
        } catch (Exception e) { }

        // 4. Todos os atos veterinários realizados em cada animal numa data
        Set<LocalDate> datas = new HashSet<>();
        animais.forEach(a -> a.getAtosVeterinarios().forEach(v -> datas.add(v.getData())));
        mortos.forEach(a -> a.getAtosVeterinarios().forEach(v -> datas.add(v.getData())));
        adoptados.forEach(a -> a.getAtosVeterinarios().forEach(v -> datas.add(v.getData())));

        for (LocalDate d : datas) {
            try (PrintWriter pw = new PrintWriter("veterinario_" + d + ".txt")) {
                for (Animal a : Stream.concat(Stream.concat(animais.stream(), mortos.stream()), adoptados.stream()).toList()) {
                    List<Veterinario> atos = a.getAtosVeterinarios().stream()
                            .filter(v -> v.getData().equals(d)).toList();
                    if (!atos.isEmpty()) {
                        pw.println(a.getNome() + ":");
                        for (Veterinario v : atos)
                            pw.println(" - " + v.getDescricao());
                    }
                }
            } catch (Exception e) { }
        }

        System.out.println("Relatórios gerados.");
    }
}