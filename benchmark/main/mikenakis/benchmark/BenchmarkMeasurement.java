package mikenakis.benchmark;

import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.IntFunction0;

import java.util.Objects;

/**
 * Combines a {@link Benchmarkable} and its dry run in one convenient structure.
 */
public class BenchmarkMeasurement
{
	/**
	 * Creates a {@link BenchmarkMeasurement} for a given {@link Benchmarkable} and a given dry-run {@link Benchmarkable}.
	 *
	 * @param benchmarkable the {@link Benchmarkable} to measure.
	 * @param dryRun        the dry-run {@link Benchmarkable}.
	 *
	 * @return a new {@link BenchmarkMeasurement}.
	 */
	public static BenchmarkMeasurement of( Benchmarkable benchmarkable, Benchmarkable dryRun )
	{
		return new BenchmarkMeasurement( benchmarkable, dryRun );
	}

	/**
	 * Creates a {@link BenchmarkMeasurement} for a given {@link Benchmarkable} without a dry-run.
	 *
	 * @param benchmarkable the {@link Benchmarkable} to measure.
	 *
	 * @return a new {@link BenchmarkMeasurement}.
	 */
	public static BenchmarkMeasurement of( Benchmarkable benchmarkable )
	{
		return new BenchmarkMeasurement( benchmarkable, null );
	}

	/**
	 * Creates a {@link BenchmarkMeasurement} for a given {@link Function0} and a given dry-run {@link Function0}.
	 *
	 * @param function the {@link Function0} to measure
	 * @param dryRun the dry-run {@link Function0}.
	 *
	 * @return a new {@link BenchmarkMeasurement}.
	 */
	public static BenchmarkMeasurement of( IntFunction0 function, IntFunction0 dryRun )
	{
		return new BenchmarkMeasurement( benchmarkable( function ), benchmarkable( dryRun ) );
	}

	/**
	 * Creates a {@link BenchmarkMeasurement} for a given {@link Function0} without a dry-run.
	 *
	 * @param function the {@link Function0} to measure
	 *
	 * @return a new {@link BenchmarkMeasurement}.
	 */
	public static BenchmarkMeasurement of( IntFunction0 function )
	{
		return new BenchmarkMeasurement( benchmarkable( function ), null );
	}

	/**
	 * Helper that wraps a {@link Function0} in a {@link Benchmarkable}.
	 *
	 * @param function the {@link Function0} to benchmark.
	 *
	 * @return a {@link Benchmarkable} that can be used for benchmarking.
	 */
	public static Benchmarkable benchmarkable( IntFunction0 function )
	{
		return ( int startIndex, int endIndex ) -> //
		{
			int result = 0;
			for( int i = startIndex; i < endIndex; i++ )
				result += function.invoke();
			return result;
		};
	}

	public final Benchmarkable benchmarkable;
	public final Benchmarkable dryRun;

	public BenchmarkMeasurement( Benchmarkable benchmarkable, Benchmarkable dryRun )
	{
		this.benchmarkable = benchmarkable;
		this.dryRun = dryRun;
	}

	@Override public boolean equals( Object other )
	{
		if( this == other )
			return true;
		if( other == null || getClass() != other.getClass() )
			return false;
		return equals( (BenchmarkMeasurement)other );
	}

	public boolean equals( BenchmarkMeasurement other )
	{
		return benchmarkable.equals( other.benchmarkable ) && dryRun.equals( other.dryRun );
	}

	@Override public int hashCode()
	{
		return Objects.hash( benchmarkable, dryRun );
	}
}
