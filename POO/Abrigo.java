import java.io.*; // Importa classes para entrada/saída, incluindo serialização de objetos
import java.time.LocalDate; // Importa a classe para manipulação de datas
import java.util.*; // Importa as estruturas de dados do Java (List, Map, etc)
import java.util.stream.Collectors; // Importa utilitários para manipulação de coleções com streams
import java.util.stream.Stream; // Importa a interface Stream para processamento funcional de listas

// Classe principal que representa o abrigo, armazena e gere os animais, rações, etc.
public class Abrigo implements Serializable {
    private static final long serialVersionUID = 1L; // Identificador de versão para serialização

    // Mapa que associa a espécie à capacidade máxima permitida no abrigo
    private Map<String, Integer> capacidadePorEspecie = new HashMap<>();
    // Lista de animais atualmente presentes no abrigo
    private List<Animal> animais = new ArrayList<>();
    // Lista de animais já adotados
    private List<Animal> adoptados = new ArrayList<>();
    // Lista de animais que morreram
    private List<Animal> mortos = new ArrayList<>();
    // Lista de rações registadas no abrigo
    private List<Racao> racoes = new ArrayList<>();
    // Mapa que associa a espécie à quantidade de ração necessária por kg de animal por dia
    private Map<String, Double> racaoPorKgPorEspecie = new HashMap<>();
    // Mapa com cuidados especiais por espécie (ex: limpar gaiola, passear cão, etc)
    private Map<String, String> cuidadosEspeciais = new HashMap<>();

