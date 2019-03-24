/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUIComponents;

import Connection.ConnPoint;
import java.awt.Toolkit;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author Jonas
 */
public class Configuration {
    private ArrayList<ConnPoint> _hostList;
    private int _samplingFrequency;
	private double[] _tones;
	private int _fftLength;
    
    public static enum modes{
        Client,
        Server
    };
    
    public static enum tones{
    	C, D, E, F, G, A, B
    }
    
    public Configuration(){
        _hostList = new ArrayList();
        _samplingFrequency = 22050;
        _tones = genTones(440.0);
        _fftLength = 2048;
    }
    
    private double[] genTones(double baseFreq) {
		double[] tonesFreq = new double[8];
		double[] tonesStep = new double[]{9.0/8.0, 9.0/8.0, 256.0/243.0, 9.0/8.0, 9.0/8.0, 9.0/8.0, 256.0/243.0  };
		tonesFreq[0] = baseFreq*(9.0/8.0)*(256.0/243.0); 
		int n = 1;
		while(n < 8){
			tonesFreq[n] = tonesFreq[n-1]*tonesStep[n-1];
			System.out.println(tonesFreq[n-1]);
			n++;
		}
		System.out.println(tonesFreq[n-1]);
		
		return tonesFreq.clone();
	}

	void setSamplingFrequency(int i) {
        _samplingFrequency = i;
    }
     int getSamplingFrequency() {
        return _samplingFrequency;
    }
    void setFFTlength(int i){
    	_fftLength = i;
    }
    int getFFTlength(){
    	return _fftLength;
    }
    public void addServer(String name, int port) {
        ConnPoint tmpConnPoint = new ConnPoint();
        tmpConnPoint.setRemotePoint(null);
        tmpConnPoint.setLocalPort(port);
        tmpConnPoint.setName(name);
        tmpConnPoint.setMode(modes.Server);
        _hostList.add(tmpConnPoint);
    }
    public void addClient(InetAddress a, int port, String name){
        ConnPoint tmpConnPoint = new ConnPoint();
        tmpConnPoint.setRemotePoint(a);
        tmpConnPoint.setName(name);
        tmpConnPoint.setRemotePort(port);
        tmpConnPoint.setMode(modes.Client);
        _hostList.add(tmpConnPoint);       
    }
    
    public void removeHost(String name){
       for( int n = 0; n < _hostList.size(); n++){
            if( name.equalsIgnoreCase(_hostList.get(n).getName())){
                _hostList.remove(n);
                return;
            }
        }
    }
    public ConnPoint getLastPoint(){
        try{
            return _hostList.get(_hostList.size()-1);
        }catch(ArrayIndexOutOfBoundsException e){
            System.err.println("No ConnPoints available");
            return null;
        }
    }
    public int getHostCount(){
        return _hostList.size();
    }
    public InetAddress getInetAddress(String name){
        for( int n = 0; n < _hostList.size(); n++){
            if( name.equalsIgnoreCase(_hostList.get(n).getName())){
                return _hostList.get(n).getRemotePoint();
            }
        }
        return null;
    }
    public ConnPoint getConnPoint(String name){
        for( int n = 0; n < _hostList.size(); n++){
            if( name.equalsIgnoreCase(_hostList.get(n).getName())){
                return _hostList.get(n);
            }
        }
        return null;
    }
    public static void centerWindow(JFrame c){
        int midX = Toolkit.getDefaultToolkit().getScreenSize().width/2-c.getWidth()/2;
        int midY = Toolkit.getDefaultToolkit().getScreenSize().height/2-c.getHeight()/2;
        c.setLocation(midX, midY);
        c.validate();
    }

    public Object[] getAllConnections() {
        return _hostList.toArray();
    }
}
