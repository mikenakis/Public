package mikenakis.tyraki;

import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.functional.Function1;

import java.util.Optional;

class FlatteningCollection<T, E> extends FlatteningEnumerable<T,E> implements UnmodifiableCollection.Defaults<T>
{
	private final UnmodifiableCollection<E> primaryCollection;
	private final Function1<UnmodifiableCollection<T>,E> multiplier;

	FlatteningCollection( UnmodifiableCollection<E> primaryCollection, Function1<UnmodifiableCollection<T>,E> multiplier )
	{
		super( primaryCollection, multiplier::invoke );
		assert allEqualityComparatorsMustBeSameAssertion( primaryCollection, multiplier );
		this.primaryCollection = primaryCollection;
		this.multiplier = multiplier;
	}

	private static <T,E> boolean allEqualityComparatorsMustBeSameAssertion( UnmodifiableEnumerable<E> primaryCollection, Function1<UnmodifiableCollection<T>,E> multiplier )
	{
		UnmodifiableEnumerator<E> primaryEnumerator = primaryCollection.newUnmodifiableEnumerator();
		if( primaryEnumerator.isFinished() )
			return true;
		EqualityComparator<? super T> equalityComparator = multiplier.invoke( primaryEnumerator.current() ).getEqualityComparator();
		primaryEnumerator.moveNext();
		while( !primaryEnumerator.isFinished() )
		{
			assert multiplier.invoke( primaryEnumerator.current() ).getEqualityComparator().equals( equalityComparator );
			primaryEnumerator.moveNext();
		}
		return true;
	}

	@Override public int size()
	{
		return primaryCollection.reduce( 0, ( sum, element ) -> sum += multiplier.invoke( element ).size() );
	}

	@Override public Optional<T> tryGet( T element )
	{
		for( E primaryElement : primaryCollection )
		{
			UnmodifiableCollection<T> secondaryCollection = multiplier.invoke( primaryElement );
			Optional<T> secondaryElement = secondaryCollection.tryGet( element );
			if( secondaryElement.isPresent() )
				return secondaryElement;
		}
		return Optional.empty();
	}

	@Override public EqualityComparator<? super T> getEqualityComparator()
	{
		return primaryCollection.tryFetchFirstElement().map( e -> multiplier.invoke( e ).getEqualityComparator() ).orElse( DefaultEqualityComparator.getInstance() );
	}
}
