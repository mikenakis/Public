package mikenakis.benchmark;

import mikenakis.benchmark.util.HistoryOfDouble;
import mikenakis.kit.functional.DoubleFunction0;
import mikenakis.kit.Kit;
import mikenakis.kit.ref.DoubleRef;
import mikenakis.kit.logging.Log;
import mikenakis.kit.ref.IntRef;
import mikenakis.testkit.TestKit;

import java.util.Locale;

import static mikenakis.kit.Kit.get;

/**
 * <p>A micro-benchmarking facility.</p>
 * <ul>
 *   <li>Incredibly accurate
 *      <ul>
 *          <li>Accepts the desired level of accuracy as a parameter, and reliably meets this accuracy.</li>
 *          <li>Repeatability is not perfect, but extremely high when compared to what is usually expected when benchmarking code running in the Java Virtual
 *          Machine. (Which is a notoriously inaccurate business.)</li>
 *      </ul>
 *   </li>
 *   <li>Incredibly efficient
 *       <ul>
 *           <li>Measures the performance of extremely fast operations such as integer addition with 0.1% accuracy in less than a millisecond.</li>
 *       </ul>
 *   </li>
 *   <li>Incredibly compact
 *       <ul>
 *           <li>Only ~20 kilobytes as opposed to the hundreds of kilobytes of other frameworks<small><sup>*1</sup></small></li>
 *           <li>Only one dependency, to {@code mikenakis.kit}, which is ~100 kilobytes, as opposed to megabytes of dependencies of other frameworks<small><sup>*1</sup></small></li>
 *       </ul>
 *   </li>
 *   <li>Incredibly easy to use
 *      <ul>
 *          <li>No need for special JVM arguments</li>
 *          <li>No need to create a special benchmarking project</li>
 *          <li>No need to write benchmarks according to a particular structure</li>
 *          <li>No annotations to memorize</li>
 *          <li>All you need to do is:
 *              <ul>
 *                  <li>Construct a {@link Benchmark} object with parameters that define the accuracy of the measurements.</li>
 *                  <li>Wrap the code that you want to benchmark inside a loop.
 *                  <li>Put that loop in a lambda.</li>
 *                  <li>Pass the lambda to a method of {@link Benchmark}.</li>
 *              </ul>
 *              and you have a measurement which is as accurate as you can reasonably expect it to be.
 *          </li>
 *      </ul>
 *   </li>
 * </ul>
 * <p>The intention is to be able to write very short and very fast benchmarks which run as part of the tests, to ensure that a certain piece of code:</p>
 * <ul>
 *     <li>meets a certain performance requirement, or</li>
 *     <li>runs faster than another piece of code by at least a certain factor.</li>
 * </ul>
 * <p>This way, you can have tests that fail if a change to a performance-critical piece of code inadvertently degrades its performance.<p>
 * <p>The code to be benchmarked is supplied by means of the {@link Benchmarkable} functional interface.<p>
 * <hr/>
 * <ul>
 * <p>(*1) Other frameworks:</p>
 *     <ul>
 *         <li>JMH</li>
 *         <li>Google Caliper</li>
 *     </ul>
 * </ul>
 */
public class Benchmark
{
	private static final double clockReadOverheadNanoseconds;
	private static final long clockResolutionNanoseconds;
	private static final double clockGranularityNanoseconds;
	static
	{
		clockReadOverheadNanoseconds = measureClockReadOverheadNanoseconds();
		clockResolutionNanoseconds = measureClockResolutionNanoseconds();
		Log.info( String.format( Locale.ROOT, "Clock resolution: %d nanoseconds; read overhead: %.3f nanoseconds.", //
			clockResolutionNanoseconds, clockReadOverheadNanoseconds ) );
		/**
		 * Note: we compute the granularity of the clock as the maximum between the clock resolution and the clock read overhead.
		 * I do not have access to a machine on which the clock read overhead is larger than the clock resolution, so this path has not been tested.
		 */
		clockGranularityNanoseconds = Math.max( clockResolutionNanoseconds, clockReadOverheadNanoseconds );
	}

	/**
	 * This is the ideal batch duration. It is chosen so that the granularity of the clock does not affect the measurement.
	 */
	private static final double idealBatchDurationNanoseconds = clockGranularityNanoseconds * 10000;
	private final double accuracy;
	private final int sampleGroupSize;
	private final Unpredictability unpredictability = new Unpredictability();

	/**
	 * Initializes a new instance of {@link Benchmark} with some reasonable defaults for the parameters.
	 */
	public Benchmark()
	{
		this( 0.001, 100 );
	}

	/**
	 * Initializes a new instance of {@link Benchmark} with given parameters.
	 *
	 * @param accuracy        the accuracy of the measurement. For example, 0.01 will give an accuracy of one percent.
	 * @param sampleGroupSize the number of consecutive measurements that must meet the required accuracy before the measurement is considered conclusive.
	 */
	public Benchmark( double accuracy, int sampleGroupSize )
	{
		this.accuracy = accuracy;
		this.sampleGroupSize = sampleGroupSize;
	}

