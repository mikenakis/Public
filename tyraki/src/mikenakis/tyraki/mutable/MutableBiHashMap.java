package mikenakis.tyraki.mutable;

import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MutableBiMap;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableMap;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;

import java.util.Optional;

/**
 * A hashing implementation of {@link MutableBiMap}.
 *
 * @author michael.gr
 */
public class MutableBiHashMap<K, V> extends AbstractMutableHashMap<K,V> implements MutableBiMap.Defaults<K,V>
{
	protected class MyMutableHashEntries extends MutableHashEntries
	{
		MyMutableHashEntries( MutableCollections mutableCollections, EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public MutableEnumerator<Binding<K,V>> newMutableEnumerator()
		{
			assert canReadAssertion();
			return new MyEnumerator( super.newMutableEnumerator() );
		}

		private class MyEnumerator implements MutableEnumerator.Decorator<Binding<K,V>>
		{
			final MutableEnumerator<Binding<K,V>> wrappedEnumerator;

			MyEnumerator( MutableEnumerator<Binding<K,V>> wrappedEnumerator )
			{
				this.wrappedEnumerator = wrappedEnumerator;
			}

			@Override public MutableEnumerator<Binding<K,V>> getDecoratedUnmodifiableEnumerator()
			{
				return wrappedEnumerator;
			}

			@Override public void deleteCurrent()
			{
				V value = getCurrent().getValue();
				assert reverse.get( value ) == getCurrent().getKey();
				wrappedEnumerator.deleteCurrent();
				reverse.removeKey( value );
			}
		}
	}

	class MyKeys extends MutableMapKeysCollection<K,V>
	{
		MyKeys( MutableCollections mutableCollections, MutableMap<K,V> map, EqualityComparator<? super K> keyEqualityComparator )
		{
			super( mutableCollections, map, keyEqualityComparator );
		}

		@Override public boolean tryRemove( K element )
		{
			assert element != null;
			assert false;
			return false;
		}
	}

	class MyValues extends MutableMapValuesCollection<K,V>
	{
		MyValues( MutableCollections mutableCollections, MutableMap<K,V> map, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( mutableCollections, map, valueEqualityComparator );
		}

		@Override public boolean tryRemove( V element )
		{
			assert element != null;
			assert false;
			return false;
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private MutableBiHashMap<V,K> reverse = null;
	private final MutableCollection<K> keys;
	private final MutableCollection<V> values;
	private final MutableHashEntries entries;

	MutableBiHashMap( MutableCollections mutableCollections, int initialCapacity, float fillFactor, Hasher<? super K> keyHasher,
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		super( mutableCollections, initialCapacity, fillFactor, keyHasher );
		//, keyEqualityComparator, valueEqualityComparator
		entries = new MyMutableHashEntries( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		keys = new MyKeys( mutableCollections, this, keyEqualityComparator );
		values = new MyValues( mutableCollections, this, valueEqualityComparator );
	}

	final void setReverse( MutableBiHashMap<V,K> reverse )
	{
		assert this.reverse == null;
		this.reverse = reverse;
	}

	@Override public MutableMap<V,K> reverse()
	{
		return reverse;
	}

	@Override public MutableCollection<Binding<K,V>> mutableEntries()
	{
		return entries;
	}

	@Override public MutableCollection<K> mutableKeys()
	{
		return keys;
	}

	@Override public MutableCollection<V> mutableValues()
	{
		return values;
	}

	@Override public boolean clear()
	{
		if( !super.clear() )
			return false;
		reverse.clear();
		return true;
	}

	@Override public Optional<V> tryAdd( K k, V v )
	{
		assert k != null;
		assert v != null;
		Optional<V> existing = super.tryAdd( k, v );
		if( existing.isPresent() )
			return existing;
		reverse.add( v, k );
		return Optional.empty();
	}
}
