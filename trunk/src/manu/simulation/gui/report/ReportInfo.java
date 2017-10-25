package manu.simulation.gui.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class ReportInfo {
	public String toolResultTemp = ".//report/toolResults.jrxml";
	public String productResultTemp = ".//report/ProductResults.jrxml";
	public String summaryResultTemp = ".//report/summaryResults.jrxml";
	public JasperReport productJasperReport;
	public JasperReport toolJasperReport;
	public String subReportPath=".//report//";
	public JasperReport summaryJasperReport;
	public String productResultFile;
	public String summaryResultFile;
	public String toolResultFile;
	public String periodStr;
	public ReportInfo(){
		JasperDesign jasperDesign = null;
		try {
			jasperDesign = JRXmlLoader.load(productResultTemp);
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(System.currentTimeMillis());
		try {
			productJasperReport = JasperCompileManager
					.compileReport(jasperDesign);
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			jasperDesign = JRXmlLoader.load(toolResultTemp);
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(System.currentTimeMillis());
		try {
			toolJasperReport = JasperCompileManager
					.compileReport(jasperDesign);
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			jasperDesign = JRXmlLoader.load(summaryResultTemp);
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(System.currentTimeMillis());
		try {
			summaryJasperReport = JasperCompileManager
					.compileReport(jasperDesign);
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
