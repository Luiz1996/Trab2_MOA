package br.uem.din.moa.View;

import br.uem.din.moa.Controller.TravellingSalesmanController;
import br.uem.din.moa.Controller.CityController;
import br.uem.din.moa.Controller.FileController;
import br.uem.din.moa.Model.City;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        List<City> myCities = new ArrayList<>();
        FileController fileController = new FileController();
        CityController cityController = new CityController();
        TravellingSalesmanController travellingSalesmanController = new TravellingSalesmanController();

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
                        travellingSalesmanController.nearestNeighborHeuristicTSP(myCities);
                    }else{
                        JOptionPane.showMessageDialog(null, "Use a Opção 1 para importar as cidades na aplicação.", "Falha", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 4:
                    if(myCities.size() > 0){
                        if(myCities.size() > 2){
                            travellingSalesmanController.nearestInsertionHeuristicTSP(myCities);
                        }else{
                            JOptionPane.showMessageDialog(null, "Para gerar o ciclo Hamiltoniano inicial, o arquivo deve conter ao menos 3 cidades!", "Falha", JOptionPane.ERROR_MESSAGE);
                        }
                    }else{
                        JOptionPane.showMessageDialog(null, "Use a Opção 1 para importar as cidades na aplicação.", "Falha", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case 5:
                    myCities = new ArrayList<>();
                    JOptionPane.showMessageDialog(null, "As cidades foram apagadas com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 6:
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
