package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableEnumerable;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

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
		assert collectionsMustBeCoherentAssertion( this.collectionsToChain );
	}

	private static <E> boolean collectionsMustBeCoherentAssertion( UnmodifiableCollection<UnmodifiableCollection<E>> collections )
	{
		for( UnmodifiableCollection<E> collection : collections )
			assert collection.coherence().mustBeReadableAssertion();
		return true;
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

	@Override public Coherence coherence()
	{
		return collectionsToChain.coherence();
	}
}
