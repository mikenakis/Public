package mikenakis.tyraki.conversion;

import mikenakis.kit.Kit;
import mikenakis.tyraki.PartialConverter;
import mikenakis.tyraki.TotalConverter;
import mikenakis.tyraki.UnmodifiableCollection;

import java.util.Optional;

class ReversingConverter<T, F> implements PartialConverter<F,T>
{
	private final UnmodifiableCollection<F> collection;
	private final TotalConverter<? extends T,? super F> forwardConverter;

	ReversingConverter( UnmodifiableCollection<F> collection, TotalConverter<? extends T,? super F> forwardConverter )
	{
		this.collection = collection;
		this.forwardConverter = forwardConverter;
	}

	@Override public Optional<F> convert( T item )
	{
		assert item != null;
		for( F from : collection )
			if( Kit.equalByValue( item, forwardConverter.invoke( from ) ) )
				return Optional.of( from );
		return Optional.empty();
	}
}
