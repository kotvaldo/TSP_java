/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tsp;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.io.FileNotFoundException;


public class TSP {

    int M;              // pocet vrcholov 
    int[][] data;       // matica vzdialenosti
    int[] x;            // riesenie
    int distance;

    public int getM() {
        return M;
    }

    public int[][] getdata() {
        return data;
    }

    public void ries() {
        // Inicializácia trasy pomocou vsúvacej heuristiky
        System.out.println("Inicializácia trasy pomocou vsúvacej heuristiky...");
        insertionHeuristic(0); // Počiatočný uzol je 0
        System.out.println("Počiatočná trasa: " + Arrays.toString(x));
        System.out.println("Celková vzdialenosť po heuristike: " + distance);

        // Zlepšenie trasy pomocou Simulated Annealing
        System.out.println("Zlepšovanie trasy pomocou Simulated Annealing...");
        simulatedAnnealing();
        //SimmulatedAnnealingTwo();
        // Výstup konečného riešenia
        System.out.println("Konečné riešenie po Simulated Annealing: " + Arrays.toString(x));
        System.out.println("Celková vzdialenosť: " + distance);
    }


    public void insertionHeuristic(int i1) {
        List<Integer> route = new ArrayList<>();
        boolean[] visited = new boolean[M];

        route.add(i1);
        visited[i1] = true;

        int i2 = -1;
        int maxDistance = Integer.MIN_VALUE;
        for (int i = 0; i < M; i++) {
            if (!visited[i] && data[i1][i] > maxDistance) {
                i2 = i;
                maxDistance = data[i1][i];
            }
        }
        route.add(i2);
        visited[i2] = true;

        // Určenie i3: Najvzdialenejší uzol od i2
        int i3 = -1;
        maxDistance = Integer.MIN_VALUE;
        for (int i = 0; i < M; i++) {
            if (!visited[i] && data[i2][i] > maxDistance) {
                i3 = i;
                maxDistance = data[i2][i];
            }
        }
        route.add(i3);
        visited[i3] = true;

        // Uzavrieme cyklus (vrátime sa na i1)
        route.add(i1);

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

            route.add(bestPosition, candidateNode);
            visited[candidateNode] = true;
        }


        int totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            int currentNode = route.get(i); // Aktuálny uzol
            int nextNode = route.get(i + 1); // Nasledujúci uzol
            totalDistance += data[currentNode][nextNode];
        }
        // Pridáme vzdialenosť od posledného uzla k prvému
        totalDistance += data[route.getLast()][route.getFirst()];

        distance = totalDistance;

        x = route.stream().mapToInt(i -> i).toArray();

        // Výstup výsledkov
        System.out.println("Trasa (vsúvacia heuristika): " + route);
        System.out.println("Celková vzdialenosť: " + totalDistance);
    }


    public void simulatedAnnealing() {
        int[] x0 = Arrays.stream(x).toArray(); // Inicializácia počiatočného riešenia
        int[] bestSolution = x0.clone(); // Najlepšie riešenie
        int[] xi = x0.clone();
        double tMax = 10000.0; // Počiatočná teplota
        double beta = 0.001; // Ochladzovací parameter
        double t = tMax; // Aktuálna teplota

        int v = Integer.MAX_VALUE; // Počet aktualizácií najlepšieho riešenia od posledného zahrievania
        int r; // Počet preskúmaných prechodov od posledného zlepšenia
        int w; // Celkový počet preskúmaných prechodov od poslednej zmeny teploty
        int u = 40; // Maximálny počet prechodov od posledného zlepšenia
        int q = 50; // Maximálny počet prechodov na jednu teplotu
        int targetSwappedNode = -1;
        int i = 0;


        while (v > 0) { // Vonkajší cyklus, kým sa najlepšie riešenie zlepšuje
            System.out.println();
            System.out.println("--------------------------------------------------------------------");
            v = 0;
            r = 0;
            w = 0;
            targetSwappedNode = i;
            int swapIndex = 0;

            while (r < u) {
                int[] x = xi.clone();

                if(targetSwappedNode == swapIndex) {
                    swapIndex++;
                    continue;
                }
                if (swapIndex >= x.length) {
                    i++;
                    if (i >= x.length) {
                        break;
                    }
                    targetSwappedNode = i;
                    swapIndex = 0;
                    continue;
                }

                int target = x[targetSwappedNode];
                int swap = x[swapIndex];
                System.out.println("Výmenné indexy: " + targetSwappedNode + " <-> " + swapIndex);

                swapNodes(x, targetSwappedNode, swapIndex);
                swapIndex++;

                x[x.length - 1] = x[0];


                int xiDist = calculateTotalDistance(xi);
                int currDist = calculateTotalDistance(x);

                System.out.println("Výmenné uzly: " + target + " <-> " + swap);
                System.out.println("Aktuálna vzdialenosť: " + currDist + ", Predošlá vzdialenosť: " + xiDist);

                w++;
                r++;

                if (w == q) {
                    t = t / (1 + (beta * t));
                    System.out.println("Zmena teploty: " + t);
                    w = 0;
                }

                if (currDist <= xiDist) {
                    r = 0;
                    xi = x.clone();
                    // Akceptujeme nové riešenie ako najlepšie
                    if (currDist < calculateTotalDistance(bestSolution)) {
                        bestSolution = xi.clone();
                        v++;
                        System.out.println("Nové najlepšie riešenie: " + Arrays.toString(bestSolution));
                        System.out.println("Nová najlepšia vzdialenosť: " + calculateTotalDistance(bestSolution));
                    }
                    targetSwappedNode = swapIndex - 1;
                } else {
                    double p = acceptanceProbability(xiDist, currDist, t);
                    double h = Math.random();
                    System.out.println("Pravdepodobnosť akceptácie: " + p + ", Náhodné číslo: " + h);

                    if (h < p) {
                        xi = x.clone();
                        r = 0;
                        System.out.println("Nové riešenie akceptované napriek vyššej vzdialenosti.");
                        targetSwappedNode = swapIndex - 1;
                    } else {
                        System.out.println("Riešenie zamietnuté.");
                    }
                }
                System.out.println("--------------------------------------------------------------------");
            }

            t = tMax;
            System.out.println("Reset teploty na " + tMax);
        }

        this.x = bestSolution.clone();
        distance = calculateTotalDistance(x);


    }



    private void swapNodes(int[] x,int i, int j) {
        int temp = x[i];
        x[i] = x[j];
        x[j] = temp;
    }

    private int calculateTotalDistance(int[] path) {
        int totalDistance = 0;
        for (int i = 0; i < path.length - 1; i++) {
            totalDistance += data[path[i]][path[i + 1]]; // Pridáme vzdialenosť medzi uzlami
        }
        // Pridáme vzdialenosť od posledného uzla späť na prvý uzol
        totalDistance += data[path[path.length - 1]][path[0]];
        return totalDistance;
    }



    private double acceptanceProbability(int currentDistance, int neighborDistance, double temperature) {
        double deltaE = neighborDistance - currentDistance;
        System.out.println("Aktuálna teplota T: " + temperature);

        if (deltaE < 0) {
            return 1.0;
        }

        return Math.exp(-deltaE / temperature);
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
                    x = new int[M + 1];
                    for (int i = 0; i < M + 1; i++) {
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
