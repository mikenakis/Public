package io.github.mikenakis.tyraki;

import io.github.mikenakis.kit.Hasher;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * An {@link Hasher} which uses {@link System#identityHashCode(Object)} to hash objects.
 *
 * @author michael.gr
 */
public final class IdentityHasher<T> implements Hasher<T>
{
	private static final Hasher<?> INSTANCE = new IdentityHasher<>();

	public static <T> Hasher<T> getInstance()
	{
		@SuppressWarnings( "unchecked" )
		Hasher<T> result = (Hasher<T>)INSTANCE;
		return result;
	}

	private IdentityHasher()
	{
	}

	@Override public int getHashCode( T item )
	{
		return System.identityHashCode( item );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
