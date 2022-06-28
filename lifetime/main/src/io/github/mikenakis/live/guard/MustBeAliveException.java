package io.github.mikenakis.live.guard;

import io.github.mikenakis.kit.exceptions.UncheckedException;
import io.github.mikenakis.live.Mortal;

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
