package code;

import java.util.HashSet;

public class GenericSearch {
public static int maximumDepthSoFar,totalExpandedNodes;
public String genericSearch(SearchProblem problem,String strategy) {
	SearchNode endNode=null;
	maximumDepthSoFar=0;
	totalExpandedNodes=1;//includes the first node
	if(strategy.equals("ID")) {
		for(int i=0;endNode==null;i++) {//breaks if we found a goal node
			endNode=generalSearchHelper(i,problem,strategy);
			if(maximumDepthSoFar<i)break;//preventing infinite loop in IDS (no solution)
		}
	}else {
		endNode=generalSearchHelper((int)1e9,problem,strategy);//unlimited depth
	}
	String answer=SolutionHelper.solution(endNode, totalExpandedNodes);//TODO
	return answer;
}
private SearchNode generalSearchHelper(int maxDepth,SearchProblem problem,String strategy) {
	// The custom queue changes according to the strategy, it stores the queue accordingly and have functions add, pop, and isEmpty
	CustomQueue myQueue=new CustomQueue(strategy);
	SearchNode initialNode=problem.initialState;
	myQueue.push(initialNode);
	HashSet<String> visitedStates=new HashSet<String>();
	
	while(!myQueue.isEmpty()) {
		SearchNode currentNode=myQueue.pop();

		String repeatedStateString = currentNode.currentState;
		if(visitedStates.contains(repeatedStateString)) {//TODO
			continue;
		}
		
		visitedStates.add(repeatedStateString);//TODO
		
		maximumDepthSoFar=Math.max(maximumDepthSoFar, currentNode.depth);//max depth reached by the searching process
		if(problem.goalTest(currentNode)) {
			return currentNode;
		}
		
		if(currentNode.depth==maxDepth) {// Limited depth, works only in iterative deepening, others have an enormus maxDepth
			continue;
		}
		//expanding step
		for(Operator op:problem.operators) {
			SearchNode nextNode=problem.generateNextNode(currentNode,op);
			if(nextNode!=null) {//not applicable
				myQueue.push(nextNode);
				totalExpandedNodes++;
			}
		}
	}
	return null;//if no solution found
}
}
