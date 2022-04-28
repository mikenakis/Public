package mikenakis.immutability.helpers;

public abstract class Stringizable
{
	public final Stringizer stringizer;

	protected Stringizable( Stringizer stringizer )
	{
		this.stringizer = stringizer;
	}
}
