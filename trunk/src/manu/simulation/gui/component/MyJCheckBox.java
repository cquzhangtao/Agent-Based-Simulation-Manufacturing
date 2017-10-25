package manu.simulation.gui.component;

import javax.swing.JCheckBox;

public class MyJCheckBox extends JCheckBox {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4020196359053251766L;
	public int index = 0;

	public MyJCheckBox(String name, boolean checked, int index) {
		super(name, checked);
		this.index = index;
		this.setOpaque(false);
	}

}