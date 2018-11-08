/*

Calls ErikWurmanSinaBakhtiariBrain.java with given weights as parameters. Iterates, at each iteration 
playing 3 games with the current parameters and averaging the results. Then it does a soft stochastic 
search, for n iterations. Outputs the best score/weights, which we log in a notebook, so we can use various
starting weights to expolore the vectore space of weights.


Calls JBrainNoGraphics with ErikWurmanSinaBakhtiariBrain (with inputted weights as parameters)
to play each game.

*/


import java.lang.Math;
import java.util.Random;
//import JBrainNoGraphics.*;

public class HillClimbing {
	double exploration_constant_percent = .975; // a percentage of the current score instead below that we can explorwith
	double min = -1.5;
	double max = 1.5;
	int n = 150;
	int trials = 20;

	public double[] SoftStochasticSearch(double maxHeight, double touchingWall, double holes, double roughness, double aggregateHeight){

		double score_for_current_weights = 0;

		for (int i = 0; i<n; i++){
			double[] weights = {maxHeight, touchingWall, holes, roughness, aggregateHeight};

			if (i==0) {
				JBrainNoGraphics game = new JBrainNoGraphics(20,10, weights[0], weights[1], weights[2], weights[3], weights[4]);

				for (int j = 0; j < trials; j++){
					game.startGame();
					int score = game.getPieces(); //score of a game with weights from the array with changed weight
					game.stopGame();
					score_for_current_weights += ((double) score / trials);
				}

			}

			System.out.println("Score: " + score_for_current_weights);

			Random rand = new Random();
			double increment = min + (max-min) * rand.nextDouble();
			int sample = rand.nextInt(weights.length);
			weights[sample] += increment; //searching random direction a bit

			double average = 0.0;
			JBrainNoGraphics game2 = new JBrainNoGraphics(20,10, weights[0], weights[1], weights[2], weights[3], weights[4]);

			for (int j = 0; j < trials; j++){
				game2.startGame();
				int score = game2.getPieces(); //score of a game with weights from the array with changed weight
				game2.stopGame();
				average += ((double) score / trials);
			}

			System.out.println("Average: " + average);


			if (average > (score_for_current_weights * exploration_constant_percent)){
				maxHeight = weights[0];
				touchingWall = weights[1];
				holes = weights[2];
				roughness = weights[3];
				aggregateHeight = weights[4];
				score_for_current_weights = average;
				for (int j = 0; j < weights.length; j++){
					System.out.println(weights[j]);
				}
			}


			if (i != 0 || i%10 == 0){
				System.out.println("Finished Iteration " + i);
				System.out.println();
				System.out.println();
			}
		}

		double[] results = {maxHeight, touchingWall, holes, roughness, aggregateHeight}; //final weights
		// print final weights (i.e. results) anf final score
		for (int i = 0; i < results.length; i++){
			System.out.println(results[i]);
		}
		System.out.println("Score with these weights:" + score_for_current_weights);
		double[] allResults = {results[0], results[1], results[2], results[3], results[4], score_for_current_weights};
		return allResults;
	}

	public static void main(String[] args) {
		HillClimbing hillClimber = new HillClimbing();


		if (args.length != 0){
			System.out.println("Calling single hill climbing");
			hillClimber.SoftStochasticSearch(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
		}
		else {
			int numRandomRestarts = 25;
			//hillClimber.SoftStochasticSearch(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
			double[] best_weights = {0,0,0,0,0};
			double bestVal = 0;
			double[] rand_weights = {0,0,0,0,0};
			for (int j = 0; j < numRandomRestarts; j++){
				for (int i = 0; i < rand_weights.length; i++){
					double weight = Math.random() * 50; //give a random weight 0 to 50
					rand_weights[i] = weight;
				}
				double[] weightsFoundAndValue = hillClimber.SoftStochasticSearch(rand_weights[0], rand_weights[1], rand_weights[2], rand_weights[3], rand_weights[4]);
				if (weightsFoundAndValue[best_weights.length] > bestVal){
					for (int i = 0; i < best_weights.length; i++){
						best_weights[i] = weightsFoundAndValue[i];
					}
					bestVal = weightsFoundAndValue[best_weights.length];
				}
				System.out.println("Finished Restart " + j);
			}
			System.out.println("Best Weights found:");
			for (int j = 0; j < best_weights.length; j++){
				System.out.println(best_weights[j]);
			}
		}
	}

}