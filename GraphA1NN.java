/* ---------------------------------------------------------------------------------
The GraphA1NN class is the starting class for the graph-based ANN search

(c) Robert Laganiere, CSI2510 2023
------------------------------------------------------------------------------------*/
/* 
STUDENT HEADER :
El Bechir El Hadj 
Student number : 300294124
*/

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;


class GraphA1NN {
	
	UndirectedGraph<LabelledPoint> annGraph;
   
	private PointSet dataset;
	private int S;
	// construct a graph from a file
    public GraphA1NN(String fvecs_filename) {

	    annGraph= new UndirectedGraph<>();
		dataset= new PointSet(PointSet.read_ANN_SIFT(fvecs_filename));
    }

	// construct a graph from a dataset
    public GraphA1NN(PointSet set){
	
	   annGraph= new UndirectedGraph<>();
       this.dataset = set;
    }

    // build the graph
    public void constructKNNGraph(int K) throws IOException, Exception {
		ArrayList<LabelledPoint> pointslist = this.dataset.getPointsList();

		//Extrait a partir du fichier les voisins de chaque point 
		ArrayList<List<Integer>> neighbours = GraphA1NN.readAdjacencyFile("knn.txt", pointslist.size());
		//Parcours la liste d'adjence et la liste de points afin de connecter chaque point a ses K voisins 
		for (int i=0; i<pointslist.size(); i++) {
			// Recupere le point i et tout ses voisins
			List<Integer> nearest_neighbours = neighbours.get(i);
			LabelledPoint v = pointslist.get(i);
			for (int j=0; j<K; j++) {
				//Connnecte le point i a ses K voisins
				annGraph.addEdge(v,pointslist.get(nearest_neighbours.get(j)));
			}
		}
	}
	
	public static ArrayList<List<Integer> > readAdjacencyFile(String fileName, int numberOfVertices) 
	                                                                 throws Exception, IOException
	{	
		ArrayList<List<Integer> > adjacency= new ArrayList<List<Integer> >(numberOfVertices);
		for (int i=0; i<numberOfVertices; i++) 
			adjacency.add(new LinkedList<>());
		
		// read the file line by line
	    String line;
        BufferedReader flightFile = 
        	      new BufferedReader( new FileReader(fileName));
        
		// each line contains the vertex number followed 
		// by the adjacency list
        while( ( line = flightFile.readLine( ) ) != null ) {
			StringTokenizer st = new StringTokenizer( line, ":,");
			int vertex= Integer.parseInt(st.nextToken().trim());
			while (st.hasMoreTokens()) { 
			    adjacency.get(vertex).add(Integer.parseInt(st.nextToken().trim()));
			}
        } 
	
	    return adjacency;
	}
	public LabelledPoint find1NN(LabelledPoint Q){

		// Inisialization de la liste A 
		ArrayList<LabelledPoint> pointslist = this.dataset.getPointsList();
		ArrayList<LabelledPoint> A = new ArrayList<LabelledPoint>(S);
		ArrayList<LabelledPoint> CurrentVertex = new ArrayList<LabelledPoint>(1);


		//On verifie que tout les points dans la liste sont unchecked
		for (int i = 0; i<pointslist.size();i++){
			if (pointslist.get(i).isChecked()){pointslist.get(i).unchecked();}
		}
		
		//On recupere un point au hasard W
		Random randomizer = new Random();
		LabelledPoint W = pointslist.get(randomizer.nextInt(0,pointslist.size()));

		//On calcule la distance entre W et Q puis on insere W dans A
		W.setKey(Q.distanceTo(W));
		W.setIKey(Q.getLabel());
		A.add(W);
		boolean all_checked = false;
		while (!all_checked){

			//Verifie s'il y'a encore des points unchecked dans la liste 
			//+ recupere le point unchecked C avec la distance la plus courte de Q
			all_checked = true;
			if (A.size() != S){
				all_checked = false;
			}
			for (LabelledPoint v : A){
				if (!v.isChecked()){
					all_checked = false;
					CurrentVertex.add(v);
					break;
				}
			}
			//Si tout les vertex dans A on ete check on peut finir l'execution
			if( all_checked == true){
				return A.get(0);
			}
			//On recupere C et on le marque comme
			LabelledPoint C = CurrentVertex.remove(0);
			C.checked();

			//On recupere et parcours les points V adjacents a C
			List<LabelledPoint> voisins = annGraph.getNeighbors(C);
			for (LabelledPoint V : voisins){

				//Calcule la distance entre le point V et Q puis on l'insere dans A
				if (V.getIKey() != Q.getLabel()){
					V.setKey(V.distanceTo(Q));
					V.setIKey(Q.getLabel());
				}
				if (A.size() < S){
					A.add(V);
					Collections.sort(A,new PointComparator());
				}
				else if (A.get(S-1).getKey() > V.getKey()){
							A.remove(S-1).unchecked();
							A.add(V);
							Collections.sort(A,new PointComparator());
				}

			}
		}
		return Q; //Dummy Return 
	}
	public int size() { return annGraph.size(); }
	public void setS(int s) {this.S = s;}

    public static void main(String[] args) throws IOException, Exception {
		//Initialize GraphA1NN
        GraphA1NN graph = new GraphA1NN(args[2]);
		//Definir la valeure de S
		graph.setS(Integer.parseInt(args[1]));
		//Recuperer les points de requete
		ArrayList<LabelledPoint> QueryList = PointSet.read_ANN_SIFT(args[3]);
		
		//Recuperer la liste des voisins
		ArrayList<List<Integer>> reponses = new ArrayList<List<Integer>>();
		if(graph.dataset.getPointsList().size() == 10000){
			reponses = graph.readAdjacencyFile("knn_3_10_100_10000.txt", 100);
		} 
		else{
			reponses = graph.readAdjacencyFile("knn_3_10_100_1000000.txt", 100);
		}
		
		//Construire le Graph avec K voisins
		graph.constructKNNGraph(Integer.parseInt(args[0]));
		
		//Trouve le voisin le plus proche de chaque point de requete
		long start = System.currentTimeMillis();
		int acuracy = 0;
		for (int i = 0; i<QueryList.size(); i++){
			LabelledPoint Vi = graph.find1NN(QueryList.get(i));
			if (reponses.get(i).contains(Vi.getLabel())){
				acuracy++;
			}
			System.out.println(i+" : "+Vi.getLabel());
		}
		//Sorties : Precision, Total runtime et Average Runtime 
		double acuracy_per100 = (double)acuracy/(double)QueryList.size()*100;
		System.out.println("La precision est de : "+ acuracy_per100+"%");
		long runtime = System.currentTimeMillis()-start;
		double avg_runtime = runtime/(double)QueryList.size();
		System.out.println("Total runtime : "+ runtime);
		System.out.println("Average runtime : " + avg_runtime);
	}
}

// Comparateur pour pouvoir trier l'ArrayList A dans find1NN
class PointComparator implements Comparator<LabelledPoint>{
	public int compare(LabelledPoint pt1, LabelledPoint pt2){
		if (pt1.getKey() == pt2.getKey())
			return 0;
		if (pt1.getKey()>pt2.getKey())
			return 1;
		else
			return -1;
	}
}
