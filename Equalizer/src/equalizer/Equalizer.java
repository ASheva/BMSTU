package equalizer;

import javax.swing.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Shevchik on 03.02.14.
 */
public class Equalizer implements FilterCoefficient {
    private static final int N = 1024;
    private static double[] rReal = new double[getN()];
    private static double[] rImg = new double[getN()];
    private static double wnkReal;
    private static double wnkImg;
    private static double wnkRealOdd;
    private static double wnkImgOdd;
    private static double[] signalData;
    private static double[] fftResult = new double[getN()];
    private static int[] intResult = new int[getN()];
    private static double[] filterLowpassResult = new double[getN()];
    private static double[] filterHighpassResult = new double[getN()];
    private static double[] filterBandpassResult = new double[getN()];
    private static double[] filterBandstopResult = new double[getN()];
    private static String signalToPlot;
    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        long timerBegin = System.currentTimeMillis();

        WaveFile wf = new WaveFile(new File("C:/Users/Shevchik/Desktop/cos/fftFiltr/fft_filter/fft_filter/example.wav"));
        System.out.println(wf.getAudioFormat());
        System.out.println("Время длительности: " + wf.getDurationTime() + "с");
        signalData = wf.read();

        signalDataToFile(signalData);
        fft(signalData);
        filtration(signalData, getFilterLowpassResult(), coeflowpass, "LowPassResult");
        filtration(signalData, getFilterHighpassResult(), coefHighpass, "HighPassResult");
        filtration(signalData, getFilterBandpassResult(), coefBandpass, "BandPassResult");
        filtration(signalData, getFilterBandstopResult(), coefBandstop, "BandStopResult");

        setSignalToPlot();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GraphPanel.createAndShowGui(getSignalToPlot());
            }
        });

        long timerEnd = System.currentTimeMillis();
        long timerDelta = timerEnd - timerBegin;
        System.out.println();
        System.out.println("Total workTime: " + timerDelta);
    }



    public static void filtration(double[] sourseData, double[] filter, double[] coef, String filterName) throws Exception{
        double summ = 0;
        for (int i = 0; i < getN(); i++) {
            if (i <= coef.length)
                for (int j = i; j > 0; j--) {
                    summ += sourseData[j] * coef[i - j];
                }
            else if (i > coef.length)
                for (int j = 0; j < coef.length; j++) {
                    summ += sourseData[i - j] * coef[j];
                }
            filter[i] = summ;
            summ = 0;
        }
        for (int k = 0; k < getN(); k++) {
            rReal[k] = 0;
            rImg[k] = 0;
        }
        for (int k = 0; k < getN() / 2; k++) {
            for (int n = 0; n < (getN() / 2); n++) {
                wnkReal = Math.cos(-2 * Math.PI * (double) (2 * n * k) / getN());
                wnkImg = Math.sin(-2 * Math.PI * (double) (2 * n * k) / getN());
                wnkRealOdd = Math.cos(-2 * Math.PI * (double) (n * (2 * k + 1)) / getN());
                wnkImgOdd = Math.sin(-2 * Math.PI * (double) (n * (2 * k + 1)) / getN());
                rReal[2 * k] += filter[n] * wnkReal + filter[n +  getN() / 2] * wnkReal;
                rReal[2 * k + 1] += filter[n] * wnkRealOdd - filter[n +  getN() / 2] * wnkRealOdd;
                rImg[2 * k] += filter[n] * wnkImg + filter[n +  getN() / 2] * wnkImg;
                rImg[2 * k + 1] += filter[n] * wnkImgOdd - filter[n +  getN() / 2] * wnkImgOdd;
            }
        }
        //нахождение амплитуд гармоник
        for (int i = 0; i < getN(); i++) {
            filter[i] = Math.sqrt(rImg[i] * rImg[i] + rReal[i] * rReal[i]);
        }
        PrintWriter pr = new PrintWriter("C:/Users/Shevchik/Desktop/cos/_dz/"+filterName+".txt");
        for (int i = 0; i < filter.length; i++) {
            pr.println((int) filter[i]);
        }
        pr.close();
    }

    public static void signalDataToFile(double[] sourseData) throws Exception{
        PrintWriter pr = new PrintWriter("C:/Users/Shevchik/Desktop/cos/_dz/wavDouble.txt");
        for (int i = 0; i < sourseData.length; i++) {
            pr.println((int)sourseData[i]);
        }
        pr.close();
    }

    public static void fft (double[] sourceData) throws Exception{
        for (int k = 0; k < getN(); k++) {
            rReal[k] = 0;
            rImg[k] = 0;
        }
        for (int k = 0; k < getN() / 2; k++) {
            for (int n = 0; n < (getN() / 2); n++) {
                wnkReal = Math.cos(-2 * Math.PI * (double) (2 * n * k) / getN());
                wnkImg = Math.sin(-2 * Math.PI * (double) (2 * n * k) / getN());
                wnkRealOdd = Math.cos(-2 * Math.PI * (double) (n * (2 * k + 1)) / getN());
                wnkImgOdd = Math.sin(-2 * Math.PI * (double) (n * (2 * k + 1)) / getN());
                rReal[2 * k] += sourceData[n] * wnkReal + sourceData[n +  getN() / 2] * wnkReal;
                rReal[2 * k + 1] += sourceData[n] * wnkRealOdd - sourceData[n +  getN() / 2] * wnkRealOdd;
                rImg[2 * k] += sourceData[n] * wnkImg + sourceData[n +  getN() / 2] * wnkImg;
                rImg[2 * k + 1] += sourceData[n] * wnkImgOdd - sourceData[n +  getN() / 2] * wnkImgOdd;
            }
        }
        //нахождение амплитуд гармоник
        for (int i = 0; i < getN(); i++) {
            getFftResult()[i] = Math.sqrt(rImg[i] * rImg[i] + rReal[i] * rReal[i]);
        }
        PrintWriter pr = new PrintWriter("C:/Users/Shevchik/Desktop/cos/_dz/fftResult.txt");
        for (int i = 0; i < getFftResult().length; i++) {
            pr.println((int) getFftResult()[i]);
        }
        pr.close();
    }

    public static int[] getIntResult() {
        return intResult;
    }

    public static void setIntResult(int[] intResult) {
        Equalizer.intResult = intResult;
    }

    public static double[] getFftResult() {
        return fftResult;
    }

    public static void setFftResult(double[] fftResult) {
        Equalizer.fftResult = fftResult;
    }

    public static double[] getFilterLowpassResult() {
        return filterLowpassResult;
    }

    public static void setFilterLowpassResult(double[] filterLowpassResult) {
        Equalizer.filterLowpassResult = filterLowpassResult;
    }

    public static double[] getFilterHighpassResult() {
        return filterHighpassResult;
    }

    public static void setFilterHighpassResult(double[] filterHighpassResult) {
        Equalizer.filterHighpassResult = filterHighpassResult;
    }

    public static double[] getFilterBandpassResult() {
        return filterBandpassResult;
    }

    public static void setFilterBandpassResult(double[] filterBandpassResult) {
        Equalizer.filterBandpassResult = filterBandpassResult;
    }

    public static double[] getFilterBandstopResult() {
        return filterBandstopResult;
    }

    public static void setFilterBandstopResult(double[] filterBandstopResult) {
        Equalizer.filterBandstopResult = filterBandstopResult;
    }

    public static int getN() {
        return N;
    }

    public static String getSignalToPlot() {
        return signalToPlot;
    }

    public static void setSignalToPlot() {
        System.out.println("Enter signal type to plotting: ");
        Equalizer.signalToPlot = scan.next();
    }
}
