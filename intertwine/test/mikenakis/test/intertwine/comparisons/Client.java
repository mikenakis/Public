package mikenakis.test.intertwine.comparisons;

import mikenakis.intertwine.Anycall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;
import mikenakis.kit.Kit;
import mikenakis.test.intertwine.comparisons.rig.FooInterface;
import mikenakis.test.intertwine.comparisons.rig.FooServer;
import mikenakis.test.intertwine.comparisons.rig.exchange.ObjectExchange;
import mikenakis.test.intertwine.comparisons.rig.exchange.object.AnycallRequest;
import mikenakis.test.intertwine.comparisons.rig.exchange.object.AnycallResponse;
import mikenakis.test.intertwine.comparisons.rig.exchange.object.AnycallToObjectExchange;
import mikenakis.test.intertwine.comparisons.rig.exchange.object.ObjectExchangeToAnycall;
import mikenakis.testkit.TestKit;
import org.junit.Test;

import java.util.NoSuchElementException;

public abstract class Client
{
	protected Client()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	protected abstract IntertwineFactory getIntertwineFactory();

	private interface AnycallFromUntwiner<T>
	{
		Anycall<T> invoke( Anycall<T> untwiner );
	}

	@Test
	public void Happy_Path_via_Intertwine_Works()
	{
		FooInterface fooServer = createFooServer( anycallFromUntwinerDirect );
		ClientHelpers.runHappyPath( fooServer );
	}

	@Test
	public void Failure_via_Intertwine_Works()
	{
		FooInterface fooServer = createFooServer( anycallFromUntwinerDirect );
		TestKit.expect( NoSuchElementException.class, () -> fooServer.getAlpha( -1 ) );
	}

	@Test
	public void Happy_Path_via_ObjectExchange_Works()
	{
		FooInterface fooServer = createFooServer( anycallFromUntwinerViaObjectExchange );
		ClientHelpers.runHappyPath( fooServer );
	}

	@Test
	public void Failure_via_ObjectExchange_Works()
	{
		FooInterface fooServer = createFooServer( anycallFromUntwinerViaObjectExchange );
		TestKit.expect( NoSuchElementException.class, () -> fooServer.getAlpha( -1 ) );
	}

	interface MyNonPublicInterface
	{
		@SuppressWarnings( "unused" )
		void invoke();
	}

	@Test
	public void Use_of_Non_Public_Interface_Fails()
	{
		IntertwineFactory intertwineFactory = getIntertwineFactory();
		TestKit.expect( IllegalAccessException.class, () -> intertwineFactory.getIntertwine( /*getClass().getClassLoader(),*/ MyNonPublicInterface.class ) );
	}

	private FooInterface createFooServer( AnycallFromUntwiner<FooInterface> anycallFromUntwiner )
	{
		FooInterface fooServer = new FooServer();
		Anycall<FooInterface> untwiner = newUntwiner( fooServer );
		Anycall<FooInterface> anycall = anycallFromUntwiner.invoke( untwiner );
		return newEntwiner( anycall );
	}

	private Anycall<FooInterface> newUntwiner( FooInterface interfaceServer )
	{
		Intertwine<FooInterface> intertwine = getIntertwineFactory().getIntertwine( /*getClass().getClassLoader(),*/ FooInterface.class );
		return intertwine.newUntwiner( interfaceServer );
	}

	private FooInterface newEntwiner( Anycall<FooInterface> anycall )
	{
		Intertwine<FooInterface> intertwine = getIntertwineFactory().getIntertwine( /*getClass().getClassLoader(),*/ FooInterface.class );
		return intertwine.newEntwiner( anycall );
	}

	private final AnycallFromUntwiner<FooInterface> anycallFromUntwinerDirect = //
		( Anycall<FooInterface> untwiner ) -> untwiner;

	private final AnycallFromUntwiner<FooInterface> anycallFromUntwinerViaObjectExchange = //
		( Anycall<FooInterface> untwiner ) ->
	{
		Intertwine<FooInterface> intertwine = getIntertwineFactory().getIntertwine( /*getClass().getClassLoader(),*/ FooInterface.class );
		ObjectExchange<AnycallResponse,AnycallRequest> objectExchange = new ObjectExchangeToAnycall<>( intertwine, untwiner );
		return new AnycallToObjectExchange<>( objectExchange );
	};
}
