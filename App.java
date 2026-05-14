import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

    /**
     * Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto
     */
    static String nomeArquivoDados;

    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    // TODO: Tarefa 5 - Substituir a pilha abaixo por uma Lista<Pedido> para
    // armazenar os pedidos.
    static Lista<Pedido> pedidos = new Lista<>();

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }

    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {

        T valor;

        System.out.println(mensagem);

        try {

            valor = classe.getConstructor(String.class)
                    .newInstance(teclado.nextLine());

        } catch (Exception e) {

            return null;
        }

        return valor;
    }

    static int menu() {

        cabecalho();

        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar por um produto, por código");
        System.out.println("3 - Procurar por um produto, por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido");
        System.out.println("6 - Filtrar pedidos por produto");
        System.out.println("0 - Sair");

        System.out.print("Digite sua opção: ");

        return Integer.parseInt(teclado.nextLine());
    }

    static Produto[] lerProdutos(String nomeArquivoDados) {

        Scanner arquivo = null;

        int numProdutos;
        String linha;
        Produto produto;

        Produto[] produtosCadastrados;

        try {

            arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));

            numProdutos = Integer.parseInt(arquivo.nextLine());

            produtosCadastrados = new Produto[numProdutos];

            for (int i = 0; i < numProdutos; i++) {

                linha = arquivo.nextLine();

                produto = Produto.criarDoTexto(linha);

                produtosCadastrados[i] = produto;
            }

            quantosProdutos = numProdutos;

        } catch (IOException excecaoArquivo) {

            produtosCadastrados = null;

        } finally {

            if (arquivo != null) {
                arquivo.close();
            }
        }

        return produtosCadastrados;
    }

    static Produto localizarProduto() {

        Produto produto = null;
        boolean localizado = false;

        cabecalho();

        System.out.println("Localizando um produto...");

        int idProduto = lerOpcao(
                "Digite o código identificador do produto desejado: ",
                Integer.class);

        for (int i = 0; i < quantosProdutos && !localizado; i++) {

            if (produtosCadastrados[i].hashCode() == idProduto) {

                produto = produtosCadastrados[i];
                localizado = true;
            }
        }

        return produto;
    }

    static Produto localizarProdutoDescricao() {

        Produto produto = null;
        boolean localizado = false;

        cabecalho();

        System.out.println("Digite o nome ou a descrição do produto desejado:");

        String descricao = teclado.nextLine();

        for (int i = 0; i < quantosProdutos && !localizado; i++) {

            if (produtosCadastrados[i].descricao
                    .equalsIgnoreCase(descricao)) {

                produto = produtosCadastrados[i];
                localizado = true;
            }
        }

        return produto;
    }

    private static void mostrarProduto(Produto produto) {

        cabecalho();

        String mensagem = "Dados inválidos para o produto!";

        if (produto != null) {

            mensagem = String.format("Dados do produto:\n%s", produto);
        }

        System.out.println(mensagem);
    }

    static void listarTodosOsProdutos() {

        cabecalho();

        System.out.println("\nPRODUTOS CADASTRADOS:");

        for (int i = 0; i < quantosProdutos; i++) {

            System.out.println(
                    String.format("%02d - %s",
                            (i + 1),
                            produtosCadastrados[i]));
        }
    }

    public static Pedido iniciarPedido() {

        int formaPagamento = lerOpcao(
                "Digite a forma de pagamento (1 à vista / 2 prazo)",
                Integer.class);

        Pedido pedido = new Pedido(LocalDate.now(), formaPagamento);

        Produto produto;
        int numProdutos;
        int quantidade;

        listarTodosOsProdutos();

        System.out.println("Incluindo produtos no pedido...");

        numProdutos = lerOpcao(
                "Quantos produtos serão incluídos no pedido?",
                Integer.class);

        for (int i = 0; i < numProdutos; i++) {

            produto = localizarProdutoDescricao();

            if (produto == null) {

                System.out.println("Produto não encontrado");
                i--;

            } else {

                quantidade = lerOpcao(
                        "Quantidade do produto:",
                        Integer.class);

                pedido.incluirProduto(produto, quantidade);
            }
        }

        return pedido;
    }

    public static void finalizarPedido(Pedido pedido) {

        cabecalho();

        if (pedido == null) {

            System.out.println("Nenhum pedido foi iniciado.");
            return;
        }

        if (pedido.getItensDoPedido().vazia()) {

            System.out.println("O pedido não possui itens.");
            return;
        }

        pedidos.inserirFinal(pedido);

        System.out.println("PEDIDO FINALIZADO COM SUCESSO!\n");
        System.out.println(pedido);
    }

    public static void filtrarPorProduto() {

        cabecalho();

        if (pedidos.vazia()) {

            System.out.println("Não existem pedidos cadastrados.");
            return;
        }

        System.out.println("Digite a descrição do produto:");
        String descricao = teclado.nextLine();

        Lista<Pedido> pedidosFiltrados = pedidos.filtrar(
                pedido -> {

                    ItemDePedido encontrado =
                            pedido.getItensDoPedido().buscarPor(
                                    new CriterioDeBuscaPorDescricao(),
                                    new ItemDePedido(
                                            new ProdutoNaoPerecivel(descricao, 0, 0),
                                            0,
                                            0));

                    return encontrado != null;
                });

        if (pedidosFiltrados.vazia()) {

            System.out.println("Nenhum pedido contém o produto informado.");
            return;
        }

        System.out.println("\nPEDIDOS ENCONTRADOS:\n");

        for (Pedido pedido : pedidosFiltrados) {
            System.out.println(pedido);
            System.out.println();
        }
    }

    public static void main(String[] args) {

        teclado = new Scanner(System.in, Charset.forName("UTF-8"));

        nomeArquivoDados = "produtos.txt";

        produtosCadastrados = lerProdutos(nomeArquivoDados);

        Pedido pedido = null;

        int opcao;

        do {

            opcao = menu();

            switch (opcao) {

                case 1 -> listarTodosOsProdutos();
                case 2 -> mostrarProduto(localizarProduto());
                case 3 -> mostrarProduto(localizarProdutoDescricao());
                case 4 -> pedido = iniciarPedido();
                case 5 -> finalizarPedido(pedido);
                case 6 -> filtrarPorProduto();
            }

            pausa();

        } while (opcao != 0);

        teclado.close();
    }
}