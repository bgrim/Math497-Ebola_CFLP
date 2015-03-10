package LocalReplacementAlgorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LocalReplacement {
	public static List<Integer> permutation;
	
	public static final int NUMBER_OF_SUBDISTRICTS=135;
	public static Client [] clients;
	public static Facility [] facilities;
	public static void main(String []args) throws FileNotFoundException{				
		//The LOCAL REPLACEMENT ALGORITHM
		
		//Build the Model 
		readFromFile("data.txt");
		
		PrintWriter long_pw = new PrintWriter(new File("long_output_r9.txt"));
		PrintWriter short_pw = new PrintWriter(new File("short_output_r9.txt"));

		for (double ip = 1; ip <= 20; ip++) {
			for (int k = 0; k < 10; k++) {
				
				//Run the k^{th} test for a given p value
				double p = (1.0*ip)/20;
				Model m = new Model(clients, facilities, p);
				
				// Compute an initial solution.	
				boolean[] sol = new boolean[facilities.length];
				do{
				for (int i = 0; i < facilities.length; i++) {
						if(Math.random()<p/20) sol[i] = true;
					}
				}while(m.objective(sol)==-1);

				// Iteratively improve
				permutation = new ArrayList<Integer>();
				for (int i = 0; i < sol.length; i++)
					permutation.add(i);
				int temp;

				int obj = m.objective(sol);

				int c = 0;
				while (true) {
					c++;
					// Check if adding a facility improves
					temp = addImprove(m, sol, obj);
					if (temp != -1) {
						obj = temp;
						continue;
					}
					// Check if deleting a facility improves
					temp = deleteImprove(m, sol, obj);
					if (temp != -1) {
						obj = temp;
						continue;
					}
					// Check if swapping a facility improves
					temp = swapImprove(m, sol, obj);
					if (temp != -1) {
						obj = temp;
						continue;
					}
					break;
				}
				
				//Print the result to log files
				System.out.println(p+" "+obj);
				short_pw.write(p+", "+obj+"\n");
				long_pw.write("k="+k+", p="+p+", obj="+obj+", c="+c+"\n");
				for (int j = 0; j < facilities.length; j++) {
					if (sol[j]) {
						System.out.println(facilities[j].getName());
						long_pw.write("  "+facilities[j].getName()+"\n");
					}
				}
				short_pw.flush();
				long_pw.flush();
			}
		}
		short_pw.close();
		long_pw.close();
	}
	
	
	public static int addImprove(Model m, boolean [] sol, int obj){
		//Trys all possible add operations to see if any improves the objective function
		//  The adds should be checked in a random order to reduce bias
		//When such an add is found, sol is modified and the new objective is returned
		//  otherwise -1 is returned
		java.util.Collections.shuffle(permutation);
		
		for(Integer i : permutation){
			if(!sol[i]){
				sol[i]=true;
				int newObj = m.objective(sol);
				if(newObj!=-1 && newObj<obj){
					return newObj;
				}
				sol[i]=false;
			}
		}
		return -1;
	}
	
	public static int deleteImprove(Model m, boolean [] sol, int obj){
		//Trys all possible delete operations to see if any improves the objective function
		//  The deletes should be checked in a random order to reduce bias
		//When such a delete is found, sol is modified and the new objective is returned
		//  otherwise -1 is returned
		java.util.Collections.shuffle(permutation);
		
		for(Integer i : permutation){
			if(sol[i]){
				sol[i]=false;
				int newObj = m.objective(sol);
				if(newObj!=-1 && newObj<obj){
					return newObj;
				}
				sol[i]=true;
			}
		}
		return -1;
	}
	
	public static int swapImprove(Model m, boolean [] sol, int obj){
		//Trys all possible swap operations to see if any improves the objective function
		//  The swaps should be checked in a random order to reduce bias
		//When such a swap is found, sol is modified and the new objective is returned
		//  otherwise -1 is returned
		java.util.Collections.shuffle(permutation);
		
		for(Integer i : permutation){
			for(Integer j : permutation){
				if(i!=j && sol[i] && !sol[j]){
					sol[i]=false;
					sol[j]=true;
					int newObj = m.objective(sol);
					if(newObj!=-1 && newObj<obj){
						return newObj;
					}
					sol[i]=true;
					sol[j]=false;
				}
			}
		}
		return -1;
	}
	
	public static void readFromFile(String filename) throws FileNotFoundException{
		Scanner in = new Scanner(new File(filename));
		
		int n = NUMBER_OF_SUBDISTRICTS; //number of subdistricts

		
		int t = in.nextInt(); //number of facility types: (cost, capacity)
		int [] cost = new int[t];
		int [] capacity = new int[t];
		for(int i=0;i<t;i++){
			cost[i]=in.nextInt();
			capacity[i]=in.nextInt();
		}
		int Effect_DISTANCE = in.nextInt();
		
		int num_existing_facilities = in.nextInt();

		Client [] clients = new Client [n];
		Facility [] facilities = new Facility[num_existing_facilities + t*n];
		//Add Clients
		for(int i=0;i<n;i++){
			String county = in.next(); //name of subdistrict
			String district = in.next(); //name of subdistrict
			double x = in.nextDouble(); //x coordinate
			double y = in.nextDouble(); //y coordinate
			int d = (int) Math.ceil(in.nextDouble()); //demand
			if(d>150) d=150;
			clients[i] = new Client(county+", "+district, x, y, d);
		}
		//Add Facilities
		for(int i=0;i<num_existing_facilities;i++){
			String name = in.next();
			double x = in.nextDouble(); //x coordinate
			double y = in.nextDouble(); //y coordinate
			int cap = in.nextInt();
			facilities[i] = new Facility(name+" "+cap+" Bed", x, y, 0, cap, Effect_DISTANCE);
		}
		for(int i=0;i<n;i++){
			String name = clients[i].getName(); //name of subdistrict
			double x = clients[i].getX(); //x coordinate
			double y = clients[i].getY(); //y coordinate
			for(int j=0;j<t;j++){
				facilities[num_existing_facilities+t*i+j] = new Facility(name+" "+capacity[j]+" Bed", x, y, cost[j], capacity[j], Effect_DISTANCE);
			}
		}
	}
}
