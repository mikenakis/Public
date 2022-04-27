package mikenakis.benchmark.util;

public class CircularQueueOfDouble
{
	private final double[] values;
	private int head;
	private int tail;

	public CircularQueueOfDouble( int capacity )
	{
		assert capacity > 0;
		values = new double[capacity + 1];
		head = 0;
		tail = 0;
	}

	public int size()
	{
		if( head >= tail )
			return head - tail;
		return values.length - tail + head;
	}

	public int capacity()
	{
		return values.length - 1;
	}

	public void enqueue( double value )
	{
		assert size() < capacity();
		values[head++] = value;
		if( head >= values.length )
			head = 0;
	}

	public double dequeue()
	{
		assert size() > 0;
		double value = values[tail++];
		if( tail >= values.length )
			tail = 0;
		return value;
	}

	public double[] toArray()
	{
		double[] array = new double[size()];
		int si = tail;
		for( int i = 0;  i < array.length;  i++ )
		{
			array[i] = values[si++];
			if( si >= values.length )
				si = 0;
		}
		return array;
	}

	public interface Procedure1Double
	{
		void invoke( double arg1 );
	}

	public void foreach( Procedure1Double consumer )
	{
		for( int si = tail;  si != head;  )
		{
			consumer.invoke( values[si] );
			si++;
			if( si >= values.length )
				si = 0;
		}
	}

	public interface DoubleFunction2Double
	{
		double invoke( double arg1, double arg2 );
	}

	public double reduce( double seed, DoubleFunction2Double consumer )
	{
		for( int si = tail;  si != head;  )
		{
			seed = consumer.invoke( seed, values[si] );
			si++;
			if( si >= values.length )
				si = 0;
		}
		return seed;
	}
}
