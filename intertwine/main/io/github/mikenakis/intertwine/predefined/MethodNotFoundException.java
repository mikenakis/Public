package io.github.mikenakis.intertwine.predefined;

import io.github.mikenakis.intertwine.Intertwine;
import io.github.mikenakis.kit.exceptions.UncheckedException;

import java.lang.reflect.Method;

/**
 * 'Method Not Found' {@link UncheckedException}.
 *
 * @author michael.gr
 */
public class MethodNotFoundException extends UncheckedException
{
	public final Intertwine<?> intertwine;
	public final Method method;

	public MethodNotFoundException( Intertwine<?> intertwine, Method method )
	{
		this.intertwine = intertwine;
		this.method = method;
	}
}
