package manu.scheduling.training.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * @author Tao Zhang
 */
public class BusyPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel iconLabel;

	private JLabel cancleLabel;

	private JLabel tipLabel;

	private JLabel progressLabel;

	private ImageIcon loading;

	private String tipText = "Please wait...";

	private boolean progressVisiable;

	private boolean cancleable=true;

	private double progress;

	private boolean cancled;
	
	private List<BusyPanelListener>busyPanelListeners=new ArrayList<BusyPanelListener>();

	public BusyPanel(final BusyComponentInterface frame) {

		super();
		setPreferredSize(new Dimension(300, 100));
		setLayout(null);
		setOpaque(false);
		setVisible(false);
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		loading = new ImageIcon(BusyPanel.class.getResource("busy_blue.gif"));
		iconLabel = new JLabel("", loading, JLabel.CENTER);
		add(iconLabel);

		tipLabel = new JLabel(tipText);
		add(tipLabel);

		if (cancleable) {
			cancleLabel = new JLabel("Cancle");
			cancleLabel.setForeground(Color.BLUE);
			add(cancleLabel);
			cancleLabel.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent arg0) {

					//frame.setBusy(false);
					cancled = true;
					for(BusyPanelListener lis:busyPanelListeners){
						lis.onCancled();
					}
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {

					cancleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}

				@Override
				public void mouseExited(MouseEvent arg0) {

				}

				@Override
				public void mousePressed(MouseEvent arg0) {

				}

				@Override
				public void mouseReleased(MouseEvent arg0) {

				}
			});

		}

		progressLabel = new JLabel("0%");
		progressLabel.setBackground(Color.white);
		progressLabel.setVisible(false);
		add(progressLabel);

		
	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		Color ppColor = new Color(1.0f, 1.0f, 1.0f, 0.7f); // r,g,b,alpha
		g.setColor(ppColor);
		g.fillRect(0, 0, this.getWidth(), this.getHeight()); // x,y,width,height

		Graphics2D g2 = (Graphics2D) g.create();
		Rectangle2D strBound = g2.getFontMetrics().getStringBounds(tipText, g2);
		int gap = 5;
		double totalHeight = loading.getIconHeight() + 2 * strBound.getHeight()
				+ 2 * gap;
		double startY = this.getHeight() / 2.0 - totalHeight / 2.0;

		iconLabel
				.setBounds(
						(int) (this.getWidth() / 2.0 - loading.getIconWidth() / 2.0),
						(int) (startY), loading.getIconWidth(),
						loading.getIconHeight());

		if (isProgressVisiable()) {
			String str = String.valueOf((int) progress) + "%";
			Rectangle2D strBound1 = g2.getFontMetrics()
					.getStringBounds(str, g2);
			float y = (float) (startY + loading.getIconHeight() / 2.0
					- strBound1.getHeight() / 2.0
					+ progressLabel.getInsets().top + 5);
			float x = (float) (this.getWidth() / 2.0 - strBound1.getWidth()
					/ 2.0 + progressLabel.getInsets().left + 5);
			progressLabel.setText(str);
			progressLabel.setBounds((int) x, (int) y,
					(int) strBound1.getWidth(), (int) strBound1.getHeight());
		}

		startY = startY + loading.getIconHeight() + gap;
		tipLabel.setBounds(
				(int) (this.getWidth() / 2.0 - strBound.getWidth() / 2.0),
				(int) (startY + strBound.getHeight() / 2.0),
				(int) strBound.getWidth() + 50, (int) strBound.getHeight());
		if (cancleable) {
			strBound = g2.getFontMetrics().getStringBounds("Cancle", g2);
			startY = startY + strBound.getHeight() + gap;
			cancleLabel.setBounds(
					(int) (this.getWidth() / 2.0 - strBound.getWidth() / 2.0),
					(int) (startY + strBound.getHeight() / 2.0),
					(int) (strBound.getWidth() + 20),
					(int) ((strBound.getHeight())));
		}

		g2.dispose();

	}

	public String getTipText() {

		return tipText;
	}

	public void setTipText(String tipText) {

		this.tipText = tipText;
		tipLabel.setText(tipText);
	}

	public boolean isProgressVisiable() {

		return progressVisiable;
	}

	public void setProgressVisiable(boolean progressVisiable) {

		this.progressVisiable = progressVisiable;
		progressLabel.setVisible(progressVisiable);
	}

	public double getProgress() {

		return progress;
	}

	public void setProgress(double progress) {

		this.progress = progress;
		if (progressVisiable) {
			this.updateUI();
		}
	}

	public void setCancled(boolean cancled) {

		this.cancled = cancled;

	}

	public boolean isCancled() {

		return cancled;
	}

	@Override
	public void setVisible(boolean b) {

		super.setVisible(b);
		if (b) {
			cancled = false;
		}
	}

	public boolean isCancleable() {
		return cancleable;
	}

	public void setCancleable(boolean cancleable) {
		this.cancleable = cancleable;
	}
	public void addBusyPanelListener(BusyPanelListener lis){
		busyPanelListeners.add(lis);
	}
}
