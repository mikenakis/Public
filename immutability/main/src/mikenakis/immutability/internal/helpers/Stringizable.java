package mikenakis.immutability.internal.helpers;

public abstract class Stringizable
{
	public final Stringizer stringizer;

	protected Stringizable( Stringizer stringizer )
	{
		this.stringizer = stringizer;
	}
}
