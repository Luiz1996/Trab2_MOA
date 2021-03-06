package br.uem.din.moa.View;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Console {

    public static void cleanDisplay() {

        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    public static void endsApplication() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.exit(0);
    }

    public static int showMenu() {
        Scanner input = new Scanner(System.in);
        System.out.println("+------ Menu do Problema do Caixeiro Viajante -------+");
        System.out.println("|  01) Importar Cidades                              |");
        System.out.println("|  02) Imprimir Cidades                              |");
        System.out.println("|  03) Heurística do Vizinho Mais Próximo            |");
        System.out.println("|  04) Heurística da Inserção Mais Próxima           |");
        System.out.println("|  05) Simulated Annealing com Vizinho Mais Próximo  |");
        System.out.println("|  06) Simulated Annealing com Inserção Mais Próxima |");
        System.out.println("|  07) Imprimir Última Rota Gerada                   |");
        System.out.println("|  08) Resetar Informações                           |");
        System.out.println("|  09) Gerar cidades (Randômico)                     |");
        System.out.println("|  00) Sair                                          |");
        System.out.println("+----------------------------------------------------+");
        System.out.print("Opcao: ");

        try{
            return input.nextInt();
        }catch (InputMismatchException ie){
            return -1;
        }
    }
}
