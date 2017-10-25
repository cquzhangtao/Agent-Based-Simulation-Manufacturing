package manu.scheduling.training.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import manu.sbo.algorithm.antcolony.AntColonyAlgorithm;
import manu.sbo.algorithm.antcolony.DecisionVaraibles;
import manu.sbo.algorithm.antcolony.EvaluationInterface;
import manu.sbo.algorithm.antcolony.Problem;
import manu.scheduling.training.BPInfo;
import manu.scheduling.training.BPNetwork;
import manu.scheduling.training.ClusterInfo;
import manu.scheduling.training.Kmeans;

public class BPControlPanel<T> extends JPanel {

	private List<AlgorithmControlListener> kmeansControlListeners = new ArrayList<AlgorithmControlListener>();
	private AntColonyAlgorithm alg;
	private boolean findParameters = false;
	private BPNetwork<T> network;

	public BPControlPanel(final BPNetwork<T> network) {
		this.network = network;

		GridLayout layout = new GridLayout(2, 11);
		this.setLayout(layout);
		this.setBorder(new EmptyBorder(5, 0, 5, 0));
		layout.setHgap(20);
		layout.setVgap(10);

		BPInfo info = network.getBpInfo();

		this.add(new JLabel("Hidden Num"));
		final JFormattedTextField num = new JFormattedTextField(String.valueOf(info.getHiddenNodeNum()));
		this.add(num);

		this.add(new JLabel("Epoch"));
		final JFormattedTextField epoch = new JFormattedTextField(String.valueOf(info.getEpoch()));
		this.add(epoch);

		this.add(new JLabel("Learning Rate"));
		final JFormattedTextField rate = new JFormattedTextField(String.valueOf(info.getEta()));
		this.add(rate);

		this.add(new JLabel("Momentum"));
		final JFormattedTextField momentum = new JFormattedTextField(String.valueOf(info.getMomentum()));
		this.add(momentum);

		this.add(new JLabel("Tolerance(%)"));
		final JFormattedTextField tol = new JFormattedTextField(String.valueOf(info.getTolerance()));
		this.add(tol);

		JButton start = new JButton("Start");
		this.add(start);

		this.add(new JLabel("MaxFailNum"));
		final JFormattedTextField maxFailNum = new JFormattedTextField(String.valueOf(info.getMaxFailNum()));
		this.add(maxFailNum);

		this.add(new JLabel("MinGradient"));
		final JFormattedTextField minGradient = new JFormattedTextField(String.valueOf(info.getMinGradient()));
		this.add(minGradient);

		this.add(new JLabel(""));
		this.add(new JLabel("||"));

		this.add(new JLabel("Generation"));
		final JFormattedTextField generation = new JFormattedTextField(20);
		this.add(generation);

		this.add(new JLabel("Ant Number"));
		final JFormattedTextField popSize = new JFormattedTextField(10);
		this.add(popSize);

		JButton start1 = new JButton("Start(Ant)");
		this.add(start1);

		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				findParameters = false;
				for (AlgorithmControlListener l : kmeansControlListeners) {
					l.onStart();
					;
				}
				Runnable run = new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						BPInfo bpInfo = network.getBpInfo();
						bpInfo.setEpoch(Integer.valueOf(epoch.getText()));
						bpInfo.setEta(Double.valueOf(rate.getText()));
						bpInfo.setMomentum(Double.valueOf(momentum.getText()));
						bpInfo.setMaxFailNum(Integer.valueOf(maxFailNum.getText()));
						bpInfo.setTolerance(Double.valueOf(tol.getText()));
						bpInfo.setMinGradient(Double.valueOf(minGradient.getText()));
						bpInfo.setHiddenNodeNum(Integer.valueOf(num.getText()));
						network.train();
						for (AlgorithmControlListener l : kmeansControlListeners) {
							l.onEnd();
						}
					}

				};
				Thread th = new Thread(run);
				th.start();

			}
		});

		start1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				findParameters = true;
				for (AlgorithmControlListener l : kmeansControlListeners) {
					l.onStart();
					;
				}
				Runnable run = new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						alg = new AntColonyAlgorithm(getProblem(network, Integer.valueOf(generation.getText()), Integer.valueOf(popSize.getText())));
						alg.setEnchanceCoefficien(1);
						alg.run();
						// System.out.println(alg.getBestSolution().toString());

						BPInfo bpInfo = network.getBpInfo();
						bpInfo.setEpoch(Integer.valueOf(epoch.getText()));
						// bpInfo.setEpoch(Integer.valueOf(num.getText()));
						bpInfo.setHiddenNodeNum((int) alg.getBestSolution().getDecisionVaraibles().get(2).getValue());
						bpInfo.setEta(alg.getBestSolution().getDecisionVaraibles().get(0).getValue());
						bpInfo.setMomentum(alg.getBestSolution().getDecisionVaraibles().get(1).getValue());

						num.setText(String.valueOf(bpInfo.getHiddenNodeNum()));
						rate.setText(String.format("%.2f", bpInfo.getEta()));
						momentum.setText(String.format("%.2f", bpInfo.getMomentum()));

						network.train();

						for (AlgorithmControlListener l : kmeansControlListeners) {
							l.onEnd();
						}

					}

				};
				Thread th = new Thread(run);
				th.start();

			}
		});

	}

	public Problem getProblem(final BPNetwork<T> network, int generation, int antNum) {
		Problem problem = new Problem();

		problem.addDecisionVaraible(10, 0.1);
		problem.addDecisionVaraible(1, 0);
		problem.addDecisionVaraible(50, 3);
		problem.setGenerationNum(generation);
		problem.setAntNum(antNum);
		problem.setDecimal(2);
		problem.setMaximization(true);
		problem.setEvaluation(new EvaluationInterface() {

			@Override
			public double evaluate(DecisionVaraibles decisionVaraibles, int gei) {

				double rate = decisionVaraibles.get(0).getValue();
				double momentum = decisionVaraibles.get(1).getValue();
				double hiddenNum = decisionVaraibles.get(2).getValue();

				BPInfo bpInfo = network.getBpInfo();
				bpInfo.setEpoch(100);
				bpInfo.setEta(rate);
				bpInfo.setMomentum(momentum);
				bpInfo.setHiddenNodeNum((int) hiddenNum);

				double accuracy = 0;
				int runs = 5;
				for (int i = 0; i < 5; i++) {
					network.train();
					accuracy += network.getAccuracy();
				}

				return accuracy / runs;
			}
		});
		return problem;
	}

	public void addBPListener(AlgorithmControlListener lis) {
		kmeansControlListeners.add(lis);
	}

	public void setCancled(boolean cancled) {
		if (findParameters)
			alg.setCancled(cancled);
		else
			network.setCancled(cancled);
	}

}
