package mikenakis.test.intertwine.programs;

import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;
import mikenakis.intertwine.implementations.methodhandle.MethodHandleIntertwineFactory;
import mikenakis.intertwine.implementations.reflecting.ReflectingIntertwineFactory;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;
import mikenakis.lambdatwine.AnyLambda;
import mikenakis.lambdatwine.Lambdatwine;
import mikenakis.lambdatwine.LambdatwineFactory;
import mikenakis.lambdatwine.implementations.methodhandle.MethodHandleLambdatwineFactory;
import mikenakis.lambdatwine.implementations.reflecting.ReflectingLambdatwineFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Test Program.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class P1IntertwineBenchmark
{
	public static void main( String[] commandLineArguments )
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();

		new P1IntertwineBenchmark( 1_000 ).run( new PrintStream( new NullOutputStream() ) );
		new P1IntertwineBenchmark( 100_000 ).run( System.out );
	}

	private static class NullOutputStream extends OutputStream
	{
		@Override public void write( int b ) throws IOException
		{
		}
	}

	private final int iterations;

	private P1IntertwineBenchmark( int iterations )
	{
		this.iterations = iterations;
	}

	private void run( PrintStream out )
	{
		for( int i = 0; i < 3; i++ )
		{
			runBenchmark( out, "Direct                   ", new DirectBenchmarkable()::run );
			runBenchmark( out, "Reflecting Intertwine    ", new ReflectingIntertwineBenchmarkable()::run );
			runBenchmark( out, "MethodHandle Intertwine  ", new MethodHandleIntertwineBenchmarkable()::run );
			runBenchmark( out, "Reflecting Lambdatwine   ", new ReflectingLambdatwineBenchmarkable()::run );
			runBenchmark( out, "MethodHandle Lambdatwine ", new MethodHandleLambdatwineBenchmarkable()::run );
			out.println();
		}
	}

	private void runBenchmark( PrintStream out, String prefix, Procedure0 runnable )
	{
		Kit.testing.runGarbageCollection();
		double t0 = timeSeconds();
		for( int i = 0; i < iterations; i++ )
			runnable.invoke();
		double d = timeSeconds() - t0;
		out.printf( "%s : %d iterations %8.4fs\n", prefix, iterations, d );
	}

	private static double timeSeconds()
	{
		return System.nanoTime() * 1e-9;
	}

	abstract static class MyBenchmarkable
	{
		final FooClient fooClient;

		MyBenchmarkable( Function1<Foo,Foo> makeFooFromFoo )
		{
			Foo foo = new FooServer();
			foo = makeFooFromFoo.invoke( foo );
			fooClient = new FooClient( foo );
		}

		public final void run()
		{
			fooClient.run();
		}
	}

	static class DirectBenchmarkable extends MyBenchmarkable
	{
		DirectBenchmarkable()
		{
			super( foo -> foo );
		}
	}

	static class IntertwineBenchmarkable extends MyBenchmarkable
	{
		IntertwineBenchmarkable( IntertwineFactory intertwineFactory )
		{
			super( foo ->
			{
				Intertwine<Foo> intertwine = intertwineFactory.getIntertwine( Foo.class );
				AnyCall<Foo> untwiner = intertwine.newUntwiner( foo );
				return intertwine.newEntwiner( untwiner );
			} );
		}
	}

	static class ReflectingIntertwineBenchmarkable extends IntertwineBenchmarkable
	{
		ReflectingIntertwineBenchmarkable()
		{
			super( ReflectingIntertwineFactory.instance );
		}
	}

	static class MethodHandleIntertwineBenchmarkable extends IntertwineBenchmarkable
	{
		MethodHandleIntertwineBenchmarkable()
		{
			super( MethodHandleIntertwineFactory.instance );
		}
	}

	abstract static class LambdatwineBenchmarkable extends MyBenchmarkable
	{
		LambdatwineBenchmarkable( LambdatwineFactory lambdatwineFactory )
		{
			super( foo ->
			{
				Lambdatwine<Foo> lambdatwiner = lambdatwineFactory.getLambdatwine( Foo.class );
				AnyLambda<Foo> untwiner = lambdatwiner.newUntwiner( foo );
				return lambdatwiner.newEntwiner( untwiner );
			} );
		}
	}

	static class ReflectingLambdatwineBenchmarkable extends LambdatwineBenchmarkable
	{
		ReflectingLambdatwineBenchmarkable()
		{
			super( new ReflectingLambdatwineFactory() );
		}
	}

	static class MethodHandleLambdatwineBenchmarkable extends LambdatwineBenchmarkable
	{
		MethodHandleLambdatwineBenchmarkable()
		{
			super( MethodHandleLambdatwineFactory.instance );
		}
	}

	public interface Foo
	{
		double fooMethod( int a, Double b );
	}

	static class FooServer implements Foo
	{
		@Override public double fooMethod( int a, Double b )
		{
			return a + b;
		}
	}

	static class FooClient
	{
		final Foo foo;
		public final double[] results = new double[1000];
		static final int A = 1;
		static final Double B = 2.0;

		FooClient( Foo foo )
		{
			this.foo = foo;
		}

		void run()
		{
			for( int i = 0; i < results.length; i++ )
				results[i] = foo.fooMethod( A, B );
		}
	}
}