package mikenakis.tyraki.mutable;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableHashMap;
import mikenakis.tyraki.MutableHashSet;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;
import mikenakis.kit.ObjectHasher;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

/**
 * Caching Hash Set.
 *
 * @author michael.gr
 */
final class CachingHashSet<E> extends AbstractMutableCollection<E> implements MutableHashSet.Defaults<E>
{
	private static final class Entry<E>
	{
		static <E> int comparator( Entry<E> a, Entry<E> b )
		{
			return Integer.compare( a.timestamp, b.timestamp );
		}

		static <E> boolean equalityComparator( Entry<E> a, Entry<E> b )
		{
			assert a != null;
			assert b != null;
			return a.timestamp == b.timestamp;
		}

		public final int timestamp;
		public final E value;

		Entry( int timestamp, E value )
		{
			this.timestamp = timestamp;
			this.value = value;
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return "timestamp=" + timestamp + "; value=" + value;
		}
	}

	private final MutableHashMap<E,Entry<E>> map;
	private final MutableCollection<Entry<E>> entries = mutableCollections.newTreeSet( ObjectHasher.INSTANCE, ( a, b ) -> Entry.comparator( a, b ) );
	private final int capacity;
	private int timestamp = 0;

	CachingHashSet( MutableCollections mutableCollections, int capacity, Hasher<? super E> hasher, EqualityComparator<? super E> equalityComparator )
	{
		super( mutableCollections, equalityComparator );
		assert capacity > 1;
		this.capacity = capacity;
		map = mutableCollections.newHashMap( hasher, equalityComparator, ( a, b ) -> Entry.equalityComparator( a, b ) );
	}

	@Override public Hasher<? super E> getElementHasher()
	{
		return map.getKeyHasher();
	}

	@Override public int size()
	{
		return map.size();
	}

	@Override public Optional<E> tryGet( E element )
	{
		assert element != null;
		Optional<Binding<E,Entry<E>>> binding = map.tryGetBindingByKey( element );
		if( binding.isEmpty() )
			return Optional.empty();
		return Optional.of( binding.get().getKey() );
	}

	@Override public int getModificationCount()
	{
		return map.mutableEntries().getModificationCount();
	}

	@Override public boolean tryReplace( E oldElement, E newElement )
	{
		Optional<Entry<E>> oldEntry = map.tryGet( oldElement );
		oldEntry.ifPresent( entries::remove );
		Entry<E> newEntry = new Entry<>( timestamp++, newElement );
		map.add( newElement, newEntry );
		entries.add( newEntry );
		trim();
		return true;
	}

	@Override public Optional<E> tryAdd( E element )
	{
		Optional<Entry<E>> oldEntry = map.tryGet( element );
		if( oldEntry.isPresent() )
			return Optional.of( oldEntry.get().value );
		Entry<E> newEntry = new Entry<>( timestamp++, element );
		map.add( element, newEntry );
		entries.add( newEntry );
		trim();
		return Optional.empty();
	}

	@Override public boolean tryRemove( E element )
	{
		Optional<Entry<E>> oldEntry = map.tryGet( element );
		if( oldEntry.isEmpty() )
			return false;
		map.removeKey( element );
		entries.remove( oldEntry.get() );
		return true;
	}

	@Override public boolean clear()
	{
		if( entries.isEmpty() )
			return false;
		boolean ok1 = map.clear();
		assert ok1;
		boolean ok2 = entries.clear();
		assert ok2;
		return false;
	}

	@Override public MutableEnumerator<E> newMutableEnumerator()
	{
		return map.mutableKeys().newMutableEnumerator();
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		return map.keys().newUnmodifiableEnumerator();
	}

	@Override public boolean containsDuplicates()
	{
		assert !super.containsDuplicates();
		return false;
	}

	private void trim()
	{
		while( entries.size() > capacity )
		{
			Entry<E> entry = entries.extractFirstElement();
			map.removeKey( entry.value );
			onElementRemoved( entry.value );
		}
	}

	@SuppressWarnings( { "EmptyMethod", "unused" } ) private void onElementRemoved( E element ) //TODO trigger event
	{
		/* nothing to do */
	}
}
