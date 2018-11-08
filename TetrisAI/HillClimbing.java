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
	int n = 1000;

	public void SoftStochasticSearch(double maxHeight, double heightRange, double holes, double roughness, double blockades){

		for (int i; i<n; i++){

			double[] weights = {maxHeight, heightRange, holes, roughness, blockades};
			JBrainNoGraphics game = new JBrainNoGraphics(20,10, maxHeight, heightRange, holes, roughness, blockades);
			game.startGame();
			int first = game.getCount(); //score of a game with weights from the array with changed weight
			game.stopGame();
			game.startGame();
			int second = game.getCount(); //score of a game with given weights
			game.stopGame();
			game.startGame();
			int third = game.getCount(); //score of a game with given weights

			double score_with_current_weights = (first + second + third) / 3.0;
			

			double increment = Math.random() - 0.5; //range of [-0.5, 0.5)
			int sample = (int)(Math.random() * weights.length);

			weights[sample] += increment; //searching random direction a bit


			JBrainNoGraphics game2 = new JBrainNoGraphics(20,10, weights[0], weights[1], weights[2], weights[3], weights[4]);
			game2.stopGame();
			game2.startGame();
			first = game2.getCount();//score of a game with weights from the array with changed weight
			game2.stopGame();
			game2.startGame();
			second = game2.getCount();//score of a game with given weights
			game2.stopGame();
			game2.startGame();
			third = game2.getCount();//score of a game with given weights

			double average = (first + second + third) / 3.0;// average of first, second, third

			if (average > (score_with_current_weights * exploration_constant_percent)){
				maxHeight = weights[0];
				heightRange = weights[1];
				holes = weights[2];
				roughness = weights[3];
				blockades = weights[4];
			}
		}
		double[] results = {maxHeight, heightRange, holes, roughness, blockades}; //final weights
		// print final weights (i.e. results) anf final score
		for (int i = 0; i < results.length; i++){
			System.out.println(results[i]);
		}
	}

	public static void main(String[] args) {
		HillClimbing hillClimber = new HillClimbing();
		hillClimber.SoftStochasticSearch(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]),
			Double.parseDouble(args[3]), Double.parseDouble(args[4]));

	}

}