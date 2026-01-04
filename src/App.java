import java.util.Scanner;
import java.util.Random;

public class App {

    final static String CARACTERES_IDENTIFICADORES_ACEITOS = "XOUC";
    final static int TAMANHO_TABULEIRO = 3;

    static char[][] tabuleiro = new char[TAMANHO_TABULEIRO][TAMANHO_TABULEIRO];
    static int tamanho = tabuleiro.length;
    static Scanner teclado = new Scanner(System.in);
    static int dificuldade;

    public static void main(String[] args) {

        boolean jogarNovamente;

        do {
            exibirBoasVindas();

            dificuldade = escolherDificuldade();

            int modo = escolherModoJogo();

            int partidas = 1;
            if (modo == 2) {
                partidas = escolherQuantidadePartidas();
            }

            int vitoriasUsuario = 0;
            int vitoriasComputador = 0;
            int empates = 0;

            char caractereUsuario = obterCaractereUsuario();
            char caractereComputador = obterCaractereComputador(caractereUsuario);

            for (int i = 1; i <= partidas; i++) {

                System.out.println("\nPartida " + i + " de " + partidas);
                inicializarTabuleiro();

                boolean vezUsuarioJogar = sortearValorBooleano();
                boolean jogoContinua;

                do {
                    jogoContinua = true;
                    exibirTabuleiro();

                    if (vezUsuarioJogar) {
                        processarVezUsuario(caractereUsuario);

                        if (teveGanhador(caractereUsuario)) {
                            exibirTabuleiro();
                            System.out.println("O usuário venceu a partida!");
                            vitoriasUsuario++;
                            jogoContinua = false;
                        }

                        vezUsuarioJogar = false;
                    } else {
                        processarVezComputador(caractereComputador, caractereUsuario);

                        if (teveGanhador(caractereComputador)) {
                            exibirTabuleiro();
                            System.out.println("O computador venceu a partida!");
                            vitoriasComputador++;
                            jogoContinua = false;
                        }

                        vezUsuarioJogar = true;
                    }

                    if (jogoContinua && teveEmpate()) {
                        exibirTabuleiro();
                        System.out.println("A partida terminou em empate!");
                        empates++;
                        jogoContinua = false;
                    }

                } while (jogoContinua);
            }

            exibirPlacar(vitoriasUsuario, vitoriasComputador, empates);

            jogarNovamente = perguntarJogarNovamente();

        } while (jogarNovamente);

        System.out.println("Até mais! Obrigado por jogar =)");
        teclado.close();
    }

    static void exibirBoasVindas() {
        System.out.println("=================================");
        System.out.println(" Bem-vindo ao Jogo da Velha ");
        System.out.println("=================================");
    }

    static int escolherDificuldade() {
        int op;
        do {
            System.out.println("\nEscolha a dificuldade:");
            System.out.println("1 - Fácil");
            System.out.println("2 - Médio");
            System.out.println("3 - Difícil");
            op = teclado.nextInt();
        } while (op < 1 || op > 3);
        return op;
    }

    static int escolherModoJogo() {
        int op;
        do {
            System.out.println("\nEscolha o modo de jogo:");
            System.out.println("1 - Partida única");
            System.out.println("2 - Torneio");
            op = teclado.nextInt();
        } while (op < 1 || op > 2);
        return op;
    }

    static int escolherQuantidadePartidas() {
        int qtd;
        do {
            System.out.print("Quantas partidas terá o torneio? ");
            qtd = teclado.nextInt();
        } while (qtd < 1);
        return qtd;
    }

