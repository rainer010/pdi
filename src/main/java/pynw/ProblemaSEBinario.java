package pynw;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;
import py.gapdi.OpenCVUtil;
import py.gapdi.helper.HOpencv;

import java.util.Random;


public class ProblemaSEBinario extends AbstractBinaryProblem {

    static Random randomGenerator = new Random();
    private Mat imagen;
    //filas numero de filas en el cuadrante nro impar
    private int dimF = 7;
    //columnas numero de columnas del cuadrante nro impar
    private int dimC = 7;
    private double CS;

    public ProblemaSEBinario(Mat imagen) {
        CS = OpenCVUtil.contraste(imagen);
        System.out.print("C inicial-:" + CS + "\n");
        this.imagen = imagen;
    }

    int sol = 0;

    @Override
    public Solucion createSolution() {
        Solucion s = new Solucion(dimC * dimF - 1);
        for (int i = 0; i < (dimC * dimF - 1); i++) {
//            if (sol == 0) {
//                s.getSolucion().set(i, false);
//            }else if(sol==1){
//                s.getSolucion().set(i,true);
//            }else{
            s.getSolucion().set(i, randomGenerator.nextBoolean());
//            }
        }
        sol++;
        return s;
    }


    //
    public Mat convertToMat(BinarySet m) {
        Mat SE = new Mat(dimF * 2 - 1, dimC * 2 - 1, CvType.CV_8UC1);
        SE.put(dimF - 1, dimC - 1, 1);
        for (int i = 0; i < m.getBinarySetLength(); i++) {
            asignarValorEnPosicion(m.get(i) ? 1 : 0, i, SE);
        }
//        asignarValorEnPosicion(1, m.getBinarySetLength()+1, SE);
        return SE;
    }

    //
    public void asignarValorEnPosicion(int valor, long posicion, Mat SE) {

        int c = SE.cols() / 2 + 1;
        int fil = (int) (posicion / c);
        int col = (int) (posicion % c);

        SE.put(fil, col, valor);
        SE.put(SE.rows() - 1 - fil, col, valor);
        SE.put(fil, SE.cols() - 1 - col, valor);
        SE.put(SE.rows() - 1 - fil, SE.cols() - 1 - col, valor);
    }


    @Override
    protected int getBitsPerVariable(int i) {
        return 1;
    }

    @Override
    public void evaluate(BinarySolution solucion) {
        evaluate((Solucion) solucion);
    }


    public void evaluate(Solucion solucion) {
//
//        for(int i=0;i<solucion.getSolucion().length();i++){
//            System.out.print(""+(solucion.getSolucion().get(i) ? 1 : 0));
//        }
//        System.out.print("FIN\n");

        Mat se = Utils.convertirEnEspiral(solucion.getSolucion());
        Mat act = null;
        try {
            act = HOpencv.newMetodo_Binario_OCV(imagen, se);
        } catch (Exception e) {
            System.out.print("ERROR " + e + "\n");
        }
        double[] e = OpenCVUtil.calcularMetrica(imagen, act, CS);
        act.release();
        solucion.setObjective(0, e[0]);
        solucion.setEvaluacion(e);
    }
}
