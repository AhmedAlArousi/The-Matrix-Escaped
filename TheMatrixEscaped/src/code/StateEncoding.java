package code;

import java.util.ArrayList;

public class StateEncoding {
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Integer>[] decodeCurrentState(SearchNode currentNode) {
		//decoding the current state from the string
		ArrayList<Integer>[]currentStateDecoded = new ArrayList[9];
		
		String[] outerArray = currentNode.currentState.split(";");
		//System.out.println(Arrays.toString(outerArray));
		for(int i = 0 ; i < outerArray.length; i++) {
			String[] innerArray = outerArray[i].split(","); 
			currentStateDecoded[i] = new ArrayList<Integer>();
			for(int j = 0; j < innerArray.length; j++) {
				String temp = innerArray[j];
				if(!temp.equals("#")) {
					currentStateDecoded[i].add(Integer.parseInt(temp));
				}
			}
		}
		return currentStateDecoded;
	}
	
	public static String encodeState(ArrayList<Integer>[] copiedCurrentStateDecoded) {
		//encoding the current state to string
		StringBuilder newState=new StringBuilder();
		for(int i=0;i<copiedCurrentStateDecoded.length;i++) {
			ArrayList<Integer>currentRow=copiedCurrentStateDecoded[i];
			for(int j=0;j<currentRow.size();j++) {
				newState.append(currentRow.get(j));
				if(j!=currentRow.size()-1)newState.append(",");
			}
			if(currentRow.size()==0)newState.append("#");
			newState.append(";");
		}
		return newState.toString();
	}
	
	public static String generateInitialState(Matrix problem,String grid) {
		//convert from the grid to the initial state
		//assign the variables of object problem
		
		String[] semiColonParser=grid.split(";");
		StringBuilder initState=new StringBuilder();
		
		String[]sp1=semiColonParser[0].split(",");//grid size
		problem.m=Integer.parseInt(sp1[0]);
		problem.n=Integer.parseInt(sp1[1]);
		problem.c=Integer.parseInt(semiColonParser[1]);//max number of carried hostages
		
		initState.append(semiColonParser[2]);//neo's init location & neo's damage
		initState.append(";0;");
		
		sp1=semiColonParser[3].split(",");//booth
		problem.boothX=Integer.parseInt(sp1[0]);
		problem.boothY=Integer.parseInt(sp1[1]);
		
		initState.append(semiColonParser[4]);//agent's
		initState.append(";");
		
		initState.append(semiColonParser[5]);//pills
		initState.append(";");
		
		problem.pad=new Point[problem.m][problem.n];
		//System.out.println("Pad Dimensions: "+pad.length+","+pad[0].length);
		sp1=semiColonParser[6].split(",");
		if(sp1.length>1){
			for(int i=0;i<sp1.length;i+=4) {
				int x=Integer.parseInt(sp1[i+2]);
				int y=Integer.parseInt(sp1[i+3]);
				problem.pad[Integer.parseInt(sp1[i])][Integer.parseInt(sp1[i+1])]=new Point(x,y);
			}
		}
		initState.append(semiColonParser[7]);//hostages

		initState.append(";#;0;0;#");
		
		return initState.toString();
	}
}
