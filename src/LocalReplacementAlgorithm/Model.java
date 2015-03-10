package LocalReplacementAlgorithm;

import java.util.LinkedList;
import java.util.List;

import LocalReplacementAlgorithm.MinCostFlow.Edge;

public class Model {
	
	private Client [] C;
	private Facility [] F;

	//These give the array layout of the bipartite graph, better for input
	private int [][] E_c; //edge cost function
	private int [][] E_u; //edge capacity function
	private double p; //percent of demand that must be satisfied, p\in [0,1]
	
	private int total_demand;
	
	//This gives the sparse representation, better for computation
	private LinkedList<Integer> [] N; //neighbor lists
	
	public Model(Client [] C, Facility [] F, double p){
		this.C = C;
		this.F = F;
		this.p=p;

		int cs = C.length;
		int fs = F.length;		

		E_c = new int[cs][fs];
		//Set all costs to be the distance between regions
		for(int i=0;i<cs;i++){
			for(int j=0;j<fs;j++){
				E_c[i][j] = C[i].distanceTo(F[j]); //arbitrary constant for scaling
			}
		}
		
		E_u = new int[cs][fs];
		
		//Set all capacities to be infinite
		//Except capacity is zero if edge to too far away
		total_demand=0;
		for(Client c : C) total_demand+=c.getDemand();

		for(int i=0;i<cs;i++){
			for(int j=0;j<fs;j++){
				if(F[j].canEffect(C[i])) E_u[i][j]=total_demand;
				else E_u[i][j]=0;
			}
		}
		
		//Create sparse linked list form
		N = new LinkedList[cs];
		for(int i=0;i<cs;i++){
			N[i] = new LinkedList<Integer>();
			for(int j=0;j<fs;j++){
				if(E_u[i][j]>0) N[i].add(j);
			}
		}
	}
	
	public int objective(boolean [] sol){
		int cs = C.length;
		int fs = F.length;
		//returns the minimum objective corresponding to a choosen set of facilities
		// requires polynomial time to compute a minimum flow over a bipartite graph
		int obj=0;
		for(int j=0;j<F.length;j++){
			if(sol[j]) obj+=F[j].getCost();
		}
		
		//Compute the minimum cost flow
	    List<Edge>[] graph = MinCostFlow.createGraph(cs+fs+3); //vertices = {s, s', clients, facilities, t}
	    
	    MinCostFlow.addEdge(graph, 0, 1, (int)Math.ceil(p*total_demand), 0);
	    
	    //s to clients
	    for(int i=0;i<cs;i++){
	    	MinCostFlow.addEdge(graph, 1, i+2, C[i].getDemand(), 0);
	    }
	    //clients to facilities
	    for(int i=0;i<cs;i++){
	    	for(Integer j : N[i]){
	    		if(sol[j]) MinCostFlow.addEdge(graph, i+2, cs+2+j, E_u[i][j], E_c[i][j]);
	    	}
	    }
	    
	    //facilities to t
	    for(int j=0;j<fs;j++){
    		if(sol[j]) MinCostFlow.addEdge(graph, cs+2+j, cs+fs+2, F[j].getCapacity(), 0);
	    }
	    
	    
	    int[] res = MinCostFlow.minCostFlow(graph, 0, cs+fs+2, Integer.MAX_VALUE);
	    int flow = res[0];
	    int flowCost = res[1];

	    //check result is feasible
	    if(flow<p*total_demand) return -1;
	    return obj+flowCost;
	}
}
