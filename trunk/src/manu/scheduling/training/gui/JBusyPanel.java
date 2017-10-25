
package manu.scheduling.training.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * 
 * @author Tao Zhang
 */
public class JBusyPanel extends JPanel implements BusyComponentInterface {
	
	private static final long serialVersionUID = 1L;
	
	private Map<Component, MouseListener[]> mouseListeners = new HashMap<Component, MouseListener[]>();
	
	private Map<Component, MouseMotionListener[]> mouseMotionListeners = new HashMap<Component, MouseMotionListener[]>();
	
	private Map<Component, MouseWheelListener[]> mouseWheelListeners = new HashMap<Component, MouseWheelListener[]>();
	
	private Container mainPanel;
	
	private JPanel contentPanel;
	
	private BusyPanel busyPanel;
	
	public JBusyPanel() {
	
		this("");
	}
	
	public JBusyPanel(String title) {

		this.setPreferredSize(new Dimension(800, 600));
		mainPanel = this;
		mainPanel.setLayout(null);
		
		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		mainPanel.add(contentPanel);
		
		busyPanel = new BusyPanel(this);
		
		mainPanel.add(busyPanel);
		
		mainPanel.setComponentZOrder(contentPanel, 0);
		mainPanel.setComponentZOrder(busyPanel, 1);
		Runnable doHelloWorld = new Runnable() {
			public void run() {
				contentPanel.setBounds(0, 0, mainPanel.getWidth(), mainPanel.getHeight());
				busyPanel.setBounds(0, 0, mainPanel.getWidth(), mainPanel.getHeight());
			}
		};
		SwingUtilities.invokeLater(doHelloWorld);
		
		mainPanel.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentHidden(ComponentEvent arg0) {
			
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {
			
			}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
			
				contentPanel.setBounds(0, 0, mainPanel.getWidth(), mainPanel.getHeight());
				busyPanel.setBounds(0, 0, mainPanel.getWidth(), mainPanel.getHeight());
				for(ComponentListener lis:contentPanel.getComponentListeners()){
					lis.componentResized(arg0);
				}
				
			}
			
			@Override
			public void componentShown(ComponentEvent arg0) {
				for(ComponentListener lis:contentPanel.getComponentListeners()){
					lis.componentShown(arg0);
				}
			}
		});
		contentPanel.addComponentListener(new ComponentListener(){

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
		
			}

			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub
				for(ComponentListener lis:contentPanel.getComponent(0).getComponentListeners()){
					lis.componentResized(e);
				}
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				for(ComponentListener lis:contentPanel.getComponent(0).getComponentListeners()){
					lis.componentShown(e);
				}
			}});
		

	}
	
	private void setComponentEnable(Component component, boolean value,boolean onlyMouseListener) {
	
		if (value) {
			MouseListener[] mls = mouseListeners.get(component);
			if (mls != null) {
				for (MouseListener ml : mls) {
					component.addMouseListener(ml);
				}
			}
			MouseMotionListener[] mmls = mouseMotionListeners.get(component);
			if (mls != null) {
				for (MouseMotionListener ml : mmls) {
					component.addMouseMotionListener(ml);
				}
			}
			MouseWheelListener[] mwls = mouseWheelListeners.get(component);
			if (mls != null) {
				for (MouseWheelListener ml : mwls) {
					component.addMouseWheelListener(ml);
				}
			}
			
		}
		else {
			MouseMotionListener[] mmls = component.getMouseMotionListeners();
			mouseMotionListeners.put(component, mmls);
			for (MouseMotionListener ml : mmls) {
				component.removeMouseMotionListener(ml);
			}
			MouseWheelListener[] mwls = component.getMouseWheelListeners();
			mouseWheelListeners.put(component, mwls);
			for (MouseWheelListener ml : mwls) {
				component.removeMouseWheelListener(ml);
			}
			MouseListener[] mls = component.getMouseListeners();
			mouseListeners.put(component, mls);
			for (MouseListener ml : mls) {
				component.removeMouseListener(ml);
			}
		}
		if(!onlyMouseListener)
			component.setEnabled(value);
		
		if (component instanceof Container) {
			Container container = (Container) component;
			for (Component comp : container.getComponents()) {
				setComponentEnable(comp, value,onlyMouseListener);
			}
		}
		
	}
	
	public void setBusy(final boolean value) {
	
				if (value) {
					mouseListeners.clear();
					setComponentEnable(contentPanel, false,false);
					busyPanel.setVisible(true);
					mainPanel.setComponentZOrder(busyPanel, 0);
					mainPanel.setComponentZOrder(contentPanel, 1);
					mainPanel.repaint();
				}
				else {
					busyPanel.setVisible(false);
					setComponentEnable(contentPanel, true,false);
					mainPanel.setComponentZOrder(busyPanel, 1);
					mainPanel.setComponentZOrder(contentPanel, 0);
					mainPanel.repaint();
				}

	}
	public void setBusyFromOtherThreads(final boolean value) {
		Runnable doHelloWorld = new Runnable() {
			public void run() {

				setBusy(value);
			}
		};
		try {
			SwingUtilities.invokeAndWait(doHelloWorld);
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void recovery() {
		Runnable doHelloWorld = new Runnable() {
			public void run() {
				setComponentEnable(contentPanel, true, true);
			}
		};
		try {
			SwingUtilities.invokeAndWait(doHelloWorld);
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public JPanel getContentPane() {
	
		return contentPanel;
	}
	
	public void setWaitingTip(String string) {
	
		busyPanel.setTipText(string);
	}
	
	public void setCancled(boolean cancled) {
	
		busyPanel.setCancled(cancled);
	}
	
	public void setProgress(double v) {
	
		busyPanel.setProgress(v);
	}
	
	public double getProgress() {
	
		return busyPanel.getProgress();
	}
	
	public boolean isCancled() {
	
		return busyPanel.isCancled();
	}
	
	public boolean isProgressVisiable() {
	
		return busyPanel.isProgressVisiable();
	}
	
	public void setProgressVisiable(boolean progressVisiable) {
	
		busyPanel.setProgressVisiable(progressVisiable);
	}
	public void setCancleable(boolean cancleable) {
		busyPanel.setCancleable(cancleable) ;
	}
	public void addBusyPanelListener(BusyPanelListener lis){
		busyPanel.addBusyPanelListener(lis);
	}
}
