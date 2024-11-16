/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tsp;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.io.FileNotFoundException;


public class TSP {

    int M;              // pocet vrcholov 
    int data[][];       // matica vzdialenosti
    int[] x;            // riesenie


    public int getM() {
        return M;
    }

    public int[][] getdata() {
        return data;
    }

    public void ries() {
        // tu pridajte heuristiku

        nearestNeighbor(4);
        //insertionHeuristic();
    }

    public void nearestNeighbor(int start) {
        start--; // Prevod z číslovania od 1 na indexovanie od 0
        boolean[] visited = new boolean[M]; // Sledovanie navštívených uzlov
        x[0] = start + 1; // Prvý uzol trasy (vrchol začína od 1 pre číslovanie)
        visited[start] = true; // Označíme počiatočný uzol ako navštívený
        int current = start; // Aktuálny uzol
        int totalDistance = 0; // Celková vzdialenosť trasy

        for (int i = 1; i < M; i++) {
            int nextNode = -1; // Najbližší uzol
            int minDistance = Integer.MAX_VALUE;

            // Nájdeme najbližší uzol, ktorý ešte nebol navštívený
            for (int j = 0; j < M; j++) {
                if (!visited[j] && data[current][j] < minDistance) {
                    nextNode = j;
                    minDistance = data[current][j];
                }
            }

            x[i] = nextNode + 1; // Uložíme uzol s číslovaním od 1
            visited[nextNode] = true; // Označíme ho ako navštívený
            totalDistance += minDistance; // Pripočítame vzdialenosť
            current = nextNode; // Aktualizujeme aktuálny uzol
        }

        totalDistance += data[current][start];

        // Výstup výsledkov
        System.out.println("Trasa (najbližší sused): " + Arrays.toString(x));
        System.out.println("Celková vzdialenosť: " + totalDistance);
    }


    public void insertionHeuristic() {
        java.util.List<Integer> route = new java.util.ArrayList<>();
        route.add(1); // Začíname vo vrchole 1 (pridáme 1 pre číslovanie od 1)
        route.add(1); // Počiatočná trasa: návrat do toho istého uzla

        boolean[] visited = new boolean[M];
        visited[0] = true;

        // Nájdeme prvé dva najbližšie uzly k uzlu 1
        int closest = -1;
        int secondClosest = -1;
        int minDistance = Integer.MAX_VALUE;

        for (int i = 1; i < M; i++) {
            if (data[0][i] < minDistance) {
                secondClosest = closest;
                closest = i;
                minDistance = data[0][i];
            }
        }

        route.add(1, closest + 1); // Pridáme 1, aby sme zodpovedali číslovaniu od 1
        visited[closest] = true;

        if (secondClosest != -1) {
            route.add(1, secondClosest + 1); // Pridáme 1, aby sme zodpovedali číslovaniu od 1
            visited[secondClosest] = true;
        }

        // Postupne vsúvame uzly do trasy
        for (int i = 1; i < M; i++) {
            if (!visited[i]) {
                int bestPosition = -1;
                int bestIncrease = Integer.MAX_VALUE;

                // Nájdeme najlepšiu pozíciu na vsunutie uzla
                for (int j = 0; j < route.size() - 1; j++) {
                    int currentNode = route.get(j) - 1; // Znížime index pre výpočty
                    int nextNode = route.get(j + 1) - 1; // Znížime index pre výpočty
                    int increase = data[currentNode][i] + data[i][nextNode] - data[currentNode][nextNode];
                    if (increase < bestIncrease) {
                        bestIncrease = increase;
                        bestPosition = j + 1;
                    }
                }

                // Vsunieme uzol na najlepšiu pozíciu
                route.add(bestPosition, i + 1); // Pridáme 1, aby sme zodpovedali číslovaniu od 1
                visited[i] = true;
            }
        }

        // Výpočet celkovej vzdialenosti
        int totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            int currentNode = route.get(i) - 1; // Znížime index pre výpočet
            int nextNode = route.get(i + 1) - 1; // Znížime index pre výpočet
            totalDistance += data[currentNode][nextNode];
        }

        System.out.println("Trasa (vsúvacia heuristika): " + route);
        System.out.println("Celková vzdialenosť: " + totalDistance);
    }


    public void read_file(File file) {
        try {

            BufferedReader bfr = new BufferedReader(new FileReader(file));
            String line = "";
            int line_index = 0;
            int row = 0, col = 0;
            while ((line = bfr.readLine()) != null) {
                if (line_index == 0) {
                    M = Integer.parseInt(line);
                    data = new int[M][M];
                    x = new int[M];
                    for (int i = 0; i < M; i++) {
                        x[i] = 0;
                    }
                } else {
                    StringTokenizer st = new StringTokenizer(line, " ");
                    col = 0;
                    while (st.hasMoreTokens()) {
                        data[row][col] = Integer.parseInt(st.nextToken());
                        System.out.println("number[" + row + "][" + col + "]:" + data[row][col]);
                        col++;
                    }
                    row++;
                }
                line_index = line_index + 1;
            }
            bfr.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("error " + e);
        }
    }
}
