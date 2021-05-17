package intertwine_test.rig.exchange.object;

public final class AnycallRequest
{
	public final String signatureString;
	public final Object[] arguments;

	public AnycallRequest( String signatureString, Object[] arguments )
	{
		this.signatureString = signatureString;
		this.arguments = arguments;
	}

	@Override public String toString()
	{
		return "signatureString:" + signatureString;
	}
}
