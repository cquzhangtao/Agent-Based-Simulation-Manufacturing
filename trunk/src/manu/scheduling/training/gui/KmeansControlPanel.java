package manu.scheduling.training.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import manu.scheduling.training.BPInfo;
import manu.scheduling.training.ClusterInfo;
import manu.scheduling.training.Kmeans;

public class KmeansControlPanel<T> extends JPanel{
	private List<AlgorithmControlListener> kmeansControlListeners=new ArrayList<AlgorithmControlListener>();
	private boolean cancled=false;
	private boolean findParameters=false;
	private Kmeans<T> kmeans;

	public KmeansControlPanel(final Kmeans<T> kmeans){
		this.kmeans=kmeans;
		GridLayout layout = new GridLayout(1,7);
		this.setLayout(layout);
		this.setBorder(new EmptyBorder(5,0,5,0));
		layout.setHgap(15);
		this.add(new JLabel("Epoch",JLabel.CENTER));
		final JFormattedTextField epoch=new JFormattedTextField(String.valueOf(kmeans.getClusterInfo().getEpoch()));
		this.add(epoch);
		
		this.add(new JLabel("Class Number",JLabel.CENTER));
		final JFormattedTextField num=new JFormattedTextField(String.valueOf(kmeans.getClusterInfo().getClassNum()));
		this.add(num);
		
		this.add(new JLabel("Tolerance",JLabel.CENTER));
		final JFormattedTextField tol=new JFormattedTextField(String.valueOf(kmeans.getClusterInfo().getTolerance()));
		this.add(tol);
		
		this.add(new JLabel("Max Iterate",JLabel.CENTER));
		final JFormattedTextField interate=new JFormattedTextField(String.valueOf(kmeans.getClusterInfo().getMaxIterate()));
		this.add(interate);
		
		JButton start=new JButton("Start");
		this.add(start);
		//this.add(new JLabel("||"));
		
		this.add(new JLabel("Max Class",JLabel.CENTER));
		final JFormattedTextField maxnum=new JFormattedTextField(50);
		this.add(maxnum);
		
		JButton start1=new JButton("Start(Num)");
		this.add(start1);
		
		start.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				setFindParameters(false);
				
				for(AlgorithmControlListener l:kmeansControlListeners){
					l.onStart();;
				}
				Runnable run=new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						kmeans.getClusterInfo().setClassNum(Integer.valueOf(num.getText()));
						kmeans.getClusterInfo().setEpoch(Integer.valueOf(epoch.getText()));
						kmeans.getClusterInfo().setTolerance(Double.valueOf(tol.getText()));
						if(kmeans.getClusterInfo().getClassNum()>kmeans.getDataNum()/20){
							kmeans.getClusterInfo().setClassNum(kmeans.getDataNum()/20);
						}
						kmeans.comput();
						
						for(AlgorithmControlListener l:kmeansControlListeners){
							l.onEnd();
						}
					}
					
				};
				Thread th=new Thread(run);
				th.start();
				
				

			}});
		
		start1.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				setFindParameters(true);
				
				for(AlgorithmControlListener l:kmeansControlListeners){
					l.onStart();;
				}
				Runnable run=new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						//kmeans.getClusterInfo().setClassNum(Integer.valueOf(num.getText()));
						kmeans.getClusterInfo().setEpoch(1);
						kmeans.getClusterInfo().setTolerance(Double.valueOf(tol.getText()));
						List<Double> errors=new ArrayList<Double>();
						for(int i=1;i<Math.min(Integer.valueOf(maxnum.getText()),kmeans.getDataNum()/20);i++){
							if(cancled){
								cancled=false;
								break;
							}
							kmeans.getClusterInfo().setClassNum(i);
							kmeans.comput();
							//errors.add(kmeans.getMinimalGlobalError());
							errors.add(kmeans.getElbow());
						}
						kmeans.setClassNumDetermineData(errors);
						
						for(AlgorithmControlListener l:kmeansControlListeners){
							l.onParameterReady();
						}
					}
					
				};
				Thread th=new Thread(run);
				th.start();
				
				

			}});
		
		
		
		
	}
	
	public void addKmeansListener(AlgorithmControlListener lis){
		kmeansControlListeners.add(lis);
	}

	public boolean isCancled() {
		return cancled;
	}

	public void setCancled(boolean cancled) {
		if(findParameters)
			this.cancled=cancled;
		else
			kmeans.setCancled(cancled);
	}

	public boolean isFindParameters() {
		return findParameters;
	}

	public void setFindParameters(boolean findParameters) {
		this.findParameters = findParameters;
	}




	

}
