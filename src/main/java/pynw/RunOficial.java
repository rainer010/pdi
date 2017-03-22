package pynw;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GenerationalGeneticAlgorithm;
import org.uma.jmetal.operator.impl.crossover.HUXCrossover;
import org.uma.jmetal.operator.impl.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import py.gapdi.OpenCVUtil;
import py.gapdi.helper.HOpencv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by rainer on 10/05/2016.
 */
public class RunOficial {
    public static final double P_CRUZAMIENTO = 0.75;
    public static final double P_MUTACION = 0.025;
    public static HUXCrossover crozz = new HUXCrossover(P_CRUZAMIENTO);
    public static SinglePointCrossover crossoverSimple=new SinglePointCrossover(P_CRUZAMIENTO);
    public static BitFlipMutation mutation = new BitFlipMutation(P_MUTACION);

    public static PrintWriter reporteTotal;

    public static void main(String[] args) throws IOException {
        System.loadLibrary("opencv_java300");
        File salidaF = new File("result/test/todos.csv");
        File dir = new File("result/test/");
        dir.mkdirs();

        if (!salidaF.exists()) {
            salidaF.createNewFile();

        }
        final FileWriter fw = new FileWriter(salidaF, true);
        reporteTotal = new PrintWriter(fw);


        reporteTotal.print("Imagen\t;Fitness\t;SSIM\t;C Metodo\t;C Original\n");
        reporteTotal.flush();
        for (int i = 1; i <= 5 ; i++) {
            excecuteInDir("db2/", "result/" + "test/" + i, 8000 * 36, 36, 7, 7);
        }
        reporteTotal.close();
        fw.close();

    }

    private static void excecuteInDir(final String source, final String out,
                                      final int numeroMaxIteraciones, final int poblacionTamanho,
                                      final int fila, final int columna) throws IOException {

        System.out.print("Nro de iteraciones:" + numeroMaxIteraciones + "\n");
        System.out.print("Tamano de la poblacion:" + poblacionTamanho + "\n");

        final File[] files = new File(source).listFiles();
        int idice = files.length - 1;

        while (idice >= 0) {
            final File file = files[idice];
            idice--;
            System.out.print(file.getName() + "\n");
            Mat imagen = Imgcodecs.imread(source + file.getName(), Imgcodecs.IMREAD_GRAYSCALE);

            System.out.print("Dim I " + imagen.dims() + "\n");
//            imagen.reshape(256,256);
            Mat rz = imagen;
//            if (imagen.size().height > 512) {
//                Imgproc.resize(imagen, rz, new Size(512, 512));
//                imagen.release();
//            }

//            Mat rz=imagen;
            rz.convertTo(rz, CvType.CV_8UC1);

            System.out.print("Dim " + rz.dims() + "\n");
            (new File(out + "/src/")).mkdirs();
            Imgcodecs.imwrite(out + "/src/" + file.getName(), rz);
            run(rz, out,
                    numeroMaxIteraciones, poblacionTamanho, fila, columna, file.getName());
        }

    }

    private static void run(Mat imp, final String out,
                            final int numeroMaxIteraciones,
                            final int tamanhoPoblacion,
                            int fila, int columna, final String filename) throws IOException {

        ProblemaSEBinario problema = new ProblemaSEBinario(imp);
        File directori = new File(out + "/");
        directori.mkdirs();

        final int[] generacion = {1};

        final Calendar tiempo_ini = Calendar.getInstance();
        final long tiempoEnMinutos = 2;
        File salidaF = new File(out + "/" + filename + ".txt");
        File graficoCajas= new File(out + "/" + filename + "_caja.txt");
        if (!salidaF.exists()) {
            salidaF.createNewFile();
        }
        if (!graficoCajas.exists()) {
            graficoCajas.createNewFile();
        }


        final FileWriter fw = new FileWriter(salidaF, true);
        final PrintWriter pw = new PrintWriter(fw);



        final FileWriter cajasfw = new FileWriter(graficoCajas, true);
        final PrintWriter cajaspw = new PrintWriter(cajasfw);


        class SayHello extends TimerTask {
            public void run() {
                System.gc();
                System.out.print("GC llamado\n");
            }
        }

// And From your main() method or any other method
        Timer timer = new Timer();
        timer.schedule(new SayHello(), 0, 50000);

        GenerationalGeneticAlgorithm<BinarySolution> algorithm = new GenerationalGeneticAlgorithm<BinarySolution>(problema,
                numeroMaxIteraciones, tamanhoPoblacion, crossoverSimple, mutation,
                new org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection<BinarySolution>(),
                new SequentialSolutionListEvaluator()) {

            @Override
            public void updateProgress() {
                super.updateProgress();

                Solucion s = (Solucion) this.getResult();
//                System.out.print(""+(1-s.getObjective(0))+"\n");
                String rS = "" + (1-s.getObjective(0)) + "\t" + (s.getEvaluacion()[1]) +
                        "\t" + s.getEvaluacion()[2] + "\n";
                rS = rS.replace(".", ",");
                pw.print(rS);

                if(generacion[0] ==1 || generacion[0] %10==0  || numeroMaxIteraciones/tamanhoPoblacion<=generacion[0]){
                    String stringcajas =  ""+ s.getObjective(0) + "\t"+ generacion[0]+"\t" + (s.getEvaluacion()[1]) +
                            "\t" + s.getEvaluacion()[2] + "\n";
                    stringcajas= stringcajas.replace(".", ",");
                    cajaspw.print(stringcajas);
                }
                generacion[0]++;
            }


        };



        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();

        pw.close();
        cajaspw.close();
        Solucion s = (Solucion) algorithm.getResult();

        Mat se = problema.convertToMat(s.getSolucion());
        Imgcodecs.imwrite(out + "/" + filename, HOpencv.newMetodo_Binario_OCV(imp, se));


        //#############################################
        String rS = "" + (s.getObjective(0)) + "\t;" + s.getEvaluacion()[1] +
                "\t;" + s.getEvaluacion()[2] + "\t;" + (OpenCVUtil.contraste(imp)) + "\n";
        rS = rS.replace(".", ",");
        rS = filename + ";\t" + rS;
        reporteTotal.print(rS);
        reporteTotal.flush();
        //#############################################


        se.convertTo(se, 0, 255.0);
        new File(out + "/se/").mkdirs();
        Imgcodecs.imwrite(out + "/se/" + filename + "F.png", se);
        imp.release();
        se.release();
        timer.cancel();
    }
}

