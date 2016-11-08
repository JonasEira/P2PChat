/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUIComponents;

import Connection.ConnPoint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.security.Principal;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;

/**
 *
 * @author Jonas
 */
class SoundCapturer implements Runnable, ActionListener{

    static boolean isBigEndian() {
        return AudioFormat.isBigEndian();
    }
    private ArrayList<SoundWatcher> _swlist;
    private final TargetDataLine _input;
    public static AudioFormat AudioFormat;
    private boolean running;
    private byte[] receivedBuffer;
    public SoundCapturer(){
        _swlist = new ArrayList();
        running = false;
        _input = null;

        
        AudioFormat = null;
        
    }
    
    public SoundCapturer(TargetDataLine input, AudioFormat audioFormat) {
        receivedBuffer = new byte[0];
        _swlist = new ArrayList<SoundWatcher>();
        running = false;
        _input = input;
     
        AudioFormat = audioFormat;
    }
    
    
    public void run(){
        System.out.println("Is Input line open? " + _input.isOpen());
        
        if(_input.isOpen()){
            // frameSize defines the number of bytes read from the stream and
            // sent to the FFT and player.. 
//            int frameSize = (int)Math.pow(2,Math.floor(Math.log(AudioFormat.getFrameRate())/Math.log(2)))/16;
            int frameSize = 8192;
//            receivedBuffer = new byte[_input.getBufferSize()];
            receivedBuffer = new byte[frameSize];
            
            _input.start();

            running = true;
//            System.out.println("Starting Line read - BufferSize: " + _input.getBufferSize() + ", input level: "
//                        + _input.getLevel() + ", BitDepth: " + _input.getFormat().getSampleSizeInBits());
            while(isRunning()){
                
                int numRead = _input.read(receivedBuffer, 0, receivedBuffer.length);
                // Notify watchers
                byte[] bufferclone = receivedBuffer.clone();
                this.notifyWatchers(bufferclone);
                
            }
            
            //_input.drain();
            
            _input.stop();
            
            _input.close();

            System.out.println("\n\nCapture Stopped");
        }
        
    }

    /**
     * @return the running
     */
    public synchronized boolean isRunning() {
        return running;
    }

    /**
     * @param running the running to set
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
    public void addSoundWatcher(SoundWatcher dw){
        printl("Added SoundWatcher: " + dw.getClass().getSimpleName());
        _swlist.add(dw);
    }
    void removeSoundWatcher(ConnPoint c) {
        _swlist.remove(c);
    }
    public void notifyWatchers(byte[] data){
//        System.out.println("Notify: " + Thread.currentThread().getName());
        for(SoundWatcher dw : _swlist){
            dw.fireSoundThrough(data);
        }
    }

    private void printl(String string) {
        System.out.println(string);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getActionCommand()=="Start"){
			
		}

		if( e.getActionCommand()=="Stop"){
			
		}
		
	}

    
}
