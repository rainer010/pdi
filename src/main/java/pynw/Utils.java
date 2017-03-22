package pynw;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import py.gapdi.OpenCVUtil;

import java.util.BitSet;

/**
 * Created by rainer on 07/03/2017.
 */
public class Utils {

    public static Mat convertirEnEspiral(BitSet bits){

        int elementos=bits.length()+1;
        int tamano= (int)Math.sqrt(elementos);
//        System.out.print(tamano+"   \n");

        Mat SE = new Mat(tamano * 2 - 1, tamano * 2 - 1, CvType.CV_8UC1);
        SE.put(tamano-1,tamano-1,1);

        int circulo=1;
        int f=tamano;
        int c=tamano-1;
        int idx=0;
        while(circulo<=tamano){

            //M[f,c]=            bits.get(idx)?1:0;
            asignarEnPosicionCaracol(f,c,SE,bits.get(idx)?1:0);
            if(f>(tamano-circulo)){
                f--;
            }else if(c<tamano){
                c++;
            }else{
                circulo++;
                f=tamano;
                c=tamano-circulo;
            }
            idx++;
        }

        return SE;
    }

    protected static void asignarEnPosicionCaracol(int f,int c,Mat s,int valor){

        int dim= (int) s.size().height;

        s.put(f,c,valor);
        s.put(f,dim-c,valor);
        s.put(dim-f,c,valor);
        s.put(dim-f,dim-c,valor);
        return;
    }

    public static void main(String [] args){
        System.loadLibrary("opencv_java300");
        BitSet set=new BitSet(1);
        for(int i=0;i<25;i++){
            set.set(i,false);
        }
        set.set(21,true);



        OpenCVUtil.imprimir(convertirEnEspiral(set));
    }

}
