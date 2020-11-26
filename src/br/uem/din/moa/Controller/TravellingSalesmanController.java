package br.uem.din.moa.Controller;

import br.uem.din.moa.Model.City;
import br.uem.din.moa.Model.Route;
import br.uem.din.moa.View.Console;

import javax.swing.*;
import java.util.*;

public class TravellingSalesmanController {
    Scanner input = new Scanner(System.in);
    Random random = new Random();
    final int ZERO = 0;
    final int UM = 1;
    final int TEN = 10;
    final int HUNDRED = 100;
    final int THOUSAND = 1000;
    final int TEN_THOUSAND = 10000;

    //heurística do vizinho mais próximo
    public List<Route> nearestNeighborHeuristicTSP(List<City> myCities) {
        //variáveis auxiliares
        int actual = 0;
        int lastPosition;
        int[] citiesOnRoute = new int[(myCities.size() + 1)];
        List<Route> myRoutes = new ArrayList<>();
        Route route = new Route();

        //iniciando rota
        route.setInitialVertex(0);
        myRoutes.add(route);

        //percorrendo todas as cidades
        for (int allNeighbors = 1; allNeighbors < (citiesOnRoute.length - 1); allNeighbors++) {
            lastPosition = (myRoutes.size() - 1);
            //possui todos os vizinhos da cidade atual, partindo da cidade C000
            List<Integer> neighbors = myCities.get(actual).getDistancias();

            //variáveis auxiliares
            int bestNeighbor = ZERO;
            int bestDistance = Integer.MAX_VALUE;

            //para cada cidade vizinha da cidade atual, partindo de C000
            for (int currentNeighbor = ZERO; currentNeighbor < neighbors.size(); currentNeighbor++) {
                int actualDistance = neighbors.get(currentNeighbor);

                if ((citiesOnRoute[currentNeighbor] != currentNeighbor/*Validando se a cidade já não existe na rota*/) &&
                        (actualDistance > ZERO) &&
                        (actualDistance < bestDistance)) {
                    bestNeighbor = currentNeighbor;
                    bestDistance = actualDistance;
                }
            }

            //atualizando informações
            actual = bestNeighbor;
            citiesOnRoute[actual] = actual;
            myRoutes.get(lastPosition).setFinalVertex(actual);
            myRoutes.get(lastPosition).setVertexDistances(myCities.get(actual).getDistancias().get(myRoutes.get(lastPosition).getInitialVertex()));

            //incluindo nova cidade na rota
            route = new Route();
            route.setInitialVertex(actual);
            myRoutes.add(route);
        }

        //terminando de setar informações na última cidade da rota
        lastPosition = (myRoutes.size() - 1);
        myRoutes.get(lastPosition).setFinalVertex(ZERO);
        myRoutes.get(lastPosition).setVertexDistances(myCities.get(myRoutes.get(lastPosition).getFinalVertex()).getDistancias().get(myRoutes.get(lastPosition).getInitialVertex()));

        return myRoutes;
    }

