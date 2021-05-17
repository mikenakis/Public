package mikenakis.testana.testplan.intent;

/**
 * Represents an intention of what to do with a test class.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class Intent
{
	public abstract boolean isToRun();
	public abstract String getMessage();

	@Override public String toString()
	{
		return getMessage();
	}

	@Override public final boolean equals( Object other )
	{
		if( other instanceof Intent )
			return equals( (Intent)other );
		return false;
	}

	public abstract boolean equals( Intent other );
}
