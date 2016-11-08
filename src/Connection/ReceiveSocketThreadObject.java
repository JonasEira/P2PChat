/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection;

import Connection.ConnPoint.state;
import GUIComponents.Configuration;
import GUIComponents.Configuration.modes;
import GUIComponents.DataWatcher;
import GUIComponents.MainWindow;
import com.sun.corba.se.spi.activation._ServerImplBase;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import p2pchat.MessageModel;

/////// ------ Thread Class ---------------------------
/**
 *
 * @author Jonas
 */
public class ReceiveSocketThreadObject implements Runnable {
    ServerSocket _ssock;
    DataOutputStream _dout;
    DataInputStream _din;
    Socket _s;
    ConnPoint _conn;
    ArrayList<DataWatcher> watchers;
    private boolean _exitInjected;
    public ReceiveSocketThreadObject(Socket _server, ConnPoint aThis) {
        _s = _server;
        _conn = aThis;
        _dout = null;
        _din = null;
        _exitInjected = false;
        watchers = new ArrayList();
    }

    public ReceiveSocketThreadObject(ServerSocket _server, ConnPoint aThis) {
        _ssock = _server;
        _conn = aThis;
        _dout = null;
        _din = null;
        _exitInjected = false;
        watchers = new ArrayList();
    }
    
    @Override
    public void run() {
        if(_conn.getMode() == _conn._mode.Server){
            if(_ssock.isClosed() == false){
                try {
                    printl("Waiting for connection from client");
                    _s = _ssock.accept();
                    _s.setSendBufferSize(65535);
                    _s.setReceiveBufferSize(65535);
//                    _s.setSoTimeout(9999);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }
            }
        }
        
        try {
            _din = new DataInputStream(_s.getInputStream());
            _dout = new DataOutputStream(_s.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if(_conn.getMode() == Configuration.modes.Client){
            _conn._remoteState = ConnPoint.state.opened;
            printl("Client Socket Open! :D");
        } else {
            _conn._localState = ConnPoint.state.opened;
            printl("Server Socket Open! :D");
        }
        byte data;
        
        while ( !_exitInjected) {

            try {
                data = _din.readByte();
                if((int)data == ConnPoint.SOUND_DATA_ID){
//                    System.out.print("Reading sound ");
                    handleRead(ConnPoint.SOUND_DATA_ID);
                } else if((int)data == ConnPoint.TEXT_ID){
//                    System.out.print("Reading text ");
                    handleRead(ConnPoint.TEXT_ID);
                } 
            } catch (IOException ex) {
                printl("IOEx - ReceiveThreadLoop");
            }
        }

        
        _conn._remoteState = ConnPoint.state.closed;
        _conn._localState = ConnPoint.state.closed;
       
        try {
            _s.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    public void close(){
        _exitInjected = true;
        try {
            _conn._remoteState = ConnPoint.state.closed;
            _conn._localState = ConnPoint.state.closed;
            _s.close();
            _ssock.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void addDataWatcher(DataWatcher d){
        watchers.add(d);
        printl("Added DataWatcher " + d.getClass().getSimpleName());
    }
    
    void removeDataWatcher(DataWatcher d) {
        watchers.remove(d);
    }
    
    public void sendToWatchers(Object o, int typeOfData){
        for(DataWatcher d : watchers){
            
            d.fireDataThrough(o, typeOfData);
//            printl("Sent to: " + d.getClass().getSimpleName());
        }
    }

    private void printl(String string) {
        System.out.println(string);
    }
    public DataOutputStream getOutputStream() {
        return this._dout;
    }

    private void handleRead(int typeOfData) {
        try {
            if(typeOfData == ConnPoint.TEXT_ID){
                int lengthToRead = (int)_din.readShort();
//                System.out.println(lengthToRead + " bytes.");
                String s = _conn.getName() + ": ";
                for(int n = 0; n < lengthToRead/2; n++){
                    s = s + _din.readChar();
                }                    
                sendToWatchers((Object)s, typeOfData);
            } else {
                int lengthToRead = (int)_din.readShort();
//                System.out.println(lengthToRead + " bytes.");
                byte[] b = new byte[lengthToRead];
                _din.readFully(b, 0, lengthToRead);
                sendToWatchers(b, typeOfData);
            }
            
        } catch (IOException ex) {
            printl("Heeeelvete, nåt gick snett med ljudparsning!!");
            ex.printStackTrace();
        }
    }

    

   
}
