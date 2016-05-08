package de.urlaubsgimpel.test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

public class ImageConversion {

    private static final int BLACK = 0;
    private static final int WHITE = 255 << 16 | 255 << 8 | 255;

    public static void main(String[] args) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/semeion.data"))) {
            String line;
            int number = 1;
            while ((line = reader.readLine()) != null) {
                String[] inputSplit = Arrays.stream(line.split(" "))
                        .map(s -> s.substring(0, 1))
                        .toArray(String[]::new);
                System.out.println(String.format("Converting image %d with %d columns.", number, inputSplit.length));
                BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
                for (int i = 0; i < Math.min(256, inputSplit.length); i++) {
                    int color = "1".equals(inputSplit[i]) ? BLACK : WHITE;
                    image.setRGB(i % 16, i / 16, color);
                }
                try (FileOutputStream outFile = new FileOutputStream(String.format("images/image%05d.png", number++))) {
                    ImageIO.write(image, "PNG", outFile);
                }
            }
        }
    }

}
