package io.github.mikenakis.kit.test;

import io.github.mikenakis.benchmark.Benchmark;
import io.github.mikenakis.benchmark.Benchmarkable;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.logging.Log;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Locale;

import static io.github.mikenakis.kit.Kit.math.π;

public class T01_Arithmetic
{
	public T01_Arithmetic()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private interface F
	{
		double invoke( double parameter );
	}

	private static double jdkMod1( double x )
	{
		return x % 1;
	}

	private static void checksOut( String name, double min, double max, double step, F function1, F function2, double tolerance )
	{
		int count = 0;
		for( double x = min; x < max; x += step )
		{
			double s1 = function1.invoke( x );
			double s2 = function2.invoke( x );
			assert Kit.math.eq( s1, s2, tolerance );
			count++;
		}
		System.out.printf( Locale.ROOT, "%s: min=%.1f, max=%.1f, step=%.4f (%d values)\n", name,
			min, max, step, count );
	}

	@Test
	public void Mod1_Checks_Out()
	{
		checksOut( "Kit.mod1()", -3, 3, 0.0001, T01_Arithmetic::jdkMod1, Kit.math::mod1, 1e-9 );
	}

	@Ignore @Test public void Fast_mod1_is_much_faster_than_jdk_mod1()
	{
		Benchmark benchmark = new Benchmark();
		double t1 = benchmark.measure( Benchmarkable.of( () -> (int)jdkMod1( π ) ) );
		double t2 = benchmark.measure( Benchmarkable.of( () -> (int)Kit.math.mod1( π ) ) );
		double r = t1 / t2;
		Log.info( String.format( Locale.ROOT, "Jdk %%: %f nanoseconds;  Kit.mod1(): %f nanoseconds (%.2f times faster)\n", //
			nanosecondsFromSeconds( t1 ), nanosecondsFromSeconds( t2 ), r ) );
		assert r > 2; //r has been observed to be as low as 2.89 on CircleCI
	}

	private static double nanosecondsFromSeconds( double seconds )
	{
		return seconds * 1e9;
	}
}
