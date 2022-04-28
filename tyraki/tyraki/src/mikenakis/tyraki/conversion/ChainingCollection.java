package mikenakis.tyraki.conversion;

import mikenakis.kit.EqualityComparator;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableEnumerable;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

/**
 * A chaining {@link UnmodifiableCollection}.
 *
 * @author michael.gr
 */
class ChainingCollection<E> extends AbstractUnmodifiableCollection<E>
{
	final UnmodifiableCollection<UnmodifiableCollection<E>> collectionsToChain;

	/**
	 * Initializes a new instance of ChainingCollection.
	 *
	 * @param collectionsToChain the collections to chain together.
	 */
	ChainingCollection( UnmodifiableEnumerable<UnmodifiableCollection<E>> collectionsToChain, EqualityComparator<E> equalityComparator )
	{
		super( equalityComparator );
		this.collectionsToChain = collectionsToChain.toList();
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return collectionsToChain.mustBeImmutableAssertion();
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		UnmodifiableCollection<UnmodifiableEnumerable<E>> enumerablesToChain = UnmodifiableCollection.downCast( collectionsToChain );
		UnmodifiableEnumerable<E> chainingEnumerable = new ChainingEnumerable<>( enumerablesToChain );
		return chainingEnumerable.newUnmodifiableEnumerator();
	}

	@Override public int size()
	{
		int sum = 0;
		for( UnmodifiableCollection<E> collection : collectionsToChain )
			sum += collection.size();
		return sum;
	}

	@Override public Optional<E> tryGet( E element )
	{
		for( UnmodifiableCollection<E> collection : collectionsToChain )
		{
			Optional<E> foundElement = collection.tryGet( element );
			if( foundElement.isPresent() )
				return foundElement;
		}
		return Optional.empty();
	}

	@Override public int getModificationCount()
	{
		int sum = 0;
		for( UnmodifiableCollection<? super E> collection : collectionsToChain )
			sum += collection.getModificationCount();
		return sum;
	}
}
