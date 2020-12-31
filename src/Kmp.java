
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Kmp {
	
	public int [] retenue ;
	public Kmp() {
		
	}
	
	private boolean search (String chaine, String text) {
		
			
		 	int i = 0;
		 	int j = 0;
	        while (i < text.length()) { 
	        	
	            if (chaine.charAt(j) == text.charAt(i)) { 
	                j++; 
	                i++; 
	                if (j == chaine.length()) return true;
	            } 
	            
	            else if (i < text.length() ) { 
	                
	                if (j != 0) 
	                    j = retenue[j - 1]; 
	                else
	                    i = i + 1; 
	            } 
	        } 
	        
	        return false;
		
	}
	
	public void calculerRetenue(String chaine) {
		
			retenue = new int[chaine.length()];
		 	int a = 0; 
	        int i = 1; 
	        retenue[0] = 0; 
	  
	       
	        while (i < chaine.length()) { 
	            if (chaine.charAt(i) == chaine.charAt(a)) { 
	                a++; 
	                retenue[i] = a; 
	                i++; 
	            } 
	            else 
	            { 
	                
	                if (a != 0) { 
	                    a = retenue[a - 1]; 
	                } 
	                else 
	                { 
	                    retenue[i] = a; 
	                    i++; 
	                } 
	            } 
	        } 
	    } 
		
	
	public void searchKmp(String chaine) {
		  try {
			  int cpt = 0;
	          Scanner scanner = new Scanner(new File("src/56667-0.txt"));
	          calculerRetenue(chaine);
	          while(scanner.hasNextLine())
	          {
	              String line = scanner.nextLine();
	              
	              if(search(chaine, line)) {
	            	  System.out.println(line);
	            	  cpt++;
	              }
	              
	          }
	          System.out.println("nombre total kmp :"+cpt);
	      } catch (FileNotFoundException e) {
	          e.printStackTrace();
	      }
	  }

}
