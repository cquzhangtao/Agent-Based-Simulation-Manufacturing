package manu.scheduling.training.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import manu.scheduling.training.BPField;
import manu.scheduling.training.KmeansField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MyJTable<T> extends JTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MyCellRenderer renderer;
	private JFrame pframe;
	
	public MyJTable(JFrame pframe,List<T> dataset,Class annotation){
		this(pframe);
		List<String> headerList = getFieldNames(dataset.get(0),annotation);
		headerList.add(0, "");
		Object[][] cellData = new Object[dataset.size()][headerList
				.size()];
		Field[] fields = dataset.get(0).getClass().getDeclaredFields();
		int rowIndex = 0;
		for (T data : dataset) {
			cellData[rowIndex][0] =rowIndex+1;
			int colIndex = 1;
			for (Field field : fields) {
				try {
					field.setAccessible(true);
					if (field.getAnnotation(annotation) != null)
					{
						if (field.get(data).getClass().isArray()) {
							double[] d = (double[]) field.get(data);
							for (int i = 0; i < d.length; i++) {
								cellData[rowIndex][colIndex] = d[i];
								colIndex++;
							}
						} else {
							cellData[rowIndex][colIndex] = field
									.getDouble(data);
							colIndex++;
						}
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			rowIndex++;
		}
		Object[] header = headerList.toArray();
		DefaultTableModel model = new DefaultTableModel(cellData, header) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Class<?> getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			}
		};
		this.setModel(model);
		
		
	}
	
	public MyJTable(JFrame pframe){
		super();
		this.pframe=pframe;
		this.renderer = new MyCellRenderer();
		this.setLocale(Locale.ENGLISH);
		setColumnSelectionAllowed(true);
		setRowSelectionAllowed(true);
		setAutoCreateRowSorter(true);
		setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);	
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		for (int i=0;i<getColumnModel().getColumnCount();i++) {
			getColumnModel().getColumn(i).setMinWidth(40);
			getColumnModel().getColumn(i).setMaxWidth(150);
			getColumnModel().getColumn(i).setHeaderRenderer(new HeaderRendererHh());
		}
		final JTableHeader header = getTableHeader();
		header.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (!e.isShiftDown())
					clearSelection();
				int pick = header.columnAtPoint(e.getPoint());
				//addColumnSelectionInterval(pick, pick);
				if (e.getClickCount() == 2) {
					drawFrequencyChart(pick);
				}
			}

		});

	}

	public MyJTable(JFrame pframe,Object[][] cellData, Object[] header2) {
		this(pframe);
		DefaultTableModel model = new DefaultTableModel(cellData, header2) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Class<?> getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			}
		};
		this.setModel(model);
		
	}
	
	public List<String> getFieldNames(T data,Class annotation){
		List<String> features = new ArrayList<String>();
		Field[] fields = data.getClass().getDeclaredFields();
		for (Field field : fields) {

			if (field.getAnnotation(annotation)!=null) {
				try {
					field.setAccessible(true);
					if (field.get(data).getClass().isArray()) {

						double[] d = (double[]) field.get(data);
						for (int i = 0; i < d.length; i++)
							features.add(field.getName() + i);

					} else {
						features.add(field.getName());
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		return features;
		
	}

	public TableCellRenderer getCellRenderer(int row, int column) {
		return renderer;
	}

	private void drawFrequencyChart(int colIndex) {
		ArrayList<Double> data = new ArrayList<Double>();
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (int i = 0; i < this.getRowCount(); i++) {
			if (getValueAt(i, colIndex) == null) {
				continue;
			}
			String str = this.getValueAt(i, colIndex).toString();
			if (!str.isEmpty()) {
				double d = Double.valueOf(str);
				data.add(d);
				if (d < min)
					min = d;
				if (d > max)
					max = d;
			}
		}
		int num = 10;
		double interval = (max - min) / num;
		int[] amount = new int[num + 1];
		if (min == max) {
			amount[0] = data.size();
		} else {
			for (double d : data) {
				for (int i = 0; i < num + 1; i++) {
					if (d >= min + i * interval && d < min + (i + 1) * interval) {
						amount[i]++;
						break;
					}
				}
			}

		}
		JDialog frame = new JDialog(pframe,"Frequency - " + this.getColumnName(colIndex));
		frame.setBounds(pframe.getX()+pframe.getWidth()/2-200, pframe.getY()+pframe.getHeight()/2-150, 400, 300);
		frame.setResizable(false);
		frame.getContentPane().add(
				xyLineChartSingleSeries("", getColumnName(colIndex),
						"Frequency", amount, interval, min));
		frame.setVisible(true);

	}

	public ChartPanel xyLineChartSingleSeries(String title, String xName,
			String yName, int[] data, double interval, double min) {

		XYSeriesCollection result = new XYSeriesCollection();

		XYSeries series1 = new XYSeries("a");
		series1.add(min, 0);
		for (int i = 0; i < data.length; i++) {
			series1.add(min + i * interval, data[i]);
			series1.add(min + (i + 1) * interval, data[i]);
		}
		series1.add(min + data.length * interval, 0);
		result.addSeries(series1);

		JFreeChart chart = ChartFactory.createXYLineChart(title, xName, yName,
				result, PlotOrientation.VERTICAL, false, false, false);
		// we put the chart into a panel
		ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
	}
}

class MyCellRenderer implements TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
//		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
//				row, column);
		//setHorizontalAlignment(SwingConstants.RIGHT);
		JLabel lable = null;
		if(column==0){
			DecimalFormat decimalformat =new DecimalFormat("######0");
			lable = new JLabel(value == null ? decimalformat.format(0) : decimalformat.format(value));
		
		
		}else{
			DecimalFormat decimalformat =new DecimalFormat("######0.00");
			lable = new JLabel(value == null ? decimalformat.format(0) : decimalformat.format(value));
			
		}
		lable.setHorizontalAlignment(JLabel.CENTER);
		lable.setFont(lable.getFont().deriveFont(Font.PLAIN));
		
		if(table.getSelectedRow()==row){
			lable.setBackground(Color.lightGray);
		}
		return lable;
		

	}

//	public void setValue(Object value) {
//
//		DecimalFormat decimalformat =new DecimalFormat("######0.00");
//		super.setValue(value == null ? decimalformat.format(0) : decimalformat.format(value));
//	
//	}

}


class HeaderRendererHh extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		JTableHeader header = table.getTableHeader();
		setForeground(header.getForeground());
		setBackground(header.getBackground());
		setFont(header.getFont());
		setOpaque(true);
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));

		// 得到列的宽度
		TableColumnModel columnModel = table.getColumnModel();
		int width = columnModel.getColumn(column).getWidth();

		value = getShowValue(value.toString(), width);
		setText(value.toString());
		setSize(new Dimension(width, this.getHeight()));

		setHorizontalAlignment(SwingConstants.CENTER);

		return this;
	}

	private Object getShowValue(String value, int colWidth) {
		// 根据当前的字体和显示值得到需要显示的宽度
		FontMetrics fm = this.getFontMetrics(this.getFont());
		int width = fm.stringWidth(value.toString());
//		int test = fm.stringWidth("好");
//		System.out.println(test * value.length());
//		System.out.println(width);
		if (width < colWidth) {
			return value;
		}
		StringBuffer sb = new StringBuffer("<html><cneter>");
		char str;
		int tempW = 0;
		for (int i = 0; i < value.length(); i++) {
			str = value.charAt(i);
			tempW += fm.charWidth(str);
			if (tempW > colWidth) {
				sb.append("<br>");
				tempW = 0;
			}
			sb.append(str);
		}
		sb.append("</center></html>");
		return sb.toString();
	}
}
