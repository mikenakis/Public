package mikenakis.test.intertwine;

import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;
import mikenakis.intertwine.implementations.caching.CachingIntertwineFactory;
import mikenakis.intertwine.implementations.compiling.CompilingIntertwineFactory;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public final class T05_Compiling extends Client
{
	private final IntertwineFactory intertwineFactory = new CachingIntertwineFactory( new CompilingIntertwineFactory( getClass().getClassLoader() ) );

	public T05_Compiling()
	{
	}

	@Override protected IntertwineFactory getIntertwineFactory()
	{
		return intertwineFactory;
	}

	@Test
	public void Test1()
	{
		FooInterface2 fooServer = new FooInterface2()
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
		Intertwine<FooInterface2> intertwine = intertwineFactory.getIntertwine( /*getClass().getClassLoader(),*/ FooInterface2.class );
		AnyCall<FooInterface2> anyCall = intertwine.newUntwiner( fooServer );
		FooInterface2 serverProxy = intertwine.newEntwiner( anyCall );
		assert serverProxy.intReturningMethod() == 0;
		assert serverProxy.stringReturningMethod( 5 ).equals( "5" );
		String result = serverProxy.theKitchenSink( (byte)1, new byte[] { 11 }, true, (short)2, 'A', 3, 4.0f, 5, 6.0, "B", new String[] { "BB" }, List.of( "C", "D" ) );
		assert result.equals( "1 [11] true 2 A 3 4.0 5 6.0 B [BB] [C, D]" );
	}
}
