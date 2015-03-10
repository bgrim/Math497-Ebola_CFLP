package LocalReplacementAlgorithm;

public class Client {
	
	private String name;
	public String getName(){ return name; }
	
	//location information
	private double x;
	public double getX(){return x;}
	private double y;
	public double getY(){return y;}

	private int demand;
	public int getDemand(){ return demand;}

	
	public Client(String name, double x, double y, int demand){
		this.name=name;
		this.x=x;
		this.y=y;
		this.demand=demand;
	}
	
	public int distanceTo(Facility f){
		 double f_x = f.getX();
		 double f_y = f.getY();
		 return (int) Math.ceil(100*Math.sqrt((x-f_x)*(x-f_x) + (y-f_y)*(y-f_y)));
	}
}
