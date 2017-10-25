package manu.scheduling.training.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.LineBorder;

public class ProgressFrameNoNum extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JProgressBar bar = new JProgressBar();
	//JLabel label = new JLabel("The training is in progress 0%.");

	public ProgressFrameNoNum(JFrame parent) {

		setResizable(false);
		setAlwaysOnTop(true);
		getContentPane().setLayout(null);
		setBounds(parent.getX() + parent.getWidth() / 2 - 150, parent.getY()
				+ parent.getHeight() / 2 - 20, 300, 40);
		setUndecorated(true);
		JPanel jp = new JPanel();
		jp.setLayout(null);
		jp.setBounds(0, 0, 300, 40);
		jp.add(bar);
		//jp.add(label);
		jp.setBorder(LineBorder.createGrayLineBorder());
		//label.setBackground(Color.gray);
		//label.setForeground(Color.BLUE);
		//label.setBounds(60, 5, 200, 20);
		bar.setMaximum(100);
		bar.setMinimum(0);
		bar.setBounds(5, 10, 290, 20);
		getContentPane().add(jp);
		setVisible(true);

	}

	public ProgressFrameNoNum() {
		// TODO Auto-generated constructor stub

	}

	public void setSize(JFrame parent) {
		setBounds(parent.getX() + parent.getWidth() / 2 - 150, parent.getY()
				+ parent.getHeight() / 2, 300, 60);
	}

	public void setProgess(final int n) {

		bar.setValue(n);
		//label.setText("The training is in progress " + n + "%.");

	}

}
