package mikenakis.benchmark.util;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.DoubleFunction2Double;

/**
 * Maintains a rolling sequence of values and calculates their sum, average ('mean'), and standard deviation from the mean ('sigma').
 * By rolling sequence we mean that it has a certain capacity, and once the capacity is reached, old values are dropped from the calculations.
 */
public class HistoryOfDouble
{
	private final CircularQueueOfDouble queue;
	private double sum = 0;
	private double mean = 0;

	public HistoryOfDouble( int capacity )
	{
		queue = new CircularQueueOfDouble( capacity );
	}

	public void add( double value )
	{
		if( queue.size() == queue.capacity() )
		{
			double evictedValue = queue.dequeue();
			sum -= evictedValue;
		}

		queue.enqueue( value );
		sum += value;

		mean = sum / queue.size();
		assert integrityAssertion();
	}

	private boolean integrityAssertion()
	{
		double expectedSum = calculateSum();
		double expectedMean = calculateMean();
		assert Kit.math.eq( sum, expectedSum, 1e-6 ) : sum + ", " + expectedSum;
		assert Kit.math.eq( mean, expectedMean, 1e-6 ) : mean + ", " + expectedMean;
		return true;
	}

	public double sum()
	{
		return sum;
	}

	public double mean()
	{
		return mean;
	}

	private final DoubleFunction2Double calculateSumOfSquaredDifferences = ( s, v ) -> s + Kit.math.squared( v - mean );

	public double sigma()
	{
		double sumOfSquaredDifferences = queue.reduce( 0, calculateSumOfSquaredDifferences );
		double sigma = Math.sqrt( sumOfSquaredDifferences / queue.size() );
		assert Kit.math.eq( sigma, calculateSigma( queue.toArray() ), 1e-9 );
		return sigma;
	}

	private double calculateSum()
	{
		double sum = queue.reduce( 0, ( s, v ) -> s + v );
		assert Kit.math.eq( sum, calculateSum( queue.toArray() ), 1e-9 );
		return sum;
	}

	private double calculateMean()
	{
		double mean = calculateSum() / queue.size();
		assert Kit.math.eq( mean, calculateMean( queue.toArray() ), 1e-9 );
		return mean;
	}

	private static double calculateSum( double[] values )
	{
		double sum = 0;
		for( double x : values )
			sum += x;
		return sum;
	}

	private static double calculateMean( double[] values )
	{
		return calculateSum( values ) / values.length;
	}

	private static double calculateSigma( double[] values )
	{
		double mean = calculateMean( values );
		double sumOfSquaredDifferences = 0;
		for( double x : values )
			sumOfSquaredDifferences += Kit.math.squared( x - mean );
		return Math.sqrt( sumOfSquaredDifferences / values.length );
	}
}
