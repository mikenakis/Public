package mikenakis.tyraki.conversion;

import mikenakis.tyraki.PartialConverter;

import java.util.Optional;

public final class ProhibitedConverter<T, F> implements PartialConverter<F,T>
{
	private static final PartialConverter<Object,Object> INSTANCE = new ProhibitedConverter<>();

	public static <T, F> PartialConverter<F,T> getInstance()
	{
		@SuppressWarnings( "unchecked" )
		PartialConverter<F,T> result = (PartialConverter<F,T>)INSTANCE;
		return result;
	}

	private ProhibitedConverter()
	{
	}

	@Override public Optional<F> convert( T item )
	{
		throw new AssertionError();
	}
}
