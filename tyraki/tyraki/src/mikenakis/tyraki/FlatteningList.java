package mikenakis.tyraki;

import mikenakis.kit.functional.Function1;

class FlatteningList<T, E> extends FlatteningCollection<T,E> implements UnmodifiableList.Defaults<T>
{
	private final UnmodifiableList<E> primaryCollection;
	private final Function1<UnmodifiableList<T>,E> multiplier;

	FlatteningList( UnmodifiableList<E> primaryCollection, Function1<UnmodifiableList<T>,E> multiplier )
	{
		super( primaryCollection, multiplier::invoke );
		this.primaryCollection = primaryCollection;
		this.multiplier = multiplier;
	}

	@Override public T get( int index )
	{
		for( E primaryElement : primaryCollection )
		{
			UnmodifiableList<T> secondaryCollection = multiplier.invoke( primaryElement );
			int size = secondaryCollection.size();
			if( index < size )
				return secondaryCollection.get( index );
			index -= size;
		}
		throw new AssertionError(); //index out of range
	}
}
