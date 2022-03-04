package code;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class CustomQueue {
//The class is a representative of 3 Data Structures, depending on the called constructor, and with different queuing functions for priority queue
Stack<SearchNode> st;
Queue<SearchNode>q;
PriorityQueue<SearchNode>pq;

CustomQueue(String strategy){
if(strategy.equals("BF")){
	q=new LinkedList<SearchNode>();
	
}else if (strategy.equals("DF")||strategy.equals("ID")){
	st=new Stack<SearchNode>();

}else if(strategy.equals("UC")){
	pq=new PriorityQueue<SearchNode>((u,v)->u.pathCost-v.pathCost==0?u.time-v.time:u.pathCost-v.pathCost);

}else if(strategy.equals("AS1")) {
	pq=new PriorityQueue<SearchNode>((u,v)->u.pathCost+u.heuristic1-v.pathCost-v.heuristic1==0?u.time-v.time:u.pathCost+u.heuristic1-v.pathCost-v.heuristic1);

}else if(strategy.equals("AS2")){
	pq=new PriorityQueue<SearchNode>((u,v)->u.pathCost+u.heuristic2-v.pathCost-v.heuristic2==0?u.time-v.time:u.pathCost+u.heuristic2-v.pathCost-v.heuristic2);

}else if(strategy.equals("GR1")) {
	pq=new PriorityQueue<SearchNode>((u,v)->u.heuristic1-v.heuristic1==0?u.time-v.time:u.heuristic1-v.heuristic1);

}else {//GR2
	pq=new PriorityQueue<SearchNode>((u,v)->u.heuristic2-v.heuristic2==0?u.time-v.time:u.heuristic2-v.heuristic2);

}
}

CustomQueue(Stack<SearchNode> st){
	this.st=st;	
}
CustomQueue(Queue<SearchNode> q){
	this.q=q;	
}
CustomQueue(PriorityQueue<SearchNode> pq){
	this.pq=pq;
}
void push(SearchNode sn) {
	if(st!=null) {
		st.add(sn);
	}else if(q!=null) {
		q.add(sn);
	}else {
		pq.add(sn);
	}
}
SearchNode pop() {
	if(st!=null) {
		return st.pop();
	}else if(q!=null) {
		return q.poll();
	}else {
		return pq.poll();
	}
}

boolean isEmpty() {
	return st!=null?st.isEmpty():q!=null?q.isEmpty():pq.isEmpty();
}

}
