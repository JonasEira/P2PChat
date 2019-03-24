/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUIComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;

import javax.swing.JPanel;

import Connection.DataTypes;
import GUIComponents.DataControl.DataFrame;

/**
 *
 * @author Jonas
 */
public class SoundPresenter extends JPanel implements DataWatcher {

	private byte[] _buffer;
	private int[] data;
	private int nMsg;
	int x1, y1, x2, y2;
	int max;
	static SoundPresenter lastSP;

	public SoundPresenter() throws HeadlessException {
		_buffer = new byte[] { 0 };
		this.setPreferredSize(new Dimension(300, 100));
		this.setOpaque(false);
		max = 0;
		lastSP = this;
		data = new int[] { 0 };
		nMsg = 0;

	}

	@Override
	public void paint(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setBackground(Color.BLACK);
		g2.clearRect(0, 0, this.getWidth(), this.getHeight());
		g2.setColor(Color.darkGray);

		int x_Axis = this.getHeight();
		// int framelength = (int)((double)_buffer.length/framerate);
		int framelength = 0;
		if (data != null) {
			framelength = data.length / 2;
		} else {
			framelength = _buffer.length / 2;
		}
		int bitDepth = (int) SoundCapturer.AudioFormat.getFrameRate()
				* SoundCapturer.AudioFormat.getSampleSizeInBits();
		int width = this.getWidth();
		int height = this.getHeight();
		int tmp1 = 0, tmp2 = 0;

		int oldMax = max;
		int newMax = bitDepth;
		for (int i = 0; i < framelength; i++) {
			if (newMax < data[i])
				newMax = data[i];
		}
		int mean = (oldMax + newMax) / 2;

		// System.out.println("Max="+max + " Bitdepth=" + bitDepth);
		for (int i = 0; i < framelength; i++) {
			if (i % (framelength / 16) == 0) {
				g2.setColor(Color.gray);
				g2.drawLine(width * i / framelength, this.getHeight(), width
						* i / framelength, 0);
			}
			g2.setColor(Color.gray);
			int yVal = (int) (this.getHeight() * (1.0 - (double) bitDepth
					/ (double) newMax));
			g2.drawLine(0, yVal, this.getWidth(), yVal);
			g2.drawString("bitDepth=" + bitDepth, 0, yVal);

			if (data != null) {
				tmp1 = data[i];
				g2.setColor(new Color(
						(int) Math
								.round(255.0 * ((double) tmp1 / (double) newMax)),
						(int) Math
								.round(128.0 + 127.0 * ((double) tmp1 / (double) newMax)),
						(int) Math
								.round(255.0 - 255.0 * ((double) tmp1 / (double) newMax))));
				x1 = width * (i - 1) / framelength;
				y1 = x_Axis - (height * tmp2) / (mean);
				x2 = width * i / framelength;
				y2 = x_Axis - (height * tmp1) / (mean);
				int xmid = (x2 + x1) / 2;
				int ymid = (y2 + y1) / 2;
				g2.drawLine(x1, y1, xmid, ymid);
				g2.setColor(new Color(
						(int) Math
								.round(255.0 * ((double) tmp1 / (double) newMax)),
						(int) Math
								.round(128.0 + 127.0 * ((double) tmp1 / (double) newMax)),
						(int) Math
								.round(255.0 - 255.0 * ((double) tmp1 / (double) newMax))));
				g2.drawLine(xmid, ymid, x2, y2);
				// int sideLength =
				// (int)Math.ceil((double)9*(double)tmp1/(double)max +
				// 3*Math.random());
				// g2.fillRect(
				// width*(i-1)/framelength,
				// x_Axis-(height*tmp1)/(max+1),
				// sideLength,
				// sideLength
				// );
				tmp2 = tmp1;
			} else {
				switch (i % 2) {
				case 0:
					tmp1 = _buffer[i];
					break;
				case 1:
					tmp1 = (_buffer[i] << 8) + tmp1;
					g2.drawLine(width * (i - 1) / framelength, x_Axis
							- (height * tmp1) / 65535, width * i / framelength,
							x_Axis - (height * tmp1) / 65535);
					tmp2 = tmp1;
					break;
				default:
					break;
				}
			}

		}
		max = max - (max - mean / 8);
		int buffLen = -1;
		if (data == null) {
			buffLen = _buffer.length;
		} else {
			buffLen = data.length;
		}
		g2.drawString("FFT length=" + buffLen, 25, 25);
		// System.out.println("Presenter (Paint): " +
		// Thread.currentThread().getName());
	}

	static void presentData(DataFrame f) {
		// Todo: Remake this class to paint a transition from last state to
		// current state.

		lastSP.data = f.getIntData();
		lastSP._buffer = null;
		lastSP.repaint();
	}

	// static void presentData(double[] f) {
	// byte[] b = new byte[f.length*2];
	// for(int n = 0; n < f.length; n++){
	// int val = (int)f[n];
	// b[2*n] = (byte)(val & 0xFF);
	// b[2*n+1] = (byte)((val >> 8) & 0xFF);
	// }
	// lastSP.data = null;
	// lastSP._buffer = b;
	// lastSP.repaint();
	//
	// }

	@Override
	public void fireDataThrough(Object o, int typeOfData) {
		if (typeOfData == DataTypes.GRAPHICAL_DATA) {
			this.data = ((DataControl.DataFrame) o).getIntData();
			this._buffer = null;
			this.repaint();
		}

	}

}
