/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUIComponents;


import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;

/**
 *
 * @author Jonas
 */
public class SoundControl {
    TargetDataLine _input;
    SourceDataLine _output;
    AudioFormat _audioFormat;
    SoundPresenter _view;
    SoundCapturer _cap;
    private DataControl _dataCtl;
    private SoundPlayer _play;
    private Thread _capThread;
    private Thread _playThread;
    private boolean _enableSelf;
    private Configuration _conf;
    private float _sampleRate;
	private int _frameSize;
    
    SoundControl(Configuration conf) {
        _conf = conf;
        _audioFormat = makeFormat();
        startRecording();
    }
    private AudioFormat makeFormat(){
        AudioFormat.Encoding enc = AudioFormat.Encoding.PCM_SIGNED;
//        sampleRate = (float)Math.pow(2,Math.ceil(Math.log(_conf.getSamplingFrequency())/Math.log(2)));
        _sampleRate = _conf.getSamplingFrequency();
        
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
//        int frameSize = 2;
        _enableSelf = false;
        System.out.println("Makeformat");
        return new AudioFormat(_sampleRate, sampleSizeInBits, channels, signed, bigEndian);
//        return new AudioFormat(enc, sampleRate, sampleSizeInBits, channels, frameSize, sampleRate, bigEndian);
    }
    public void stopRecording(){
    	if(_cap != null){
    		_cap.setRunning(false);  // This is wrong. Thread object not visible here.
    		try {
				_capThread.join(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	if(_play != null){
    		_play.setRunning(false);
    		try {
				_playThread.join(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    		
        // Sending WINDOW_CLOSING on behalf of _view top level component to the System Event Queue.
        WindowEvent wev = new WindowEvent((JFrame)_view.getTopLevelAncestor(), WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);

    }
    private void startRecording() {
        //Check if the line format is supported.
        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, _audioFormat);
        
        DataLine.Info sourceInfo = new DataLine.Info(
                         SourceDataLine.class, _audioFormat);
        
        Info[] mixerinfo = AudioSystem.getMixerInfo();
        for(Info i : mixerinfo){
            System.out.println(i.getName() + "\t" + i.getDescription());
        }
        
        if (!AudioSystem.isLineSupported(targetInfo)) {
            System.err.println("Error: ");
        }
        if (!AudioSystem.isLineSupported(sourceInfo)) {
            System.err.println("Error: ");
        }
        
        // Obtain and open the lines.
        try {
            _input = (TargetDataLine) AudioSystem.getLine(targetInfo);
            _input.open(_audioFormat);
            
            _output = (SourceDataLine) AudioSystem.getLine(sourceInfo);
            _output.open(_audioFormat);
            
            _view = new SoundPresenter();
            _dataCtl = new DataControl();
            _dataCtl.addDataWatcher(_view);
            _dataCtl.setFrequency(_conf.getSamplingFrequency());
            JFrame test = new JFrame();
            test.add(_view);
            test.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            test.setVisible(true);
            test.pack();
            Configuration.centerWindow(test);
            _frameSize = _conf.getFFTlength();
            _cap = new SoundCapturer(_input, _audioFormat, _frameSize);
            _play = new SoundPlayer(_output, _audioFormat);
            _play.setEnableSelf(_enableSelf);
            _cap.addSoundWatcher(_play);
            _cap.addSoundWatcher(_dataCtl);
            _playThread = new Thread(_play);
            _capThread = new Thread(_cap);
            
            _playThread.start();
            _capThread.start();
            
        } catch (LineUnavailableException ex) {
            System.out.println("The line is unavailable");
            ex.printStackTrace();
        }
        
        
    }
    public SoundCapturer getCapturer(){
        return _cap;
    }
    public SoundPlayer getPlayer() {
        return _play;
    }
    public void setEnabled(boolean enabled){
        if(_play != null){
            _play.setEnableSelf(enabled);
        } else {
            _enableSelf = enabled;
        }
    }
}
