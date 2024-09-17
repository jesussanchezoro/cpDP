package cpdp.execute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CountNodesAfterReduction {

    public static void main(String[] args) {
        String path = "salida_consola 2.txt";
        try (BufferedReader bf = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = bf.readLine()) != null) {
                String[] tokens = line.split("/")[2].split("_");
                String name = tokens[0];
                int p = Integer.parseInt(tokens[1]);
                int q = Integer.parseInt(tokens[2]);
                System.out.print(name + "\t" + p + "\t" + q + "\t");
                bf.readLine();
                tokens = bf.readLine().split("\\s+");
                int before = Integer.parseInt(tokens[0]);
                int after = Integer.parseInt(tokens[1]);
                System.out.println(before + "\t" + after);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