	/**
	 * Performs a measurement.
	 *
	 * @param benchmarkable the code to measure.
	 *
	 * @return the amount of time, in seconds, that it takes to execute the given piece of code.
	 */
	public double measure( Benchmarkable benchmarkable )
	{
		return measureNanoseconds( benchmarkable ) * 1e-9;
	}

	private double measureNanoseconds( Benchmarkable benchmarkable )
	{
		/**
		 * We begin with a batch length of 1, and we perform the following steps:
		 * - we measure one batch and obtain a duration,
		 * - we calculate a new batch length so that the duration of the next batch will be close to the ideal duration,
		 * - we repeat the above until the minimum duration we have seen so far converges to a stable value.
		 */
		IntRef batchLengthRef = new IntRef( 1 );
		DoubleRef minimumDurationNanosecondsRef = new DoubleRef( Double.MAX_VALUE );
		//Log.debug( "calculateBatchLength()..." );
		TestKit.runGarbageCollection();
		converge( accuracy, sampleGroupSize, () -> //
		{
			double durationNanoseconds = measureOneBatchNanoseconds( benchmarkable, batchLengthRef.value, unpredictability );
			//Log.debug( String.format( Locale.ROOT, "    converging: %d iterations per batch -> %f nanoseconds per iteration", batchLengthRef.value, durationNanoseconds ) );
			batchLengthRef.value = calculateBatchLengthForIdealBatchDuration( durationNanoseconds );
			minimumDurationNanosecondsRef.value = Math.min( minimumDurationNanosecondsRef.value, durationNanoseconds );
			return minimumDurationNanosecondsRef.value;
		} );
		/**
		 * the convergence algorithm returns the mean, but since we are calculating minimums, we can just return the minimum that we have
		 * already calculated, it will be slightly more accurate than the mean.
		 */
		return minimumDurationNanosecondsRef.value;
	}

	private static int index = 0;
	private static final int[] multipliers = { 123, 1234, 12345 };

	private static int calculateBatchLengthForIdealBatchDuration( double durationNanoseconds )
	{
		assert durationNanoseconds > 0;
		int multiplier = multipliers[index++ % multipliers.length];
		double ratio = Math.min( Integer.MAX_VALUE, clockGranularityNanoseconds * multiplier / durationNanoseconds );
		return (int)Math.ceil( ratio );
	}

	private static double measureOneBatchNanoseconds( Benchmarkable benchmarkable, int batchLength, Unpredictability unpredictability )
	{
		assert batchLength > 0;
		long elapsedNanoseconds;
		int result;
		for( ;; )
		{
			int startIndex = unpredictability.nextValue();
			int endIndex = startIndex + batchLength;
			long startNanoseconds = System.nanoTime();
			result = benchmarkable.invoke( startIndex, endIndex );
			long endNanoSeconds = System.nanoTime();
			elapsedNanoseconds = endNanoSeconds - startNanoseconds;
			if( elapsedNanoseconds > 0 )
				break;
		}
		assert elapsedNanoseconds > 0;
		get( result );
		//Log.trace( String.format( Locale.ROOT, "%f", elapsedNanoseconds ) );
		return elapsedNanoseconds / (double)batchLength;
	}

	private static void getClockReadingsNanoseconds( long[] readingsNanoseconds )
	{
		assert readingsNanoseconds.length > 1;
		long t0 = System.nanoTime();
		long t1 = System.nanoTime();
		while( t1 == t0 )
			t1 = System.nanoTime();
		readingsNanoseconds[0] = t1;
		for( int i = 1; i < readingsNanoseconds.length; i++ )
			readingsNanoseconds[i] = System.nanoTime();
	}

	/**
	 * Repeatedly invokes a supplied {@link DoubleFunction0} to calculate a value until it converges to a stable result.
	 * Convergence is achieved when the the last n values have a deviation from the mean that is below m units.
	 *
	 * @param m              the maximum deviation from the mean, relative to the mean; must be greater than zero.
	 *                       (e.g. m = 0.01 yields a precision of 1%.)
	 * @param n              the number of recent values to check; must be 2 or more.
	 * @param doubleFunction the function to invoke to obtain values.
	 *
	 * @return the mean of the last n values returned by the supplied {@link DoubleFunction0}.
	 */
	private static double converge( double m, int n, DoubleFunction0 doubleFunction )
	{
		assert m > 0;
		assert n >= 2;
		HistoryOfDouble history = new HistoryOfDouble( n );
		for( int i = 0; i < n; i++ )
			history.add( doubleFunction.invoke() );
		while( history.sigma() > Math.abs( history.mean() * m ) )
			history.add( doubleFunction.invoke() );
		return history.mean();
	}

