package equalizer;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Created by Shevchik on 03.02.14.
 */
public class Equalizer implements FilterCoefficient {
    private static int N = 512;
    private double[] signalData;
    private double[] fftResult;
    private double[] filterResult;

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

        PrintWriter pr = new PrintWriter("C:/Users/Shevchik/Desktop/vova/dz/wavDouble.txt");
        for (int i = 0; i < signalData.length; i++) {
            pr.println(signalData[i]);
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