    // --- Métodos de persistência (guardar e carregar o estado do abrigo em ficheiro) ---
    public void guardarEmFicheiro(String nome) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nome))) {
            oos.writeObject(this); // Serializa o objeto Abrigo para ficheiro
        } catch (Exception e) {
            System.out.println("Erro ao guardar: " + e.getMessage()); // Mensagem de erro se falhar
        }
    }
    public static Abrigo carregarDeFicheiro(String nome) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nome))) {
            return (Abrigo) ois.readObject(); // Lê e deserializa o objeto Abrigo do ficheiro
        } catch (Exception e) {
            System.out.println("Novo abrigo criado."); // Se falhar, cria um novo abrigo vazio com dados de exemplo
            Abrigo ab = new Abrigo();
            ab.dadosExemplo();
            return ab;
        }
    }

    // --- Dados de exemplo para arranque rápido do sistema ---
    private void dadosExemplo() {
        capacidadePorEspecie.put("Cão", 10); // Define capacidade máxima de cães
        capacidadePorEspecie.put("Gato", 10); // ... gatos
        capacidadePorEspecie.put("Pássaro", 5); // ... pássaros

        racaoPorKgPorEspecie.put("Cão", 0.025); // Quantidade diária de ração/kg para cães
        racaoPorKgPorEspecie.put("Gato", 0.030); // ... gatos
        racaoPorKgPorEspecie.put("Pássaro", 0.005); // ... pássaros

        cuidadosEspeciais.put("Cão", "Passeio diário e atenção ao pelo."); // Cuidados especiais para cães
        cuidadosEspeciais.put("Gato", "Limpeza de caixa de areia."); // ... gatos
        cuidadosEspeciais.put("Pássaro", "Limpar gaiola e trocar água."); // ... pássaros

        // Adiciona rações de exemplo ao abrigo
        racoes.add(new Racao("RacaoBarataCao", "Cão", 0, 20, 2.5));
        racoes.add(new Racao("RacaoPremiumCao", "Cão", 2, 20, 5.0));
        racoes.add(new Racao("RacaoBarataGato", "Gato", 0, 20, 3.0));
        racoes.add(new Racao("RacaoBarataPassaro", "Pássaro", 0, 5, 1.0));
    }

    // --- Menu de consultas ao utilizador ---
    public void menuConsultas(Scanner sc) {
        // Mostra opções de consulta ao utilizador
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
        String op = sc.nextLine(); // Lê a opção escolhida

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
            default: System.out.println("Opção inválida!"); // Caso a opção não exista
        }
    }

    // --- Métodos de gestão do abrigo (CRUD) ---

    // Adiciona um novo animal ao abrigo, perguntando todos os dados ao utilizador
    public void adicionarAnimal(Scanner sc) {
        System.out.print("Nome: "); String nome = sc.nextLine();
        System.out.print("Espécie: "); String especie = sc.nextLine();
        System.out.print("Género (M/F): "); String genero = sc.nextLine();
        System.out.print("Peso (kg): "); double peso = Double.parseDouble(sc.nextLine());
        System.out.print("Raça: "); String raca = sc.nextLine();
        System.out.print("Idade (anos): "); int idade = Integer.parseInt(sc.nextLine());
        System.out.print("Observações: "); String obs = sc.nextLine();
        Animal a = new Animal(nome, especie, genero, peso, raca, idade, obs);

        // Verifica se a capacidade máxima para aquela espécie já foi atingida
        int cap = capacidadePorEspecie.getOrDefault(especie, 10);
        long count = animais.stream().filter(an -> an.getEspecie().equalsIgnoreCase(especie)).count();
        if (count >= cap) {
            System.out.println("ALERTA: Capacidade de " + especie + " ultrapassada! Existem " + (count+1-cap) + " a mais.");
        } else if (count == cap-1) {
            System.out.println("ALERTA: Só resta um lugar para " + especie); // Alerta de último lugar
        }

        animais.add(a); // Adiciona o animal à lista
        System.out.println("Animal adicionado!");
    }

    // Marca um animal como adoptado e move-o para a lista de adoptados
    public void adoptarAnimal(Scanner sc) {
        System.out.print("Nome do animal: ");
        String nome = sc.nextLine();
        Animal a = getAnimalNoAbrigoPorNome(nome); // Procura o animal pelo nome
        if (a != null) {
            System.out.print("Nome do adoptante: ");
            String adoptante = sc.nextLine();
            a.setAdoptante(adoptante); // Regista o nome do adoptante
            a.setDataAdopcao(LocalDate.now()); // Regista a data de adoção
            animais.remove(a); // Remove da lista de animais presentes
            adoptados.add(a); // Adiciona à lista de adoptados
            System.out.println("Animal adoptado!");
        } else {
            System.out.println("Animal não encontrado no abrigo.");
        }
    }

    // Marca um animal como morto e move-o para a lista de mortos
    public void animalMorreu(Scanner sc) {
        System.out.print("Nome do animal: ");
        String nome = sc.nextLine();
        Animal a = getAnimalNoAbrigoPorNome(nome);
        if (a != null) {
            a.setDataObito(LocalDate.now()); // Regista data de óbito
            animais.remove(a); // Remove da lista principal
            mortos.add(a); // Adiciona à lista de mortos
            System.out.println("Animal marcado como falecido.");
        } else {
            System.out.println("Animal não encontrado no abrigo.");
        }
    }

    // Permite registar atos veterinários em todos os animais do abrigo
    public void registrarAtoVeterinario(Scanner sc) {
        System.out.print("Data (YYYY-MM-DD): ");
        String dataStr = sc.nextLine();
        LocalDate data = Util.strToDate(dataStr); // Converte a string para data
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

    // Procura um animal na lista de presentes pelo nome
    private Animal getAnimalNoAbrigoPorNome(String nome) {
        return animais.stream().filter(a -> a.getNome().equalsIgnoreCase(nome)).findFirst().orElse(null);
    }

    // Lista os nomes dos animais agrupados por espécie
    public void listarNomesPorEspecie() {
        Set<String> especies = animais.stream().map(Animal::getEspecie).collect(Collectors.toSet());
        for (String esp : especies) {
            System.out.println("Espécie: " + esp);
            animais.stream().filter(a -> a.getEspecie().equalsIgnoreCase(esp))
                    .forEach(a -> System.out.println("  " + a.getNome()));
        }
    }

    // Conta o número de animais de uma espécie específica
    public void listarNumeroPorEspecie(Scanner sc) {
        System.out.print("Espécie: ");
        String esp = sc.nextLine();
        long n = animais.stream().filter(a -> a.getEspecie().equalsIgnoreCase(esp)).count();
        System.out.println("Total de " + esp + ": " + n);
    }

    // Lista nome e idade dos animais de uma espécie
    public void listarNomeIdadePorEspecie(Scanner sc) {
        System.out.print("Espécie: ");
        String esp = sc.nextLine();
        animais.stream().filter(a -> a.getEspecie().equalsIgnoreCase(esp))
                .forEach(a -> System.out.println(a.getNome() + " (" + a.getIdade() + " anos)"));
    }

    // Lista nome, idade e observações dos animais de uma determinada raça
    public void listarNomeIdadeObsPorRaca(Scanner sc) {
        System.out.print("Raça: ");
        String raca = sc.nextLine();
        animais.stream().filter(a -> a.getRaca().equalsIgnoreCase(raca))
                .forEach(a -> System.out.println(a.getNome() + ", " + a.getIdade() + " anos, Obs: " + a.getObservacoes()));
    }

    // Lista o número de animais adoptados por espécie
    public void listarAdoptadosPorEspecie() {
        Map<String, Long> adotadosPorEspecie = adoptados.stream()
                .collect(Collectors.groupingBy(Animal::getEspecie, Collectors.counting()));
        adotadosPorEspecie.forEach((esp, n) -> System.out.println(esp + ": " + n));
    }

    // Mostra a quantidade de ração recomendada por espécie
    public void listarRacaoPorEspecie() {
        racaoPorKgPorEspecie.forEach((esp, q) -> System.out.println(esp + ": " + q + " kg por kg/peso por dia"));
    }

    // Calcula o custo diário de alimentação para todas as espécies
    public void estimativaCustoDiarioTodos() {
        double total = 0;
        for (String esp : racaoPorKgPorEspecie.keySet()) {
            total += estimativaCustoDiario(esp);
        }
        System.out.println("Estimativa custo diário (todos): " + Util.formatMoeda(total));
    }

    // Calcula o custo diário de alimentação para uma espécie escolhida
    public void estimativaCustoDiarioPorEspecie(Scanner sc) {
        System.out.print("Espécie: ");
        String esp = sc.nextLine();
        System.out.println("Estimativa custo diário (" + esp + "): " + Util.formatMoeda(estimativaCustoDiario(esp)));
    }

    // Função auxiliar: calcula o custo diário de alimentação para uma espécie
    private double estimativaCustoDiario(String esp) {
        // Soma o peso de todos os animais da espécie
        double pesoTotal = animais.stream().filter(a -> a.getEspecie().equalsIgnoreCase(esp)).mapToDouble(Animal::getPeso).sum();
        double racaoKg = racaoPorKgPorEspecie.getOrDefault(esp, 0.03) * pesoTotal;
        // Procura o preço mínimo da ração disponível para a espécie
        OptionalDouble precoMin = racoes.stream().filter(r -> r.getEspecie().equalsIgnoreCase(esp))
                .mapToDouble(Racao::getPrecoPorKg).min();
        if (precoMin.isPresent()) {
            return racaoKg * precoMin.getAsDouble();
        } else {
            return 0;
        }
    }

    // Mostra todos os detalhes de um animal, pesquisando em todas as listas
    public void detalhesAnimal(Scanner sc) {
        System.out.print("Nome do animal: ");
        String nome = sc.nextLine();
        Animal a = Stream.concat(Stream.concat(animais.stream(), adoptados.stream()), mortos.stream())
                .filter(an -> an.getNome().equalsIgnoreCase(nome)).findFirst().orElse(null);
        if (a != null) System.out.println(a);
        else System.out.println("Animal não encontrado.");
    }

    // --- Exportação de relatórios em ficheiros TXT ---
    public void gerarRelatoriosTxt() {
        // 1. Animais (nome, peso, idade) por espécie num ficheiro: nome_da_espécie.txt
        Set<String> especies = animais.stream().map(Animal::getEspecie).collect(Collectors.toSet());
        for (String esp : especies) {
            try (PrintWriter pw = new PrintWriter(esp + ".txt")) {
                for (Animal a : animais)
                    if (a.getEspecie().equalsIgnoreCase(esp))
                        pw.println(a.getNome() + ", " + a.getPeso() + "kg, " + a.getIdade() + " anos");
            } catch (Exception e) { }
        }

        // 2. Animais (nome, idade, raça) sem raça indefinida, por espécie
        for (String esp : especies) {
            try (PrintWriter pw = new PrintWriter(esp + "_raca.txt")) {
                for (Animal a : animais)
                    if (a.getEspecie().equalsIgnoreCase(esp) && !a.getRaca().equalsIgnoreCase("indefinida"))
                        pw.println(a.getNome() + ", " + a.getIdade() + " anos, " + a.getRaca());
            } catch (Exception e) { }
        }

        // 3. Animais adoptados (nome, espécie e nome do novo dono)
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