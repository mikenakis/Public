package lambdatwine_test;

import lambdatwine_test.rig.FooInterface;
import lambdatwine_test.rig.FooServer;
import lambdatwine_test.rig.exchange.ObjectExchange;
import lambdatwine_test.rig.exchange.object.AnyLambdaRequest;
import lambdatwine_test.rig.exchange.object.AnyLambdaResponse;
import lambdatwine_test.rig.exchange.object.AnyLambdaToObjectExchange;
import lambdatwine_test.rig.exchange.object.ObjectExchangeToAnyLambda;
import mikenakis.kit.Kit;
import mikenakis.lambdatwine.AnyLambda;
import mikenakis.lambdatwine.Lambdatwine;
import mikenakis.lambdatwine.LambdatwineFactory;
import mikenakis.lambdatwine.implementations.reflecting.ReflectingLambdatwineFactory;
import org.junit.Test;

import java.util.NoSuchElementException;

public abstract class Client
{
	protected Client()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	protected abstract LambdatwineFactory getLambdatwineFactory();

	private interface AnyLambdaFromUntwiner<T>
	{
		AnyLambda<T> invoke( AnyLambda<T> untwiner );
	}

	public interface NonFunctionalInterface
	{
		void a();
		void b();
	}

	@Test
	public void Non_Functional_Interface_Fails()
	{
		LambdatwineFactory lambdatwineFactory = getLambdatwineFactory();
		Kit.testing.expectException( AssertionError.class, () -> lambdatwineFactory.getLambdatwine( NonFunctionalInterface.class ) );
	}

	@Test
	public void Happy_Path_via_Lambdatwine_Works()
	{
		FooInterface fooServer = createFooServer( anyLambdaFromUntwinerDirect );
		ClientHelpers.runHappyPath( fooServer );
	}

	@Test
	public void Regular_Failure_via_Lambdatwine_Works()
	{
		FooInterface fooServer = createFooServer( anyLambdaFromUntwinerDirect );
		Kit.testing.expectException( NoSuchElementException.class, () -> fooServer.theMethod( -1, null ) );
	}

	@Test
	public void Happy_Path_via_ObjectExchange_Works()
	{
		FooInterface fooServer = createFooServer( anyLambdaFromUntwinerViaObjectExchange );
		ClientHelpers.runHappyPath( fooServer );
	}

	@Test
	public void Regular_Failure_via_ObjectExchange_Works()
	{
		FooInterface fooServer = createFooServer( anyLambdaFromUntwinerViaObjectExchange );
		Kit.testing.expectException( NoSuchElementException.class, () -> fooServer.theMethod( -1, null ) );
	}

	interface MyNonPublicInterface
	{
		@SuppressWarnings( "unused" )
		void invoke();
	}

	@Test
	public void Use_of_Non_Public_Interface_Fails()
	{
		LambdatwineFactory lambdatwineFactory = new ReflectingLambdatwineFactory();
		Kit.testing.expectException( IllegalAccessException.class, () -> lambdatwineFactory.getLambdatwine( MyNonPublicInterface.class ) );
	}

	private FooInterface createFooServer( AnyLambdaFromUntwiner<FooInterface> anyLambdaFromUntwiner )
	{
		FooInterface fooServer = new FooServer();
		AnyLambda<FooInterface> untwiner = newUntwiner( fooServer );
		AnyLambda<FooInterface> anyLambda = anyLambdaFromUntwiner.invoke( untwiner );
		return newEntwiner( anyLambda );
	}

	private AnyLambda<FooInterface> newUntwiner( FooInterface interfaceServer )
	{
		assert FooInterface.class.isAssignableFrom( interfaceServer.getClass() );
		Lambdatwine<FooInterface> lambdatwine = getLambdatwineFactory().getLambdatwine( FooInterface.class );
		return lambdatwine.newUntwiner( interfaceServer );
	}

	private FooInterface newEntwiner( AnyLambda<FooInterface> anyLambda )
	{
		Lambdatwine<FooInterface> lambdatwine = getLambdatwineFactory().getLambdatwine( FooInterface.class );
		FooInterface entwiner = lambdatwine.newEntwiner( anyLambda );
		assert FooInterface.class.isAssignableFrom( entwiner.getClass() );
		return entwiner;
	}

	private final AnyLambdaFromUntwiner<FooInterface> anyLambdaFromUntwinerDirect = //
		( AnyLambda<FooInterface> untwiner ) -> untwiner;

	private final AnyLambdaFromUntwiner<FooInterface> anyLambdaFromUntwinerViaObjectExchange = //
		( AnyLambda<FooInterface> untwiner ) ->
	{
		ObjectExchange<AnyLambdaResponse,AnyLambdaRequest> objectExchange = new ObjectExchangeToAnyLambda<>( untwiner );
		return new AnyLambdaToObjectExchange<>( objectExchange );
	};
}
