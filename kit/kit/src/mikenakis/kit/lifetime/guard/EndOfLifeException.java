package mikenakis.kit.lifetime.guard;

import mikenakis.kit.UncheckedException;
import mikenakis.kit.lifetime.Closeable;

public class EndOfLifeException extends UncheckedException
{
	public final Class<? extends Closeable> closeableClass;

	public EndOfLifeException( Class<? extends Closeable> closeableClass )
	{
		this.closeableClass = closeableClass;
	}
}
