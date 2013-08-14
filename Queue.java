/**
 * Queue interface provided to create the queue for assignment 5
 */
public interface Queue<T> 
{
	boolean offer(T item); // return false if failed	
	T remove(); // throw NoSuchElementException if empty	
	T poll();   // return null if empty	
	T peek();   // return null if empty	
	T element();// throws  NoSuchElementException if empty

}
