import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Automate {
	
	final int epsilone = 0;
	
	static int cptD = 1;
	static  int cpt = 0;
	
	public int [][] matAutomate;
	public int [] statesFinal;
	
	public Automate(RegExTree RT) {
		
		AutoWithE automateWithE = new AutoWithE().creation(RT);
		Node init = creerAutomateSansE(automateWithE);
		init = creerDeterministe(init);
		matAutomate = new int [cptD][256];
		statesFinal = new int [cptD];
		transAutoMatrice(init, new int [cptD]);
		cptD = 1;
		
		
	}
	
	 public void search() {
		  try {
			  int cpt = 0;
			  System.out.println(new File(".").getAbsoluteFile());
	          Scanner scanner = new Scanner(new File("src/56667-0.txt"));
	          while(scanner.hasNextLine())
	          {
	              String line = scanner.nextLine();
	              char [] tab = line.toCharArray();
	              for(int i=0; i<tab.length;i++) {
	            	  if(verifie(i, tab)) {
	            		  cpt++;
	            		  System.out.println(line);
	            		  break;
	            		  
	            	  }
	              }
	              //do stuff
	          }
	          System.out.println("nombre total auto :"+cpt);
	      } catch (FileNotFoundException e) {
	          e.printStackTrace();
	      }
	  }
	  
	  private boolean verifie(int indice, char [] line) {
		  
		  int state = 1;
		  for(; indice < line.length; indice++) {
			  if (this.statesFinal[state] == 1) 
				  return true;
			  
			  int lettre = (int) line[indice];
			  if(lettre > 256) return false;
			  if(matAutomate[state][lettre] != 0) {
				  // etat suivant
				  state = matAutomate[state][lettre];  
			  }
			  else return false;
		  }
		  
		  return statesFinal[state] == 1;
	  }
	  

	
	
	private Node creerAutomateSansE (AutoWithE automateWithE) {
			
			int [] visites = new int [cpt]; 
			Map<Integer,Node> newNodes = new HashMap<>();
			Node init = new Node(automateWithE.init.state, new HashMap<Integer, ArrayList<Node>>());
			newNodes.put(init.state, init);
			sansE_rec(automateWithE.init, visites, init.state,automateWithE.finale.state, newNodes);
			return init;
		}
		
		
		
	// depuis : le dernier noeud important visité
	private void sansE_rec(Node n, int [] visites, int depuis, int stateFinale, Map<Integer, Node> newNodes ) {
			
			HashMap<Integer, ArrayList<Node>> map = (HashMap<Integer, ArrayList<Node>>) n.transitions;
			if(n.state == stateFinale) {
				newNodes.get(depuis).setFinal();
			}
			for (Map.Entry m : map.entrySet()) {
					for(Node node :(ArrayList<Node>) m.getValue()) {
						if(m.getKey()==(Integer)epsilone) {
							
							
							sansE_rec(node, visites, depuis, stateFinale, newNodes );
						}
						else {
							if(newNodes.get(node.state)==null) {
								Node newNode = new Node(node.state, new HashMap<Integer, ArrayList<Node>>());
								newNodes.put(node.state, newNode);	
							}
							
							Node ndepuis = newNodes.get(depuis);
							ndepuis.addTransition((int)m.getKey(), newNodes.get(node.state));
							
							if(visites[node.state]==0) {
								visites[node.state] = 1;
								sansE_rec(node, visites, node.state, stateFinale, newNodes);
			           
							}
						
						}
			            
					}
					
	        }
			
		    
		}



	
	public Node creerDeterministe(Node init) {
		
		HashSet<Node> set = new HashSet<>();
		set.add(init);
		
		Node newNode = new Node(cptD++, new HashMap<Integer, ArrayList<Node>>());
		
		newNode.setEnsemble(set);
		
		ArrayList<Node> nodesD = new ArrayList<>();
		nodesD.add(newNode);
		deterministe(newNode, nodesD);
		
		
		return newNode;
		
	}
	
	private Node deterministe(Node n, ArrayList<Node> nodesD) {
		
		HashMap<Integer, HashSet<Node>> map = new HashMap<>();
		for (Node en : n.nodes) {
			for (Map.Entry m : en.transitions.entrySet()) {
				for(Node node :(ArrayList<Node>) m.getValue()) {
					if (map.get(m.getKey())==null) {
						map.put((Integer) m.getKey(), new HashSet<>());
					}
					map.get(m.getKey()).add(node);
				}
				//Node newNode = new Node(cptD++, new HashMap<Integer, ArrayList<Node>>());
				//newNode.addTransition(map.getKey(), deterministe(i, etatsFinaux));
				
				
					
			}
		}
		
		for (Map.Entry m : map.entrySet()) {
			Node newNode = dejaCreer((HashSet<Node>)m.getValue(), nodesD); 
			if( newNode == null) {
				newNode = new Node(cptD++, new HashMap<Integer, ArrayList<Node>>());
				newNode.setEnsemble((HashSet<Node>)m.getValue());
				for(Node s:(HashSet<Node>)m.getValue()) {
					if (s.isFinal) {
						newNode.setFinal();
						break;
					}
				}
				nodesD.add(newNode);
				
				deterministe(newNode, nodesD);
			}
			ArrayList<Node> l = new ArrayList<>();
			l.add(newNode);
			n.addTransition((int) m.getKey(), newNode);
			
		}
		 return n;
	}
	
	private Node dejaCreer(HashSet<Node> en, ArrayList<Node> nd ) {
		for(Node n: nd) {
			if (n.nodes.equals(en)) {
				return n;
			}
		}
		return null;
	}
	
	// On transforme notre arbre en une matrice
	private void transAutoMatrice(Node node, int []visites) {
		
		for (Map.Entry m : node.transitions.entrySet()) {
			Node fils = ((ArrayList<Node>) m.getValue()).get(0);
			if(fils.isFinal) {
				statesFinal[fils.state] = 1;
			}
			matAutomate[node.state][(int) m.getKey()] = fils.state;
			if(visites[fils.state]==0) {
				visites[fils.state] = 1;
				transAutoMatrice(fils, visites);
			}
		}
		
	}
	
	
	
	
	
	
	public class Node {
		public int state;
		public Map<Integer, ArrayList<Node>> transitions;
		public boolean isFinal = false;
		
		public Set<Node> nodes = new HashSet<>();
		
		public String name;
		
		public Node(int state, Map<Integer, ArrayList<Node>> m ) {
			this.state = state;
			this.transitions = m;
		}
		
		public void addTransition(int s, ArrayList<Node> ns) {
			if(transitions.containsKey(s)) {
				transitions.put(s, ns);
			}
			
			
		}
		
		public void addTransition(int s, Node n) {
			if(transitions.containsKey(s)) {
				transitions.get(s).add(n);
			}
			else {
				ArrayList<Node> liste = new ArrayList<>();
				liste.add(n);
				transitions.put(s,liste);
			}
			
		}
		
		public void setFinal() {
			isFinal = true;
		}
		
		@Override
		public String toString() {
			return "etat "+this.state;
		}
		
		public void  setEnsemble(HashSet<Node> nodes) {
			this.nodes = nodes;
		}
		

		

	}
	
	public class AutoWithE {

		
		public Node finale ;
		public Node init ;
		
		
		public AutoWithE() {}
		private AutoWithE(Node init, Node finale) {
			this.init = init;
			this.finale = finale;
			
		}
		
		 public AutoWithE creation(RegExTree RT) {
			    if (RT.root==RegEx.CONCAT) return conc(creation(RT.subTrees.get(0)),creation(RT.subTrees.get(1)));
			    if (RT.root==RegEx.ETOILE) return etoile(creation(RT.subTrees.get(0)));
			    if (RT.root==RegEx.ALTERN) return altern(creation(RT.subTrees.get(0)),creation(RT.subTrees.get(1)));
			    return carac(RT.root);	 
		 }
		 
		 public AutoWithE carac(int c) {
			 
			 Node finaleNode = new Node(cpt++, new HashMap<>());
			 
			 Map<Integer, ArrayList<Node>> t = new HashMap<>();
			 ArrayList<Node> liste = new ArrayList<Node> ();
			 liste.add(finaleNode);
			 t.put(c, liste);
			 
			 Node newNode = new Node(cpt++, t);
			 
			 return new AutoWithE(newNode, finaleNode);
		 }
		 
		 public AutoWithE altern(AutoWithE a1, AutoWithE a2) {
			 Map<Integer, ArrayList<Node>> t = new HashMap<>();
			 
			 ArrayList<Node> liste = new ArrayList<Node> ();
			 liste.add(a1.init);
			 liste.add(a2.init);
			 t.put(0, liste);
			 
			 Node init = new Node(cpt++, t);
			 Node finale = new Node(cpt++, new HashMap<>());
			 
			 a1.finale.addTransition(0, finale);
			 a2.finale.addTransition(0, finale);
			 
			
			 return new AutoWithE(init, finale);
			 	 
		 }
		 
		 public AutoWithE conc(AutoWithE a1, AutoWithE a2) {
			 a1.finale.addTransition(0, a2.init);
			 return new AutoWithE (a1.init, a2.finale);
		 }
		 
		 public AutoWithE etoile (AutoWithE a) {
			 
			 Node finale = new Node(cpt++, new HashMap<>());
			 
			 a.finale.addTransition(0, a.init);
			 a.finale.addTransition(0, finale);
			 
			 Map<Integer, ArrayList<Node>> t = new HashMap<>();
			 
			 ArrayList<Node> liste = new ArrayList<Node> ();
			 liste.add(a.init);
			 liste.add(finale);
			 t.put(0, liste);
			 
			 Node init = new Node(cpt++, t);
			 return new AutoWithE(init, finale);
		 }		 
	}

}
