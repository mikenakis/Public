package mikenakis.test.intertwine;

import mikenakis.test.intertwine.rig.FooInterface;
import mikenakis.test.intertwine.rig.FooServer;
import mikenakis.test.intertwine.rig.exchange.ObjectExchange;
import mikenakis.test.intertwine.rig.exchange.object.AnycallRequest;
import mikenakis.test.intertwine.rig.exchange.object.AnycallResponse;
import mikenakis.test.intertwine.rig.exchange.object.AnycallToObjectExchange;
import mikenakis.test.intertwine.rig.exchange.object.ObjectExchangeToAnycall;
import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.intertwine.IntertwineFactory;
import mikenakis.kit.Kit;
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

	private interface AnyCallFromUntwiner<T>
	{
		AnyCall<T> invoke( AnyCall<T> untwiner );
	}

	@Test
	public void Happy_Path_via_Intertwine_Works()
	{
		FooInterface fooServer = createFooServer( anyCallFromUntwinerDirect );
		ClientHelpers.runHappyPath( fooServer );
	}

	@Test
	public void Failure_via_Intertwine_Works()
	{
		FooInterface fooServer = createFooServer( anyCallFromUntwinerDirect );
		Kit.testing.expectException( NoSuchElementException.class, () -> fooServer.getAlpha( -1 ) );
	}

	@Test
	public void Happy_Path_via_ObjectExchange_Works()
	{
		FooInterface fooServer = createFooServer( anyCallFromUntwinerViaObjectExchange );
		ClientHelpers.runHappyPath( fooServer );
	}

	@Test
	public void Failure_via_ObjectExchange_Works()
	{
		FooInterface fooServer = createFooServer( anyCallFromUntwinerViaObjectExchange );
		Kit.testing.expectException( NoSuchElementException.class, () -> fooServer.getAlpha( -1 ) );
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
		Kit.testing.expectException( IllegalAccessException.class, () -> intertwineFactory.getIntertwine( MyNonPublicInterface.class ) );
	}

	private FooInterface createFooServer( AnyCallFromUntwiner<FooInterface> anyCallFromUntwiner )
	{
		FooInterface fooServer = new FooServer();
		AnyCall<FooInterface> untwiner = newUntwiner( fooServer );
		AnyCall<FooInterface> anyCall = anyCallFromUntwiner.invoke( untwiner );
		return newEntwiner( anyCall );
	}

	private AnyCall<FooInterface> newUntwiner( FooInterface interfaceServer )
	{
		Intertwine<FooInterface> intertwine = getIntertwineFactory().getIntertwine( FooInterface.class );
		return intertwine.newUntwiner( interfaceServer );
	}

	private FooInterface newEntwiner( AnyCall<FooInterface> anyCall )
	{
		Intertwine<FooInterface> intertwine = getIntertwineFactory().getIntertwine( FooInterface.class );
		return intertwine.newEntwiner( anyCall );
	}

	private final AnyCallFromUntwiner<FooInterface> anyCallFromUntwinerDirect = //
		( AnyCall<FooInterface> untwiner ) -> untwiner;

	private final AnyCallFromUntwiner<FooInterface> anyCallFromUntwinerViaObjectExchange = //
		( AnyCall<FooInterface> untwiner ) ->
	{
		Intertwine<FooInterface> intertwine = getIntertwineFactory().getIntertwine( FooInterface.class );
		ObjectExchange<AnycallResponse,AnycallRequest> objectExchange = new ObjectExchangeToAnycall<>( intertwine, untwiner );
		return new AnycallToObjectExchange<>( objectExchange );
	};
}
