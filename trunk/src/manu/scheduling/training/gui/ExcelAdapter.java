package manu.scheduling.training.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

public class ExcelAdapter implements ActionListener {
	private String rowstring, value;
	private Clipboard system;
	private StringSelection stsel;
	private JTable JTable1;

	/**
	 * Excel 适配器由 JTable 构成， 它实现了 JTable 上的复制粘贴 功能，并充当剪贴板监听程序。
	 */
	public ExcelAdapter(JTable myJTable) {
		JTable1 = myJTable;
		KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.CTRL_MASK, false);
		// 确定复制按键用户可以对其进行修改
		// 以实现其它按键组合的复制功能。
		KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V,
				ActionEvent.CTRL_MASK, false);
		// 确定粘贴按键用户可以对其进行修改
		// 以实现其它按键组合的复制功能。
		JTable1.registerKeyboardAction(this, "Copy", copy,
				JComponent.WHEN_FOCUSED);
		JTable1.registerKeyboardAction(this, "Paste", paste,
				JComponent.WHEN_FOCUSED);
		system = Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	/**
	 * 此适配器运行图表的公共读方法。
	 */
	public JTable getJTable() {
		return JTable1;
	}

	public void setJTable(JTable JTable1) {
		this.JTable1 = JTable1;
	}

	/**
	 * 在我们监听此实现的按键上激活这种方法。 此处，它监听复制和粘贴 ActionCommands。 包含不相邻单元格的选择导致选择无效，
	 * 而且此后复制动作无法执行。 粘贴的方法是将选定内容的左上角与 JTable 的当前选定内容的第一个元素对齐。
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareTo("Copy") == 0) {
			StringBuffer sbf = new StringBuffer();
			// 检查以确保我们仅选择了单元格的
			// 相邻块
			int numcols = JTable1.getSelectedColumnCount();
			int numrows = JTable1.getSelectedRowCount();
			int[] rowsselected = JTable1.getSelectedRows();
			int[] colsselected = JTable1.getSelectedColumns();
			if (!((numrows - 1 == rowsselected[rowsselected.length - 1]
					- rowsselected[0] && numrows == rowsselected.length) && (numcols - 1 == colsselected[colsselected.length - 1]
					- colsselected[0] && numcols == colsselected.length))) {
				JOptionPane.showMessageDialog(null, "Invalid Copy Selection",
						"Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);
				return;
			}
			for (int i = 0; i < numrows; i++) {
				for (int j = 0; j < numcols; j++) {
					sbf.append(JTable1.getValueAt(rowsselected[i],
							colsselected[j]));
					if (j < numcols - 1)
						sbf.append(" ");
				}
				sbf.append(" ");
			}
			stsel = new StringSelection(sbf.toString());
			system = Toolkit.getDefaultToolkit().getSystemClipboard();
			system.setContents(stsel, stsel);
		}
		if (e.getActionCommand().compareTo("Paste") == 0) {
			System.out.println("Trying to Paste");
			int startRow = (JTable1.getSelectedRows())[0];
			int startCol = (JTable1.getSelectedColumns())[0];
			try {
				String trstring = (String) (system.getContents(this)
						.getTransferData(DataFlavor.stringFlavor));
				System.out.println("String is:" + trstring);
				StringTokenizer st1 = new StringTokenizer(trstring, " ");
				for (int i = 0; st1.hasMoreTokens(); i++) {
					rowstring = st1.nextToken();
					StringTokenizer st2 = new StringTokenizer(rowstring, " ");
					for (int j = 0; st2.hasMoreTokens(); j++) {
						value = (String) st2.nextToken();
						if (startRow + i < JTable1.getRowCount()
								&& startCol + j < JTable1.getColumnCount())
							JTable1.setValueAt(value, startRow + i, startCol
									+ j);
						System.out.println("Putting " + value + "atrow="
								+ startRow + i + "column=" + startCol + j);
					}
				}
			} catch (Exception ex) {
			}
		}
	}
}
