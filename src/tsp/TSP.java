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
   
   public int[][] getdata()  {
        return data;
   }
   
   public void ries() {
       // tu pridajte heuristiku
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
                    data = new int [M][M];
                    x    = new int[M];
                    for( int i=0; i < M; i++) {
                        x[i]=0;
                    }    
                } else {
                    StringTokenizer st = new StringTokenizer(line, " ");
                    col = 0;
                    while (st.hasMoreTokens()) {
                        data[row][col] =   Integer.parseInt(st.nextToken());
                        System.out.println("number["+ row + "]["+ col + "]:" +data[row][col] );
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
