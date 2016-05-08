package de.urlaubsgimpel.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;

public class DatasetConversion {

    public static void main(String[] args) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/semeion.data"));
             PrintWriter writer = new PrintWriter(new FileWriter("data/semeion_spark.data"))) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                StringBuilder outputLine = new StringBuilder();
                String[] inputSplit = Arrays.stream(inputLine.split(" "))
                        .map(s -> s.substring(0, 1))
                        .toArray(String[]::new);
                for (int i = 0; i < 256; i++) {
                    outputLine.append(" ")
                            .append(i + 1)
                            .append(":")
                            .append(inputSplit[i].substring(0, 1));
                }
                for (int i = 0; i < 10; i++) {
                    if ("1".equals(inputSplit[256 + i])) {
                        outputLine.insert(0, Integer.toString(i) + " ");
                    }
                }
                writer.println(outputLine);
            }
        }
    }

}
