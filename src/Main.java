import java.io.IOException;

/**
 * Created by makbuk on 08.04.20.
 */
public class Main {
    
    public static void main(String[] args) throws IOException {
 
//        ImgProcessing imgProcessing = new ImgProcessing("test.jpg");
//        imgProcessing.printArray();
//        imgProcessing.writeArray();

        new ImgProcessing("test.jpg")
                .setScale(1)
                .printArray();

    }
}


//        void bradleyThreshold(char src, char res, int width, int height) {
//            final int S = width/8;
//            int s2 = S/2;
//            final double t = 0.15;
//            long integral_image = 0;
//            long sum = 0;
//            int count = 0;
//            int index;
//            int x1, y1, x2, y2;
//
//            //рассчитываем интегральное изображение
//            double integralImage = new unsigned long [width*height*sizeof(unsigned long*)];
//
//            for (int i = 0; i < width; i++) {
//                sum = 0;
//                for (int j = 0; j < height; j++) {
//                    index = j * width + i;
//                    sum += src[index];
//                    if (i == 0)
//                        integral_image[index] = sum;
//                    else
//                        integral_image[index] = integral_image[index-1] + sum;
//                }
//            }
//
//            //находим границы для локальные областей
//            for (int i = 0; i < width; i++) {
//                for (int j = 0; j < height; j++) {
//                    index = j * width + i;
//
//                    x1=i-s2;
//                    x2=i+s2;
//                    y1=j-s2;
//                    y2=j+s2;
//
//                    if (x1 < 0)
//                        x1 = 0;
//                    if (x2 >= width)
//                        x2 = width-1;
//                    if (y1 < 0)
//                        y1 = 0;
//                    if (y2 >= height)
//                        y2 = height-1;
//
//                    count = (x2-x1)*(y2-y1);
//
//                    sum = integral_image[y2*width+x2] - integral_image[y1*width+x2] -
//                            integral_image[y2*width+x1] + integral_image[y1*width+x1];
//                    if ((long)(src[index]*count) < (long)(sum*(1.0-t)))
//                        res[index] = 0;
//                    else
//                        res[index] = 255;
//                }
//            }
//
//            delete[] integral_image;
//        }
//    }