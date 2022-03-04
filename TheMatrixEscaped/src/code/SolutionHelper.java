package code;

import java.util.ArrayList;
import java.util.Arrays;

public class SolutionHelper {
	//class used for extracting solution and displaying
	static ArrayList<SearchNode> nodePathToGoal;
	
	public static String solution(SearchNode endNode,int totalNumberOfExpandedNodes) {
		//returns the string representation of the answer
		StringBuilder ans=new StringBuilder();
		nodePathToGoal=new ArrayList<SearchNode>();
		ArrayList<Operator> pathToGoal=new ArrayList<Operator>();
		if(endNode==null) {
			return "No Solution";
		}
		backTrack(endNode,pathToGoal);
		
		//path is added reversed in arraylist path
		for(int i=pathToGoal.size()-1;i>=0;i--) {
			String op=pathToGoal.get(i).toString().toLowerCase();
			if(op.equals("takepill")) {
				op="takePill";
			}
			ans.append(op);
			if(i!=0)ans.append(",");
		}
		ans.append(";");
		String[]splitted=endNode.currentState.split(";");
		ans.append(Integer.parseInt(splitted[6]));
		ans.append(";");
		ans.append(Integer.parseInt(splitted[7]));
		ans.append(";");
		ans.append(totalNumberOfExpandedNodes);
	
		return ans.toString();
	}
	
	private static void backTrack(SearchNode endNode,ArrayList<Operator>pathToGoal) {
		//backtracks through the parents of the endNode to yield the sequence of actions led to the solution
		if(endNode.parent==null)return;
		pathToGoal.add(endNode.action);
		nodePathToGoal.add(endNode);
		backTrack(endNode.parent,pathToGoal);
	}
	
	static void visualizeNode(Matrix problem){
		//visualizing path to goal if exists

		int m=problem.m;
		int n=problem.n;
		int boothX=problem.boothX;
		int boothY=problem.boothY;
		Point[][]pad=problem.pad;
		
		
		String visualizedAnswer="";
		nodePathToGoal.add(problem.initialState);//includes the initial grid
		for(int node=nodePathToGoal.size()-1;node>=0;node--){
			SearchNode currState=nodePathToGoal.get(node);
			ArrayList<Integer>[]currStateDecoded=decodeCurrentStateVisualize(currState);
			String [][] grid2D = new String[m][n];//switch
			
			for(String[] x: grid2D ) Arrays.fill(x, "Empty");
			
			//grid2D[currStateDecoded[0].get(0)][currStateDecoded[0].get(1)]= "Neo";
			grid2D[boothX][boothY] = "TB";
			int counter = 0;
			for(int i = 0 ; i < m ; i ++) {//switch
				for(int j = 0 ; j < n; j ++) {
					if(grid2D[i][j].equals("Empty") && pad[i][j] != null) {
						grid2D[i][j] = "PAD" + counter;
						grid2D[pad[i][j].x][pad[i][j].y] = "PAD" + counter;
						counter++;
					}
				}
			}
			for(int i = 0 ; i < currStateDecoded[4].size(); i+=3) {
				grid2D[currStateDecoded[4].get(i)][currStateDecoded[4].get(i+1)] = "H(" + currStateDecoded[4].get(i+2) + ")";
			}
			
			for(int i = 0 ; i < currStateDecoded[2].size(); i+=2) {
				grid2D[currStateDecoded[2].get(i)][currStateDecoded[2].get(i+1)] = "Agent";
			}
			for(int i = 0 ; i < currStateDecoded[3].size(); i+=2) {
				grid2D[currStateDecoded[3].get(i)][currStateDecoded[3].get(i+1)] = "Pill";
			}
			for(int i = 0 ; i < currStateDecoded[8].size(); i+=2) {
				grid2D[currStateDecoded[8].get(i)][currStateDecoded[8].get(i+1)] = "Mutant";
			}
			if(grid2D[currStateDecoded[0].get(0)][currStateDecoded[0].get(1)].equals("Empty"))
				grid2D[currStateDecoded[0].get(0)][currStateDecoded[0].get(1)]= "Neo";
			else
				grid2D[currStateDecoded[0].get(0)][currStateDecoded[0].get(1)]+= " | Neo";
			if(node==nodePathToGoal.size()-1) {
				visualizedAnswer+="Initial Grid: \n";
			}
			else{
				visualizedAnswer+=((nodePathToGoal.size()-node-1)+"\n"+nodePathToGoal.get(node).action+"\n");
			}
			for(String [] x : grid2D) visualizedAnswer+=(Arrays.toString(x))+'\n';
			visualizedAnswer+="Carried: "+currStateDecoded[5].toString()+"\n";
			visualizedAnswer+="Neo\'s Damage: "+currStateDecoded[1].get(0)+"\n\n";
		}
		System.out.println(visualizedAnswer);
	}
	
	public static void printGrid(ArrayList<Integer>[]currentStateDecoded,Matrix problem) {
		//printing the initial grid alone
		int m=problem.m;
		int n=problem.n;
		int boothX=problem.boothX;
		int boothY=problem.boothY;
		Point[][]pad=problem.pad;
		
		String [][] grid2D = new String[m][n];//switch
		
		for(String[] x: grid2D ) Arrays.fill(x, "Empty");
		
		grid2D[currentStateDecoded[0].get(0)][currentStateDecoded[0].get(1)]= "Neo";
		grid2D[boothX][boothY] = "TB";
		int counter = 0;
		for(int i = 0 ; i < m ; i ++) {//switch
			for(int j = 0 ; j < n; j ++) {
				if(grid2D[i][j].equals("Empty") && pad[i][j] != null) {
					grid2D[i][j] = "PAD" + counter;
					grid2D[pad[i][j].x][pad[i][j].y] = "PAD" + counter;
					counter++;
				}
			}
		}
		for(int i = 0 ; i < currentStateDecoded[4].size(); i+=3) {
			grid2D[currentStateDecoded[4].get(i)][currentStateDecoded[4].get(i+1)] = "H(" + currentStateDecoded[4].get(i+2) + ")";
		}
		
		for(int i = 0 ; i < currentStateDecoded[2].size(); i+=2) {
			grid2D[currentStateDecoded[2].get(i)][currentStateDecoded[2].get(i+1)] = "Agent";
		}
		for(int i = 0 ; i < currentStateDecoded[3].size(); i+=2) {
			grid2D[currentStateDecoded[3].get(i)][currentStateDecoded[3].get(i+1)] = "Pill";
		}
		
		for(String [] x : grid2D) System.out.println(Arrays.toString(x));
		
		System.out.println('\n');
		
	}
	
	//helper function for visualization
	@SuppressWarnings("unchecked")
	private static ArrayList<Integer>[] decodeCurrentStateVisualize(SearchNode currentNode) {
		ArrayList<Integer>[]currStateDecoded = new ArrayList[9];
		String[] outerArray = currentNode.currentState.split(";");
		for(int i = 0 ; i < outerArray.length; i++) {
			String[] innerArray = outerArray[i].split(","); 
			currStateDecoded[i] = new ArrayList<Integer>();
			for(int j = 0; j < innerArray.length; j++) {
				String temp = innerArray[j];
				if(!temp.equals("#")) {
					currStateDecoded[i].add(Integer.parseInt(temp));
				}
			}
		}
		return currStateDecoded;
	}

}
