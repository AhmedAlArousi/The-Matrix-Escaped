package code;

import java.util.ArrayList;

public abstract class SearchProblem {
// The 5-Tuple of a search problem
public ArrayList<Operator> operators;
public SearchNode initialState;
//state space
public abstract boolean goalTest(SearchNode node); 
public abstract int pathCost();


//checking if a state is not valid
protected abstract SearchNode generateNextNode(SearchNode currentNode, Operator op);

}
