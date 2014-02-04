package equalizer;

/**
 * Created by Shevchik on 04.02.14.
 */

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class WaveFile {

    private int INT_SIZE = 4;
    public final int NOT_SPECIFIED = -1;
    private int sampleSize = NOT_SPECIFIED;
    private long framesCount = NOT_SPECIFIED;
    private byte[] data = null;  // массив байт представляющий аудио-данные
    private AudioInputStream ais = null;
    private AudioFormat af = null;
    private static final double MAX_16_BIT = Short.MAX_VALUE;     // 32,767

    /**
     * Создает объект из указанного wave-файла
     *
     * @param file - wave-файл
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    WaveFile(File file) throws UnsupportedAudioFileException, IOException {

        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        // получаем поток с аудио-данными
        ais = AudioSystem.getAudioInputStream(file);

        // получаем информацию о формате
        af = ais.getFormat();

        // количество кадров в файле
        framesCount = ais.getFrameLength();

        // размер сэмпла в байтах
        sampleSize = af.getSampleSizeInBits() / 8;

        // размер данных в байтах
        long dataLength = framesCount * af.getSampleSizeInBits() * af.getChannels() / 8;

        // читаем в память все данные из файла разом
        data = new byte[(int) dataLength];
        ais.read(data);
    }

    /**
     * Создает объект из массива целых чисел
     *
     * @param sampleSize - количество байт занимаемых сэмплом
     * @param sampleRate - частота
     * @param channels   - количество каналов
     * @param samples    - массив значений (данные)
     * @throws Exception если размер сэмпла меньше, чем необходимо
     *                   для хранения переменной типа int
     */
    WaveFile(int sampleSize, float sampleRate, int channels, int[] samples) throws Exception {

        if (sampleSize < INT_SIZE) {
            throw new Exception("sample size < int size");
        }

        this.sampleSize = sampleSize;
        this.af = new AudioFormat(sampleRate, sampleSize * 8, channels, true, false);
        this.data = new byte[samples.length * sampleSize];

        // заполнение данных
        for (int i = 0; i < samples.length; i++) {
            setSampleInt(i, samples[i]);
        }

        framesCount = data.length / (sampleSize * af.getChannels());
        ais = new AudioInputStream(new ByteArrayInputStream(data), af, framesCount);
    }

    /**
     * Возвращает формат аудио-данных
     *
     * @return формат
     */
    public AudioFormat getAudioFormat() {
        return af;
    }

    /**
     * Возвращает копию массива байт представляющих
     * данные wave-файла
     *
     * @return массив байт
     */
    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    /**
     * Возвращает копию массива данных в виде значений типа double
     */
    public double[] read() {
        byte[] dat = Arrays.copyOf(data, data.length);
        int N = dat.length;
        double[] d = new double[N / 2];
        for (int i = 0; i < N / 2; i++) {
            d[i] = ((short) (((dat[2 * i + 1] & 0xFF) << 8) + (dat[2 * i] & 0xFF)));
        }
        return d;
    }

    /**
     * Возвращает количество байт которое занимает
     * один сэмпл
     *
     * @return размер сэмпла
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Возвращает продолжительность сигнала в секундах
     *
     * @return продолжительность сигнала
     */
    public double getDurationTime() {
        return getFramesCount() / getAudioFormat().getFrameRate();
    }

    /**
     * Возвращает количество фреймов (кадров) в файле
     *
     * @return количество фреймов
     */
    public long getFramesCount() {
        return framesCount;
    }

    /**
     * Сохраняет объект WaveFile в стандартный файл формата WAVE
     *
     * @param file
     * @throws IOException
     */
    public void saveFile(File file) throws IOException {
        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(data),
                af, framesCount), AudioFileFormat.Type.WAVE, file);
    }

    /**
     * Устанавливает значение сэмпла
     *
     * @param sampleNumber - номер сэмпла
     * @param sampleValue  - значение сэмпла
     */
    public void setSampleInt(int sampleNumber, int sampleValue) {

        // представляем целое число в виде массива байт
        byte[] sampleBytes = ByteBuffer.allocate(sampleSize).
                order(ByteOrder.LITTLE_ENDIAN).putInt(sampleValue).array();

        // последовательно записываем полученные байты
        // в место, которое соответствует указанному
        // номеру сэмпла
        for (int i = 0; i < sampleSize; i++) {
            data[sampleNumber * sampleSize + i] = sampleBytes[i];
        }
    }
}
