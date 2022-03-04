package code;

//currentState represented as a String 
//x,y;			[0]
//NeosDamage;	[1]
//ag_xi,ag_yi;	[2]
//pill_xi,pill_yi;  [3]
//hostage_xi,hostage_yi,hostage_dmgi;   [4]
//carried_dmgi;    [5]
//deaths;    [6]
//kills;     [7]     Agents and mutants killed
//mutant_xi,mutant_yi;  [8]

//mutant is hostage of damage 100, ag is the agent,deaths number of dead hostages
//and kills is the number of killed agents/mutants
public abstract class SearchNode {
	//The 5 tuple
	SearchNode parent;
	String currentState;
	int pathCost;
	Operator action;
	int depth;
	
	int heuristic1;
	int heuristic2;
	int time;
	public SearchNode(SearchNode parent, String currentState, int pathCost, Operator action, int depth) {
		this.parent = parent;
		this.currentState = currentState;
		this.pathCost = pathCost;
		this.action = action;
		this.depth = depth;
	}
	
}
