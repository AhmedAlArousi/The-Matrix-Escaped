package code;

import java.util.ArrayList;

public class UpdateState {
	//This class contains all methods used when creating the next state, they generate the side effects
	public static int removeKilledFromMutantsOrAgents(ArrayList<Integer>[] copiedCurrentStateDecoded, int neoX, int neoY) {
		//kills all neighboring agents/mutants and returns their number
		int removed=0;
		ArrayList<Integer>removedIndices=new ArrayList<Integer>();
		//search in agents to kill
		for(int i=0;i<copiedCurrentStateDecoded[2].size();i+=2) {
			int x=copiedCurrentStateDecoded[2].get(i);
			int y=copiedCurrentStateDecoded[2].get(i+1);
			if(x==neoX+1&&y==neoY
					||x==neoX-1&&y==neoY
					||x==neoX&&y==neoY+1
					||x==neoX&&y==neoY-1) {
				removed++;
				removedIndices.add(i);
			}
		}
		
		//remove them
		for(int i=removedIndices.size()-1;i>=0;i--){
			copiedCurrentStateDecoded[2].remove((int)removedIndices.get(i)+1);
			copiedCurrentStateDecoded[2].remove((int)removedIndices.get(i));
		}

		removedIndices.clear();
		
		//search in mutants to kill
		for(int i=0;i<copiedCurrentStateDecoded[8].size();i+=2) {
			int x=copiedCurrentStateDecoded[8].get(i);
			int y=copiedCurrentStateDecoded[8].get(i+1);
			if(x==neoX+1&&y==neoY
					||x==neoX-1&&y==neoY
					||x==neoX&&y==neoY+1
					||x==neoX&&y==neoY-1) {
				removed++;
				removedIndices.add(i);
			}
		}
		//remove them
		for(int i=removedIndices.size()-1;i>=0;i--){
			copiedCurrentStateDecoded[8].remove((int)removedIndices.get(i)+1);
			copiedCurrentStateDecoded[8].remove((int)removedIndices.get(i));
		}
		return removed;
	}
	static void moveHostageFromGridToCarried(ArrayList<Integer>[] copiedCurrentStateDecoded, int neoX, int neoY) {
		//removing hostage from the grid and adding it to the carried hostages
		int indexOfCarriedHostage=-1;
		for(int i=0;i<copiedCurrentStateDecoded[4].size();i+=3) {
			int x=copiedCurrentStateDecoded[4].get(i);
			int y=copiedCurrentStateDecoded[4].get(i+1);
			if(x==neoX&&y==neoY) {
				indexOfCarriedHostage=i;
				break;
			}
		}
		int damageOfCarriedHostage=copiedCurrentStateDecoded[4].get(indexOfCarriedHostage+2);
		copiedCurrentStateDecoded[4].remove(indexOfCarriedHostage+2);
		copiedCurrentStateDecoded[4].remove(indexOfCarriedHostage+1);
		copiedCurrentStateDecoded[4].remove(indexOfCarriedHostage);
		copiedCurrentStateDecoded[5].add(damageOfCarriedHostage);
	}
	static void removePill(ArrayList<Integer>[] copiedCurrentStateDecoded, int neoX, int neoY) {
		//searches for the pill's index and removes it 
		int pillIndex=-1;
		for(int i=0;i<copiedCurrentStateDecoded[3].size();i+=2) {
			int x=copiedCurrentStateDecoded[3].get(i);
			int y=copiedCurrentStateDecoded[3].get(i+1);
			if(x==neoX&&y==neoY) {
				pillIndex=i;
				break;
			}
		}
		copiedCurrentStateDecoded[3].remove(pillIndex+1);
		copiedCurrentStateDecoded[3].remove(pillIndex);
		
	}
	static void updateDamagesOfHostages(ArrayList<Integer>[] copiedCurrentStateDecoded, boolean takePill) {
		int damageChange=takePill?-20:2;
		
		//loop over hostages in grid
		ArrayList<Integer>indicesOfCurrentDeadHostages=new ArrayList<Integer>();
		for(int i=2;i<copiedCurrentStateDecoded[4].size();i+=3) {
			int damageOfCurrentHostage=copiedCurrentStateDecoded[4].get(i);
			if(damageChange==2&&damageOfCurrentHostage<100&&damageOfCurrentHostage+damageChange>=100) {
				//this hostage dies in this step and turns into a mutant
				indicesOfCurrentDeadHostages.add(i);
				int currentDeaths=copiedCurrentStateDecoded[6].get(0);
				copiedCurrentStateDecoded[6].set(0, currentDeaths+1);
			}
			if(damageChange!=-20||damageOfCurrentHostage<100)
				damageOfCurrentHostage=Math.max(Math.min(damageOfCurrentHostage+damageChange, 100), 0);
			copiedCurrentStateDecoded[4].set(i,damageOfCurrentHostage);
		}
		
		//loop over carried hostages
		for(int i=0;i<copiedCurrentStateDecoded[5].size();i++) {
			int damageOfCarriedHostage=copiedCurrentStateDecoded[5].get(i);
			if(damageChange==2&&damageOfCarriedHostage<100&&damageOfCarriedHostage+damageChange>=100) {
				//this hostage dies while neo holding it, and increases deaths
				int currentDeaths=copiedCurrentStateDecoded[6].get(0);
				copiedCurrentStateDecoded[6].set(0, currentDeaths+1);
			}
			if(damageChange!=-20||damageOfCarriedHostage<100)
				damageOfCarriedHostage=Math.max(Math.min(damageOfCarriedHostage+damageChange, 100), 0);
			copiedCurrentStateDecoded[5].set(i,damageOfCarriedHostage);
		}
		
		//turn dead hostages into mutants
		for(int index:indicesOfCurrentDeadHostages) {
			//move them to the mutants
			copiedCurrentStateDecoded[8].add(copiedCurrentStateDecoded[4].get(index-2));
			copiedCurrentStateDecoded[8].add(copiedCurrentStateDecoded[4].get(index-1));
		}
		
		//remove them from current hostages in map
		for(int i=indicesOfCurrentDeadHostages.size()-1;i>=0;i--) {
			int currentIndex=indicesOfCurrentDeadHostages.get(i);
			copiedCurrentStateDecoded[4].remove(currentIndex);
			copiedCurrentStateDecoded[4].remove(currentIndex-1);
			copiedCurrentStateDecoded[4].remove(currentIndex-2);
		}
	}
}
