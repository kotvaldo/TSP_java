/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tsp;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.io.FileNotFoundException;

import static java.util.Collections.swap;


public class TSP {

    int M;              // pocet vrcholov 
    int data[][];       // matica vzdialenosti
    int[] x;            // riesenie
    int distance;
    int minIndex;
    public int getM() {
        return M;
    }

    public int[][] getdata() {
        return data;
    }

    public void ries() {
        //nearestNeighbor(0);

        int[] minX = new int[M + 1]; // O 1 väčšie pre návrat na štart
        int minDistance = Integer.MAX_VALUE;
        int minIndex = -1;

        // Pre každý vrchol voláme `nearestNeighbor`
        for (int i = 0; i < M; i++) {
            nearestNeighbor(i); // Vypočíta trasu so štartom v uzle `i`
            if (distance < minDistance) { // Porovnáme dĺžku trasy
                minDistance = distance;
                minX = Arrays.copyOf(x, x.length); // Skopírujeme trasu
                minIndex = i;
            }
        }

        x = minX;
        distance = minDistance;

        System.out.println("Najmenšia cesta zo všetkých je z vrchola: " + minIndex + "," + Arrays.toString(x));
        System.out.println("Dlzka pola pred vymenou: " + x.length);
        System.out.println("Dĺžka tej cesty: " + distance);

        swapAndEvaluate();
        System.out.println("Trasa po výmene: "+ Arrays.toString(x));
        System.out.println("Dlzka pola po výmene: " + x.length);
        System.out.println("Dĺžka tej cesty: " + distance);

        //insertionHeuristic(0);
    }

    public void nearestNeighbor(int start) {
        boolean[] visited = new boolean[M + 1]; // Sledovanie navštívených uzlov
        x[0] = start; // Prvý uzol trasy (vrchol začína od 1 pre číslovanie)
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

            x[i] = nextNode; // Uložíme uzol s číslovaním od 1
            visited[nextNode] = true; // Označíme ho ako navštívený
            totalDistance += minDistance; // Pripočítame vzdialenosť
            current = nextNode; // Aktualizujeme aktuálny uzol
        }

        totalDistance += data[current][start];
        x[x.length - 1] = start;
        // Výstup výsledkov
        //System.out.println("Trasa (najbližší sused): " + Arrays.toString(x));
        //System.out.println("Celková vzdialenosť: " + totalDistance);
        distance = totalDistance;
    }


    public void insertionHeuristic(int startNode) {
        List<Integer> route = new ArrayList<>();
        route.add(startNode);
        boolean[] visited = new boolean[M];
        visited[startNode] = true;

        int closest = -1;
        int minDistance = Integer.MAX_VALUE;

        for (int i = 0; i < M; i++) {
            if (!visited[i] && data[startNode][i] < minDistance) {
                closest = i;
                minDistance = data[startNode][i];
            }
        }

        route.add(closest);
        visited[closest] = true;

        int secondClosest = -1;
        for (int i = 0; i < M; i++) {
            if (!visited[i] && data[closest][i] < minDistance) {
                secondClosest = i;
                minDistance = data[closest][i];
            }

        }

        route.add(secondClosest); // Pridáme druhý najbližší uzol
        visited[secondClosest] = true;

        route.add(startNode); // Začíname vo vrchole
        // Postupne vsúvame uzly do trasy
        while (route.size() < M + 1) {
            int candidateNode = -1;
            int bestIncrease = Integer.MAX_VALUE;
            int bestPosition = -1;

            for (int i = 0; i < M; i++) {
                if (!visited[i]) {
                    for (int j = 0; j < route.size() - 1; j++) {
                        int increase = data[route.get(j)][i] + data[i][route.get(j + 1)] - 			data[route.get(j)][route.get(j + 1)];

                        if (increase < bestIncrease) {
                            candidateNode = i;
                            bestIncrease = increase;
                            bestPosition = j + 1;
                        }
                    }
                }
            }

            // Insert the candidate node
            route.add(bestPosition, candidateNode);
            visited[candidateNode] = true;
        }


        int totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            int currentNode = route.get(i); // Aktuálny uzol
            int nextNode = route.get(i + 1); // Nasledujúci uzol
            totalDistance += data[currentNode][nextNode];
        }


        x = route.stream().mapToInt(i -> i).toArray();

        // Výstup výsledkov
        System.out.println("Trasa (vsúvacia heuristika): " + route);
        System.out.println("Celková vzdialenosť: " + totalDistance);
    }



    public void swapAndEvaluate() {
        int[] bestSwapPath = Arrays.copyOf(x, x.length);
        int bestSwapDistance = distance;

        for (int i = 1; i < M - 1; i++) {
            for (int j = i + 1; j < M - 1; j++) {
                int deltaCost = calculateDeltaCost(i, j);

                if (deltaCost < 0) {
                    swapNodes(i, j);
                    int newDistance = calculateTotalDistance(x);

                    if (newDistance < bestSwapDistance) {
                        bestSwapDistance = newDistance;
                        bestSwapPath = Arrays.copyOf(x, x.length);
                    }

                    // Undo the swap to continue checking other pairs
                    swapNodes(i, j);
                }
            }
        }

        // Update the solution with the best swap result
        x = bestSwapPath;
        distance = bestSwapDistance;
    }

    // Calculate ΔCost for swapping two nodes in the path
    private int calculateDeltaCost(int i, int j) {
        int iPrev = (i - 1 + M) % M;
        int iNext = (i + 1) % M;
        int jPrev = (j - 1 + M) % M;
        int jNext = (j + 1) % M;

        int originalCost = data[x[iPrev]][x[i]] + data[x[i]][x[iNext]] + data[x[jPrev]][x[j]] + data[x[j]][x[jNext]];
        int newCost = data[x[iPrev]][x[j]] + data[x[j]][x[iNext]] + data[x[jPrev]][x[i]] + data[x[i]][x[jNext]];

        return newCost - originalCost;
    }

    // Swap nodes i and j in the path
    private void swapNodes(int i, int j) {
        int temp = x[i];
        x[i] = x[j];
        x[j] = temp;
    }

    // Calculate the total distance of the current path
    private int calculateTotalDistance(int[] path) {
        int distance = 0;
        for (int k = 0; k < path.length - 1; k++) {
            distance += data[path[k]][path[k + 1]];
        }
        return distance;
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
