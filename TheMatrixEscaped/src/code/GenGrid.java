package code;

import java.util.ArrayList;

public class GenGrid {
	public static String genGrid() {
		String gridString="";
		
		int M=(int) (Math.random()*11)+5;
		
		int N=(int) (Math.random()*11)+5;
		
		int C=(int) (Math.random()*4)+1;
		
		int neoX=(int) (Math.random()*(M));
		int neoY=(int) (Math.random()*(N));
		
		int boothX;
		int boothY;
		while(true){
			boothX=(int) (Math.random()*(M));
			boothY=(int) (Math.random()*(N));
			if(boothX!=neoX||boothY!=neoY) break;
		}
		
		int numOfHostages=(int) (Math.random()*8)+3;// from 3 to 10
		ArrayList<Integer> hostages=new ArrayList<Integer>();
		for(int i=0;i<numOfHostages;i++){
			while(true){
				int hostX=(int) (Math.random()*(M));
				int hostY=(int) (Math.random()*(N));
				//check if the position is not the pos of neo, booth, or any hostage
				if((hostX!=neoX||hostY!=neoY)&&(hostX!=boothX||hostY!=boothY)){
					boolean availablePos=isLocationAvailable(hostX,hostY,hostages,true);					
					if(availablePos){
						hostages.add(hostX);
						hostages.add(hostY);
						int hostDamage=(int)(Math.random()*99)+1;
						hostages.add(hostDamage);
						break;
					}
				}
			}
		}
		
		int numberOfAgents=(int) (Math.random()*((N*M)-numOfHostages-1));
		ArrayList<Integer> agents=new ArrayList<Integer>();
		for(int i=0;i<numberOfAgents;i++){
			while(true){
				int agentX=(int) (Math.random()*(M));
				int agentY=(int) (Math.random()*(N));
				//check if the position is not the pos of neo, booth,
				//any hostage, or any agent
				if((agentX!=neoX||agentY!=neoY)&&(agentX!=boothX||agentY!=boothY)){
					boolean availablePosHost=isLocationAvailable(agentX,agentY,hostages,true);
					boolean availablePosAgents=isLocationAvailable(agentX,agentY,agents,false);					
					if(availablePosHost&&availablePosAgents){
						agents.add(agentX);
						agents.add(agentY);
						break;
					}
				}
			}
		}

		
		int numberOfPills=(int) (Math.random()*numOfHostages);
		if((N*M)-2-numberOfAgents-numOfHostages<numberOfPills)
			numberOfPills=(N*M)-2-numberOfAgents-numOfHostages;
		ArrayList<Integer> pills=new ArrayList<Integer>();
		for(int i=0;i<numberOfPills;i++){
			while(true){
				int pillX=(int) (Math.random()*(M));
				int pillY=(int) (Math.random()*(N));
				//check if the position is not the pos of neo, booth,
				//any hostage, any agent, or any pill
				if((pillX!=neoX||pillY!=neoY)&&(pillX!=boothX||pillY!=boothY)){
					boolean availablePosHost=isLocationAvailable(pillX,pillY,hostages,true);
					boolean availablePosAgents=isLocationAvailable(pillX,pillY,agents,false);	
					boolean availablePosPills=isLocationAvailable(pillX,pillY,pills,false);					
					if(availablePosHost&&availablePosAgents&&availablePosPills){
						pills.add(pillX);
						pills.add(pillY);
						break;
					}
				}
			}
		}

		
		int numberOfPads=(int) (Math.random()*((N*M)-numOfHostages-numberOfAgents-numberOfPills-1));
		numberOfPads=numberOfPads%2==0?numberOfPads:numberOfPads-1;
		ArrayList<Integer> pads=new ArrayList<Integer>();
		for(int i=0;i<numberOfPads;i++){
			while(true){
				int padX=(int) (Math.random()*(M));
				int padY=(int) (Math.random()*(N));
				//check if the position is not the pos of neo, booth,
				//any hostage, any agent, any pill, or any pad
				if((padX!=neoX||padY!=neoY)&&(padX!=boothX||padY!=boothY)){
					boolean availablePosHost=isLocationAvailable(padX,padY,hostages,true);
					boolean availablePosAgents=isLocationAvailable(padX,padY,agents,false);	
					boolean availablePosPills=isLocationAvailable(padX,padY,pills,false);
					boolean availablePosPads=isLocationAvailable(padX,padY,pads,false);					
					if(availablePosHost&&availablePosAgents&&availablePosPills&&availablePosPads){
						pads.add(padX);
						pads.add(padY);
						break;
					}
				}
			}
		}
		//generate the final string 
		//add # if there is no agents, pills, or pads
		gridString+=M+","+N+";"+C+";"+neoX+","+neoY+";"+boothX+","+boothY+";";
		
		int i;
		for(i=0;i<agents.size()-1;i++){
			gridString+=agents.get(i)+",";
		}
		if(agents.size()>0)gridString+=agents.get(i);
		else gridString+="#";
		gridString+=";";
		
		if(pills.size()==0){
			gridString+="#";
		}
		else{
			for(i=0;i<pills.size()-1;i++){
				gridString+=pills.get(i)+",";
			}
			if(pills.size()>0)gridString+=pills.get(i);
		}
		gridString+=";";

		if(pads.size()==0){
			gridString+="#";
		}else{
			for(i=0;i<pads.size();i++){
				gridString+=pads.get(i)+",";
			}


			for(i=0;i<pads.size()-4;i+=4){
				gridString+=pads.get(i+2)+",";
				gridString+=pads.get(i+3)+",";
				gridString+=pads.get(i)+",";
				gridString+=pads.get(i+1)+",";
			}
			if(pads.size()>0){
				gridString+=pads.get(i+2)+",";
				gridString+=pads.get(i+3)+",";
				gridString+=pads.get(i)+",";
				gridString+=pads.get(i+1);
			}
		}
		gridString+=";";

		for(i=0;i<hostages.size()-1;i++){
			gridString+=hostages.get(i)+",";
		}
		if(hostages.size()>0)gridString+=hostages.get(i);
		gridString+=";";

		return gridString;
	}
	
	private static boolean isLocationAvailable(int x,int y,ArrayList<Integer> arr,boolean isHostage){
		int inc=2;
		if(isHostage)
			inc=3;
		boolean occupied=false;
		for(int j=0;j<arr.size();j+=inc){
			if(x==arr.get(j)&&y==arr.get(j+1)){
				occupied=true;
				break;
			}
		}
		return !occupied;
	}
}