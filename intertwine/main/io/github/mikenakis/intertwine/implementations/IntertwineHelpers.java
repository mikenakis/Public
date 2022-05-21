package io.github.mikenakis.intertwine.implementations;

import io.github.mikenakis.kit.Kit;

import java.lang.reflect.Method;

public final class IntertwineHelpers
{
	public static final Method equalsMethod = Kit.unchecked( () -> Object.class.getDeclaredMethod( "equals", Object.class ) );
	public static final Method hashCodeMethod = Kit.unchecked( () -> Object.class.getDeclaredMethod( "hashCode" ) );
	public static final Method toStringMethod = Kit.unchecked( () -> Object.class.getDeclaredMethod( "toString" ) );

	private IntertwineHelpers()
	{
	}
}
