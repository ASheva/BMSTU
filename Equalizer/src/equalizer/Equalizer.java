package equalizer;

import java.io.File;
import java.io.PrintWriter;

/**
 * Created by Shevchik on 03.02.14.
 */
public class Equalizer implements FilterCoefficient {
    private static int N = 512;
    private static double[] rReal = new double[N];
    private static double[] rImg = new double[N];
    private static double wnkReal;
    private static double wnkImg;
    private static double wnkRealOdd;
    private static double wnkImgOdd;
    private static double[] signalData;
    private static double[] fftResult = new double[N];
    private static double[] filterResult = new double[N];

    public static void main(String[] args) throws Exception {
        WaveFile wf = new WaveFile(new File("C:/Users/Shevchik/Desktop/vova/fftFiltr/fft_filter/fft_filter/example.wav"));
        System.out.println(wf.getAudioFormat());
        System.out.println("Время длительности: " + wf.getDurationTime() + "s");
        double[] signalData = wf.read();

        /*
        //Вывод wav в виде массива значений double
        for (int i = 0; i < signalData.length; i++){
            System.out.print(signalData[i]);
        }
        */

        signalDataToFile(signalData);
        fft(signalData);

    }

    public static void signalDataToFile(double[] sourseData) throws Exception{
        PrintWriter pr = new PrintWriter("C:/Users/Shevchik/Desktop/vova/_dz/wavDouble.txt");
        for (int i = 0; i < sourseData.length; i++) {
            pr.println(sourseData[i]);
        }
        pr.close();
    }

    public static void fft (double[] sourceData) throws Exception{
        for (int k = 0; k < N; k++) {
            rReal[k] = 0;
            rImg[k] = 0;
        }
        for (int k = 0; k < N / 2; k++) {
            for (int n = 0; n < (N / 2); n++) {
                wnkReal = Math.cos(-2 * Math.PI * (double) (2 * n * k) / N);
                wnkImg = Math.sin(-2 * Math.PI * (double) (2 * n * k) / N);
                wnkRealOdd = Math.cos(-2 * Math.PI * (double) (n * (2 * k + 1)) / N);
                wnkImgOdd = Math.sin(-2 * Math.PI * (double) (n * (2 * k + 1)) / N);
                rReal[2 * k] += sourceData[n] * wnkReal + sourceData[n +  N / 2] * wnkReal;
                rReal[2 * k + 1] += sourceData[n] * wnkRealOdd - sourceData[n +  N / 2] * wnkRealOdd;
                rImg[2 * k] += sourceData[n] * wnkImg + sourceData[n +  N / 2] * wnkImg;
                rImg[2 * k + 1] += sourceData[n] * wnkImgOdd - sourceData[n +  N / 2] * wnkImgOdd;
            }
        }
        //нахождение амплитуд гармоник
        for (int i = 0; i < N; i++) {
            fftResult[i] = Math.sqrt(rImg[i] * rImg[i] + rReal[i] * rReal[i]);
        }
        PrintWriter pr = new PrintWriter("C:/Users/Shevchik/Desktop/vova/_dz/fftResult.txt");
        for (int i = 0; i < fftResult.length; i++) {
            pr.println(fftResult[i]);
        }
        pr.close();
    }

    //Cooley-Tukey FFT
    public static Complex[] fft(Complex[] x) {
        int N = x.length;

        if (N == 1) return new Complex[]{x[0]};

        if (N % 2 != 0) {
            throw new RuntimeException("N не является степенью 2");
        }

        Complex[] even = new Complex[N / 2];
        for (int k = 0; k < N / 2; k++) {
            even[k] = x[2 * k];
        }
        Complex[] q = fft(even);

        Complex[] odd = even;  // reuse the array
        for (int k = 0; k < N / 2; k++) {
            odd[k] = x[2 * k + 1];
        }
        Complex[] r = fft(odd);

        Complex[] y = new Complex[N];
        for (int k = 0; k < N / 2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k] = q[k].plus(wk.times(r[k]));
            y[k + N / 2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }

    public double[] getSignalData() {
        return signalData;
    }

    public void setSignalData(double[] signalData) {
        this.signalData = signalData;
    }

    public double[] getFftResult() {
        return fftResult;
    }

    public void setFftResult(double[] fftResult) {
        this.fftResult = fftResult;
    }

    public double[] getFilterResult() {
        return filterResult;
    }

    public void setFilterResult(double[] filterResult) {
        this.filterResult = filterResult;
    }
}
