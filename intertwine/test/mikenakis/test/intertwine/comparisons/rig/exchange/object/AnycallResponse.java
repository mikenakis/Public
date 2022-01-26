package mikenakis.test.intertwine.comparisons.rig.exchange.object;

public final class AnycallResponse
{
	public static AnycallResponse success( Object result )
	{
		return new AnycallResponse( true, result );
	}

	public static AnycallResponse failure( Throwable throwable )
	{
		return new AnycallResponse( false, throwable );
	}

	public final boolean success;
	private final Object exceptionOrResult;

	private AnycallResponse( boolean success, Object exceptionOrResult )
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
