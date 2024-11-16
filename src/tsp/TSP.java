/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tsp;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
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

        nearestNeighbor(0);
        //insertionHeuristic();
    }

    public void nearestNeighbor(int start) {
        boolean[] visited = new boolean[M]; // Sledovanie navštívených uzlov
        x[0] = start; // Začíname vo vrchole "start"
        visited[start] = true;
        int current = start;
        int totalDistance = 0;

        for (int i = 1; i < M; i++) {
            int nextNode = -1;
            int minDistance = Integer.MAX_VALUE;

            // Nájdeme najbližší uzol
            for (int j = 0; j < M; j++) {
                if (!visited[j] && data[current][j] < minDistance) {
                    nextNode = j;
                    minDistance = data[current][j];
                }
            }

            // Pridáme najbližší uzol do trasy
            x[i] = nextNode;
            visited[nextNode] = true;
            totalDistance += minDistance;
            current = nextNode;
        }

        // Návrat späť do počiatočného uzla
        totalDistance += data[current][start];

        System.out.println("Trasa (najbližší sused): " + java.util.Arrays.toString(x));
        System.out.println("Celková vzdialenosť: " + totalDistance);
    }

    public void insertionHeuristic() {
        java.util.List<Integer> route = new java.util.ArrayList<>();
        route.add(0); // Začíname vo vrchole 0
        route.add(0); // Počiatočná trasa: návrat do toho istého uzla

        boolean[] visited = new boolean[M];
        visited[0] = true;

        // Nájdeme prvé dva najbližšie uzly k uzlu 0
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

        route.add(1, closest);
        visited[closest] = true;

        if (secondClosest != -1) {
            route.add(1, secondClosest);
            visited[secondClosest] = true;
        }

        // Postupne vsúvame uzly do trasy
        for (int i = 1; i < M; i++) {
            if (!visited[i]) {
                int bestPosition = -1;
                int bestIncrease = Integer.MAX_VALUE;

                // Nájdeme najlepšiu pozíciu na vsunutie uzla
                for (int j = 0; j < route.size() - 1; j++) {
                    int increase = data[route.get(j)][i] + data[i][route.get(j + 1)] - data[route.get(j)][route.get(j + 1)];
                    if (increase < bestIncrease) {
                        bestIncrease = increase;
                        bestPosition = j + 1;
                    }
                }

                // Vsunieme uzol na najlepšiu pozíciu
                route.add(bestPosition, i);
                visited[i] = true;
            }
        }

        // Výpočet celkovej vzdialenosti
        int totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += data[route.get(i)][route.get(i + 1)];
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
