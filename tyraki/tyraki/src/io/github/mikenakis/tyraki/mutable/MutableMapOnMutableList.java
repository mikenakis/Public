package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.kit.DefaultEqualityComparator;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.tyraki.AbstractEnumerator;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.MapEntry;
import io.github.mikenakis.tyraki.MutableCollection;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.MutableList;
import io.github.mikenakis.tyraki.MutableMap;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

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
			assert mustBeWritableAssertion();
			return new MyEnumerator();
		}

		@Override public UnmodifiableEnumerator<Binding<Integer,V>> newUnmodifiableEnumerator()
		{
			assert mustBeReadableAssertion();
			return new MyEnumerator();
		}
	}

	private class MyEnumerator extends AbstractEnumerator<Binding<Integer,V>> implements MutableEnumerator.Defaults<Binding<Integer,V>>
	{
		int index = 0;
		boolean deleted = false;

		@Override public boolean isFinished()
		{
			assert mustBeReadableAssertion();
			return index >= values.size();
		}

		@Override public Binding<Integer,V> current()
		{
			assert mustBeReadableAssertion();
			V element = values.get( index );
			return MapEntry.of( index, element );
		}

		@Override public UnmodifiableEnumerator<Binding<Integer,V>> moveNext()
		{
			assert mustBeWritableAssertion(); //hmm, I am not sure about this.
			if( !deleted )
				index++;
			deleted = false;
			return this;
		}

		@Override public void deleteCurrent()
		{
			assert mustBeWritableAssertion();
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
		keys = new MutableMapKeysCollection<>( mutableCollections, this, DefaultEqualityComparator.getInstance() );
		entries = new MyEntries( mutableCollections, DefaultEqualityComparator.getInstance(), values.getEqualityComparator() );
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
		assert mustBeReadableAssertion();
		return values.size();
	}

	@Override public boolean containsKey( Integer key )
	{
		assert key != null;
		assert mustBeReadableAssertion();
		return key >= 0 && key < values.size();
	}

	@Override public Optional<Binding<Integer,V>> tryGetBindingByKey( Integer key )
	{
		assert key != null;
		assert mustBeReadableAssertion();
		if( key < 0 || key >= values.size() )
			return Optional.empty();
		return Optional.of( MapEntry.of( key, values.get( key ) ) );
	}

	@Override public Optional<V> tryAdd( Integer key, V value )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		if( key != values.size() )
			return Optional.of( value );
		values.add( value );
		return Optional.empty();
	}

	@Override public boolean tryReplaceValue( Integer key, V value )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		values.replaceAt( key, value );
		return true;
	}

	@Override public boolean tryRemoveKey( Integer key )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		values.removeAt( key );
		return true;
	}

	@Override public boolean clear()
	{
		assert mustBeWritableAssertion();
		return values.clear();
	}
}
