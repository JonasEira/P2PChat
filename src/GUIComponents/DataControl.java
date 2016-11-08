/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUIComponents;

import com.sun.media.sound.FFT;
import java.awt.List;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.sound.sampled.AudioFormat;


/**
 *
 * @author Jonas
 */
public class DataControl implements SoundWatcher{

    private ArrayList<DataFrame> frames;
    private final ArrayList<DataWatcher> watchers;
    private int dataLength;
    private int n = 0;
    private long avgTime = 0;
    FFTThreadObject fftObject;
	private int _sampleRate;
    
    public DataControl(){
        frames = new ArrayList();
        watchers = new ArrayList();
        dataLength = 10;
        fftObject = new FFTThreadObject();
        Thread fftThread = new Thread(fftObject);
        fftThread.start();
    }
    
    @Override
    public void fireSoundThrough(byte[] b) {
//        DataFrame d = new DataFrame();
//        d.setData(b);
        fftObject.setFrame(new DataFrame(b));
//        frames.add(d);
//        if(frames.size() > dataLength){
//            frames.remove(0);
//        }
//        printl("Length of data: " + frames.size());
//        System.out.println("Control: " + Thread.currentThread().getName() + ", Frames: " + frames.size());
    }
    
    public void addDataWatcher(DataWatcher d){
        watchers.add(d);
    }
    public void removeDataWatcher(DataWatcher d){
        watchers.remove(d);
    }
    public void clearDataWatchers(){
        watchers.clear();
    }
    void notifyWatchers(Object o, int dataType){
        for(DataWatcher d : watchers){
            synchronized(watchers){
                d.fireDataThrough(o, dataType);
            }
        }
    }
    
    public void setDataLength(int n){
        dataLength = n;
    }
    
    private void printl(String string) {
        System.out.println(string);
    }

// -------------------------------------------------------
    public class DataFrame {
        private byte[] _byteData;
        private int[] _abs;
        private int _maxFreq;
                
        private DataFrame(){
            
        }

        private DataFrame(byte[] b) {
            _byteData = b.clone();
        }
        void setData(byte[] d){
            _byteData = d;
        }
        byte[] getData(){
            return _byteData;
        }
        
        public synchronized void fftConversion(){
            try {
                
                long currTime = System.nanoTime();
                int[] intData = shiftValuesToInt(_byteData);
                //Calculate the sample rate
                
                Complex[] cvalues = new Complex[intData.length];
                for(int j = 0; j < cvalues.length; j++){
                    cvalues[j] = new Complex(intData[j],0);
                }
                Complex[] r = fft(cvalues);
                int[] y = new int[r.length];
                int length = y.length;
				for(int k = 0; k < length; k++){
                    if((k <= length/10) ){
                    	y[k] = (int)(10.0*k*r[k].abs()/length);
                    } else if ( k >= (9*length)/10 ) {
                    	double factor = (double)(length-k)/(double)length*10.0/9.0;
                    	y[k] = (int)(factor * r[k].abs());
                    } else {
                    	y[k] = (int)r[k].abs();
                    }
                }
                _abs = y.clone();
                long execTime = System.nanoTime() - currTime;
                
                avgTime = (avgTime + execTime)/2;
                n++;
                if(n == 5){
                    System.out.println("Avg. ffttime/frame, last 5 frames=" + avgTime/1000 + " us");
                    n = 0;
                    avgTime = 0;
                }
                this.filterDC();
                this.findBaseFrequency();
                SoundPresenter.presentData(this);
                
               
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        private int[] shiftValuesToInt(byte[] b) {
            boolean bigEndian = SoundCapturer.isBigEndian();
            int n = b.length/2;
            int x[] = new int[n];

            for (int i = 0; i < n*2; i+=2) {
                    int b1 = b[i];
                    int b2 = b[i + 1];
                    int value;
                    if (bigEndian){
                        value = (b1 << 8) + b2;
                    } else {
                        value = b1 + (b2 << 8);
                    }
                    x[i/2] = value;
            }
            return x;
        }

        private void findBaseFrequency() {
            int max = 0;
            int max_loc = 0;
            for(int n = 0; n < _abs.length; n++){
                if(_abs[n] > max){
                    max_loc = n;
                    max = _abs[n];
                }
            }
            
            _maxFreq = (int)((double)max_loc/(double)_abs.length*(double)_sampleRate);
            printl("Frequency =" + _maxFreq);
            
        }

        int[] getIntData() {
            return _abs;
        }

        /**
         * @return the _maxFreq
         */
        public int getMaxFreq() {
            return _maxFreq;
        }

        private void filterDC() {
            int minfreq = 3;
            int maxfreq = _abs.length - 4;
                    
            for(int n = 0; n < _abs.length; n++){
                if(n < minfreq){
                    _abs[n] = 0;
                }
                if(n > maxfreq){
                    _abs[n] = 0;
                }
            }
        }
    }

// -------------------------------------------------------
    private class FFTThreadObject implements Runnable {
        DataFrame _f;
        private boolean _stopSignal;
        private boolean _runOnceSignal;
        public FFTThreadObject() {
            _stopSignal = false;
            _f = new DataFrame();
            _runOnceSignal = false;
        }
        public synchronized void setFrame(DataFrame f){
            _f = f;
            _runOnceSignal = true;
        }
        @Override
        public void run() {
            while(!_stopSignal){
                while(_runOnceSignal == false){
                    try {
                        Thread.currentThread().sleep(25);
                    } catch (InterruptedException ex) {
                        System.out.println("InterruptedDFT");
                    }
                }
//                System.out.println("Starting fft on " + _f._byteData.length);
                _f.fftConversion();
                
                _runOnceSignal = false;
            }
        }
        public void stop(){
            _stopSignal = true;
        }
    }
    public Complex[] fft(Complex[] x) {
        int N = x.length;
//        System.out.println("N=" + N);
        // base case
        if (N == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) {  throw new RuntimeException("N is not a power of 2"); }

        // fft of even terms
        Complex[] even = new Complex[N/2];
        for (int k = 0; k < N/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < N/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + N/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }
    public Complex[] ifft(Complex[] x) {
        int N = x.length;
        Complex[] y = new Complex[N];

        // take conjugate
        for (int i = 0; i < N; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < N; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by N
        for (int i = 0; i < N; i++) {
            y[i] = y[i].times(1.0 / N);
        }

        return y;

    }

	public void setFrequency(int sampleRate) {
		_sampleRate = sampleRate;		
	}
}
