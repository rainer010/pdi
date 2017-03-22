package pynw;

import ar.com.dgarcia.lang.iterators.adapters.Resetable2IterableAdapter;
import ar.com.dgarcia.lang.iterators.combinatorial.CombinatorialIterator;
import ar.com.dgarcia.lang.iterators.typed.IntegerRangeIterator;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import py.gapdi.OpenCVUtil;
import py.gapdi.helper.HOpencv;

import java.io.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by rainer on 16/12/2016.
 */
public class Test {
    public static void main(String s[]) throws IOException {
        System.loadLibrary("opencv_java300");
        Iterable[] lista = new Iterable[5 * 5 - 1];
        for (int i = 0; i < (5 * 5 - 1); i++) {
            lista[i] = Resetable2IterableAdapter.createFrom(IntegerRangeIterator.create(0, 2));
        }
        class SayHello extends TimerTask {
            public void run() {

                System.gc();
                System.out.print("GC llamado\n");
            }
        }

// And From your main() method or any other method
        Timer timer = new Timer();
        timer.schedule(new SayHello(), 0, 50000);


        CombinatorialIterator<Integer> combinations = CombinatorialIterator.<Integer>createFrom(lista);


        Mat imagen = Imgcodecs.imread("db2/mdb045.png", Imgcodecs.IMREAD_GRAYSCALE);
        Mat rz = imagen;
        if (imagen.size().height > 512) {
            Imgproc.resize(imagen, rz, new Size(512, 512));
            imagen.release();
        }
        imagen = rz;


        double anterior = 1;
        double evaluacion[] = null;
        double contraste = OpenCVUtil.contraste(imagen);

        long i = 1;


        File f;
        f = new File("result/buscarmejor.txt");

//Escritura
        PrintWriter wr = null;
        FileWriter w = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(w);
        wr = new PrintWriter(bw);
        wr.write("");//escribimos en el archivo

        //ahora cerramos los flujos de canales de datos, al cerrarlos el archivo quedará guardado con información escrita
        //de no hacerlo no se escribirá nada en el archivo


        while (combinations.hasNext())

        {
            List<Integer> listaSE = combinations.next();
            Mat se = convertToMat(listaSE, 5, 5);
            Mat act = HOpencv.newMetodo_Binario_OCV(imagen, se);
            double[] metrica = OpenCVUtil.calcularMetrica(imagen, act, contraste);

            if (metrica[0] < anterior) {
                anterior = metrica[0];
                evaluacion = metrica;

                wr.append("" + i + "\t;" + (1 - anterior) + "\n"); //concatenamos en el archivo sin borrar lo existente
                imprimir(listaSE, wr);
                wr.flush();
            }
//            imprimir(listaSE);

            if (i % 100000 == 0) {
                System.out.print("" + i + "\n");
            }
            act.release();
            se.release();
            i++;
        }

        System.out.print("Resultado:\n");
        System.out.print("f: " + evaluacion[0] + "\n");
        System.out.print("ssim: " + evaluacion[1] + "\n");
        System.out.print("c: " + evaluacion[2] + "\n");

        wr.close();
        bw.close();
        timer.cancel();
        return;

    }

    public static void imprimir(List<Integer> lista, PrintWriter pw) {
        String conv = "";
        for (Integer i : lista) {
            conv = conv + i;
        }
        pw.append(conv + "\n");
    }

    public static void imprimir(List<Integer> lista) {
        String conv = "";
        for (Integer i : lista) {
            conv = conv + i;
        }
        System.out.print(conv + "\n");
    }

    public static Mat convertToMat(List<Integer> lista, int dimF, int dimC) {
        Mat SE = new Mat(dimF * 2 - 1, dimC * 2 - 1, CvType.CV_8UC1);
        SE.put(dimF - 1, dimC - 1, 1);
        for (int i = 0; i < lista.size(); i++) {
            asignarValorEnPosicion(lista.get(i), i, SE);
        }
        return SE;
    }

    //
    public static void asignarValorEnPosicion(int valor, long posicion, Mat SE) {

        int c = SE.cols() / 2 + 1;
        int fil = (int) (posicion / c);
        int col = (int) (posicion % c);
        SE.put(fil, col, valor);
        SE.put(SE.rows() - 1 - fil, col, valor);
        SE.put(fil, SE.cols() - 1 - col, valor);
        SE.put(SE.rows() - 1 - fil, SE.cols() - 1 - col, valor);
    }
}
