package br.uem.din.moa.Controller;

import br.uem.din.moa.Model.City;
import br.uem.din.moa.Model.Route;

import java.util.ArrayList;
import java.util.List;

public class TravellingSalesmanController {
    final int ZERO = 0;
    final int TEN = 10;
    final int HUNDRED = 100;
    final int THOUSAND = 1000;
    final int TEN_THOUSAND = 10000;
    int totalDistance;

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

    //heurística da inserção mais próxima
    public List<Route> simulatedAnnealingMetaHeuristicTSP(List<City> myCities, List<Route> initialRoute) {
        List<Route> myRoute = new ArrayList<>();

        //corrigir retorno depois
        //return myRoute;
        return initialRoute;
    }

    //imprimindo a rota com a formatação adequada
    public void printRouteTSP(List<Route> myRoutes, int routeType) {
        totalDistance = 0;
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
