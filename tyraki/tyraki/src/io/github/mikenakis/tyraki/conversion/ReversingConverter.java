package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.tyraki.UnmodifiableCollection;

import java.util.Optional;

class ReversingConverter<T, F> implements Function1<Optional<? extends F>,T>
{
	private final UnmodifiableCollection<F> collection;
	private final Function1<? extends T,? super F> forwardConverter;

	ReversingConverter( UnmodifiableCollection<F> collection, Function1<? extends T,? super F> forwardConverter )
	{
		this.collection = collection;
		this.forwardConverter = forwardConverter;
	}

	@Override public Optional<F> invoke( T item )
	{
		assert item != null;
		for( F from : collection )
			if( Kit.equalByValue( item, forwardConverter.invoke( from ) ) )
				return Optional.of( from );
		return Optional.empty();
	}
}
