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
    int data[][];       // matica vzdialenosti
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
        // Pridáme vzdialenosť od posledného uzla k prvému
        totalDistance += data[route.getLast()][route.getFirst()];

        distance = totalDistance;

        x = route.stream().mapToInt(i -> i).toArray();

        // Výstup výsledkov
        System.out.println("Trasa (vsúvacia heuristika): " + route);
        System.out.println("Celková vzdialenosť: " + totalDistance);
    }



    public void simulatedAnnealing() {
        int[] currentSolution = Arrays.copyOf(x, x.length);
        int currentDistance = calculateTotalDistance(currentSolution);
        int[] bestSolution = Arrays.copyOf(currentSolution, currentSolution.length);
        int bestDistance = currentDistance;

        double temperature = 10000.0; // Počiatočná teplota
        double coolingRate = 0.995; // Faktor ochladzovania
        int maxNeighbors = 40; // Maximálny počet skúmaných susedov na teplotu
        int maxNoImprovement = 50; // Maximálny počet prechodov bez zlepšenia

        int noImprovementCounter = 0; // Počet prechodov bez zlepšenia
        Random random = new Random();

        int iterationCount = 0; // Počítadlo iterácií
        String terminationReason = ""; // Dôvod ukončenia

        while (temperature > 1 && noImprovementCounter < maxNoImprovement) {
            System.out.println("Iterácia " + iterationCount + " | Teplota: " + temperature + " | Najlepšia vzdialenosť: " + bestDistance);

            for (int iteration = 0; iteration < maxNeighbors; iteration++) {
                // Generovanie susedného riešenia (výmena dvoch vrcholov)
                int[] neighborSolution = Arrays.copyOf(currentSolution, currentSolution.length);

                // Vyberieme náhodný vrchol (index) z aktuálnej trasy
                int i = random.nextInt(M); // Index vrcholu (od 0 po M-1)

                // Identifikujeme jeho suseda (nasledujúci uzol v trase)
                int j = (i + 1) % M; // Použitie modulu zabezpečí, že posledný vrchol sa spojí s prvým

                // Výmena vrcholov
                int temp = neighborSolution[i];
                neighborSolution[i] = neighborSolution[j];
                neighborSolution[j] = temp;


                // Vypočítame vzdialenosť nového riešenia
                int neighborDistance = calculateTotalDistance(neighborSolution);

                // Vypočítame pravdepodobnosť akceptácie
                double probability = acceptanceProbability(currentDistance, neighborDistance, temperature);

                // Vypisovanie informácií
                System.out.println("  Iterácia " + iteration + ":");
                System.out.println("    Vygenerovaná vzdialenosť: " + neighborDistance);
                System.out.println("    Pravdepodobnosť akceptácie: " + probability);

                double h = random.nextDouble();
                System.out.println("    Vygenoravané h: " + h);
                // Akceptujeme nové riešenie podľa Simulated Annealing kritéria
                if (probability > h) {
                    currentSolution = neighborSolution;
                    currentDistance = neighborDistance;
                    System.out.println("    Riešenie akceptované.");
                } else {
                    System.out.println("    Riešenie odmietnuté.");
                }

                // Aktualizujeme najlepšie riešenie
                if (currentDistance < bestDistance) {
                    bestSolution = Arrays.copyOf(currentSolution, currentSolution.length);
                    bestDistance = currentDistance;
                    noImprovementCounter = 0; // Resetujeme počítadlo bez zlepšenia
                    System.out.println("    Najlepšie riešenie aktualizované: " + Arrays.toString(bestSolution));
                } else {
                    noImprovementCounter++;
                }
            }

            // Ochladzovanie
            System.out.println("Teplota pred ochladzovaním: " + temperature);
            temperature *= coolingRate;
            System.out.println("Teplota po ochladzovaní: " + temperature);
            iterationCount++;

            // Skontrolujeme dôvod ukončenia
            if (temperature <= 1) {
                terminationReason = "Teplota klesla pod prahovú hodnotu.";
            } else if (noImprovementCounter >= maxNoImprovement) {
                terminationReason = "Maximálny počet iterácií bez zlepšenia bol dosiahnutý.";
            }
        }

        // Aktualizácia najlepšieho riešenia
        x = bestSolution;
        distance = bestDistance;

        // Výstup dôvodu ukončenia
        System.out.println("Algoritmus bol ukončený. Dôvod: " + terminationReason);
        System.out.println("Najlepšia trasa po Simulated Annealing: " + Arrays.toString(x));
        System.out.println("Celková vzdialenosť: " + distance);
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



    // Kritérium akceptácie nového riešenia
    private double acceptanceProbability(int currentDistance, int neighborDistance, double temperature) {
        double deltaE = neighborDistance - currentDistance;
        System.out.println("    ΔE (neighborDistance - currentDistance): " + deltaE);
        System.out.println("    Aktuálna teplota T: " + temperature);

        if (deltaE < 0) {
            return 1.0; // Lepšie riešenie je vždy akceptované
        }

        double probability = Math.exp(-deltaE / temperature);
        System.out.println(" Vypočítaná pravdepodobnosť: " + probability);

        return probability;
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
