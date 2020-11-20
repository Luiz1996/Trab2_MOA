package br.uem.din.moa.View;

import br.uem.din.moa.Controller.TravellingSalesmanController;
import br.uem.din.moa.Controller.CityController;
import br.uem.din.moa.Controller.FileController;
import br.uem.din.moa.Model.City;
import br.uem.din.moa.Model.Route;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        int routeType = 0;
        List<City> myCities = new ArrayList<>();
        FileController fileController = new FileController();
        CityController cityController = new CityController();
        TravellingSalesmanController travellingSalesmanController = new TravellingSalesmanController();
        List<Route> myRoute = new ArrayList<>();

        int option = Console.showMenu();
        while (option != 0) {
            Console.cleanDisplay();
            switch (option) {
                case 1:
                    if(myCities.size() > 0){
                        JOptionPane.showMessageDialog(null, "Para reimportar as cidades, use a Opção 5 e resete as informações.", "Falha", JOptionPane.ERROR_MESSAGE);
                    }else{
                        myCities = fileController.importCities(myCities);
                    }
                    break;
                case 2:
                    if(myCities.size() > 0){
                        cityController.printCities(myCities);
                    }else{
                        JOptionPane.showMessageDialog(null, "Use a Opção 1 para importar as cidades na aplicação.", "Falha", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 3:
                    if(myCities.size() > 0){
                        myRoute = travellingSalesmanController.nearestNeighborHeuristicTSP(myCities);
                        routeType = 3;
                        JOptionPane.showMessageDialog(null, "A heurística do Vizinho Mais Próximo gerou a rota com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "Use a Opção 1 para importar as cidades na aplicação.", "Falha", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 4:
                    if(myCities.size() > 0){
                        if(myCities.size() > 2){
                            myRoute = travellingSalesmanController.nearestInsertionHeuristicTSP(myCities);
                            routeType = 4;
                            JOptionPane.showMessageDialog(null, "A heurística do Inserção Mais Próxima gerou a rota com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        }else{
                            JOptionPane.showMessageDialog(null, "Para gerar o ciclo Hamiltoniano inicial, o arquivo deve conter ao menos 3 cidades!", "Falha", JOptionPane.ERROR_MESSAGE);
                        }
                    }else{
                        JOptionPane.showMessageDialog(null, "Use a Opção 1 para importar as cidades na aplicação.", "Falha", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 5:
                    if(myCities.size() > 0){
                        myRoute = travellingSalesmanController.nearestNeighborHeuristicTSP(myCities);
                        myRoute = travellingSalesmanController.simulatedAnnealingMetaHeuristicTSP(myCities, myRoute);
                        routeType = 5;
                        JOptionPane.showMessageDialog(null, "A meta-heurística Simulated Annealing com Vizinho Mais Próximo gerou a rota com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "Use a Opção 1 para importar as cidades na aplicação.", "Falha", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 6:
                    if(myCities.size() > 0){
                        myRoute = travellingSalesmanController.nearestInsertionHeuristicTSP(myCities);
                        myRoute = travellingSalesmanController.simulatedAnnealingMetaHeuristicTSP(myCities, myRoute);
                        routeType = 6;
                        JOptionPane.showMessageDialog(null, "A meta-heurística Simulated Annealing com Inserção Mais Próxima gerou a rota com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "Use a Opção 1 para importar as cidades na aplicação.", "Falha", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 7:
                    if(myRoute.size() > 0){
                        travellingSalesmanController.printRouteTSP(myRoute, routeType);
                    }else{
                        JOptionPane.showMessageDialog(null, "Nenhuma rota foi gerada, use as funções 3, 4, 5 ou 6.", "Falha", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 8:
                    myCities = new ArrayList<>();
                    myRoute = new ArrayList<>();
                    JOptionPane.showMessageDialog(null, "As informações foram apagadas com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 9:
                    fileController.makeCitiesFile();
                    Console.cleanDisplay();
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
            option = Console.showMenu();
        }
    }
}
