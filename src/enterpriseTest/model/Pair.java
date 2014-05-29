package enterpriseTest.model;


public class Pair<T> implements Comparable<Pair<T>>{

	public T key;
	public double value;
	
	
	public Pair(T key, double value) {
		this.key = key;
		this.value = value;
	}


	@Override
	public int compareTo(Pair<T> pair) {
		//  Return a negative integer, zero,
		// or a positive integer as this object
		// is less than, equal to, or greater
		// than the specified object. 
		
		return
			(value > pair.value) ?  1 :
			(value < pair.value) ? -1 : 0;
				
	}
}
