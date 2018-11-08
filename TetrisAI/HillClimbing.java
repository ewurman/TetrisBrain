/*

Calls ErikWurmanSinaBakhtiariBrain.java with given weights as parameters. Iterates, at each iteration 
playing 3 games with the current parameters and averaging the results. Then it does a soft stochastic 
search, for n iterations. Outputs the best score/weights, which we log in a notebook, so we can use various
starting weights to expolore the vectore space of weights.


Calls JBrainNoGraphics with ErikWurmanSinaBakhtiariBrain (with inputted weights as parameters)
to play each game.

*/


import java.lang.Math;
//import JBrainNoGraphics.*;

public class HillClimbing {
	double exploration_constant_percent = 0.99; // a percentage of the current score instead below that we can explorwith
	int n = 100;
	int trials = 100;

	public void SoftStochasticSearch(double maxHeight, double holes, double roughness, double blockades){

		double score_for_current_weights = 0;
		for (int i = 0; i<n; i++){

			double[] weights = {maxHeight, holes, roughness, blockades};
			JBrainNoGraphics game = new JBrainNoGraphics(20,10, maxHeight, holes, roughness, blockades);
			double score_with_current_weights = 0;

			for (int j = 0; j < trials; j++){
				game.startGame();
				int score = game.getPieces(); //score of a game with weights from the array with changed weight
				game.stopGame();
				score_with_current_weights += ((double) score )/ trials;
			}


			double increment = Math.random() - 0.5; //range of [-0.5, 0.5)
			int sample = (int)(Math.random() * weights.length);

			weights[sample] = Math.max(0, weights[sample] + increment); //searching random direction a bit

			double average = 0.0;
			JBrainNoGraphics game2 = new JBrainNoGraphics(20,10, weights[0], weights[1], weights[2], weights[3]);
			for (int j = 0; j < trials; j++){
				game2.startGame();
				int score = game2.getPieces(); //score of a game with weights from the array with changed weight
				//System.out.println(Pieces: " + game2.getPieces());
				game2.stopGame();
				average += ((double) score )/ trials;
			}


			if (average > (score_with_current_weights * exploration_constant_percent)){
				maxHeight = weights[0];
				holes = weights[1];
				roughness = weights[2];
				blockades = weights[3];
				score_for_current_weights = average;
			}


			if (i != 0 && i%10 == 0){
				System.out.println("Finished Iteration " + i);
			}

		}
		double[] results = {maxHeight, holes, roughness, blockades}; //final weights
		// print final weights (i.e. results) anf final score
		for (int i = 0; i < results.length; i++){
			System.out.println(results[i]);
		}
		System.out.println("Score with these weights:" + score_for_current_weights);
	}

	public static void main(String[] args) {
		HillClimbing hillClimber = new HillClimbing();
		//hillClimber.SoftStochasticSearch(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
		//(Math.random() * weights.length);

	}

}