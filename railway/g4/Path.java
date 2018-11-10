package railway.g4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.lang.Math;
// To access data classes.
import railway.sim.utils.*;

public class Path implements Comparable<Path>{
	public List<Integer> path = new ArrayList<Integer>(); // One Path
	public double min_distance = 0;
	public double max_distance = 0;
	public double distance = 0;
	public int from = 0;
	public int to = 0;

	// Store data about paths
	public Path()
	{
		
	}

	public Path(int from, int to, List<Integer> lst, double max, double min)
	{
		this.from = from;
		this.to = to;
		this.min_distance = min;
		this.max_distance = max;
		for (int i:lst){
			path.add(i);
		}
	}

	public void print(){
	    System.out.println();
	    System.out.printf("Path from %d to %d\n",from, to);
		for (int i: path){
			System.out.printf("%d ",i);
		}
		System.out.println(min_distance);
		System.out.println(max_distance);
		System.out.printf(" with distance %f\n",min_distance);
        System.out.println("====================");
	}
    
    // Easy to get minimum path
	public int compareTo(Path other)
	{
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than 
        // other and 0 if they are supposed to be equal
		if(this.distance < other.distance)
		{
			return -1;
		}
		else if(this.distance == other.distance)
		{
			return 0;
		}
		else
		{
			return 1;
		}
    }
}
