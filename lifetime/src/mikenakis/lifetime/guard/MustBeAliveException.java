package mikenakis.lifetime.guard;

import mikenakis.kit.UncheckedException;
import mikenakis.lifetime.Mortal;

public class MustBeAliveException extends UncheckedException
{
	public final Class<? extends Mortal> mortalClass;

	public MustBeAliveException( Class<? extends Mortal> mortalClass )
	{
		this.mortalClass = mortalClass;
	}

	public MustBeAliveException( Class<? extends Mortal> mortalClass, Throwable cause )
	{
		super( cause );
		this.mortalClass = mortalClass;
	}
}