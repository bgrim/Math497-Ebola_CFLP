package LocalReplacementAlgorithm;

public class Facility {
	
	private String name;
	public String getName(){ return name; }
	
	//location information
	private double x;
	public double getX(){return x;}
	private double y;
	public double getY(){return y;}

	public double radius; //how far away it can effect something
	
	private int cost;
	public int getCost(){ return cost;}

	private int capacity;
	public int getCapacity(){ return capacity;}

	
	public Facility(String name, double x, double y, int cost, int capacity, double radius){
		this.name=name;
		this.x=x;
		this.y=y;
		this.cost=cost;
		this.capacity=capacity;
		this.radius=radius;
	}
	
	public double distanceTo(Client c){
		 double c_x = c.getX();
		 double c_y = c.getY();
		 return 100*Math.sqrt((x-c_x)*(x-c_x) + (y-c_y)*(y-c_y));
	}
	
	public boolean canEffect(Client c){
		return (int)Math.ceil(distanceTo(c)) <= radius;
	}
}
