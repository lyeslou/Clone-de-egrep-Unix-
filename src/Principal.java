
public class Principal {

	public static void main(String[] args) throws Exception {
		
		
		String regEx = "Ba";
		
		// 0 pour Aho Ulman sinon KMP
		int quelAlgo = 1;
		
		
		if (quelAlgo == 0) {
			RegExTree ret = RegEx.parse(regEx);
			Automate auto = new Automate(ret);
			auto.search();
		}
		else {
			Kmp kmp = new Kmp();
			kmp.searchKmp(regEx);
		}
		
		
	}

}
