package br.uem.din.moa.Controller;

import br.uem.din.moa.Model.City;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class FileController {
    Scanner input = new Scanner(System.in);

    public List<City> importCities(List<City> myCities){
        String pathFile;
        City actualCity = new City();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Texto (*.txt)", "txt"));
        fileChooser.showDialog(fileChooser, "Abrir arquivo texto");

        try{
            pathFile = fileChooser.getSelectedFile().toString().trim();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Nenhum arquivo foi selecionado ou arquivo inválido.", "Falha", JOptionPane.ERROR_MESSAGE);
            return myCities;
        }

        //abrindo arquivo para leitura
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(pathFile))){

            //obtendo dados da primeira linha do arquivo texto
            String completeLine = bufferedReader.readLine().trim();
            String[] distances = completeLine.split(",");

            //setando dados da primeira linha
            for(int i = 0; i < distances.length; i++){
                actualCity.getDistancias().add(i, Integer.parseInt(distances[i].trim()));
            }
            myCities.add(actualCity);

            //variavel auxiliar para leitura das demais linhas
            int count = (distances.length - 1);

            while(count > 0){
                count--;
                actualCity = new City();

                //obtendo dados das demais linhas
                completeLine = bufferedReader.readLine().trim();
                distances = completeLine.split(",");

                //setando dados das demais linhas
                for(int i = 0; i < distances.length; i++){
                    actualCity.getDistancias().add(i, Integer.parseInt(distances[i].trim()));
                }
                myCities.add(actualCity);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Falha ao ler arquivo!\nPrograma Abortado\nErro: ".concat(e.getMessage().trim()), "Falha ao ler arquivo", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        JOptionPane.showMessageDialog(null, "O arquivo foi importado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        return myCities;
    }

    public void makeCitiesFile() throws IOException {
        input = new Scanner(System.in);
        int numberOfCities;
        int maxDistanceValue;
        int distanceRandom;
        Random random = new Random();
        String pathFile;
        StringBuilder textFile = new StringBuilder();
        JFileChooser selectPathFile = new JFileChooser();

        System.out.print("Quantas cidades há no problema?(> 0 e < 1000)\nR:");

        try {
            numberOfCities = input.nextInt();
        } catch (InputMismatchException e) {
            JOptionPane.showMessageDialog(null, "O valor informado é inválido.", "Falha", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.print("\n\nQual a distância máxima entre as cidades?(> 0 e < 10.000)\nR:");

        try {
            maxDistanceValue = input.nextInt();
        } catch (InputMismatchException e) {
            JOptionPane.showMessageDialog(null, "O valor informado é inválido.", "Falha", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("numberOfCities: " + numberOfCities);
        System.out.println("maxDistanceValue: " + maxDistanceValue);

        //realizando validações para garantir que algum dado será gerado, contudo, que não atrapalhe a formatação
        if(numberOfCities > 999 || maxDistanceValue > 9999 || numberOfCities <= 0 || maxDistanceValue <= 0){
            JOptionPane.showMessageDialog(null, "O número de cidades ou a distância entre as cidades não são válidos.", "Falha", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //declarando matriz
        int[][] matriz =  new int[numberOfCities][numberOfCities];

        //gerando dados das novas cidades
        for(int  i = 0; i < numberOfCities; i++){
            for(int j = (i + 1); j < numberOfCities; j++){
                if(i != j){
                    distanceRandom = random.nextInt(maxDistanceValue + 1);

                    //distância zero deve ser permitida somente quando i == j
                    if(distanceRandom == 0){
                        j--;
                    }else{
                        matriz[i][j] = distanceRandom;
                        matriz[j][i] = distanceRandom;
                    }
                }
            }
        }

        //setando extensão dos filtros
        selectPathFile.setFileFilter(new FileNameExtensionFilter("Texto (*.txt)", "txt"));

        //selecionando caminho e nome do arquivo .txt
        if (selectPathFile.showDialog(null, "Salvar") == JFileChooser.APPROVE_OPTION) {
            pathFile = (selectPathFile.getSelectedFile().getPath()).trim();
            pathFile = pathFile.replaceAll(".txt", "");
            pathFile = pathFile.concat(".txt");
        } else {
            JOptionPane.showMessageDialog(null, "As novas cidades não foram salvas em arquivo .txt.", "Falha", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //escrevendo o arquivo no caminho pré selecionado
        try (FileWriter fl = new FileWriter(pathFile.trim())) {
            PrintWriter saveFile = new PrintWriter(fl);

            for(int i = 0; i < numberOfCities; i++){
                for(int j = 0; j < numberOfCities; j++){
                    if(j == (numberOfCities - 1)){
                        textFile.append(matriz[i][j]).append("\n");
                    }else{
                        textFile.append(matriz[i][j]).append(",");
                    }
                }
            }
            saveFile.printf(textFile.toString());
        }
        JOptionPane.showMessageDialog(null, "As cidades foram geradas com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
}
