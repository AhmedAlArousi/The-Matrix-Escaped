package code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class Dijkstra {
	static ArrayList<int[]>adj[];
	static int nodes;
	static TreeMap<Integer,Point>hostagesMap;
	static TreeMap<Integer,Integer>hostagesDmg;
	static int[] dijkstraHeuristic(Matrix problem,ArrayList<Integer>currentStateDecoded[]) {
		constructGraph(problem,currentStateDecoded);
		
		int[]distNeo=dijkstra(0);
		int[]distBooth=dijkstra(1);
		int pillsNum=currentStateDecoded[3].size()/2;
		ArrayList<Integer>carriedDamages=currentStateDecoded[5];
		
		int estimatedDeathCarried=edc(distNeo,distBooth,pillsNum,carriedDamages);
		int estimatedDeathInGrid=edig(distNeo,distBooth,pillsNum,carriedDamages,problem);
		
		return new int[] {estimatedDeathCarried,estimatedDeathInGrid};
	}
	private static int edig(int[] distNeo, int[] distBooth, int pillsNum, ArrayList<Integer> carriedDamages,Matrix problem) {
		int c=problem.c;
		int count=0;
		if(carriedDamages.size()==c) {
			int dst=distNeo[1]+1;//if carried full, must go to position
			for(int id:hostagesMap.keySet()) {
				int total=dst+distBooth[id]*2;
				int dmg=hostagesDmg.get(id);
				if(dmg+total*2-pillsNum*20>=100) {
					count++;
				}
			}
		}else {
			for(int id:hostagesMap.keySet()) {
				int total=distNeo[id]+distBooth[id];
				int dmg=hostagesDmg.get(id);
				if(dmg+total*2-pillsNum*20>=100) {
					count++;
				}
			}
		}
		return count;
	}
	private static int edc(int[] distNeo, int[] distBooth, int pillsNum, ArrayList<Integer> carriedDamages) {
		int count=0;
		int distToBooth= distNeo[1];
		for(int damage:carriedDamages) {
			if(damage!=100&&damage-pillsNum*20 + distToBooth*2>=100) {//hostage that is not already deads
				count++;
			}
		}
		return count;
	}
	private static void constructGraph(Matrix problem, ArrayList<Integer>[] currentStateDecoded) {
		int m=problem.m;
		int n=problem.n;
		int c=problem.c;
		
		int boothX=problem.boothX;
		int boothY=problem.boothY;
		
		int neoX=currentStateDecoded[0].get(0);
		int neoY=currentStateDecoded[0].get(1);
		
		Point[][]pad=problem.pad;
		
		TreeMap<Integer,Point> padMap=new TreeMap();
		int pads=0;
		int idx=2;
		int[][]padsIndices=new int[m][n];
		for(int i=0;i<m;i++)Arrays.fill(padsIndices[i], -1);
		for(int i=0;i<pad.length;i++) {
			for(int j=0;j<pad[0].length;j++) {
				if(pad[i][j]!=null) {
					padsIndices[i][j]=idx;
					padMap.put(idx++,new Point(i,j));
					pads++;
				}
			}
		}
		
		hostagesMap=new TreeMap();
		hostagesDmg=new TreeMap();
		for(int i=0;i<currentStateDecoded[4].size();i+=3) {
			hostagesDmg.put(idx, currentStateDecoded[4].get(i+2));
			hostagesMap.put(idx++, new Point(currentStateDecoded[4].get(i),currentStateDecoded[4].get(i+1)));
		}
		int hostages=currentStateDecoded[4].size()/3;
		nodes=2+pads+hostages;//neo, booth, pads, and hostages
		//dist=new int[nodes][nodes];
//		n=nodes;
		// neo pos 0
		// booth pos 1
		// pads position 2 -> pads+1
		// pills position pads+2 -> pads+pills+1
		// hostages positions pads+pills+2 -> nodes-1
		
		
		adj=new ArrayList[nodes];
		for(int i=0;i<adj.length;i++)adj[i]=new ArrayList<int[]>();
		
		int distNeoToBooth=manhattan(neoX,neoY,boothX,boothY);
		//neo's arcs
		adj[0].add(new int[] {1,distNeoToBooth});
		adj[1].add(new int[] {0,distNeoToBooth});
		
		
		//hostages' arcs
		for(int id:hostagesMap.keySet()) {
			Point hostage=hostagesMap.get(id);
			int distNeoToHostage=manhattan(neoX, neoY, hostage.x, hostage.y);
			int distBoothToHostage=manhattan(boothX, boothY, hostage.x, hostage.y);
			
			//neo
			adj[0].add(new int[] {id,distNeoToHostage+1});//carrying costs 1
			adj[id].add(new int[] {0,distNeoToHostage});
			
			//booth
			adj[1].add(new int[] {id,distBoothToHostage});
			adj[id].add(new int[] {1,distBoothToHostage+1});
	}
		//pads arc's
		for(int id:padMap.keySet()) {
			Point padx=padMap.get(id);
			Point correspondingPad=pad[padx.x][padx.y];
			
			int distNeoToPad=manhattan(neoX, neoY, padx.x, padx.y);
			int distBoothToPad=manhattan(boothX, boothY, padx.x, padx.y);
			
			//neo
			adj[0].add(new int[] {id,distNeoToPad});
			adj[id].add(new int[] {0,distNeoToPad});
			
			//booth
			adj[1].add(new int[] {id,distBoothToPad});
			adj[id].add(new int[] {1,distBoothToPad});
			
			//distance of 2 pads
			int idPad2=padsIndices[correspondingPad.x][correspondingPad.y];
			adj[id].add(new int[] {idPad2,1});
			//hostages
			for(int idHostage:hostagesMap.keySet()) {
				Point hostage=hostagesMap.get(idHostage);
				int distHostageToPad=manhattan(padx.x, padx.y, hostage.x, hostage.y);
				adj[idHostage].add(new int[] {id,distHostageToPad});
				adj[id].add(new int[] {idHostage,distHostageToPad+1});
			}
		}
	}
	
	
	static int[] dijkstra(int src) {
		int[]dist = new int[nodes];
		Arrays.fill(dist, Integer.MAX_VALUE);
		PriorityQueue<int[]> pq = new PriorityQueue<int[]>((u,v)->u[1]-v[1]);
		pq.add(new int[] {src,0});
		while(!pq.isEmpty()) {
			int[] p = pq.poll();
			if(p[0] == nodes)
				break;
			if(dist[p[0]] < p[1])
				continue;
			for(int[] e:adj[p[0]]) {
				if(dist[e[0]] > p[1] + e[1]) {
					pq.add(new int[] {e[0],(int) (dist[e[0]] = p[1] + e[1]) });
				}
			}
		}
		return dist;
	}
	
	private static int manhattan(int x1, int y1, int x2, int y2) {
		return Math.abs(x1-x2)+Math.abs(y1-y2);
	}
}
