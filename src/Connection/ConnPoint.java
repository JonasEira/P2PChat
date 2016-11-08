/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection;

import GUIComponents.Configuration;

import GUIComponents.DataWatcher;
import GUIComponents.SoundWatcher;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Jonas
 */
public class ConnPoint implements SoundWatcher {
	private InetAddress _remotePoint;
	private InetAddress _localPoint;
	private String _name;
	private ArrayList<DataWatcher> _watchers;

	DataInputStream _input;
	ServerSocket _server;
	ReceiveSocketThreadObject _rsThreadObject;
	SendSocketThreadObject _ssThreadObject;
	Socket _clientSock;
	Configuration.modes _mode;
	int _remotePort, _localPort;

	state _localState, _remoteState;

	private void printl(String string) {
		System.out.println(string);
	}

	@Override
	public void fireSoundThrough(byte[] b) {
		if (_ssThreadObject != null) {
			_ssThreadObject.sendData(b.clone());
		}
	}

	public ReceiveSocketThreadObject getSocketManager() {
		return this._rsThreadObject;
	}

	public state getRemoteState() {
		return this._remoteState;
	}

	public void sendText(String string) throws ChatException {
		if (_ssThreadObject != null) {
			_ssThreadObject.sendText(string);
		} else {
			throw new ChatException("Send thread down.");
		}
	}

	public static enum state {
		closed, waiting, opened, listening
	}

	public void setMode(Configuration.modes mode) {
		_mode = mode;
	}

	public Configuration.modes getMode() {
		return _mode;
	}

	public ConnPoint() {
		try {
			_localPoint = InetAddress.getByName("localhost");
			_remotePoint = InetAddress.getByName("localhost");
			_localState = state.closed;
			_remoteState = state.closed;
			_watchers = new ArrayList<>();
		} catch (IOException ex) {
			System.err.println("ServerSocket error: \n" + ex.toString());
		}
	}

	public void setRemotePoint(InetAddress remotePoint) {
		if (remotePoint != null) {
			this._remotePoint = remotePoint;
		}
	}

	public InetAddress getRemotePoint() {
		return _remotePoint;
	}

	public String getName() {
		return this._name;
	}

	public void setName(String a) {
		this._name = a;
	}

	public void setLocalPort(int i) {
		this._localPort = i;
	}

	public int getLocalPort() {
		return this._localPort;
	}

	public void setRemotePort(int p) {
		_remotePort = p;
	}

	public int getRemotePort() {
		return _remotePort;
	}

	public void addDataWatcher(DataWatcher d) {
		if (_rsThreadObject != null) {
			_rsThreadObject.addDataWatcher(d);
		} else {
			_watchers.add(d);
		}
	}

	public void removeDataWatcher(DataWatcher d) {
		if (_rsThreadObject != null) {
			_rsThreadObject.removeDataWatcher(d);
		} else {
			_watchers.remove(d);
		}
	}

	public void connect() {
		try {
			_clientSock = new Socket(_remotePoint, _remotePort);

			_rsThreadObject = new ReceiveSocketThreadObject(_clientSock, this);
			for (DataWatcher dw : _watchers) {
				_rsThreadObject.addDataWatcher(dw);
				printl("x");
			}

			Thread receiveThread = new Thread(_rsThreadObject);
			System.out.println("Starting Receive Thread for user: "
					+ this._name);
			receiveThread.start();

			_ssThreadObject = new SendSocketThreadObject(
					_rsThreadObject.getOutputStream(), this);
			Thread sendThread = new Thread(_ssThreadObject);
			System.out.println("Starting Send Thread for user: " + this._name);
			sendThread.start();

		} catch (IOException ex) {
			_remoteState = state.closed;
			System.err.println("Error connecting: \n" + ex.toString());
		}
	}

	public void listen() {
		try {
			_server = new ServerSocket(_localPort);
			_rsThreadObject = new ReceiveSocketThreadObject(_server, this);
			for (DataWatcher dw : _watchers) {
				_rsThreadObject.addDataWatcher(dw);
			}
			Thread receiveThread = new Thread(_rsThreadObject);
			System.out.println("Starting Receive Thread for user: "
					+ this._name);
			receiveThread.start();
			_localState = state.listening;

			_ssThreadObject = new SendSocketThreadObject(
					_rsThreadObject.getOutputStream(), this);
			Thread sendThread = new Thread(_ssThreadObject);
			System.out.println("Starting Send Thread for user: " + this._name);
			sendThread.start();
		} catch (IOException ex) {
			_localState = state.closed;
		}
	}

	public void close() {
		if (this._mode == null) {
			System.err.println("EH?");
		} else {
			_rsThreadObject.close();
			_ssThreadObject.close();
		}
	}
}
