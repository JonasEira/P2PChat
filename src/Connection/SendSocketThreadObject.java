/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection;

import GUIComponents.Configuration;
import java.io.*;

/**
 *
 * @author Jonas
 */
public class SendSocketThreadObject implements Runnable {
    private boolean _running = true;
    private boolean _fireOnce;
    private DataOutputStream _output;
    private byte[] _data;
    private ConnPoint _p;

    public SendSocketThreadObject(){}
    public SendSocketThreadObject(DataOutputStream d){
        _output = d;
    }
    public SendSocketThreadObject(DataOutputStream d, ConnPoint p){
        _output = d;
        _p = p;
    }
    public void close(){
        _running = false;
    }
    public void sendText(String s) {
        
        if((_p.getMode() == _p._mode.Client) && (_p._remoteState == _p._remoteState.opened) 
                || ((_p.getMode() == _p._mode.Server) && (_p._localState == _p._localState.opened))) {
                if(_output == null){ _output = _p.getSocketManager().getOutputStream(); }
                printl("Send text!!");
                int len = s.length()*2;
                byte[] data = new byte[len + 3];
                data[0] = (byte)ConnPoint.TEXT_ID;
                data[1] = (byte)(len >> 8 & 0xFF);
                data[2] = (byte)(len & 0xFF);
                
                for(int n = 0; n < s.length(); n++){
                    int c = s.codePointAt(n);
                    System.out.println("c=" + c);
                    data[n*2+3] = (byte)(c >> 8 & 0xFF);
                    data[n*2+4] = (byte)(c & 0xFF);
                }
                _data = data.clone();
                _fireOnce = true;
                printl("fireonce set!");
            }
    }
    
    private synchronized byte[] getByteArray(){
        return _data;
    }
    
    public void sendData(byte[] data){
        if((_p._mode == Configuration.modes.Client && _p._remoteState == ConnPoint.state.opened) 
        || (_p._mode == Configuration.modes.Server && _p._localState == ConnPoint.state.opened)){
            if(_output == null){ _output = _p.getSocketManager().getOutputStream(); }
//            printl("Passing here.");
            byte[] tmp = new byte[data.length + 3];
            
            tmp[0] = (byte)_p.SOUND_DATA_ID;
            tmp[1] = (byte)(data.length >> 8 & 0xFF);
            tmp[2] = (byte)(data.length & 0xFF);
            for(int n = 0; n < data.length; n++){
                tmp[n+3] = data[n];
            }
            _data = tmp.clone();
            _fireOnce = true;
        }
    }
    
    @Override
    public void run() {
        while(_running){
            while(!_fireOnce){
                try {                    
                    Thread.currentThread().sleep(16);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
//            printl("Fired!");
            try {
                DataOutputStream d = getOutputStream();
//                printl("Sending " + getByteArray().length);
                d.write(getByteArray(), 0, getByteArray().length);
            } catch (IOException ex) {
               printl("IOEx - SendThreadLoop");
            }
            _fireOnce = false;
        }
    }

    private static void printl(String string) {
        System.out.println(string);
    }

    private DataOutputStream getOutputStream() {
        return _output;
    }
    
}