    static void inicializarTabuleiro() {
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                tabuleiro[i][j] = ' ';
            }
        }
    }

    static boolean sortearValorBooleano() {
        return new Random().nextBoolean();
    }

    static void exibirTabuleiro() {
        System.out.println();
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                System.out.print(" " + tabuleiro[i][j] + " ");
                if (j < tamanho - 1) System.out.print("|");
            }
            System.out.println();
            if (i < tamanho - 1) System.out.println("---+---+---");
        }
        System.out.println();
    }

    static void processarVezUsuario(char c) {
        String livres = retornarPosicoesLivres();
        int[] jogada = obterJogadaUsuario(livres);
        atualizaTabuleiro(jogada, c);
    }

    static void processarVezComputador(char cComp, char cUser) {

        if (dificuldade > 1) {
            int[] jogada = tentarVencerOuBloquear(cComp);
            if (jogada != null) {
                atualizaTabuleiro(jogada, cComp);
                return;
            }
            if (dificuldade == 3) {
                jogada = tentarVencerOuBloquear(cUser);
                if (jogada != null) {
                    atualizaTabuleiro(jogada, cComp);
                    return;
                }
            }
        }

        String[] jogadas = retornarPosicoesLivres().split(";");
        String escolha = jogadas[new Random().nextInt(jogadas.length)];
        atualizaTabuleiro(converterJogadaStringParaVetorInt(escolha), cComp);
    }

    static int[] tentarVencerOuBloquear(char c) {
        for (String p : retornarPosicoesLivres().split(";")) {
            int[] j = converterJogadaStringParaVetorInt(p);
            tabuleiro[j[0]][j[1]] = c;
            boolean ganhou = teveGanhador(c);
            tabuleiro[j[0]][j[1]] = ' ';
            if (ganhou) return j;
        }
        return null;
    }

    static boolean teveEmpate() {
        return retornarPosicoesLivres().isEmpty();
    }

    static String retornarPosicoesLivres() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                if (tabuleiro[i][j] == ' ') sb.append(i).append(j).append(";");
            }
        }
        return sb.toString();
    }

    static int[] obterJogadaUsuario(String livres) {
        while (true) {
            System.out.print("Digite linha e coluna (ex: 1 1): ");
            int l = teclado.nextInt() - 1;
            int c = teclado.nextInt() - 1;
            if (livres.contains("" + l + c)) return new int[]{l, c};
            System.out.println("Jogada inválida!");
        }
    }

    static int[] converterJogadaStringParaVetorInt(String j) {
        return new int[]{
            Character.getNumericValue(j.charAt(0)),
            Character.getNumericValue(j.charAt(1))
        };
    }

    static void atualizaTabuleiro(int[] j, char c) {
        tabuleiro[j[0]][j[1]] = c;
    }

    static boolean teveGanhador(char c) {
        for (int i = 0; i < tamanho; i++) {
            if (tabuleiro[i][0] == c && tabuleiro[i][1] == c && tabuleiro[i][2] == c) return true;
            if (tabuleiro[0][i] == c && tabuleiro[1][i] == c && tabuleiro[2][i] == c) return true;
        }
        return (tabuleiro[0][0] == c && tabuleiro[1][1] == c && tabuleiro[2][2] == c) ||
               (tabuleiro[0][2] == c && tabuleiro[1][1] == c && tabuleiro[2][0] == c);
    }

    static char obterCaractereUsuario() {
        char c;
        do {
            System.out.print("Escolha o caractere do usuário (" + CARACTERES_IDENTIFICADORES_ACEITOS + "): ");
            c = teclado.next().toUpperCase().charAt(0);
        } while (!CARACTERES_IDENTIFICADORES_ACEITOS.contains("" + c));
        return c;
    }

    static char obterCaractereComputador(char cUser) {
        char c;
        do {
            System.out.print("Escolha o caractere do computador (" + CARACTERES_IDENTIFICADORES_ACEITOS + "): ");
            c = teclado.next().toUpperCase().charAt(0);
        } while (!CARACTERES_IDENTIFICADORES_ACEITOS.contains("" + c) || c == cUser);
        return c;
    }

    static void exibirPlacar(int u, int c, int e) {
        System.out.println("\n===== PLACAR FINAL =====");
        System.out.println("Usuário: " + u);
        System.out.println("Computador: " + c);
        System.out.println("Empates: " + e);

        if (u > c) System.out.println("Vencedor do torneio: Usuário!");
        else if (c > u) System.out.println("Vencedor do torneio: Computador!");
        else System.out.println("O torneio terminou empatado!");
    }

    static boolean perguntarJogarNovamente() {
        System.out.print("\nDeseja jogar novamente? (S/N): ");
        return teclado.next().toUpperCase().charAt(0) == 'S';
    }
}