    //heurística da inserção mais próxima
    public List<Route> nearestInsertionHeuristicTSP(List<City> myCities) {
        //variáveis auxiliares
        int newVertexOnTheRoute = 0;
        int indexToBeChanged = 0;
        int[] citiesOnRoute = new int[myCities.size()];
        List<Route> myRoutes = startRoute_C000_C001_C002(myCities, citiesOnRoute); //inicializando ciclo hamiltoniano com os três primeiros vértices
        int remainingCities = (myCities.size() - myRoutes.size());
        int initialCycleSize = myRoutes.size();

        //este for realizará iterações até que todas as cidades sejam inseridas na rota
        for (int newCity = ZERO; newCity < remainingCities; newCity++) {
            int actualDistance = Integer.MAX_VALUE;

            //este for percorrerá cada cidade da rota para identificar a nova cidade mais próxima
            for (int route = ZERO; route < myRoutes.size(); route++) {

                //este for representa a cidade atual, tem como finalidade validar se o mesmo já está na rota e se a distância satisfaz as condições mínimas
                //pula os índices 0, 1 e 2 pois estes ja estão no ciclo hamiltoniano inicial
                for (int actualCity = initialCycleSize; actualCity < myCities.size(); actualCity++) {

                    //validando se a nova cidade deve ou não pertencer à rota
                    if ((citiesOnRoute[actualCity] != actualCity/*Validando se a cidade já não existe na rota*/) &&
                            (myCities.get(actualCity).getDistancias().get(myRoutes.get(route).getFinalVertex()) < actualDistance)) {

                        //setando informações atualizadas
                        actualDistance = myCities.get(actualCity).getDistancias().get(myRoutes.get(route).getFinalVertex());
                        indexToBeChanged = route;
                        newVertexOnTheRoute = actualCity;
                    }
                }
            }

            //atualizando variáveis
            citiesOnRoute[newVertexOnTheRoute] = newVertexOnTheRoute;

            //novo vértice que entrará na rota
            Route route = new Route();
            route.setInitialVertex(newVertexOnTheRoute);
            route.setVertexDistances(myCities.get(newVertexOnTheRoute).getDistancias().get(myRoutes.get(indexToBeChanged).getFinalVertex()));
            route.setFinalVertex(myRoutes.get(indexToBeChanged).getFinalVertex());

            //atualizando vértice que já existia na rota
            myRoutes.get(indexToBeChanged).setFinalVertex(newVertexOnTheRoute);
            myRoutes.get(indexToBeChanged).setVertexDistances(myCities.get(myRoutes.get(indexToBeChanged).getInitialVertex()).getDistancias().get(newVertexOnTheRoute));

            //inserindo o novo vértice na rota e reposicionando(deslocando para a direita) todos os demais
            Route routeAux;
            for (int i = (indexToBeChanged + 1); i < myRoutes.size(); i++) {
                routeAux = myRoutes.get(i);
                myRoutes.set(i, route);
                route = routeAux;
            }
            myRoutes.add(route);
        }
        return myRoutes;
    }

    //método responsável por iniciar o ciclo contendo as cidades C000, C001 e C002
    private List<Route> startRoute_C000_C001_C002(List<City> myCities, int[] citiesOnRoute) {
        List<Route> myRoutes = new ArrayList<>();

        //iniciando ciclo hamiltoniano com 3 vértices, partindo de C000 e retornando ao mesmo...
        for (int i = 0; i <= 2; i++) {
            Route route = new Route();

            citiesOnRoute[i] = i;
            route.setInitialVertex(i);

            if (i == 2) {
                route.setFinalVertex((0));
                route.setVertexDistances(myCities.get(i).getDistancias().get((0)));
            } else {
                route.setFinalVertex((i + 1));
                route.setVertexDistances(myCities.get(i).getDistancias().get((i + 1)));
            }

            myRoutes.add(route);
        }

        return myRoutes;
    }

