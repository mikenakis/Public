package mikenakis.test.lambdatwine.rig.exchange.object;

public final class AnyLambdaResponse
{
	public static AnyLambdaResponse success( Object result )
	{
		return new AnyLambdaResponse( true, result );
	}

	public static AnyLambdaResponse failure( Throwable throwable )
	{
		return new AnyLambdaResponse( false, throwable );
	}

	public final boolean success;
	private final Object exceptionOrResult;

	private AnyLambdaResponse( boolean success, Object exceptionOrResult )
	{
		this.success = success;
		this.exceptionOrResult = exceptionOrResult;
	}

	public Object payload()
	{
		assert success;
		return exceptionOrResult;
	}

	public Throwable throwable()
	{
		assert !success;
		return (Throwable)exceptionOrResult;
	}

	@Override public String toString()
	{
		return "success: " + success + " payload: " + exceptionOrResult;
	}
}
