package manu.simulation.gui.report;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JPanel;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.swing.JRViewer;

public class ReportPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JRViewer viewer;
	public ReportPanel(String xmlFile,JasperReport jasperReport,String queryString,String subDir) {
		this.setLayout(null);
		
		try {
			// First, load JasperDesign from XML and compile it into
			// JasperReport
			// JasperDesign jasperDesign =
			// JasperCompileManager.compileReport(sourceFileName).loadXmlDesign("path-to-your-jrxml-file\\sample.jrxml");
			//JasperDesign jasperDesign = JRXmlLoader
			//		.load(templateFile);
			//System.out.println(System.currentTimeMillis());
			//JasperReport jasperReport = JasperCompileManager
			//		.compileReport(jasperDesign);
			//System.out.println(System.currentTimeMillis());
			// Second, create a map of parameters to pass to the report.
			 Map<String, Object> parameters = new HashMap<String, Object>();
			 parameters.put("SUBREPORT_DIR", subDir);
			JRXmlDataSource datasource = new JRXmlDataSource(xmlFile,queryString);//"/Results/Products/Product");
			datasource.setLocale(Locale.ENGLISH);
			// Third, get a database connection
			// Connection conn = ;
			// Fourth, create JasperPrint using fillReport() method
			//System.out.println(System.currentTimeMillis());
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport, parameters, datasource);
			//System.out.println(System.currentTimeMillis());
			// You can use JasperPrint to create PDF
			// JasperExportManager.exportReportToPdfFile(jasperPrint,"desired-path\\SampleReport.pdf");

			// Or to view report in the JasperViewer
			//JasperViewer.viewReport(jasperPrint);
			viewer=new JRViewer(jasperPrint);
			
			
		
			this.add(viewer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void setBounds(int x,int y,int width,int height){
		super.setBounds(x, y, width, height);
		//if(viewer!=null){
		viewer.setBounds(0, 0, this.getWidth(), this.getHeight());
		viewer.setFitWidthZoomRatio();
		//}
	}

}
