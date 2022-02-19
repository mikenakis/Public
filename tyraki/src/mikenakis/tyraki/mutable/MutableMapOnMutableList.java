package mikenakis.tyraki.mutable;

import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.MutableMap;
import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.kit.EqualityComparator;

import java.util.Optional;

/**
 * {@link MutableMap} on {@link MutableList} using integer keys.
 *
 * @author michael.gr
 */
final class MutableMapOnMutableList<V> extends AbstractMutableMap<Integer,V>
{
	private final class MyEntries extends AbstractMutableEntries
	{
		MyEntries( MutableCollections mutableCollections, EqualityComparator<? super Integer> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public int getModificationCount()
		{
			return keys.getModificationCount() + values.getModificationCount();
		}

		@Override public MutableEnumerator<Binding<Integer,V>> newMutableEnumerator()
		{
			assert canWriteAssertion();
			return new MyEnumerator();
		}

		@Override public boolean isFrozen()
		{
			return MutableMapOnMutableList.this.isFrozen();
		}
	}

	private class MyEnumerator extends AbstractMutableEnumerator<Binding<Integer,V>>
	{
		int index = 0;
		boolean deleted = false;

		MyEnumerator()
		{
			super( MutableMapOnMutableList.this.getMutableCollections() );
		}

		@Override public boolean isFinished()
		{
			assert isReadableAssertion();
			return index >= values.size();
		}

		@Override public Binding<Integer,V> getCurrent()
		{
			assert isReadableAssertion();
			V element = values.get( index );
			return MapEntry.of( index, element );
		}

		@Override public UnmodifiableEnumerator<Binding<Integer,V>> moveNext()
		{
			assert isWritableAssertion(); //hmm, I am not sure about this.
			if( !deleted )
				index++;
			deleted = false;
			return this;
		}

		@Override public void deleteCurrent()
		{
			assert isWritableAssertion();
			assert !deleted;
			values.removeAt( index );
			deleted = true;
		}
	}

	private final MutableCollection<Integer> keys;
	private final MutableList<V> values;
	private final MyEntries entries;

	MutableMapOnMutableList( MutableCollections mutableCollections, MutableList<V> values )
	{
		super( mutableCollections );
		this.values = values;
		keys = new MutableMapKeysCollection<>( getMutableCollections(), this, DefaultEqualityComparator.getInstance() );
		entries = new MyEntries( mutableCollections, DefaultEqualityComparator.getInstance(), values.getEqualityComparator() );
	}

	@Override public boolean isFrozen()
	{
		return values.isFrozen();
	}

	@Override public MutableCollection<Binding<Integer,V>> mutableEntries()
	{
		return entries;
	}

	@Override public MutableCollection<Integer> mutableKeys()
	{
		return keys;
	}

	@Override public MutableCollection<V> mutableValues()
	{
		return values;
	}

	@Override public int size()
	{
		assert isReadableAssertion();
		return values.size();
	}

	@Override public boolean containsKey( Integer key )
	{
		assert key != null;
		assert isReadableAssertion();
		return key >= 0 && key < values.size();
	}

	@Override public Optional<Binding<Integer,V>> tryGetBindingByKey( Integer key )
	{
		assert key != null;
		assert isReadableAssertion();
		if( key < 0 || key >= values.size() )
			return Optional.empty();
		return Optional.of( MapEntry.of( key, values.get( key ) ) );
	}

	@Override public Optional<V> tryAdd( Integer key, V value )
	{
		assert key != null;
		assert isWritableAssertion();
		if( key != values.size() )
			return Optional.of( value );
		values.add( value );
		return Optional.empty();
	}

	@Override public boolean tryReplaceValue( Integer key, V value )
	{
		assert key != null;
		assert isWritableAssertion();
		values.replaceAt( key, value );
		return true;
	}

	@Override public boolean tryRemoveKey( Integer key )
	{
		assert key != null;
		assert isWritableAssertion();
		values.removeAt( key );
		return true;
	}

	@Override public boolean clear()
	{
		assert isWritableAssertion();
		return values.clear();
	}
}
