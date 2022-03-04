package code;

import java.util.ArrayList;

//structure of currentStateDecoded (array of arraylists)
//x,y;			[0]
//NeosDamage;	[1]
//ag_xi,ag_yi;	[2]
//pill_xi,pill_yi;  [3]
//hostage_xi,hostage_yi,hostage_dmgi;   [4]
//carried_dmgi;    [5]
//deaths;    [6]
//kills;     [7]     
//mutant_xi,mutant_yi;  [8]
public class Matrix extends SearchProblem{
    ArrayList<Integer>[]currentStateDecoded;
    ArrayList<Integer>[]copiedCurrentStateDecoded;
    int m,n,c,boothX,boothY;
	Point[][]pad;
	String strategy;
	int timeCounter;
	public static String solve(String grid,String strategy, boolean visualize) {
		Matrix problem =new Matrix();
		String initialStateString=StateEncoding.generateInitialState(problem,grid);
		problem.addOperators();
		problem.initialState=new MatrixNode(null, initialStateString, 0, null, 0,0,0,0);
		problem.currentStateDecoded=StateEncoding.decodeCurrentState(problem.initialState);
		problem.strategy=strategy;
		//printing grid alone without path is the next commented line
		//SolutionHelper.printGrid(problem.currentStateDecoded,problem);
		GenericSearch gs=new GenericSearch();
		String answer = gs.genericSearch(problem, strategy);
		if(visualize) {
			SolutionHelper.visualizeNode(problem);
		}
		return answer;	
	}
	private void addOperators() {
		operators=new ArrayList<Operator>();
		operators.add(Operator.DOWN);
		operators.add(Operator.UP);
		operators.add(Operator.RIGHT);
		operators.add(Operator.LEFT);
		operators.add(Operator.KILL);
		operators.add(Operator.FLY);
		operators.add(Operator.TAKEPILL);
		operators.add(Operator.CARRY);
		operators.add(Operator.DROP);
		
	}
	public static String genGrid() {
		return GenGrid.genGrid();
	}
	@Override
	public boolean goalTest(SearchNode currentNode) {
		//The goal test is achieved when no living hostages are on the grid, neo is not carrying any hostages, all the turned hostages are dead and neo is back at the booth
		
		//decoding the state to use in the rest of the code
		currentStateDecoded=StateEncoding.decodeCurrentState(currentNode);
		
		int carriedNum = currentStateDecoded[5].size();
		int hostagesNum = currentStateDecoded[4].size();
		int mutantNum = currentStateDecoded[8].size();
		int neoX=currentStateDecoded[0].get(0);
		int neoY=currentStateDecoded[0].get(1);
		return carriedNum == 0 && hostagesNum == 0 && mutantNum == 0 &&neoX==boothX&&neoY==boothY;
	}
	@Override
	public int pathCost() {
		//number of deaths and kills for the newly created node
		int deathCost = copiedCurrentStateDecoded[6].get(0)*(int)1e7;
		int killsCost = copiedCurrentStateDecoded[7].get(0)*(int)1e5;
		// depth is added but in generateNextNode
		return deathCost+killsCost;
	}
	@Override
	protected SearchNode generateNextNode(SearchNode currentNode, Operator operator) {
		//A validation check is done a first
		//currentStateDecoded=StateEncoding.decodeCurrentState(currentNode);
		if(!OperatorValidation.validate(currentNode,currentStateDecoded,operator,this)) {
			return null;
		}
		//each time we generate a copy of the state to alter it and not affecting the original one
		copiedCurrentStateDecoded=copy(currentStateDecoded);
		int neoX=copiedCurrentStateDecoded[0].get(0);
		int neoY=copiedCurrentStateDecoded[0].get(1);
		int currentDamage=copiedCurrentStateDecoded[1].get(0);
		boolean takePill=false;
		if(operator==Operator.KILL) {			
			//increase kills and increases neo's damage
			currentDamage=Math.min(currentDamage+20, 100);
			int killed=UpdateState.removeKilledFromMutantsOrAgents(copiedCurrentStateDecoded,neoX,neoY);
			int currentKills=copiedCurrentStateDecoded[7].get(0);
			currentKills+=killed;
			copiedCurrentStateDecoded[7].set(0,currentKills);
		}else if(operator==Operator.DROP) {
			//removes carried hostages
			copiedCurrentStateDecoded[5].clear();
		}else if(operator==Operator.CARRY) {
			//it removes the hostage from the grid to neo's carried hostages
			UpdateState.moveHostageFromGridToCarried(copiedCurrentStateDecoded,neoX,neoY);
		}else if(operator==Operator.FLY) {
			//updating neo's position to the position of the corresponding pad
			Point correspondingPad=pad[neoX][neoY];
			neoX=correspondingPad.x;
			neoY=correspondingPad.y;
		}else if(operator==Operator.TAKEPILL) {
			//takes the pill and updates neo's damage then the rest of the hostages
			currentDamage=Math.max(currentDamage-20, 0);
			UpdateState.removePill(copiedCurrentStateDecoded,neoX,neoY);
			takePill = true;
		}else {
			//in this case it's a movement step, so neo's position is updated
			neoX+=operator==Operator.DOWN?1:operator==Operator.UP?-1:0;
			neoY+=operator==Operator.RIGHT?1:operator==Operator.LEFT?-1:0;
		}
		
		//altering the state to form the new created state
		copiedCurrentStateDecoded[0].set(0,neoX);
		copiedCurrentStateDecoded[0].set(1,neoY);
		copiedCurrentStateDecoded[1].set(0, currentDamage);
		
		UpdateState.updateDamagesOfHostages(copiedCurrentStateDecoded,takePill);
		

		int pathCost = pathCost() +currentNode.depth+1;//added depth to the cost
		String newState=StateEncoding.encodeState(copiedCurrentStateDecoded);
		
		//adding heuristics 
		int heuristic1=0,heuristic2=0;
		if(strategy.equals("AS1")||strategy.equals("GR1")) {
			heuristic1=calculateH1(copiedCurrentStateDecoded);
		}else if(strategy.equals("AS2")||strategy.equals("GR2")) {
			heuristic2=calculateH2(copiedCurrentStateDecoded);
		}
		timeCounter++;
		SearchNode generatedNode=new MatrixNode(currentNode, newState,pathCost, operator, currentNode.depth+1,heuristic1,heuristic2,timeCounter);
		
		return generatedNode;
	}
	private int calculateH1(ArrayList<Integer>[] copiedCurrentStateDecoded) {//deaths only
		int[]deaths=Dijkstra.dijkstraHeuristic(this, copiedCurrentStateDecoded);
		int totalDeaths=deaths[0]+deaths[1];
		return totalDeaths*(int)1e7;
	}
	private int calculateH2(ArrayList<Integer>[] copiedCurrentStateDecoded) {//deaths and kills
		int[]deaths=Dijkstra.dijkstraHeuristic(this, copiedCurrentStateDecoded);
		int mutants=copiedCurrentStateDecoded[8].size();
		int totalDeaths=deaths[0]+deaths[1];
		int kills=mutants+deaths[1];
		//deaths[1] is estimated deaths on grid, and those must be killed
		return totalDeaths*(int)1e7 + kills*(int)1e5;
	}

	private static ArrayList<Integer>[] copy(ArrayList<Integer>[] currentStateDecoded2) {
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] copy=new ArrayList[currentStateDecoded2.length];
		for(int i=0;i<copy.length;i++) {
			copy[i]=new ArrayList<Integer>();
			copy[i].addAll(currentStateDecoded2[i]);
		}
		return copy;
	}
}

