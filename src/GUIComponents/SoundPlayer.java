/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUIComponents;

import Connection.DataTypes;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author Jonas
 */
public class SoundPlayer implements Runnable, DataWatcher, SoundWatcher {
    SourceDataLine _output;
    AudioFormat _audioFormat;
    private boolean _running;
    private ConcurrentLinkedQueue<byte[]> _byteQueue;
    private boolean _writing;
    private boolean _enableSelf;

    public SoundPlayer(SourceDataLine output, AudioFormat audioFormat) {
        _audioFormat = audioFormat;
        _output = output;
        _byteQueue = new ConcurrentLinkedQueue<>();
    }

    void setRunning(boolean b) {
        _running = b;
    }

    @Override
    public void run() {
        System.out.println("Is Ouput line open? " + _output.isOpen());
         if( _output.isOpen() ){
            byte[] buffer = new byte[_output.getBufferSize()];
            
            _output.start();

            _running = true;
            System.out.println("Starting Line write - BufferSize: " + _output.getBufferSize() + ", output level: "
                        + _output.getLevel() + ", BitDepth: " + _output.getFormat().getSampleSizeInBits());
            int numWritten = 0;
            while(isRunning()){
                if(getQueueSize() == 1){
                    buffer = _byteQueue.remove();
                     numWritten = _output.write(buffer, 0, buffer.length);
                } else if(getQueueSize() > 1){
                    buffer = mergeByteBuffers(_byteQueue.remove());
                    numWritten = _output.write(buffer, 0, buffer.length);
                }
                
                // Debug stuff:
            }
            _byteQueue.clear();
            
            _output.drain();
            
            _output.stop();

            _output.close();
            
            System.out.println("\n\nPlayer Stopped");
        }
    }

    private synchronized boolean isRunning() {
        return _running;
    }

    private int getQueueSize() {
        if(!_writing){
            return _byteQueue.size();
        } else {
            return -1;
        }
    }

    @Override
    public void fireDataThrough(Object o, int typeOfData) {
        if(typeOfData == DataTypes.SOUND_DATA_ID){
//            appendToQueue((byte[])o);
            lazyPrint("Writing to sound."+((byte[])o).length);
            appendToQueue((byte[])o);
        } else {
            lazyPrint("Received but wrong shit.");
        }
    }

    private void appendToQueue(byte[] b) {
        _writing = true;
        _byteQueue.add(b.clone());
        _writing = false;
    }

    private void lazyPrint(String string) {
        System.out.println(string);
    }

    @Override
    public void fireSoundThrough(byte[] b) {
        if(_enableSelf){
            _output.write((byte[])b, 0, ((byte[])b).length);
            
        } else{
            _output.flush();
        }
    }

    /**
     * @return the _enableSelf
     */
    public boolean isEnableSelf() {
        return _enableSelf;
    }

    /**
     * @param enableSelf the _enableSelf to set
     */
    public void setEnableSelf(boolean enableSelf) {
        this._enableSelf = enableSelf;
    }

    private byte[] mergeByteBuffers(byte[] previousByte) {
        byte[] nextByte = _byteQueue.remove();
        if(previousByte.length > nextByte.length){
            for(int n = 0; n < nextByte.length; n++){
                previousByte[n] = (byte)((previousByte[n] + nextByte[n])/2);
            }
            if(getQueueSize() > 0){
                return mergeByteBuffers(previousByte);
            } else {
                return previousByte;
            }
        } else {
            for(int n = 0; n < previousByte.length; n++){
                nextByte[n] = (byte)((previousByte[n] + nextByte[n])/2);
            }
            if(getQueueSize() > 0){
                return mergeByteBuffers(nextByte);
            } else {
                return nextByte;
            }
        }
    }
    
}
