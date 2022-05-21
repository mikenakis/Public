package io.github.mikenakis.benchmark;

/**
 * Provides access to a piece of code which is to be benchmarked. See {@link Benchmarkable#invoke}
 */
public interface Benchmarkable
{
	/**
	 * Executes a piece of code which is to be benchmarked.
	 * <p>
	 * This function is supposed to execute the code under benchmark in a loop.  This allows the benchmarking framework to execute your code in batches
	 * whose duration is much larger than the granularity of the system clock, thus ensuring that the granularity of the system clock does not affect
	 * the measurements.
	 * <p>
	 * The number of iterations of the loop is controlled by a start index and an end index. What really matters is the difference between these two
	 * numbers, but the framework will supply a start index that varies so as to prevent the JITter from establishing any assumptions about your loop.
	 * The idea is that this should prevent recompilation, but I am not sure it actually has any effect.
	 * <p>
	 * The return value is ignored; you can compute something and return it in order to prevent the compiler from optimizing away your code. If you
	 * do not have an {@code int} handy to return, you can return the hash code of any object.
	 *
	 * @param startIndex the start index (inclusive) of the loop in which to execute the code under benchmark.
	 * @param endIndex   the end index (exclusive) of the loop in which to execute the code under benchmark.
	 *
	 * @return anything; it is ignored.
	 */
	int invoke( int startIndex, int endIndex );

	interface IntFunction0
	{
		int invoke();
	}

	static Benchmarkable of( IntFunction0 function )
	{
		return ( int startIndex, int endIndex ) -> //
		{
			int result = 0;
			for( int i = startIndex; i < endIndex; i++ )
				result += function.invoke();
			return result;
		};
	}
}
