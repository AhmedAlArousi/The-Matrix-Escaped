package code;

import java.util.ArrayList;
import java.util.TreeSet;

public class OperatorValidation {
	//This class uses validate method to validate if a given action is legal to be done one the current explored node
	public static boolean validate(SearchNode currentNode,ArrayList<Integer>[] currentStateDecoded, Operator operator, Matrix problem) {
		//extracting the constant parameters of the matrix problem
		int m=problem.m;
		int n=problem.n;
		int c=problem.c;
		
		int boothX=problem.boothX;
		int boothY=problem.boothY;
		
		Point[][]pad=problem.pad;
		
		Operator action =currentNode.action;
		int x=currentStateDecoded[0].get(0);
		int y=currentStateDecoded[0].get(1);
		TreeSet<Point>neighbouring=neighbouringAgents(currentStateDecoded,x, y);

		switch(operator) {
		case DOWN:return isValidMove(currentStateDecoded,x+1,y,m,n,neighbouring)&&action!=Operator.UP;
		case UP:return isValidMove(currentStateDecoded,x-1,y,m,n,neighbouring)&&action!=Operator.DOWN;
		case RIGHT:return isValidMove(currentStateDecoded,x,y+1,m,n,neighbouring)&&action!=Operator.LEFT;
		case LEFT:return isValidMove(currentStateDecoded,x,y-1,m,n,neighbouring)&&action!=Operator.RIGHT;
		case FLY:return isValidFlying(pad,x,y)&&action!=Operator.FLY;
		case TAKEPILL:return isValidTakingPill(currentStateDecoded,x,y);
		case CARRY:return isValidCarrying(currentStateDecoded, x, y, c);
		case DROP:return isValidDropping(currentStateDecoded, x, y, boothX, boothY);
		case KILL:return isValidKilling(currentStateDecoded,x,y,neighbouring);

		}
		return false;
	}
	private static boolean isValidFlying(Point[][] pad, int x, int y) {
		//There exists a pad in that position
		return pad[x][y]!=null;
	}
	private static boolean isValidKilling(ArrayList<Integer>[] currentStateDecoded, int x, int y,
			TreeSet<Point> neighbouring) {
		//there exists at least one neighbor which is a mutant or an agent, current cell does not contain a hostage that will die in next step and neo won't die if he performs the killing action
		return neighbouring.size()>0&&!hostageDying(currentStateDecoded, x, y)&&currentStateDecoded[1].get(0)<80;
	}
	private static boolean isValidMove(ArrayList<Integer>[] currentStateDecoded, int x, int y, int m, int n, TreeSet<Point> neighbouring) {
		//position is valid, there is no hostage will turn into mutant existing in that position and no agents or mutants in that cell
		return isValidPosition(x, y, m, n)&&!hostageDying(currentStateDecoded, x, y)&&!neighbouring.contains(new Point(x,y));
	}
	private static boolean isValidPosition(int neighbouringX, int neighbouringY,int m,int n) {
		//position is within the grid
		return neighbouringX>=0&&neighbouringX<m&&neighbouringY>=0&&neighbouringY<n;//switch m,n
	}
	private static boolean isValidTakingPill(ArrayList<Integer>[]currentStateDecoded,int x, int y) {
		//there exists a pill in that position
		ArrayList<Integer>pillsPositions=currentStateDecoded[3];
		for(int i=0;i<pillsPositions.size();i+=2) {
			int pillX=pillsPositions.get(i);
			int pillY=pillsPositions.get(i+1);
			if(x==pillX&&y==pillY)return true;
		}
		return false;
	}
	private static boolean isValidCarrying(ArrayList<Integer>[]currentStateDecoded,int x, int y,int c) {
		//there exists a hostage in that position
		int curHeld=currentStateDecoded[5].size();
		if(curHeld==c)return false;
		ArrayList<Integer>hostagesPositions=currentStateDecoded[4];
		for(int i=0;i<hostagesPositions.size();i+=3) {
			int hostageX=hostagesPositions.get(i);
			int hostageY=hostagesPositions.get(i+1);
			if(x==hostageX&&y==hostageY)return true;
		}
		return false;
	}
	private static boolean isValidDropping(ArrayList<Integer>[]currentStateDecoded,int x, int y,int boothX,int boothY) {
		//there exists a telephone booth in that position
		return x==boothX&&y==boothY&&currentStateDecoded[5].size()>0;
	}
	
	private static TreeSet<Point> neighbouringAgents(ArrayList<Integer>[]currentStateDecoded,int agentX, int agentY) {
		//return a treeset containing positions of agents or mutants in 4 neighboring cells
		TreeSet<Point>agents=new TreeSet<Point>();
		ArrayList<Integer>mutantsPositions=currentStateDecoded[8];
		for(int i=0;i<mutantsPositions.size();i+=2) {
			int mutantX=mutantsPositions.get(i);
			int mutantY=mutantsPositions.get(i+1);
			if(agentX+1==mutantX&&agentY==mutantY
					||agentX-1==mutantX&&agentY==mutantY
					||agentX==mutantX&&agentY+1==mutantY
					||agentX==mutantX&&agentY-1==mutantY) {
						agents.add(new Point(mutantX,mutantY));
					}
		}
		
		ArrayList<Integer>agentsPositions=currentStateDecoded[2];
		for(int i=0;i<agentsPositions.size();i+=2) {
			int agX=agentsPositions.get(i);
			int agY=agentsPositions.get(i+1);
			if(agentX+1==agX&&agentY==agY
					||agentX-1==agX&&agentY==agY
					||agentX==agX&&agentY+1==agY
					||agentX==agX&&agentY-1==agY) {
				agents.add(new Point(agX,agY));
			}
		}
		return agents;
	}
	private static boolean hostageDying(ArrayList<Integer>[]currentStateDecoded,int neighbouringX, int neighbouringY) {
		//checks if is there a hostage in this position of a health 98 or 99 (dies in next step)
		ArrayList<Integer>hostages=currentStateDecoded[4];
		for(int i=0;i<hostages.size();i+=3) {
			if(hostages.get(i)==neighbouringX&&hostages.get(i+1)==neighbouringY&&hostages.get(i+2)>=98)
				return true;
		}
		return false;
	}

}
