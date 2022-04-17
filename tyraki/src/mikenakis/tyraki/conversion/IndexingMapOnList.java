package mikenakis.tyraki.conversion;

import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.IntegerSeriesList;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.tyraki.UnmodifiableList;
import mikenakis.tyraki.UnmodifiableMap;
import mikenakis.kit.EqualityComparator;

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
			};
		}
	}

	private final UnmodifiableList<V> list;
	private final MyEntriesCollection entries;

	IndexingMapOnList( UnmodifiableList<V> list )
	{
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
