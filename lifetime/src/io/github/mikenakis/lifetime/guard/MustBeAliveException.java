package io.github.mikenakis.lifetime.guard;

import io.github.mikenakis.kit.UncheckedException;
import io.github.mikenakis.lifetime.Mortal;

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
