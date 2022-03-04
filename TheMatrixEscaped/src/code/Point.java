package code;


public class Point implements Comparable<Point>{
	int x,y;
    Point(int x,int y){
    	this.x=x;
    	this.y=y;
    }
	@Override
	public int compareTo(Point p) {
		return x==p.x?y-p.y:x-p.x;
	}
}