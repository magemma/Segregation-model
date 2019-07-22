import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author gemma This class represents a population
 *         distributed in a certain location. It provides
 *         some methods that model its behaviour and some
 *         utilities for dealing with the Graph library,
 *         provided by Princeton University
 *
 */
public class Population {
	Graph G;
	// Obs: the function V(G) returns the number of nodes of
	// the graph G
	int N;// The total number of sites in the area
	int I;// The total number of individuals in the system
	double pn;// Probability of being neighbour sites
	double T;// Threshold for migration from one site to
				// another one in the
				// graph
	int M;// Maximum number of steps
	int q;// Maximum value of any cultural trait
	int F;// Amount of cultural traits
	String fileName;
	HashMap<Integer, Integer> indToNode;// This is "full",
										// since N>I
	HashMap<Integer, Integer> nodeToInd;// If a node is
										// empty -1 is the
										// value of
										// the key
	ArrayList<ArrayList<Integer>> sigma;// Works with
										// individuals
	// sigma[individualx][featurek] -> the trait of the
	// featurek-th feature of
	// individualx
	
	/**
	 * This method turns an Iterable<Integer> into an
	 * ArrayList<Integer>
	 * 
	 * @param itr the iterable
	 * 
	 * @return ret the ArrayList
	 */
	private static ArrayList<Integer>
			toArray(Iterable<Integer> itr) {
		ArrayList<Integer> ret = new ArrayList<>();
		for (Integer t : itr) {
			ret.add(t);
		}
		return ret;
	}

