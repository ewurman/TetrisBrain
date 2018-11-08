/*

Calls ErikWurmanSinaBakhtiariBrain.java with given weights as parameters. Iterates, at each iteration 
playing 3 games with the current parameters and averaging the results. Then it does a soft stochastic 
search, for n iterations. Outputs the best score/weights, which we log in a notebook, so we can use various
starting weights to expolore the vectore space of weights.


Calls JBrainNoGraphics with ErikWurmanSinaBakhtiariBrain (with inputted weights as parameters)
to play each game.

*/

import java.lang.Math;

public class HillClimbing {
	int exploration_constant = 3; //Maybe do a percentage of the currect score instead?
	int n = 1000;

	public void SoftStochasticSearch(int maxHeight, int heightRange, int holes, int roughness, int blockades){

		int[] weights = [maxHeight, heightRange, holes, roughness, blockades];
		for (int i; i<n; i++){

			int current_score = 0;//TODO score of game with current
			

			double increment = Math.random() - 0.5; //range of [-0.5, 0.5)
			int sample = (int)(Math.random() * weights.length);

			weights[sample] += increment; //searching random direction a bit


			double first; = //score of a game with weights from the array with changed weight
			double second; = //score of a game with given weights
			double third; = //score of a game with given weights

			double average = (first + second + third) / 3;// average of first, second, third

			if (average > (current_score - exploration_constant)){
				maxHeight = weights[0];

			}
		}
		results = ; //final weights
		// print final weights (i.e. results) anf final score
	}
}