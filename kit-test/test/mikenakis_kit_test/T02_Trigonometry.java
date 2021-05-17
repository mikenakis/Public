package mikenakis_kit_test;

import mikenakis.kit.Kit;
import mikenakis.benchmark.Benchmark;
import mikenakis.benchmark.BenchmarkMeasurement;
import mikenakis.kit.logging.Log;
import org.junit.Test;

import java.util.Locale;

import static mikenakis.kit.Kit.math.π;

public class T02_Trigonometry
{
	public T02_Trigonometry()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private interface F
	{
		double invoke( double parameter );
	}

	private static void checksOut( String name, double min, double max, double step, F function1, F function2, double tolerance )
	{
		double sum = 0.0;
		double maxError = 0.0;
		int count = 0;
		for( double x = min; x < max; x += step )
		{
			double s1 = function1.invoke( x );
			double s2 = function2.invoke( x );
			assert Kit.math.eq( s1, s2, tolerance );
			double error = s2 - s1;
			maxError = Math.max( maxError, Math.abs( error ) );
			sum += Kit.math.squared( error );
			count++;
		}
		double standardDeviation = Math.sqrt( sum / count );
		System.out.printf( Locale.ROOT, "%s: min=%.1f, max=%.1f, step=%.4f (%d values) max error = %.4f, standard deviation = %.4f\n", name,
			min, max, step, count, maxError, standardDeviation );
	}

	private static void checksOutShortRange( String name, F function1, F function2, double tolerance )
	{
		checksOut( name + " short range", SHORT_RANGE_MIN, SHORT_RANGE_MAX, SHORT_RANGE_STEP, function1, function2, tolerance );
	}

	private static void checksOutLongRange( String name, F function1, F function2, double tolerance )
	{
		checksOut( name + " long range", LONG_RANGE_MIN, LONG_RANGE_MAX, LONG_RANGE_STEP, function1, function2, tolerance );
	}

	private static final double SHORT_RANGE_MIN = -6.5;
	private static final double SHORT_RANGE_MAX = 6.5;
	private static final double SHORT_RANGE_STEP = 0.0001;

	private static final double LONG_RANGE_MIN = -1_000_000_007;
	private static final double LONG_RANGE_MAX = 1_000_000_007;
	private static final double LONG_RANGE_STEP = 1000.0001;

	@Test
	public void Sin_Checks_Out_Over_Short_Range()
	{
		checksOutShortRange( "sin()", Math::sin, Kit.math::sin, 1e-2 );
	}

	@Test
	public void Sin_Checks_Out_Over_Long_Range()
	{
		checksOutLongRange( "sin()", Math::sin, Kit.math::sin, 1e-2 );
	}

	@Test
	public void Sin2pi_Checks_Out_Over_Short_Range()
	{
		checksOutShortRange( "sin2pi()", x -> Math.sin( 2 * π * x ), Kit.math::sin2pi, 1e-2 );
	}

	@Test
	public void Sin2pi_Checks_Out_Over_Long_Range()
	{
		checksOutLongRange( "sin2pi()", x -> Math.sin( 2 * π * x ), Kit.math::sin2pi, 1e-2 );
	}

	@Test
	public void Cos_Checks_Out_Over_Short_Range()
	{
		checksOutShortRange( "cos()", Math::cos, Kit.math::cos, 1e-2 );
	}

	@Test
	public void Cos_Checks_Out_Over_Long_Range()
	{
		checksOutLongRange( "cos()", Math::cos, Kit.math::cos, 1e-2 );
	}

	@Test
	public void Cos2pi_Checks_Out_Over_Short_Range()
	{
		checksOutShortRange( "cos2pi()", x -> Math.cos( 2 * π * x ), Kit.math::cos2pi, 1e-2 );
	}

	@Test
	public void Cos2pi_Checks_Out_Over_Long_Range()
	{
		checksOutLongRange( "cos2pi()", x -> Math.cos( 2 * π * x ), Kit.math::cos2pi, 1e-2 );
	}

	@Test
	public void Kit_sin_is_considerably_faster_than_jdk_sin()
	{
		/* benchmark built-in sin() vs fast sin() */
		BenchmarkMeasurement dryRunMeasurement = BenchmarkMeasurement.of( ( int startIndex, int endIndex ) -> //
		{
			double result = startIndex;
			for( int i = startIndex; i < endIndex; i++ )
				result += i;
			return (int)result;
		} );
		BenchmarkMeasurement jdkSinMeasurement = BenchmarkMeasurement.of( ( int startIndex, int endIndex ) -> //
		{
			double result = startIndex;
			for( int i = startIndex; i < endIndex; i++ )
				result += Math.sin( i );
			return (int)result;
		} );
		BenchmarkMeasurement kitSinMeasurement = BenchmarkMeasurement.of( ( int startIndex, int endIndex ) -> //
		{
			double result = startIndex;
			for( int i = startIndex; i < endIndex; i++ )
				result += Kit.math.sin( i );
			return (int)result;
		} );
		Benchmark benchmark = new Benchmark( 1e-6, 1000 );
		double t0 = benchmark.measure( dryRunMeasurement );
		double t1 = benchmark.measure( jdkSinMeasurement ) - t0;
		double t2 = benchmark.measure( kitSinMeasurement ) - t0;
		double r = t1 / t2;
		Log.info( String.format( Locale.ROOT, "Math.sin(): %f nanoseconds;  Kit.sin(): %f nanoseconds (%.2f times faster)\n",
			t1 * 1e9, t2 * 1e9, r ) );
		assert r > 2;
//		double t0 = Kit.measureDuration( 10, T02_Trigonometry::exerciseNothing );
//		double t1 = Kit.measureDuration( 10, T02_Trigonometry::exerciseJdkSin ) - t0;
//		double t2 = Kit.measureDuration( 10, T02_Trigonometry::exerciseKitSin ) - t0;
//		double r = t1 / t2;
//		System.out.printf( Locale.ROOT, "Math.sin(): %.3f s;  Kit.sin(): %.3f s (%.2f times faster)\n", t1, t2, r );
//		assert r > 5;
	}
}
