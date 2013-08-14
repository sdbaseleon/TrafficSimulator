
import java.util.NoSuchElementException;

/**
 * QueueArray class created for Assignment 5
 */
public class QueueArray<T> implements Queue<T> 
{
		private T[] queue;
	   	private int front, rear, size, capacity;

		public QueueArray(int capacity) 
		{ 
			// this is the constructor	
			queue = (T[]) new Object[capacity];	
			front = rear = size = 0;
			if (size == 0)
				front=rear;	
			this.capacity = capacity;  
		}
		// return false if failed  
		public boolean offer(T item) 
		{
			if (size==capacity)
				return false;
			if (size==0)	    
				queue[rear] = item;	
			else 
			{	    
				rear = (rear+1)%capacity;	    
				queue[rear] = item;	
			}	
			size++;	
			return true;   
		}
		// throw NoSuchElementException if empty   
		public T remove() 
		{	
			if (size==0) 	   
				throw new NoSuchElementException();	
			size--;	
			T item = queue[front];	
			if (size!=0) 
				front = (front+1)%capacity;	
			return item;   
		}
		public T poll() 
		{	
			if (size==0) 
				return null;	
			size--;	
			T item = queue[front];
			if (size!=0) 
				front = (front+1)%capacity;
		
			return item;   
		}
		// return null if empty    
		public T peek() 
		{	
			if (size==0) return null;	
			return  queue[front];  
		}

		// throws  NoSuchElementException if empty   
		public T element() 
		{	
			if (size==0) throw new NoSuchElementException();	
			return  queue[front];    
		} 
                
                public int size(){
                    return size;
                }
		
		public boolean isEmpty()
		{
			if(size == 0)
				return true;
			return false;
			
		}

}
