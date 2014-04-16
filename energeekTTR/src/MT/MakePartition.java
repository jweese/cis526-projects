package MT;

import java.util.Random;

public class MakePartition {
	
	int[] make_xval_partition(int n, int n_folds){
		int Arraysize = n;
		int []Arranged = new int[n];
		int []part = new int[n];
		for(int j = 0; j < n;j++){
			Arranged[j] = 0;
			part[j] = 0;
		}
		int fold = 1;
		Random random = new Random();
		int index;
		while(Arraysize>0){
			
		    index  = random.nextInt(Arraysize);
		
		    if (fold > n_folds){
		        fold = 1;
		    }
		    int indextemp = -1;
		    int i;
		    for (i =0; i < n;i++){
		        
		        if(Arranged[i] == 0){
		            indextemp = indextemp + 1;
		        }
		        if (indextemp == index)
		            break;
		        
		    }
		    if(i <n){
		    part[i] = fold;
		    Arranged[i] = 1;
		    }
		    else if(i == n){
		    	part[i-1] = fold;
			    Arranged[i-1] = 1;
		    }
		    Arraysize = Arraysize - 1;
		    fold = fold + 1;
	}
		return part;
	}

}
