package mikenakis.kit.lifetime.guard;

import mikenakis.kit.UncheckedException;
import mikenakis.kit.lifetime.Closeable;

public class MustBeAliveException extends UncheckedException
{
	public final Class<? extends Closeable> closeableClass;

	public MustBeAliveException( Class<? extends Closeable> closeableClass )
	{
		this.closeableClass = closeableClass;
	}

	public MustBeAliveException( Class<? extends Closeable> closeableClass, Throwable cause )
	{
		super( cause );
		this.closeableClass = closeableClass;
	}
}