    public List<Route> getParametersForSimulatedAnnealing (List<City> myCities, List<Route> initialRoute){
        //declaração de variáveis/parâmetros
        input = new Scanner(System.in);
        double decrementTemperature = decreaseInTemperature();
        double initialTemperature = 0;
        double finalTemperature = 0;
        int numberOfNeighbors = 0;
        int totalCostActualRoute = getRouteCost(initialRoute);

        //obtendo informações sobre a temperatura inicial
        System.out.print("Informe a temperatura inicial(>0.0): ");
        try{
            initialTemperature = input.nextDouble();
            if(initialTemperature <= 0){
                JOptionPane.showMessageDialog(null, "A temperatura inicial deve, obrigatoriamente, ser maior que zero.", "Falha", JOptionPane.ERROR_MESSAGE);
                Console.cleanDisplay();
                return new ArrayList<>();
            }
        }catch (InputMismatchException ie){
            JOptionPane.showMessageDialog(null, "O valor informado é inválido!\n\nAplicação será encerrada!", "Falha", JOptionPane.ERROR_MESSAGE);
            Console.endsApplication();
        }

        //obtendo informações sobre a temperatura final
        System.out.print("Informe a temperatura final(>=0.0 e <" + initialTemperature + "): ");
        try{
            finalTemperature = input.nextDouble();
            if(finalTemperature < 0 || finalTemperature >= initialTemperature){
                JOptionPane.showMessageDialog(null, "A temperatura final deve, obrigatoriamente, ser maior ou igual a zero e também menor que a temperatura inicial[0.0 ~ " + initialTemperature + "].", "Falha", JOptionPane.ERROR_MESSAGE);
                Console.cleanDisplay();
                return new ArrayList<>();
            }
        }catch (InputMismatchException ie){
            JOptionPane.showMessageDialog(null, "O valor informado é inválido!\n\nAplicação será encerrada!", "Falha", JOptionPane.ERROR_MESSAGE);
            Console.endsApplication();
        }

        //obtendo informações sobre a quantidade de vizinhos que serão avaliados
        System.out.print("Informe a quantidade de vizinhos(>0 e <=" + initialRoute.size() + "): ");
        try{
            numberOfNeighbors = input.nextInt();
            if(numberOfNeighbors <= 0 || numberOfNeighbors > initialRoute.size()){
                JOptionPane.showMessageDialog(null, "A vizinhança a ser avaliada deve, obrigatoriamente, ser maior que zero e menor que ou igual a " + initialRoute.size() + ".", "Falha", JOptionPane.ERROR_MESSAGE);
                Console.cleanDisplay();
                return new ArrayList<>();
            }
        }catch (InputMismatchException ie){
            JOptionPane.showMessageDialog(null, "O valor informado é inválido!\n\nAplicação será encerrada!", "Falha", JOptionPane.ERROR_MESSAGE);
            Console.endsApplication();
        }

        Console.cleanDisplay();

        System.out.println("A quantidade de vizinhos fornecidos é de: " + numberOfNeighbors);
        System.out.println("A temperatura inicial fornecida é de: " + initialTemperature);
        System.out.println("A temperatura final fornecida é de: " + finalTemperature);
        System.out.printf("A temperatura será decrementada de %.2fºC em %.2fºC graus.\n", decrementTemperature, decrementTemperature);
        System.out.println("A rota inicial possui um custo de: " + totalCostActualRoute);

        return simulatedAnnealingMetaHeuristicTSP(myCities, initialRoute, initialTemperature, finalTemperature, numberOfNeighbors, decrementTemperature, totalCostActualRoute);
    }

    //heurística da inserção mais próxima
    private List<Route> simulatedAnnealingMetaHeuristicTSP(List<City> myCities, List<Route> initialRoute, double initialTemperature, double finalTemperature, int numberOfNeighbors, double decrementTemperature, int totalCostActualRoute) {
        //variáveis auxiliares
        int[] mySwappedCities =  new int[numberOfNeighbors];
        int[] costToAddAndRemove =  new int[2];
        List<Route> actualRoute = initialRoute;
        List<Route> bestRoute = initialRoute;
        int totalCostBestRoute = totalCostActualRoute;
        int totalCostNewRoute = totalCostActualRoute;

        //inicializando vetor de cidades trocadas com um índice inválido(-1)
        //caso contrário ele nunca permitiria trocas no índice zero
        restartIndexesVector(mySwappedCities);

        while(initialTemperature > finalTemperature){
            for(int evaluateNeighbors = 0; evaluateNeighbors < numberOfNeighbors; evaluateNeighbors++){
                //obtendo rota a partir de uma vizinhança
                List<Route> newRoute =  generateNeighboringRoute(actualRoute, mySwappedCities, myCities, costToAddAndRemove);

                //atualizando custo da nova rota
                totalCostNewRoute += costToAddAndRemove[ZERO];
                totalCostNewRoute -= costToAddAndRemove[UM];

                if((totalCostNewRoute - totalCostActualRoute) < 0){
                    actualRoute = newRoute;
                    totalCostActualRoute = totalCostNewRoute;
                    if(totalCostNewRoute < totalCostBestRoute){
                        bestRoute = newRoute;
                        totalCostBestRoute = totalCostNewRoute;
                    }
                }else{
                    double randomNumber = getRandomDoubleValue();
                    double expValue = Math.exp(((totalCostNewRoute - totalCostActualRoute) * - UM) / initialTemperature);
                    if(randomNumber < expValue){
                        actualRoute = newRoute;
                    }
                }
            }

            //atualizando vetor de cidades já trocadas
            restartIndexesVector(mySwappedCities);

            //atualiza a temperatura
            initialTemperature -= decrementTemperature;
        }

        return bestRoute;
    }