	/**
	 * This method returns the list of empty sites in the
	 * current population
	 * 
	 * @return res the list of integers
	 */
	private ArrayList<Integer> emptyNodes() {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < this.nodeToInd.size(); i++) {
			if (nodeToInd.get(i) == -1) {
				res.add(i);
			}
		}
		return res;
	}

	/**
	 * This method computes the similarity between two
	 * sites, aka the similarity between the inhabitants of
	 * such sites. It goes without saying that if at least
	 * one of such nodes is empty an "error code"
	 * 
	 * @param nodei
	 * @param nodej
	 * @return the value of similarity (between 0 and 1), it
	 *         returns -1 in case of empty site(s).
	 * 
	 *         Notice that we decided not to use an
	 *         exception because it was easier to manage in
	 *         the code
	 */
	private double computeSimilarity(int nodei, int nodej) {
		int individuali = (int) this.nodeToInd.get(nodei);
		int individualj = (int) this.nodeToInd.get(nodej);
		// If at least one of such nodes is not populated
		if (individuali == -1 || individualj == -1) {
			return -1;
		}
		double sym = 0;
		for (int k = 0; k < this.F; k++) {
			int sigmai = (int) this.sigma.get(individuali)
					.get(k);
			if (sigmai == (int) this.sigma.get(individualj)
					.get(k)) {
				sym += 1;
			}
		}
		sym = sym / this.F;
		return sym;
	}

	/**
	 * This method allows an individual to move towards an
	 * empty site in the network
	 * 
	 * @param r           for random choices
	 * @param individualx the moving individual
	 */
	private void moveToRandomEmptySite(Random r,
			int individualx) {
		int nodex = (int) this.indToNode.get(individualx);
		ArrayList<Integer> res = this.emptyNodes();
		int idx = r.nextInt(res.size());
		int newNode = res.get(idx);
		// Change individual-> node correspondence
		this.indToNode.put(individualx, newNode);
		// Set new node
		this.nodeToInd.put(newNode, individualx);
		// Unset old node
		this.nodeToInd.put(nodex, -1);
	}

	/**
	 * This function writes the weighted graph into a file,
	 * using the format <from, to, weight> The weight
	 * represents the similarity between two adjacent nodes.
	 * If at least one of such nodes is not populated the
	 * weight of the edge is -1. If a node is isolated, it
	 * is printed without any neighbour
	 * 
	 * @param outfile the path of the output file
	 * @param iter    the number of the iteration
	 * @throws IOException
	 */
	private void writeWeightedGraph(String outfile,
			int iter) throws IOException {
		BufferedWriter writer =
				new BufferedWriter(new FileWriter(
						outfile + iter + ".csv", true));
		for (int nodei = 0; nodei < this.N; nodei++) {
			Iterable<Integer> neighs = this.G.adj(nodei);
			// If there is at least a neighbour write line
			int numNeighs = 0;
			for (Integer nodej : neighs) {
				numNeighs++;
				String line = String.valueOf(nodei);
				line = line + " " + String.valueOf(nodej);
				line = line + " " + String.valueOf(this
						.computeSimilarity(nodei, nodej));
				writer.write(line + "\n");
			}
			// Else write only node number
			if (numNeighs == 0) {
				String line = String.valueOf(nodei);
				writer.write(line + "\n");
			}
		}
		writer.close();
	}

	/**
	 * This method implements the migrations as described in
	 * the algorithm
	 * 
	 * @param r -> for random generation
	 */
	private void startMigrations(Random r) {
		boolean convergence = false;
		int m = 0;
		while (m < this.M && !convergence) {
			// Make copy of the current state for testing
			// convergence
			HashMap<Integer, Integer> currIndToNode =
					new HashMap<Integer, Integer>();
			for (int i = 0; i < this.indToNode
					.size(); i++) {
				currIndToNode.put(i, this.indToNode.get(i));
			}
			ArrayList<ArrayList<Integer>> currSigma =
					new ArrayList<ArrayList<Integer>>();
			for (int i = 0; i < this.N; i++) {
				ArrayList<Integer> row = new ArrayList<>();
				for (int j = 0; j < this.F; j++) {
					row.add(this.sigma.get(i).get(j));
				}
				currSigma.add(row);
			}
			// Here the algorithm begins
			m++;
			for (int individuali =
					0; individuali < this.I; individuali++) {
				int nodei = (int) this.indToNode
						.get(individuali);
				Iterable<Integer> neighs =
						this.G.adj(nodei);
				ArrayList<Integer> arrNeighs =
						Population.toArray(neighs);
				if (arrNeighs.size() == 0) {
					// individual moves to random empty site
					moveToRandomEmptySite(r, individuali);
					continue;
				}
				ArrayList<Integer> populatedNeighs =
						new ArrayList<Integer>();
				for (int i = 0; i < arrNeighs.size(); i++) {
					if (this.nodeToInd.get(i) != -1) {
						populatedNeighs.add(i);
					}
				}
				int numPopulatedNeighs =
						populatedNeighs.size();
				if (numPopulatedNeighs == 0) {
					// individual moves to random empty site
					moveToRandomEmptySite(r, individuali);
					continue;
				}
				boolean nodeFound = false;
				int individualj = -1;
				int nodej = -1;
				double omegaij = -1;
				int iterNum = 0;
				while (!nodeFound
						&& iterNum < numPopulatedNeighs
								* numPopulatedNeighs) {
					int idx = r.nextInt(numPopulatedNeighs);
					iterNum++;
					nodej = populatedNeighs.get(idx);
					individualj =
							(int) this.nodeToInd.get(nodej);
					if (individualj != -1) {
						omegaij = computeSimilarity(nodei,
								nodej);
						// If the two nodes do not share the
						// same culture
						if (omegaij != 1) {
							nodeFound = true;
						}
					}
				}
				// If the i-th individual has all
				// same-culture neighs, visit
				// next individual
				if (!nodeFound) {
					continue;
				}
				int k = r.nextInt(this.F);
				;
				while (this.sigma.get(individuali).get(k)
						.equals(this.sigma.get(individualj)
								.get(k))) {
					// The cultural influence must occur
					// among the differing
					// ones
					k = r.nextInt(this.F);
				}
				double pi = Math.random();
				if (pi <= omegaij) {
					ArrayList<Integer> rowi =
							this.sigma.get(individuali);
					ArrayList<Integer> rowj =
							this.sigma.get(individualj);
					rowi.set(k, rowj.get(k));
					this.sigma.set(individuali, rowi);
				} else {
					double omegai = 0;
					for (individualj =
							0; individualj < arrNeighs
									.size(); individualj++) {
						omegai = computeSimilarity(nodei,
								nodej);
					}
					omegai = omegai / arrNeighs.size();
					if (omegai < this.T) {
						// Individual moves to random empty
						// site
						moveToRandomEmptySite(r,
								individuali);
					}
				}
			}
			// Check convergence
			if (this.indToNode.equals(currIndToNode)
					&& this.sigma.equals(currSigma)) {
				convergence = true;
				System.out.println(
						"~~~~~~~~~~Convergence with " + m
								+ " iterations~~~~~~~~~~~");
				try {
					writeWeightedGraph(fileName, m);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				// Write graph to file each 100 steps
				if (m % 100 == 0) {
					try {
						writeWeightedGraph(fileName, m);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		if (!convergence) {
			System.out.println(
					"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~NO Convergence~~~~~~~~~~~~~~~~~~");
		}

	}

	/**
	 * Constructor
	 * 
	 * @param r
	 * @param N
	 * @param I
	 * @param pn
	 * @param T
	 * @param M
	 * @param q
	 * @param F
	 * @param fileName
	 */
	public Population(Random r, int N, int I, double pn,
			double T, int M, int q, int F,
			String fileName) {
		this.N = N;
		this.I = I;
		this.pn = pn;
		this.T = T;
		this.M = M;
		this.q = q;
		this.F = F;
		this.fileName = fileName;
		// Generation of the graph
		G = GraphGenerator.simple(N, pn);
		// Generation of the dictionaries <individual,
		// nodeID> and <nodeID,
		// individual>
		this.indToNode = new HashMap<Integer, Integer>();
		this.nodeToInd = new HashMap<Integer, Integer>();
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < N; j++) {
				this.nodeToInd.put(j, -1);
			}
		}
		for (int i = 0; i < I; i++) {
			// For each individual a node in the network is
			// chosen at random
			int node = r.nextInt(this.N);
			while (this.nodeToInd.get(node) != -1) {
				node = r.nextInt(this.N);
			}
			// If the node is not inhabited
			this.indToNode.put(i, node);
			this.nodeToInd.put(node, i);
		}
		this.sigma = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < this.N; i++) {
			ArrayList<Integer> row = new ArrayList<>();
			for (int j = 0; j < this.F; j++) {
				// Set the j-th feature with a random trait
				int trait = r.nextInt(q);
				row.add(trait);
			}
			this.sigma.add(row);
		}
	}

	/**
	 * This is the engine of the experiments, which reads
	 * the parameters from standard input, creates the
	 * population and starts the migration process
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Set seed for random numbers
		Random random = new Random();
		// Read parameters
		int N = Integer.valueOf(args[0]);
		int I = Integer.valueOf(args[1]);
		double pn = Float.valueOf(args[2]);
		double T = Float.valueOf(args[3]);
		int M = Integer.valueOf(args[4]);
		int q = Integer.valueOf(args[5]);
		int F = Integer.valueOf(args[6]);
		String fileName = args[7];

		/* Test case 1 */
		/*
		 * int N = 10;//10000; int I = 5; double pn = 0.016;
		 * double T = 0.3; int M = 1000; int q = 5; int F =
		 * 15; String fileName = "bau";
		 * System.out.println(fileName);
		 */

		/* Test case 2 */
		/*
		 * int N = 100;//10000; int I = 50; double pn =
		 * 0.1;//0.016; double T = 0.1; int M = 1000; int q
		 * = 20; int F = 100; String fileName = "bau";
		 * System.out.println(fileName);
		 */
		Population myPopulation = new Population(random, N,
				I, pn, T, M, q, F, fileName);
		/* Debug prints */
		/*
		 * for (int node=0; node<N; node++) {
		 * System.out.println("Node: " + node + " Ind: " +
		 * myPopulation.nodeToInd.get(node)); }
		 * System.out.print(myPopulation.G.toString());
		 */
		myPopulation.startMigrations(random);
	}
}
