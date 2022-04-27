package mikenakis.tyraki.conversion;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.UnmodifiableCollection;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Filtering {@link UnmodifiableCollection}.
 *
 * @param <E> the type of elements in the Collection.
 *
 * @author michael.gr
 */
final class FilteringCollection<E> extends FilteringEnumerable<E> implements UnmodifiableCollection.Defaults<E>
{
	private final UnmodifiableCollection<E> collection;

	FilteringCollection( UnmodifiableCollection<E> collection, Predicate<E> predicate )
	{
		super( collection, predicate );
		this.collection = collection;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return collection.mustBeImmutableAssertion();
	}

	@Override public EqualityComparator<? super E> getEqualityComparator()
	{
		return collection.getEqualityComparator();
	}

	@Override public int size()
	{
		return countElements();
	}

	@Override public int getModificationCount()
	{
		return collection.getModificationCount();
	}

	@Override public Optional<E> tryGet( E element )
	{
		assert element != null;
		if( !predicate.test( element ) )
			return Optional.empty();
		return collection.tryGet( element );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return size() + " elements";
	}
}