    private List<Route> generateNeighboringRoute(List<Route> actualRoute, int[] mySwappedCities, List<City> myCities, int[] costToAddAndRemove){
        //sorteando as cidades da rota que serão alteradas
        int swappedCities1;
        int swappedCities2 = random.nextInt((actualRoute.size() - 1));
        int swappedCities3;
        costToAddAndRemove[ZERO] = ZERO;
        costToAddAndRemove[UM] = ZERO;

        //este while serve para não permitir trocas iguais na rota
        //ele valida se já houve a troca entre as cidades sorteadas
        while(citiesSwitched(swappedCities2, mySwappedCities)){
            swappedCities2 = random.nextInt((actualRoute.size() - 1));
        }

        //nova rota gerada após as trocas das cidades
        List<Route> newRoute = actualRoute;

        //atualizando índices da rota que serão trocados
        if(swappedCities2 == 0){
            swappedCities3 = 1;
            swappedCities1 = (actualRoute.size() - 1);
        }else if(swappedCities2 == (actualRoute.size() - 1)){
            swappedCities3 = 0;
            swappedCities1 = (swappedCities2 - 1);
        }else{
            swappedCities3 = (swappedCities2 + 1);
            swappedCities1 = (swappedCities2 - 1);
        }

        System.out.println("swappedCities1: " + swappedCities1);
        System.out.println("swappedCities2: " + swappedCities2);
        System.out.println("swappedCities3: " + swappedCities3);

        printRouteTSP(actualRoute,3);

        /*GERANDO NOVA ROTA, OU SEJA, UMA NOVA ROTA ALTERANDO PARA UMA NOVA VIZINHANÇA*/
        //obtendo custos a ser removidos da rota
        costToAddAndRemove[UM] += newRoute.get(swappedCities1).getVertexDistances();
        costToAddAndRemove[UM] += newRoute.get(swappedCities2).getVertexDistances();
        costToAddAndRemove[UM] += newRoute.get(swappedCities3).getVertexDistances();

        //variáveis auxiliares nas trocas
        Route rt1 =  new Route();
        Route rt2 =  new Route();
        Route rt3 =  new Route();

        //índice central da rota dó troca de posição, custo mantém-se igual
        rt2.setFinalVertex(newRoute.get(swappedCities2).getInitialVertex());
        rt2.setInitialVertex(newRoute.get(swappedCities2).getFinalVertex());
        rt2.setVertexDistances(newRoute.get(swappedCities2).getVertexDistances());
        newRoute.set(swappedCities2, rt2);

        costToAddAndRemove[ZERO] += rt2.getVertexDistances();

        //atualizando primeiro índice
        rt1.setInitialVertex(newRoute.get(swappedCities1).getInitialVertex());
        rt1.setFinalVertex(newRoute.get(swappedCities3).getInitialVertex());
        rt1.setVertexDistances(myCities.get(rt1.getInitialVertex()).getDistancias().get(rt1.getFinalVertex()));
        newRoute.set(swappedCities1, rt1);

        costToAddAndRemove[ZERO] += rt1.getVertexDistances();

        //atualizando terceiro índice
        rt3.setInitialVertex(newRoute.get(swappedCities1).getFinalVertex());
        rt3.setFinalVertex(newRoute.get(swappedCities3).getFinalVertex());
        rt3.setVertexDistances(myCities.get(rt3.getInitialVertex()).getDistancias().get(rt3.getFinalVertex()));
        newRoute.set(swappedCities3, rt3);

        costToAddAndRemove[ZERO] += rt3.getVertexDistances();

        printRouteTSP(newRoute,3);

        return newRoute;
    }

    private void restartIndexesVector(int[] mySwappedCities){
        Arrays.fill(mySwappedCities, -UM);
    }

