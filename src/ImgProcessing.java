import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ImgProcessing extends DetectImageTransparency{
    private String fileName;
    private int width;
    private int height;
    private int chunkSize;
    private BufferedImage sourceImage;
    private BufferedImage resultImage;


    ImgProcessing(String fileName) {
        this.fileName = fileName;
        setFile();
    }

    void setFile() {
        try {
            this.sourceImage = ImageIO.read(new File(fileName));
            this.width = sourceImage.getWidth();
            this.height = sourceImage.getHeight();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    Color[][] getColorArray() {
        Color[][] arrayColor = new Color[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                arrayColor[y][x] = new Color(sourceImage.getRGB(x, y));
            }
        }
        return arrayColor;
    }

    char[][] getCharArray(Color[][] colorArray) {
        char[][] array = new char[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (colorArray[y][x].getBlue() < 150) { //оптимизировать
                    array[y][x] = '#';
                } else {
                    array[y][x] = ' ';
                }
            }
        }
        return array;
    }

    void printArray() {
        char[][] chars = getCharArray(getColorArray());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(chars[y][x]);
                System.out.print(' ');
            }
            System.out.println();
        }
    }

    void writeArray() {
        try {
//            char[][] chars = getCharArray(getColorArray());
            char[][] chars = getChunkedArray(getCharArray(getColorArray()), 10);
            BufferedWriter writer = new BufferedWriter(new FileWriter("output_4.txt"));
            for (char[] temp : chars) {
                for (char temp2 : temp) {
                    writer.write(temp2);
                    writer.write(' ');
                }
                writer.write('\n');
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    char[][] getChunkedArray(char[][] array, int scale) {
        int height = this.height / scale;
        int width = this.width / scale;
        char[][] chunkedArray = new char[height][width];
        int yCount = 0;
        int xCount = 0;
        for (int y = 0, i = height - 1; y < height; y++, i--) {
            for (int x = 0; x < width; x++) {
                int blackCount = 0;
                int whiteCount = 0;
                for (int yy = yCount; yy < yCount + scale; yy++) {
                    for (int xx = xCount; xx < xCount + scale; xx++) {
                        if (array[yy][xx] == ' ') {
                            whiteCount++;
                        } else {
                            blackCount++;
                        }
                    }
                }
                if (whiteCount > blackCount) {
                    chunkedArray[x][y] = ' ';
                } else if (blackCount > whiteCount) {
                    chunkedArray[x][y] = '#';
                } else {
                    chunkedArray[x][y] = '_';
                }
                if (yCount < this.height - scale) {
                    yCount += scale;
                }

            }
            if (xCount < this.width - scale) {
                xCount += scale;
            }
            yCount = 0;
        }
        return chunkedArray;
    }

    void binarify() { //бинаризация картинки
        try {
            setFile();
            this.resultImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), sourceImage.getType());

            for (int x = 0; x < sourceImage.getWidth(); x++) {
                for (int y = 0; y < sourceImage.getHeight(); y++) {

                    Color color = new Color(sourceImage.getRGB(x, y));

                    int blue = color.getBlue();
                    int red = color.getRed();
                    int green = color.getGreen();

//                int grey = (int) (red * 0.21 + green * 0.72 + blue * 0.07) / 3; // светлота
                    int grey = (red + green + blue) / 3; // среднее

                    // перевод в оттенки серого
//
//                    int newRed = grey;
//                    int newGreen = grey;
//                    int newBlue = grey;

                    if (grey > (255 / 2) || isTransparent(x, y)) {
                        Color newColor = new Color(255, 255, 255);
                        resultImage.setRGB(x, y, newColor.getRGB());
                    } else  {
                        Color newColor = new Color(0, 0, 0);
                        resultImage.setRGB(x, y, newColor.getRGB());
                    }
                }
            }
            // Созраняем результат в новый файл
            File output = new File("peka.png");
            ImageIO.write(resultImage, "png", output);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public void checkTransparency(BufferedImage image){
        if (containsAlphaChannel(image)){
            System.out.println("image contains alpha channel");
        } else {
            System.out.println("image does NOT contain alpha channel");
        }

        if (containsTransparency(image)){
            System.out.println("image contains transparency");
        } else {
            System.out.println("Image does NOT contain transparency");
        }
    }

    boolean containsAlphaChannel(BufferedImage image){
        return image.getColorModel().hasAlpha();
    }

    boolean containsTransparency(BufferedImage image){
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (isTransparent(j, i)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isTransparent(int x, int y) {
        int pixel = sourceImage.getRGB(x,y);
        return (pixel>>24) == 0x00;
    }
}
