package manu.scheduling.training.gui;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.LineBorder;

public class ProgressFrame extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JProgressBar bar = new JProgressBar();
	JLabel label = new JLabel("The training is in progress 0%.");
	static Point origin = new Point();
	public ProgressFrame(JFrame parent) {

		setResizable(false);
		setAlwaysOnTop(true);
		getContentPane().setLayout(null);
		setBounds(parent.getX() + parent.getWidth() / 2 - 150, parent.getY()
				+ parent.getHeight() / 2 - 30, 300, 60);
		setUndecorated(true);
		JPanel jp = new JPanel();
		jp.setLayout(null);
		jp.setBounds(0, 0, 300, 60);
		jp.add(bar);
		jp.add(label);
		jp.setBorder(LineBorder.createGrayLineBorder());
		label.setBackground(Color.gray);
		label.setForeground(Color.BLUE);
		label.setBounds(60, 5, 200, 20);
		bar.setMaximum(100);
		bar.setMinimum(0);
		bar.setBounds(5, 30, 290, 20);
		getContentPane().add(jp);
		setVisible(true);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				origin.x = e.getX();
				origin.y = e.getY();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {

				Point p = getLocation();
				setLocation(p.x + e.getX() - origin.x, p.y + e.getY()
						- origin.y);
			}
		});

	}

	public ProgressFrame() {
		// TODO Auto-generated constructor stub

	}

	public void setSize(JFrame parent) {
		setBounds(parent.getX() + parent.getWidth() / 2 - 150, parent.getY()
				+ parent.getHeight() / 2, 300, 60);
	}

	public void setProgess(final int n) {

		bar.setValue(n);
		label.setText("The training is in progress " + n + "%.");

	}

}
