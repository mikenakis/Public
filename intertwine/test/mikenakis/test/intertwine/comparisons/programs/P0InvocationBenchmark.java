package mikenakis.test.intertwine.comparisons.programs;

import mikenakis.benchmark.Benchmark;
import mikenakis.benchmark.Benchmarkable;
import mikenakis.kit.Kit;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Compares the relative speed of various types of invocations.
 * <p>
 * Initially from <a href=http://stackoverflow.com/a/19563000/773113>Stackoverflow: Faster alternatives to Java's reflection</a>
 * but heavily modified afterwards.
 * <p>
 * Some useful JVM flags while running this program and checking the results:
 * - -XX:+PrintGC
 * - - Shows when GC occurs. Apparently, there is no GC during most of the runs, except for the reflection run.
 * - - Actually, the reflection run probably suffers as much as it does precisely because it triggers GC.
 * - -XX:-BackgroundCompilation
 * - - Forces waiting for compilation instead of doing it in the background
 * - -Xbatch
 * - - Supposedly the same as -XX:-BackgroundCompilation.
 * - -XX:+PrintCompilation
 * - - Shows when methods are being compiled or recompiled. (Unfortunately, recompilation takes place during all runs.)
 * - -XX:-TieredCompilation
 * - - I think this essentially disables recompilation.
 * - -Djava.compiler=NONE
 * - - Disables compilation and recompilation.
 * - -Xcomp
 * - - Forces immediate compilation of each method and appears to prevent re-compilation. (But greatly slows down startup.)
 * <p>
 * Full list of options is here: https://www.oracle.com/java/technologies/javase/vmoptions-jsp.html
 * More discussion here: <a href="https://stackoverflow.com/a/15021568/773113">Stackoverflow: Can I force the JVM to natively compile a given method?</a>
 */
public final class P0InvocationBenchmark
{
	private P0InvocationBenchmark()
	{
	}

	public static void main( String... args )
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();

		Invokable invokableInstance = new Invokable()
		{
			@Override public double invoke( int arg1, double arg2 )
			{
				return arg1 * arg2;
			}
		};

		Map<String,Benchmarkable> benchmarkables = new LinkedHashMap<>();
		Kit.map.add( benchmarkables, "Static Direct", newBenchmarkMeasurement( staticDirectInvoker() ) );
		Kit.map.add( benchmarkables, "Static Reflection", newBenchmarkMeasurement( staticReflectionInvoker() ) );
		Kit.map.add( benchmarkables, "Static MethodHandle", newBenchmarkMeasurement( staticMethodHandleInvoker() ) );
		Kit.map.add( benchmarkables, "Static Lambda", newBenchmarkMeasurement( staticLambdaInvoker() ) );
		Kit.map.add( benchmarkables, "Instance Direct", newBenchmarkMeasurement( instanceDirectInvoker( invokableInstance ) ) );
		Kit.map.add( benchmarkables, "Instance Reflection", newBenchmarkMeasurement( instanceReflectionInvoker( invokableInstance ) ) );
		Kit.map.add( benchmarkables, "Instance MethodHandle", newBenchmarkMeasurement( instanceMethodHandleInvoker( invokableInstance ) ) );
		Kit.map.add( benchmarkables, "Instance Lambda", newBenchmarkMeasurement( instanceLambdaInvoker( invokableInstance ) ) );
		Kit.map.add( benchmarkables, "Dry Run", newBenchmarkMeasurement( nullInvoker() ) );

		Map<Duration,Map<Benchmarkable,Duration>> benchmarkDurationsToMeasurements = new LinkedHashMap<>();
		for( int i = 10; i <= 150; i += 10 )
		{
			var benchmarkDuration = Duration.ofMillis( i );
			System.out.printf( "%d ms benchmarks...\n", i );
			Map<Benchmarkable,Duration> measurement = benchmark( benchmarkables );
			Kit.map.add( benchmarkDurationsToMeasurements, benchmarkDuration, measurement );
		}

		System.out.printf( "%24s", " " );
		for( Duration benchmarkDuration : benchmarkDurationsToMeasurements.keySet() )
			System.out.printf( "%5d", benchmarkDuration.toMillis() );
		System.out.println();

