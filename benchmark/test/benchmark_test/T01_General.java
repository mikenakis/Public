package benchmark_test;

import mikenakis.kit.Kit;
import mikenakis.benchmark.Benchmark;
import mikenakis.benchmark.Benchmarkable;
import mikenakis.kit.logging.Log;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Locale;

import static mikenakis.kit.Kit.get;

public class T01_General
{
	public T01_General()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private final Benchmarkable integerAdditionMeasurement = ( startIndex, endIndex ) -> //
	{
		int a = get( 0 );
		for( int i = startIndex; i < endIndex; i++ )
			a += i;
		return a;
	};

	private final Benchmarkable integerSubtractionMeasurement = ( startIndex, endIndex ) -> //
	{
		int a = get( 0 );
		for( int i = startIndex; i < endIndex; i++ )
			a -= i;
		return a;
	};

	private final Benchmarkable integerMultiplicationMeasurement = ( startIndex, endIndex ) -> //
	{
		int a = get( 1 );
		for( int i = startIndex; i < endIndex; i++ )
			a *= i;
		return a;
	};

	private final Benchmarkable integerDivisionMeasurement = ( startIndex, endIndex ) -> //
	{
		int a = get( 1000 );
		for( int i = startIndex; i < endIndex; i++ )
			a += a / i;
		return a;
	};

	@Ignore //does not pass in continuous build.
	@Test public void T1_Integer_Arithmetic_Is_Measured_To_Perform_As_Expected()
	{
		Benchmark benchmark = new Benchmark( 1e-3, 100 );
		double addSeconds = benchmark.measure( integerAdditionMeasurement );
		double subSeconds = benchmark.measure( integerSubtractionMeasurement );
		double mulSeconds = benchmark.measure( integerMultiplicationMeasurement );
		double divSeconds = benchmark.measure( integerDivisionMeasurement );
		Log.info( String.format( Locale.ROOT, "Integer addition:       %.3f nanoseconds", addSeconds * 1e9 ) );
		Log.info( String.format( Locale.ROOT, "Integer subtraction:    %.3f nanoseconds", subSeconds * 1e9 ) );
		Log.info( String.format( Locale.ROOT, "Integer multiplication: %.3f nanoseconds", mulSeconds * 1e9 ) );
		Log.info( String.format( Locale.ROOT, "Integer division:       %.3f nanoseconds", divSeconds * 1e9 ) );

		// On my machine (HP ZBook with Intel i7-8750H CPU @ 2.20 GHz)
		// the clock resolution is 100 nanoseconds, the clock read overhead is 22.222 nanoseconds,
		// and the measurements are as follows:
		// Integer addition:       0.3 nanoseconds
		// Integer subtraction:    0.3 nanoseconds
		// Integer multiplication: 0.8 nanoseconds
		// Integer division:       7 nanoseconds

		assert Kit.math.eq( addSeconds, subSeconds,  addSeconds * 0.1 ); //addition and subtraction are equal within 10%
		assert Kit.math.eq( addSeconds * 3, mulSeconds, mulSeconds * 0.5 ); //multiplication is 3 times slower than addition, within 50%
		assert Kit.math.eq( mulSeconds * 10, divSeconds, divSeconds * 0.2 ); //division is 10 times slower than multiplication, within 20%
	}

	@Test @Ignore public void T1_Repeatability_Of_Integer_Arithmetic()
	{
		Benchmark benchmark = new Benchmark( 1e-3, 100 );
		double addSeconds = benchmark.measure( integerAdditionMeasurement );
		double subSeconds = benchmark.measure( integerSubtractionMeasurement );
		double mulSeconds = benchmark.measure( integerMultiplicationMeasurement );
		double divSeconds = benchmark.measure( integerDivisionMeasurement );
		int count = 1000;
		for( int i = 0; i < count; i++ )
		{
			Log.info( "Iteration " + i );
			double newAddSeconds = benchmark.measure( integerAdditionMeasurement );
			double newSubSeconds = benchmark.measure( integerSubtractionMeasurement );
			double newMulSeconds = benchmark.measure( integerMultiplicationMeasurement );
			double newDivSeconds = benchmark.measure( integerDivisionMeasurement );
			Log.info( String.format( Locale.ROOT, "Integer addition:       %.3f nanoseconds (%.3f%% error)", newAddSeconds * 1e9, error( addSeconds, newAddSeconds ) ) );
			Log.info( String.format( Locale.ROOT, "Integer subtraction:    %.3f nanoseconds (%.3f%% error)", newSubSeconds * 1e9, error( subSeconds, newSubSeconds ) ) );
			Log.info( String.format( Locale.ROOT, "Integer multiplication: %.3f nanoseconds (%.3f%% error)", newMulSeconds * 1e9, error( mulSeconds, newMulSeconds ) ) );
			Log.info( String.format( Locale.ROOT, "Integer division:       %.3f nanoseconds (%.3f%% error)", newDivSeconds * 1e9, error( divSeconds, newDivSeconds ) ) );
			if( get( false ) )
			{
				assert error( newAddSeconds, addSeconds ) < 10;
				assert error( newSubSeconds, subSeconds ) < 10;
				assert error( newMulSeconds, mulSeconds ) < 10;
				assert error( newDivSeconds, divSeconds ) < 10;
			}
		}
	}

	private static double error( double a, double b )
	{
		double d = Math.abs( a - b );
		return d * 100 / a;
	}
}
