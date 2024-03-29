package io.github.mikenakis.intertwine.test.comparisons.programs;

import io.github.mikenakis.intertwine.Anycall;
import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.intertwine.IntertwineFactory;
import io.github.mikenakis.intertwine.implementations.compiling.CompilingIntertwineFactory;
import io.github.mikenakis.intertwine.test.comparisons.implementations.alternative.methodhandle.MethodHandleIntertwineFactory;
import io.github.mikenakis.intertwine.test.comparisons.implementations.alternative.reflecting.ReflectingIntertwineFactory;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.kit.functional.Procedure0;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Test Program.
 *
 * @author michael.gr
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
		@Override public void write( int b )
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
			runBenchmark( out, "Compiling Intertwine     ", new CompilingIntertwineBenchmarkable()::run );
			out.println();
		}
	}

	private void runBenchmark( PrintStream out, String prefix, Procedure0 runnable )
	{
		Kit.runGarbageCollection();
		double t0 = Kit.time.timeSeconds();
		for( int i = 0; i < iterations; i++ )
			runnable.invoke();
		double d = Kit.time.timeSeconds() - t0;
		out.printf( "%s : %d iterations %8.4fs\n", prefix, iterations, d );
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
				Anycall<Foo> untwiner = intertwine.newUntwiner( foo );
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

	static class CompilingIntertwineBenchmarkable extends IntertwineBenchmarkable
	{
		CompilingIntertwineBenchmarkable()
		{
			super( new CompilingIntertwineFactory() );
		}
	}

	static class MethodHandleIntertwineBenchmarkable extends IntertwineBenchmarkable
	{
		MethodHandleIntertwineBenchmarkable()
		{
			super( MethodHandleIntertwineFactory.instance );
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
