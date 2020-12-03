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
        int[] citiesOnRoute = new int[myCities.size()];
        List<Route> myRoutes = startRoute_C000_C001_C002(myCities, citiesOnRoute); //inicializando ciclo hamiltoniano com os três primeiros vértices
        int remainingCities = (myCities.size() - myRoutes.size());
        int initialCycleSize = myRoutes.size();

        //este for realizará iterações até que todas as cidades sejam inseridas na rota
        for (int newCity = ZERO; newCity < remainingCities; newCity++) {
            int actualDistance = Integer.MAX_VALUE;

            //este for percorrerá cada cidade da rota para identificar a nova cidade mais próxima
            for (Route myRoute : myRoutes) {

                //este for representa a cidade atual, tem como finalidade validar se o mesmo já está na rota e se a distância satisfaz as condições mínimas
                //pula os índices 0, 1 e 2 pois estes ja estão no ciclo hamiltoniano inicial
                for (int actualCity = initialCycleSize; actualCity < myCities.size(); actualCity++) {

                    //validando se a nova cidade deve ou não pertencer à rota
                    if ((citiesOnRoute[actualCity] != actualCity/*Validando se a cidade já não existe na rota*/) &&
                            (myCities.get(actualCity).getDistancias().get(myRoute.getFinalVertex()) < actualDistance)) {

                        //setando informações atualizadas
                        actualDistance = myCities.get(actualCity).getDistancias().get(myRoute.getFinalVertex());
                        newVertexOnTheRoute = actualCity;
                    }
                }
            }

            //atualizando vetor de cidades que já entraram na rota
            citiesOnRoute[newVertexOnTheRoute] = newVertexOnTheRoute;

            //adicionando nova cidade na rota
            int beforeCost = Integer.MAX_VALUE, routeSize = myRoutes.size(), actualCost;
            List<Route> bestRoute = new ArrayList<>();
            for(int routePossibility = ZERO; routePossibility < routeSize; routePossibility++){
                List<Route> routeCopy = copyRoute(myRoutes);

                //inserindo novo vertice
                Route route = new Route();
                route.setInitialVertex(routeCopy.get(routePossibility).getInitialVertex());
                route.setFinalVertex(newVertexOnTheRoute);
                route.setVertexDistances(myCities.get(newVertexOnTheRoute).getDistancias().get(route.getInitialVertex()));

                //atualizando vertice que já existia
                routeCopy.get(routePossibility).setInitialVertex(newVertexOnTheRoute);
                routeCopy.get(routePossibility).setVertexDistances(myCities.get(routeCopy.get(routePossibility).getFinalVertex()).getDistancias().get(newVertexOnTheRoute));

                //posicionando cidades nas posições corretas
                //realizando descolamentos para a direita
                Route routeAux;
                for (int positioningARoute = routePossibility; positioningARoute < routeSize; positioningARoute++) {
                    routeAux = routeCopy.get(positioningARoute);
                    routeCopy.set(positioningARoute, route);
                    route = routeAux;
                }
                routeCopy.add(route);

                //validando custo da atual rota
                actualCost = getRouteCost(routeCopy);
                if(actualCost < beforeCost){
                    bestRoute = routeCopy;
                    beforeCost = actualCost;
                }
            }
            myRoutes = bestRoute;
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
        System.out.print("Informe a quantidade de vizinhos(>0): ");
        try{
            numberOfNeighbors = input.nextInt();
            if(numberOfNeighbors <= 0){
                JOptionPane.showMessageDialog(null, "A vizinhança a ser avaliada deve, obrigatoriamente, ser maior que zero.", "Falha", JOptionPane.ERROR_MESSAGE);
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

        return simulatedAnnealingMetaHeuristicTSP(myCities, initialRoute, initialTemperature, finalTemperature, numberOfNeighbors, decrementTemperature);
    }

    //heurística da inserção mais próxima
    private List<Route> simulatedAnnealingMetaHeuristicTSP(List<City> myCities, List<Route> initialRoute, double initialTemperature, double finalTemperature, int numberOfNeighbors, double decrementTemperature) {
        //variáveis auxiliares
        List<Route> actualRoute = copyRoute(initialRoute);
        List<Route> bestRoute = copyRoute(initialRoute);

        int totalCostActualRoute = getRouteCost(initialRoute);
        int totalCostBestRoute = getRouteCost(initialRoute);

        while(initialTemperature > finalTemperature){
            for(int evaluateNeighbors = 0; evaluateNeighbors < numberOfNeighbors; evaluateNeighbors++){
                //obtendo rota a partir de uma vizinhança
                List<Route> bestNeighbor = returnsBestNeighbor(actualRoute, myCities);
                int totalCostNewRoute = getRouteCost(bestNeighbor);

                int deltaCost = (totalCostNewRoute - totalCostActualRoute);

                if(deltaCost < 0){
                    actualRoute = copyRoute(bestNeighbor);
                    totalCostActualRoute = getRouteCost(bestNeighbor);

                    if(totalCostNewRoute <= totalCostBestRoute){
                        bestRoute = copyRoute(bestNeighbor);
                        totalCostBestRoute = getRouteCost(bestNeighbor);
                    }
                }else{
                    double randomNumber = getRandomDoubleValue();
                    double expValue = Math.exp((-deltaCost)/initialTemperature);

                    if(randomNumber < expValue){
                        actualRoute = copyRoute(bestNeighbor);
                        totalCostActualRoute = totalCostNewRoute;
                    }
                }
            }

            //atualiza a temperatura
            initialTemperature -= decrementTemperature;
        }

        return bestRoute;
    }

    public List<Route> returnsBestNeighbor(List<Route> actualRoute, List<City> myCities){
        List<Route> bestNeighbor;
        List<Route> newRoute =  generateNeighboringRoute_TwoCitiesRandom(actualRoute, myCities);
        List<Route> newRoute1 = generateNeighboringRoute_TwoConsecutiveCities(newRoute, myCities);
        List<Route> newRoute2 = generateNeighboringRoute_TwoConsecutiveCities(newRoute, myCities);

        int totalCostNewRoute = getRouteCost(newRoute);
        int totalCostNewRoute_1 = getRouteCost(newRoute1);
        int totalCostNewRoute_2 = getRouteCost(newRoute2);

        if(totalCostNewRoute < totalCostNewRoute_1){
            bestNeighbor = copyRoute(newRoute);
        }else{
            if(totalCostNewRoute_1 < totalCostNewRoute_2){
                bestNeighbor = copyRoute(newRoute1);
            }else{
                bestNeighbor = copyRoute(newRoute);
            }
        }

        return bestNeighbor;
    }

    private List<Route> copyRoute(List<Route> routeOriginal){
        List<Route> routeCopied = new ArrayList<>();

        for (Route route : routeOriginal) {
            Route rt = new Route();

            rt.setInitialVertex(route.getInitialVertex());
            rt.setVertexDistances(route.getVertexDistances());
            rt.setFinalVertex(route.getFinalVertex());

            routeCopied.add(rt);
        }

        return routeCopied;
    }

    private List<Route> generateNeighboringRoute_TwoCitiesRandom(List<Route> actualRoute, List<City> myCities){
        //sorteando as cidades da rota que serão alteradas
        int firstIndexRandom = random.nextInt(actualRoute.size() - 4) + 2;
        int secondIndexRandom = random.nextInt(actualRoute.size() - 4) + 2;

        if(firstIndexRandom == secondIndexRandom){
            while(firstIndexRandom == secondIndexRandom){
                secondIndexRandom = random.nextInt(actualRoute.size() - 4) + 2;
            }
        }

        //nova rota gerada após as trocas das cidades
        List<Route> newRoute = copyRoute(actualRoute);

        int cityAux = newRoute.get(secondIndexRandom).getFinalVertex();

        newRoute.get(secondIndexRandom).setFinalVertex(newRoute.get(firstIndexRandom).getFinalVertex());
        newRoute.get(secondIndexRandom).setVertexDistances(myCities.get(newRoute.get(secondIndexRandom).getInitialVertex()).getDistancias().get(newRoute.get(secondIndexRandom).getFinalVertex()));

        newRoute.get(secondIndexRandom + 1).setInitialVertex(newRoute.get(firstIndexRandom).getFinalVertex());
        newRoute.get(secondIndexRandom + 1).setVertexDistances(myCities.get(newRoute.get(secondIndexRandom + 1).getInitialVertex()).getDistancias().get(newRoute.get(secondIndexRandom + 1).getFinalVertex()));

        newRoute.get(firstIndexRandom).setFinalVertex(cityAux);
        newRoute.get(firstIndexRandom).setVertexDistances(myCities.get(newRoute.get(firstIndexRandom).getInitialVertex()).getDistancias().get(newRoute.get(firstIndexRandom).getFinalVertex()));

        newRoute.get(firstIndexRandom + 1).setInitialVertex(cityAux);
        newRoute.get(firstIndexRandom + 1).setVertexDistances(myCities.get(newRoute.get(firstIndexRandom + 1).getInitialVertex()).getDistancias().get(newRoute.get(firstIndexRandom + 1).getFinalVertex()));

        return newRoute;
    }

    //gera uma vizinhança pra duas cidades consecutivas
    private List<Route> generateNeighboringRoute_TwoConsecutiveCities(List<Route> actualRoute, List<City> myCities){
        //sorteando as cidades da rota que serão alteradas
        int swappedCities1;
        int swappedCities2 = random.nextInt(actualRoute.size());
        int swappedCities3;

        //nova rota gerada após as trocas das cidades
        List<Route> newRoute = copyRoute(actualRoute);

        //atualizando índices da rota que serão trocados
        if(swappedCities2 == 0){
            swappedCities3 = 1;
            swappedCities1 = (actualRoute.size() - 1);
        }else if(swappedCities2 == (actualRoute.size() - 1)){
            swappedCities3 = 0;
            swappedCities1 = (swappedCities2 - 1);
        }else{
            swappedCities1 = (swappedCities2 - 1);
            swappedCities3 = (swappedCities2 + 1);
        }

        //variáveis auxiliares nas trocas
        Route rt1 =  new Route();
        Route rt2 =  new Route();
        Route rt3 =  new Route();

        //índice central da rota dó troca de posição, custo mantém-se igual
        rt2.setFinalVertex(newRoute.get(swappedCities2).getInitialVertex());
        rt2.setInitialVertex(newRoute.get(swappedCities2).getFinalVertex());
        rt2.setVertexDistances(newRoute.get(swappedCities2).getVertexDistances());

        //atualizando primeiro índice
        rt1.setInitialVertex(newRoute.get(swappedCities1).getInitialVertex());
        rt1.setFinalVertex(newRoute.get(swappedCities3).getInitialVertex());
        rt1.setVertexDistances(myCities.get(rt1.getInitialVertex()).getDistancias().get(rt1.getFinalVertex()));

        //atualizando terceiro índice
        rt3.setInitialVertex(newRoute.get(swappedCities1).getFinalVertex());
        rt3.setFinalVertex(newRoute.get(swappedCities3).getFinalVertex());
        rt3.setVertexDistances(myCities.get(rt3.getInitialVertex()).getDistancias().get(rt3.getFinalVertex()));

        newRoute.set(swappedCities1, rt1);
        newRoute.set(swappedCities2, rt2);
        newRoute.set(swappedCities3, rt3);

        return newRoute;
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