	private static double measureClockReadOverheadNanoseconds()
	{
		/**
		 * On my machine, the overhead seems to be ~22 nanoseconds.
		 */
		TestKit.runGarbageCollection();
		long[] readings = new long[100];
		DoubleRef minimumOverheadNanosecondsRef = new DoubleRef( Double.POSITIVE_INFINITY );
		converge( 0.001, 1000, () -> //
		{
			getClockReadingsNanoseconds( readings );
			double thisClockOverheadNanoseconds = (readings[readings.length - 1] - readings[0]) / (double)(readings.length - 1);
			minimumOverheadNanosecondsRef.value = Math.min( minimumOverheadNanosecondsRef.value, thisClockOverheadNanoseconds );
			return minimumOverheadNanosecondsRef.value;
		} );
		/* the convergence algorithm returns the mean, but since we are calculating minimums, we can just return the minimum that we have
		   already calculated, it will be slightly more accurate than the mean. */
		return minimumOverheadNanosecondsRef.value;
	}

	private static long measureClockResolutionNanoseconds()
	{
		/**
		 * On my machine, repeatedly reading the clock and calculating the difference from the previous reading yields:
		 *     - the value 100
		 *     - a few consecutive zeros
		 *     - then 100 again
		 *     - and so on.
		 * This means that:
		 *     - most times System.nanoTime() does not advance
		 *     - when it does advance, it is by a whole 100 nanoseconds.
		 * Usually the number of consecutive zero readings is 4, sometimes it is 5.
		 * This is in agreement with the finding that reading the clock on my machine has an overhead of between 20 and 25 nanoseconds:
		 *     - the clock advances every 100 nanoseconds
		 *     - we read it either 4 or 5 times between each time it advances
		 *     - so we read no change either 3 out of 4 times, or 4 out of 5 times.
		 * On rare occasions there are abnormalities:
		 *     - the non-zero value read may be 200 or higher instead of 100
		 *     - the number of consecutive zeros may be as little as one.
		 * These abnormalities mean that every once in a while we miss some readings. This can be due to:
		 *     - the task scheduler of the operating system suspends our measuring thread to run some other thread
		 *     - the JVM decides to do something else inside our measuring thread.
		 * The goal of the following algorithm is to find the clock resolution.
		 */
		long[] readings = new long[1000];
		getClockReadingsNanoseconds( readings );
		long resolution = Long.MAX_VALUE;
		for( int i = 1; i < readings.length; i++ )
		{
			long thisResolution = readings[i] - readings[i - 1];
			if( thisResolution == 0 )
				continue;
			assert thisResolution > 0;
			resolution = Math.min( resolution, thisResolution );
		}
		assert resolution != Long.MAX_VALUE;
		return resolution;
	}

	private static class Unpredictability
	{
		private static final int mask = ((1 << 10) - 1);
		private int value = 0;

		int nextValue()
		{
			value = (value + 1) & mask;
			return 1000 + value;
		}
	}

	/**
	 * Keep this function for reference; it is from the google caliper project.
	 * It performs 9000 calls to System.nanoTime().
	 * Multiple successive invocations return different results, so it is unreliable.
	 * No matter how many times I call it, it returns values ranging from 24 nanoseconds to 44 nanoseconds, all of which are bogus,
	 * since the granularity of the clock on my machine is known to be 100 ns.
	 * Perhaps they have a different definition of "granularity".
	 */
	//	@SuppressWarnings( "unused" ) static void reportGoogleNanoTimeGranularity()
	//	{
	//		final int TRIALS = 1000;
	//		long total = 0L;
	//		for( int i = 0; i < TRIALS; i++ )
	//		{
	//			long first = System.nanoTime();
	//			long second = System.nanoTime();
	//			long third = System.nanoTime();
	//			long fourth = System.nanoTime();
	//			long fifth = System.nanoTime();
	//			long sixth = System.nanoTime();
	//			long seventh = System.nanoTime();
	//			long eighth = System.nanoTime();
	//			long ninth = System.nanoTime();
	//			total += second - first;
	//			total += third - second;
	//			total += fourth - third;
	//			total += fifth - fourth;
	//			total += sixth - fifth;
	//			total += seventh - sixth;
	//			total += eighth - seventh;
	//			total += ninth - eighth;
	//		}
	//		long divisor = TRIALS * 8;
	//		long div = total / divisor;
	//		long rem = total - divisor * div;
	//		long nanos;
	//		if( rem == 0L )
	//			nanos = div;
	//		else
	//		{
	//			int signum = 1 | (int)((total ^ divisor) >> 63);
	//			nanos = signum > 0 ? div + (long)signum : div;
	//		}
	//		Log.info( "Google says system clock granularity is " + nanos + " nanoseconds" );
	//	}
}