		for( String name : benchmarkables.keySet() )
		{
			System.out.printf( "%24s", name );
			Benchmarkable benchmarkable = Kit.map.get( benchmarkables, name );
			for( Duration benchmarkDuration : benchmarkDurationsToMeasurements.keySet() )
			{
				Map<Benchmarkable,Duration> measurement = Kit.map.get( benchmarkDurationsToMeasurements, benchmarkDuration );
				Duration measuredDuration = Kit.map.get( measurement, benchmarkable );
				System.out.printf( "%5d", measuredDuration.toNanos() );
			}
			System.out.println();
		}
		System.out.println();
	}

	private static Map<Benchmarkable,Duration> benchmark( Map<String,Benchmarkable> benchmarkables )
	{
		Benchmark benchmark = new Benchmark();
		return benchmarkables.values().stream().collect( Collectors.toMap( b -> b, b -> Kit.time.durationFromSeconds( benchmark.measure( b ) ) ) );
		//		Map<Benchmarkable,Double> ratioMap = normalize( durationMap );
		//		int maxNameLength = benchmarkables.keySet().stream().mapToInt( s -> s.length() ).max().orElseThrow();
		//		for( Map.Entry<String,Benchmarkable> entry : benchmarkables.entrySet() )
		//		{
		//			String name = entry.getKey();
		//			Benchmarkable benchmarkable = entry.getValue();
		//			Duration duration1 = Kit.map.get( durationMap, benchmarkable );
		//			double ratio = Kit.map.get( ratioMap, benchmarkable );
		//			out.printf( Locale.ROOT, "%s%" + maxNameLength + "s: %10d ns -> %10d%%\n", prefix, name, duration1.toNanos(), (int)(ratio * 100) );
		//		}
	}

	private static Map<Benchmarkable,Double> normalize( Map<Benchmarkable,Duration> map )
	{
		Duration min = map.values().stream().min( Duration::compareTo ).orElseThrow();
		return map.entrySet().stream().collect( Collectors.toMap( e -> e.getKey(), e -> secondsFromDuration( e.getValue() ) / secondsFromDuration( min ) ) );
	}

	private static double secondsFromDuration( Duration duration )
	{
		return duration.toNanos() * 1e-9;
	}

	private interface Invokable
	{
		double invoke( int arg1, double arg2 );
	}

	private static double invokableMethod( int arg1, double arg2 ) //implements interface 'Invokable'
	{
		return arg1 * arg2;
	}

	private static Benchmarkable benchmarkable( Invoker invoker )
	{
		return ( startIndex, endIndex ) -> //
		{
			double arg2 = invoker.invokeTheInvokable( 1, 0.5 );
			for( int arg1 = startIndex + 1; arg1 < endIndex; arg1++ )
				arg2 += invoker.invokeTheInvokable( arg1, arg2 );
			return (int)arg2;
		};
	}

	private static Benchmarkable newBenchmarkMeasurement( Invoker invoker )
	{
		return benchmarkable( invoker );
	}

	private interface Invoker
	{
		double invokeTheInvokable( int arg1, double arg2 );
	}

	private static Invoker nullInvoker()
	{
		return ( int arg1, double arg2 ) -> 0;
	}

	private static Invoker staticDirectInvoker()
	{
		return P0InvocationBenchmark::invokableMethod;
	}

	private static Invoker staticReflectionInvoker()
	{
		Method method = Kit.unchecked( () -> P0InvocationBenchmark.class.getDeclaredMethod( "invokableMethod", int.class, double.class ) );
		return ( arg1, arg2 ) -> Kit.unchecked( () -> (double)method.invoke( null, arg1, arg2 ) );
	}

	private static Invoker staticMethodHandleInvoker()
	{
		MethodHandle methodHandle = Kit.unchecked( () -> //
		{
			Method method = P0InvocationBenchmark.class.getDeclaredMethod( "invokableMethod", int.class, double.class );
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			return lookup.unreflect( method );
		} );
		return ( arg1, arg2 ) -> Kit.invokeThrowableThrowingFunction( () -> (double)methodHandle.invokeExact( arg1, arg2 ) );
	}

	private static Invoker staticLambdaInvoker()
	{
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType( double.class, int.class, double.class );
		MethodHandle targetMethod = Kit.unchecked( () -> lookup.findStatic( P0InvocationBenchmark.class, "invokableMethod", methodType ) );
		CallSite callSite = Kit.unchecked( () -> LambdaMetafactory.metafactory( lookup, "invokeTheInvokable", MethodType.methodType( Invoker.class ), methodType, targetMethod, methodType ) );
		MethodHandle methodHandle = callSite.getTarget();
		return (Invoker)Kit.invokeThrowableThrowingFunction( () -> methodHandle.invoke() );
	}

	private static Invoker instanceDirectInvoker( Invokable invokable )
	{
		return invokable::invoke;
	}

	private static Invoker instanceReflectionInvoker( Invokable invokable )
	{
		Method method = Kit.unchecked( () -> invokable.getClass().getDeclaredMethod( "invoke", int.class, double.class ) );
		return ( arg1, arg2 ) -> Kit.unchecked( () -> (double)method.invoke( invokable, arg1, arg2 ) );
	}

	private static Invoker instanceMethodHandleInvoker( Invokable invokable )
	{
		MethodHandle methodHandle = Kit.unchecked( () -> //
		{
			Method method = Invokable.class.getDeclaredMethod( "invoke", int.class, double.class );
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			return lookup.unreflect( method );
		} );
		return ( arg1, arg2 ) -> Kit.invokeThrowableThrowingFunction( () -> (double)methodHandle.invokeExact( invokable, arg1, arg2 ) );
	}

	private static Invoker instanceLambdaInvoker( Invokable invokable )
	{
		// source: https://stackoverflow.com/a/27605965/773113
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType( double.class, int.class, double.class );
		MethodHandle targetMethod = Kit.unchecked( () -> lookup.findVirtual( Invokable.class, "invoke", methodType ) );
		CallSite callSite = Kit.unchecked( () -> LambdaMetafactory.metafactory( lookup, "invokeTheInvokable", MethodType.methodType( Invoker.class, Invokable.class ), methodType, targetMethod, methodType ) );
		MethodHandle methodHandle = callSite.getTarget().bindTo( invokable );
		return (Invoker)Kit.invokeThrowableThrowingFunction( () -> methodHandle.invoke() );
	}
}
