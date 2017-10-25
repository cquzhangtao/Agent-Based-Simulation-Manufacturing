package manu.simulation.gui.component;

import javax.swing.tree.DefaultMutableTreeNode;

public class MyNode extends DefaultMutableTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4612596626972755957L;
	public int parentIndex = 0;
	public int index = 0;
	public String name = "";

	public MyNode(int parentIndex, int index, String name) {
		this.parentIndex = parentIndex;
		this.index = index;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}