import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ImgProcessing {
    private String fileName;

    private int width;
    private int height;
    private int type;

    private int chunkSize;
    private int scale = 1;
    private BufferedImage sourceImage;
    private BufferedImage resultImage;
    private char[][] outputArray;
    Color[][] colorArray;

    ImgProcessing(String fileName) {
        this.fileName = fileName;
        initFile();
        generateColorArray();
        generateSymbolArray();
    }

    public static class ASCIIArt extends ImgProcessing {
        ASCIIArt(String fileName) {
            super(fileName);
        }
    }

    public int getScale() {
        return scale;
    }

    public ImgProcessing setScale(int scale) {
        this.scale = scale;
        this.outputArray = getScaledArray();
        return this;
    }

    void initFile() {
        try {
            sourceImage = ImageIO.read(new File(fileName));
            width = sourceImage.getWidth();
            height = sourceImage.getHeight();
            type = sourceImage.getType();

            resultImage = new BufferedImage(width, height, type);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    private void generateColorArray() {
        colorArray = new Color[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                colorArray[y][x] = new Color(sourceImage.getRGB(x, y));
            }
        }
    }

    private void generateSymbolArray() {
        outputArray = new char[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (colorArray[y][x].getBlue() < 150) { //оптимизировать
                    outputArray[y][x] = '#';
                } else {
                    outputArray[y][x] = ' ';
                }
            }
        }
    }

    public ImgProcessing printArray() {
        for (int y = 0; y < height / scale; y++) {
            for (int x = 0; x < width / scale; x++) {
                System.out.print(outputArray[y][x]);
                System.out.print(' ');
            }
            System.out.println();
        }
        return this;
    }

    public ImgProcessing saveToTxtFile(String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            for (char[] temp : outputArray) {
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
        return this;
    }

    private char[][] getScaledArray() {
        int height = this.height / scale;
        int width = this.width / scale;
        char[][] scaledArray = new char[height][width];
        int yCount = 0;
        int xCount = 0;
        for (int y = 0, i = height - 1; y < height; y++, i--) {
            for (int x = 0; x < width; x++) {
                int blackCount = 0;
                int whiteCount = 0;
                for (int yy = yCount; yy < yCount + scale; yy++) {
                    for (int xx = xCount; xx < xCount + scale; xx++) {
                        if (outputArray[yy][xx] == ' ') {
                            whiteCount++;
                        } else {
                            blackCount++;
                        }
                    }
                }
                if (whiteCount > blackCount) {
                    scaledArray[x][y] = ' ';
                    System.out.print(' ');
                    System.out.print(' ');
                } else if (blackCount > whiteCount) {
                    scaledArray[x][y] = '#';
                    System.out.print('#');
                    System.out.print(' ');
                } else {
                    scaledArray[x][y] = '_';
                    System.out.print('*');
                    System.out.print(' ');
                }
                if (yCount < this.height - scale) {
                    yCount += scale;
                }

            }
            if (xCount < this.width - scale) {
                xCount += scale;
            }
            yCount = 0;
            System.out.println();
        }
        return scaledArray;
    }

    public ImgProcessing binarify() { //бинаризация картинки
        for (int x = 0; x < sourceImage.getWidth(); x++) {
            for (int y = 0; y < sourceImage.getHeight(); y++) {

                Color color = new Color(sourceImage.getRGB(x, y));

                int blue = color.getBlue();
                int red = color.getRed();
                int green = color.getGreen();

//            int grey = (int) (red * 0.21 + green * 0.72 + blue * 0.07) / 3; // светлота
                int grey = (red + green + blue) / 3; // среднее

                // перевод в оттенки серого
//
//                int newRed = grey;
//                int newGreen = grey;
//                int newBlue = grey;

                if (grey > (255 / 2) || isTransparent(x, y)) {
                    Color newColor = new Color(255, 255, 255);
                    resultImage.setRGB(x, y, newColor.getRGB());
                } else {
                    Color newColor = new Color(0, 0, 0);
                    resultImage.setRGB(x, y, newColor.getRGB());
                }
            }
        }
        return this;
    }

    public ImgProcessing saveToPngFile(String fileName) {
        String format = fileName.substring(fileName.indexOf(".") + 1);
        try {
            ImageIO.write(resultImage, format, new File(fileName));
        } catch (IOException e) {
            System.out.println(e);
        }
        return this;
    }

    private boolean isBinary() {
        for (int x = 0; x < sourceImage.getWidth(); x++) {
            for (int y = 0; y < sourceImage.getHeight(); y++) {
                if (sourceImage.getRGB(x,y) > 0
                    && sourceImage.getRGB(x,y) < 255)
                    return false;
                }
            }
        return true;
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
