package mikenakis.test.intertwine;

import mikenakis.intertwine.Anycall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;
import mikenakis.intertwine.implementations.caching.CachingIntertwineFactory;
import mikenakis.intertwine.implementations.compiling.CompilingIntertwineFactory;
import mikenakis.kit.Kit;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * NOTE: the {@code new Runnable().run()} business is a trick for creating multiple local namespaces within a single java source file.
 * Each test function needs its own local namespace because it needs to declare multiple test interfaces which must all be public.
 * Without local namespaces all these interfaces would have to be given unique names manually, which would be not only cumbersome but also error-prone.
 */
public final class T01_CompilingIntertwine
{
	private final IntertwineFactory intertwineFactory = new CachingIntertwineFactory( new CompilingIntertwineFactory( getClass().getClassLoader() ) );

	public T01_CompilingIntertwine()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test public void Test1()
	{
		new Runnable()
		{
			public interface Alpha
			{
				int intReturningMethod();
				String theKitchenSink( byte a, byte[] aa, boolean b, short c, char d, int e, float f, long g, double h, String i, String[] ii, List<?> j );
			}

			private final Alpha implementation = new Alpha()
			{
				@Override public int intReturningMethod()
				{
					return 0;
				}
				@Override public String theKitchenSink( byte a, byte[] aa, boolean b, short c, char d, int e, float f, long g, double h, String i, String[] ii, List<?> j )
				{
					return a + " " + Arrays.toString( aa ) + " " + b + " " + c + " " + d + " " + e + " " + f + " " + g + " " + h + " " + i + " " + Arrays.toString( ii ) + " " + j;
				}
			};

			@Override public void run()
			{
				Intertwine<Alpha> intertwine = intertwineFactory.getIntertwine( /*getClass().getClassLoader(),*/ Alpha.class );
				Anycall<Alpha> untwiner = intertwine.newUntwiner( implementation );
				Alpha entwiner = intertwine.newEntwiner( untwiner );
				assert entwiner.intReturningMethod() == 0;
				String result = entwiner.theKitchenSink( (byte)1, new byte[] { 11 }, true, (short)2, 'A', 3, 4.0f, 5, 6.0, "B", new String[] { "BB" }, List.of( "C", "D" ) );
				assert result.equals( "1 [11] true 2 A 3 4.0 5 6.0 B [BB] [C, D]" );
			}
		}.run();
	}

	@Test public void Test2()
	{
		new Runnable()
		{
			public interface Alpha
			{
				int intReturningMethod();
				String theKitchenSink( byte a, byte[] aa, boolean b, short c, char d, int e, float f, long g, double h, String i, String[] ii, List<?> j );
			}

			public interface Bravo extends Alpha
			{
				String stringReturningMethod( int i );
			}

			private final Bravo implementation = new Bravo()
			{
				@Override public int intReturningMethod()
				{
					return 0;
				}
				@Override public String stringReturningMethod( int i )
				{
					return "" + i;
				}
				@Override public String theKitchenSink( byte a, byte[] aa, boolean b, short c, char d, int e, float f, long g, double h, String i, String[] ii, List<?> j )
				{
					return a + " " + Arrays.toString( aa ) + " " + b + " " + c + " " + d + " " + e + " " + f + " " + g + " " + h + " " + i + " " + Arrays.toString( ii ) + " " + j;
				}
			};

			@Override public void run()
			{
				Intertwine<Bravo> intertwine = intertwineFactory.getIntertwine( /*getClass().getClassLoader(),*/ Bravo.class );
				Anycall<Bravo> untwiner = intertwine.newUntwiner( implementation );
				Bravo entwiner = intertwine.newEntwiner( untwiner );
				assert entwiner.intReturningMethod() == 0;
				assert entwiner.stringReturningMethod( 5 ).equals( "5" );
				String result = entwiner.theKitchenSink( (byte)1, new byte[] { 11 }, true, (short)2, 'A', 3, 4.0f, 5, 6.0, "B", new String[] { "BB" }, List.of( "C", "D" ) );
				assert result.equals( "1 [11] true 2 A 3 4.0 5 6.0 B [BB] [C, D]" );
			}
		}.run();
	}
}
