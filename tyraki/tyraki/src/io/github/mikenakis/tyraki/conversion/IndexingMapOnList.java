package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.kit.DefaultEqualityComparator;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.IntegerSeriesList;
import io.github.mikenakis.tyraki.MapEntry;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableList;
import io.github.mikenakis.tyraki.UnmodifiableMap;

import java.util.Optional;

/**
 * {@link UnmodifiableMap} on {@link UnmodifiableList} using integer keys.
 *
 * @author michael.gr
 */
class IndexingMapOnList<V> extends AbstractMap<Integer,V>
{
	private final class MyEntriesCollection extends AbstractMapEntriesCollection<Integer,V>
	{
		MyEntriesCollection( EqualityComparator<? super Integer> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( IndexingMapOnList.this, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public boolean mustBeImmutableAssertion()
		{
			return IndexingMapOnList.this.mustBeImmutableAssertion();
		}

		@Override public int getModificationCount()
		{
			return list.getModificationCount();
		}

		@Override public UnmodifiableEnumerator<Binding<Integer,V>> newUnmodifiableEnumerator()
		{
			return new AbstractUnmodifiableEnumerator<>()
			{
				private int index = 0;

				@Override public boolean isFinished()
				{
					return index >= list.size();
				}

				@Override public Binding<Integer,V> current()
				{
					return MapEntry.of( index, list.get( index ) );
				}

				@Override public UnmodifiableEnumerator<Binding<Integer,V>> moveNext()
				{
					index++;
					return this;
				}

				@Override public Coherence coherence()
				{
					return IndexingMapOnList.this.coherence();
				}
			};
		}

		@Override public Coherence coherence()
		{
			return IndexingMapOnList.this.coherence();
		}
	}

	private final UnmodifiableList<V> list;
	private final MyEntriesCollection entries;

	IndexingMapOnList( UnmodifiableList<V> list )
	{
		super( list.coherence() );
		assert list != null;
		this.list = list;
		entries = new MyEntriesCollection( DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return list.mustBeImmutableAssertion();
	}

	@Override public UnmodifiableCollection<Binding<Integer,V>> entries()
	{
		return entries;
	}

	@Override public int size()
	{
		return list.size();
	}

	@Override public Optional<Binding<Integer,V>> tryGetBindingByKey( Integer key )
	{
		assert key != null;
		if( key < 0 || key >= list.size() )
			return Optional.empty();
		return Optional.of( MapEntry.of( key, list.get( key ) ) );
	}

	@Override public UnmodifiableCollection<Integer> keys()
	{
		return new IntegerSeriesList( list.size() );
	}

	@Override public UnmodifiableCollection<V> values()
	{
		return list;
	}
}
