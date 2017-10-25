package manu.sbo.algorithm.antcolony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Zhangtao
 *
 */
public abstract class AntColonyAlgorithmAbstract {
	private int antNum;
	private int generationNum = 100;
	private Network network;
	private Map<Ant, Double> bestAnts;
	private int bestAntNum = 5;
	private double evaporationCoefficien = 0.1;
	private double enchanceCoefficien = 2;
	private boolean maximization = false;
	private boolean cancled = false;

	protected abstract double evaluateAnt(Ant ant, int generationIndex);

	/**
	 * @param network
	 * @param generationNum
	 * @param antNum
	 */
	public AntColonyAlgorithmAbstract(Network network, int generationNum, int antNum) {
		this.network = network;
		this.generationNum = generationNum;
		this.antNum = antNum;
		bestAnts = new HashMap<Ant, Double>();
	}

	public void run() {
		int genIndex = 0;
		while (genIndex++ < generationNum) {
			if (cancled) {
				cancled = false;
				break;
			}
			Map<Ant, Double> antsinGeneration = new HashMap<Ant, Double>();
			for (int i = 0; i < antNum; i++) {
				Random rnd = new Random();
				if (rnd.nextDouble() < 0.8||bestAnts.size()==0) {
					Ant ant = new Ant();
					ant.run(network);

					double value = evaluateAnt(ant, genIndex);
					if (value < 0) {
						continue;
					}
					ant.setValue(value);
					antsinGeneration.put(ant, value);
				} else {
					ArrayList<Ant> ants=new ArrayList<Ant>(bestAnts.keySet());
					Ant ant=ants.get(rnd.nextInt(ants.size()));
					antsinGeneration.put(ant, ant.getValue());

				}

			}
			Ant bestAnt = null;
			if (maximization) {
				bestAnt = getWorstAnt(antsinGeneration);
			} else {
				bestAnt = getBestAnt(antsinGeneration);
			}
			double bestValue = antsinGeneration.get(bestAnt);
			bestAnts.put(bestAnt, bestValue);

			if (bestAnts.size() > bestAntNum) {
				bestAnts.remove(getWorstAnt());
			}
			// for(Node node:bestAnt.getPath()){
			// System.out.print(node.getId()+", ");
			// }
			// System.out.println();
			System.out.println("Generation, " + genIndex + ", value, " + bestValue);

			enhance(bestAnt, bestValue);
			evaporate();
		}
	}

	public Ant getBestAnt(Map<Ant, Double> ants) {
		Ant bestAnt = ants.keySet().toArray(new Ant[0])[0];
		double min = Double.MAX_VALUE;
		for (Ant ant : ants.keySet()) {
			if (min > ants.get(ant)) {
				min = ants.get(ant);
				bestAnt = ant;
			}
		}
		return bestAnt;
	}

	public Ant getBestAnt() {
		return getBestAnt(bestAnts);
	}

	public Ant getWorstAnt() {
		return getWorstAnt(bestAnts);
	}

	private Ant getWorstAnt(Map<Ant, Double> ants) {
		Ant worstAnt = null;
		double max = Double.MIN_VALUE;
		for (Ant ant : ants.keySet()) {
			if (max < ants.get(ant)) {
				max = ants.get(ant);
				worstAnt = ant;
			}
		}
		return worstAnt;
	}

	protected void evaporate() {
		for (Side side : network.getSides().values()) {
			side.setWieght(side.getWieght() * (1 - evaporationCoefficien));
		}
	}

	protected void enhance(Ant ant, double value) {
		List<Node> path = ant.getPath();
		for (int i = 0; i < path.size() - 1; i++) {
			Side side = network.getSide(path.get(i), path.get(i + 1));
			double increase = 0;
			if (maximization) {
				increase = value;
			} else {
				increase = 1 / value;
			}
			side.setWieght(side.getWieght() + enchanceCoefficien * increase);
		}

	}

	public void setEvaporationCoefficien(double evaporationCoefficien) {
		this.evaporationCoefficien = evaporationCoefficien;
	}

	public double getEvaporationCoefficien() {
		return evaporationCoefficien;
	}

	public void setEnchanceCoefficien(double enchanceCoefficien) {
		this.enchanceCoefficien = enchanceCoefficien;
	}

	public double getEnchanceCoefficien() {
		return enchanceCoefficien;
	}

	public boolean isMaximization() {
		return maximization;
	}

	public void setMaximization(boolean maximization) {
		this.maximization = maximization;
	}

	public boolean isCancled() {
		return cancled;
	}

	public void setCancled(boolean cancled) {
		this.cancled = cancled;
	}

}
