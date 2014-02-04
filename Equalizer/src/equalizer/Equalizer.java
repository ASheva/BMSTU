package equalizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by Shevchik on 03.02.14.
 */
public class Equalizer implements FilterCoefficient {
    private static int N = 1024;
    private static int[] num = new int[N];
    private static double[] rReal = new double[N];
    private static double[] rImg = new double[N];
    private static double wnkReal;
    private static double wnkImg;
    private static double wnkRealOdd;
    private static double wnkImgOdd;
    private static double[] signalData;
    private static double[] fftResult = new double[N];
    private static int[] intResult = new int[N];
    private static double[] filterLowpassResult = new double[N];
    private static double[] filterHighpassResult = new double[N];
    private static double[] filterBandpassResult = new double[N];
    private static double[] filterBandstopResult = new double[N];

    public static void main(String[] args) throws Exception {
        long timerBegin = System.currentTimeMillis();

        for (int i = 0; i < N; i++){
            num[i]=i;
        }
        WaveFile wf = new WaveFile(new File("C:/Users/Shevchik/Desktop/cos/fftFiltr/fft_filter/fft_filter/example.wav"));
        System.out.println(wf.getAudioFormat());
        System.out.println("Время длительности: " + wf.getDurationTime() + "с");
        signalData = wf.read();

        signalDataToFile(signalData);
        fft(signalData);
        filtration(signalData, filterLowpassResult, coeflowpass, "LowPassResult");
        filtration(signalData, filterHighpassResult, coefHighpass, "HighPassResult");
        filtration(signalData, filterBandpassResult, coefBandpass, "BandPassResult");
        filtration(signalData, filterBandstopResult, coefBandstop, "BandStopResult");

        PaintFrame frame = new PaintFrame();
        frame.setDefaultCloseOperation(PaintFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        long timerEnd = System.currentTimeMillis();
        long timerDelta = timerEnd - timerBegin;
        System.out.println();
        System.out.println("Total workTime: " + timerDelta);
    }

    public static void filtration(double[] sourseData, double[] filter, double[] coef, String filterName) throws Exception{
        double summ = 0;
        for (int i = 0; i < N; i++) {
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
                rReal[2 * k] += filter[n] * wnkReal + filter[n +  N / 2] * wnkReal;
                rReal[2 * k + 1] += filter[n] * wnkRealOdd - filter[n +  N / 2] * wnkRealOdd;
                rImg[2 * k] += filter[n] * wnkImg + filter[n +  N / 2] * wnkImg;
                rImg[2 * k + 1] += filter[n] * wnkImgOdd - filter[n +  N / 2] * wnkImgOdd;
            }
        }
        //нахождение амплитуд гармоник
        for (int i = 0; i < N; i++) {
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
        PrintWriter pr = new PrintWriter("C:/Users/Shevchik/Desktop/cos/_dz/fftResult.txt");
        for (int i = 0; i < fftResult.length; i++) {
            pr.println((int)fftResult[i]);
        }
        pr.close();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //                  innerClass PaintFrame(JFrame)                                            //
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public static class PaintFrame extends JFrame{
        private int leftX = 0;
        private int topY = 0;
        private int width = 1400;
        private int height = 700;

        public PaintFrame() {
            setBounds(getLeftX(), getTopY(), getWidth(), getHeight());
            setTitle("Signal plotting");
            HashMap<String, Effect> effects = new HashMap<String, Effect>();
            /*
            effects.put("FFT", new );
            effects.put("Lowpass", new );
            effects.put("Highpass", new );
            effects.put("Bandpass", new );
            effects.put("Bandstop", new );
            */
            PaintPanel panel = new PaintPanel();
            add(panel);
        }

        public int getLeftX() {
            return leftX;
        }

        public int getTopY() {
            return topY;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    public static class PaintPanel extends JPanel {
        public PaintPanel(){
            makeButton("FFT", fftResult);
            makeButton("Lowpass", filterLowpassResult);
            makeButton("Highpass", filterHighpassResult);
            makeButton("Bandpass", filterBandpassResult);
            makeButton("Bandstop", filterBandstopResult);
        }

        void makeButton(String name, double[] arr){
            JButton button = new JButton(name);
            add(button);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                }
            });
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2D = (Graphics2D) g;

            g2D.setPaint(Color.RED);

            for (int i = 0; i < N; i++){
                intResult[i]=(int)fftResult[i]/3000;
            }
            g2D.drawPolyline(num, intResult, N);
        }
    }
}
