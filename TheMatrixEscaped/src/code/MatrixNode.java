package code;

public class MatrixNode extends SearchNode{
	public MatrixNode(SearchNode parent, String currentState, int pathCost, Operator action, int depth,int h1,int h2,int time) {
		super(parent,currentState,pathCost,action,depth);
		heuristic1=h1;
		heuristic2=h2;
		this.time=time;
	}
}