    private boolean citiesSwitched(int swappedCities, int[] mySwappedCities) {
        for (int mySwappedCity : mySwappedCities) {
            if (swappedCities == mySwappedCity) {
                return true;
            }
        }
        return false;
    }

    private int getRouteCost(List<Route> myRoute){
        int totalRouteCost = 0;

        for(Route route: myRoute){
            totalRouteCost += route.getVertexDistances();
        }

        return totalRouteCost;
    }

    private double decreaseInTemperature(){
        double decrementTemperature = random.nextDouble();

        while(decrementTemperature < 0.5 || decrementTemperature > 0.9){
            decrementTemperature = random.nextDouble();
        }

        return decrementTemperature;
    }

    private double getRandomDoubleValue(){
        double randomValue = random.nextDouble();

        while(randomValue < 0 || randomValue > 1.0){
            randomValue = random.nextDouble();
        }

        return randomValue;
    }

    //imprimindo a rota com a formatação adequada
    public void printRouteTSP(List<Route> myRoutes, int routeType) {
        int totalDistance = 0;

        //imprimindo cabeçalho de informações
        if(routeType == 3){
            System.out.println("Iniciando impressão da rota gerada pela Heurística do Vizinho Mais Próximo...");
            System.out.println("A primeira cidade a entrar na rota foi a C000.");
        }else if(routeType == 4){
            System.out.println("Iniciando impressão da rota gerada pela Heurística da Inserção Mais Próxima...");
            System.out.println("O ciclo hamiltoniano inicial tinha apenas as cidades C000, C001 e C002.");
        }else if(routeType == 5){
            System.out.println("Iniciando impressão da rota gerada pela Meta-Heurística Simulated Annealing com Vizinho Mais Próximo...");
            System.out.println("A primeira cidade a entrar na rota foi a C000.");
        }else if(routeType == 6){
            System.out.println("Iniciando impressão da rota gerada pela Meta-Heurística Simulated Annealing com Inserção Mais Próxima...");
            System.out.println("O ciclo hamiltoniano inicial tinha apenas as cidades C000, C001 e C002.");
        }

        //iniciando impressão de rota
        System.out.println("Rota realizada.: ");

        System.out.println("\t\t\t\t\t+------------------------+");
        System.out.println("\t\t\t\t\t| INIT  || DIST || FINL  |");
        System.out.println("\t\t\t\t\t|-------||------||-------|");

        for (Route route : myRoutes) {
            System.out.print("\t\t\t\t\t");

            if (route.getInitialVertex() < TEN) {
                System.out.print("| C00" + route.getInitialVertex() + "  ||");
            } else if (route.getInitialVertex() < HUNDRED) {
                System.out.print("| C0" + route.getInitialVertex() + "  ||");
            } else if (route.getInitialVertex() < THOUSAND) {
                System.out.print("| C" + route.getInitialVertex() + "  ||");
            } else {
                System.out.print("| C" + route.getInitialVertex() + " ||");
            }

            if (route.getVertexDistances() < TEN) {
                System.out.print("    " + route.getVertexDistances() + " ||");
            } else if (route.getVertexDistances() < HUNDRED) {
                System.out.print("   " + route.getVertexDistances() + " ||");
            } else if (route.getVertexDistances() < THOUSAND) {
                System.out.print("  " + route.getVertexDistances() + " ||");
            } else if (route.getVertexDistances() < TEN_THOUSAND) {
                System.out.print(" " + route.getVertexDistances() + " ||");
            }

            if (route.getFinalVertex() < TEN) {
                System.out.println(" C00" + route.getFinalVertex() + "  |");
            } else if (route.getFinalVertex() < HUNDRED) {
                System.out.println(" C0" + route.getFinalVertex() + "  |");
            } else if (route.getFinalVertex() < THOUSAND) {
                System.out.println(" C" + route.getFinalVertex() + "  |");
            } else {
                System.out.println(" C" + route.getFinalVertex() + " |");
            }
            totalDistance += route.getVertexDistances();
        }
        System.out.println("\t\t\t\t\t+------------------------+");
        System.out.println("Distância Total: " + totalDistance);
    }
}
