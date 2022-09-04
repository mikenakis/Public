package io.github.mikenakis.tyraki.legacy;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.NullaryCoherence;
import io.github.mikenakis.coherence.exceptions.MustBeFrozenException;
import io.github.mikenakis.kit.DefaultEqualityComparator;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.BindingEqualityComparator;
import io.github.mikenakis.tyraki.MapEntry;
import io.github.mikenakis.tyraki.MutableCollection;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.MutableMap;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableMap;
import io.github.mikenakis.tyraki.conversion.ConversionCollections;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * A {@link MutableMap} on java {@link Map}.
 *
 * @author michael.gr
 */
final class LegacyMutableMapOnJavaMap<K, V> implements MutableMap.Defaults<K,V>
{
	@Override public Coherence coherence()
	{
		return NullaryCoherence.instance;
	}

	private final class MyEntries implements MutableCollection.Defaults<Binding<K,V>>
	{
		final EqualityComparator<Binding<K,V>> bindingEqualityComparator = new BindingEqualityComparator<>( DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );

		MyEntries()
		{
		}

		@Override public Coherence coherence()
		{
			return LegacyMutableMapOnJavaMap.this.coherence();
		}

		@Override public boolean mustBeImmutableAssertion()
		{
			throw new MustBeFrozenException( null );
		}

		@Override public EqualityComparator<? super Binding<K,V>> getEqualityComparator()
		{
			return bindingEqualityComparator;
		}

		@Override public int size()
		{
			return LegacyMutableMapOnJavaMap.this.size();
		}

		@Override public Optional<Binding<K,V>> tryGet( Binding<K,V> element )
		{
			assert element != null;
			Optional<Binding<K,V>> binding = tryGetBindingByKey( element.getKey() );
			return binding.map( b -> values().getEqualityComparator().equals( b.getValue(), element.getValue() )? b : null );
		}

		@Override public int getModificationCount()
		{
			return modificationCount;
		}

		private final Function1<Binding<K,V>,Entry<K,V>> converter = e -> MapEntry.of( e.getKey(), e.getValue() );

		@Override public MutableEnumerator<Binding<K,V>> newMutableEnumerator()
		{
			Iterator<Entry<K,V>> iterator = javaMap.entrySet().iterator();
			MutableEnumerator<Entry<K,V>> enumerator = LegacyCollections.newEnumeratorOnJavaIterator( iterator, () -> modificationCount++ );
			return ConversionCollections.newConvertingMutableEnumerator( enumerator, converter );
		}

		@Override public UnmodifiableEnumerator<Binding<K,V>> newUnmodifiableEnumerator()
		{
			return newMutableEnumerator();
		}

		@Override public Optional<Binding<K,V>> tryAdd( Binding<K,V> element )
		{
			return LegacyMutableMapOnJavaMap.this.tryAdd( element.getKey(), element.getValue() ).map( existing ->
				MapEntry.of( element.getKey(), existing ) );
		}

		@Override public boolean tryReplace( Binding<K,V> oldElement, Binding<K,V> newElement )
		{
			if( !contains( oldElement ) )
				return false;
			if( contains( newElement ) )
				return false;
			if( keys().getEqualityComparator().equals( newElement.getKey(), oldElement.getKey() ) )
				return tryReplaceValue( oldElement.getKey(), newElement.getValue() );
			removeKey( oldElement.getKey() );
			add( newElement );
			return true;
		}

		@Override public boolean tryRemove( Binding<K,V> element )
		{
			if( !contains( element ) )
				return false;
			tryRemoveKey( element.getKey() );
			return true;
		}

		@Override public boolean clear()
		{
			return LegacyMutableMapOnJavaMap.this.clear();
		}
	}

	private final class MutableMapKeysCollection implements MutableCollection.Defaults<K>
	{
		MutableMapKeysCollection()
		{
		}

		@Override public Coherence coherence()
		{
			return NullaryCoherence.instance;
		}

		@Override public boolean mustBeImmutableAssertion()
		{
			throw new MustBeFrozenException( null );
		}

		@Override public int size()
		{
			return LegacyMutableMapOnJavaMap.this.size();
		}

		@Override public Optional<K> tryGet( K element )
		{
			assert element != null;
			Optional<Binding<K,V>> binding = tryGetBindingByKey( element );
			return binding.map( b -> b.getKey() );
		}

		@Override public int getModificationCount()
		{
			return entries().getModificationCount();
		}

		@Override public MutableEnumerator<K> newMutableEnumerator()
		{
			return mutableEntries().newMutableEnumerator().map( kvBinding -> kvBinding.getKey() );
		}

		@Override public UnmodifiableEnumerator<K> newUnmodifiableEnumerator()
		{
			return entries().newUnmodifiableEnumerator().map( kvBinding -> kvBinding.getKey() );
		}

		@Override public boolean tryReplace( K oldElement, K newElement )
		{
			if( !containsKey( oldElement ) )
				return false;
			if( containsKey( newElement ) )
				return false;
			V oldValue = get( oldElement );
			removeKey( oldElement );
			LegacyMutableMapOnJavaMap.this.add( newElement, oldValue );
			return true;
		}

		@Override public Optional<K> tryAdd( K element )
		{
			assert false : element; //cannot add a key, because then I do not know what value to associate it with.
			return Optional.empty();
		}

		@Override public boolean tryRemove( K element )
		{
			return tryRemoveKey( element );
		}

		@Override public boolean clear()
		{
			return LegacyMutableMapOnJavaMap.this.clear();
		}

		@Override public EqualityComparator<? super K> getEqualityComparator()
		{
			return DefaultEqualityComparator.getInstance();
		}

		@Override public boolean equals( Object other )
		{
			if( other == this )
				return true;
			if( other == null )
				return false;
			if( other instanceof UnmodifiableCollection )
			{
				@SuppressWarnings( "unchecked" ) UnmodifiableCollection<K> otherAsUnmodifiableCollection = (UnmodifiableCollection<K>)other;
				return equalsCollection( otherAsUnmodifiableCollection );
			}
			assert false;
			return false;
		}

		@Override public int hashCode()
		{
			return calculateHashCode();
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return size() + " elements";
		}
	}

	private final class MutableMapValuesCollection implements MutableCollection.Defaults<V>
	{
		MutableMapValuesCollection()
		{
		}

		@Override public Coherence coherence()
		{
			return NullaryCoherence.instance;
		}

		@Override public boolean mustBeImmutableAssertion()
		{
			throw new MustBeFrozenException( null );
		}

		@Override public int size()
		{
			return LegacyMutableMapOnJavaMap.this.size();
		}

		@Override public Optional<V> tryGet( V element )
		{
			assert element != null;
			Optional<Binding<K,V>> binding = tryGetBindingsByValue( element ).tryFetchSingleElement();
			return binding.map( b -> b.getValue() );
		}

		@Override public int getModificationCount()
		{
			return entries().getModificationCount();
		}

		@Override public MutableEnumerator<V> newMutableEnumerator()
		{
			return mutableEntries().newMutableEnumerator().map( kvBinding -> kvBinding.getValue() );
		}

		@Override public UnmodifiableEnumerator<V> newUnmodifiableEnumerator()
		{
			return entries().newUnmodifiableEnumerator().map( kvBinding -> kvBinding.getValue() );
		}

		@Override public boolean tryReplace( V oldElement, V newElement )
		{
			//TODO: if there was a MutableEnumerator.setCurrent() function, we could replace this loop with one which enumerates values instead of entries.
			for( Binding<K,V> binding : mutableEntries() )
			{
				if( binding.getValue() == oldElement )
				{
					replaceValue( binding.getKey(), newElement );
					return true;
				}
			}
			return false;
		}

		@Override public Optional<V> tryAdd( V element )
		{
			assert false; //this is meaningless, because we do not know what key to bind this value to.
			return Optional.empty();
		}

		//TODO: this method could accept a boolean specifying whether we want all values removed, or just the first matching value removed.
		@Override public boolean tryRemove( V element )
		{
			for( MutableEnumerator<V> enumerator = newMutableEnumerator(); !enumerator.isFinished(); enumerator.moveNext() )
			{
				if( enumerator.current() == element )
				{
					enumerator.deleteCurrent();
					return true;
				}
			}
			return false;
		}

		@Override public boolean clear()
		{
			return LegacyMutableMapOnJavaMap.this.clear();
		}

		@Override public EqualityComparator<? super V> getEqualityComparator()
		{
			return DefaultEqualityComparator.getInstance();
		}

		@Override public boolean equals( Object other )
		{
			if( other == this )
				return true;
			if( other == null )
				return false;
			if( other instanceof UnmodifiableCollection )
			{
				@SuppressWarnings( "unchecked" ) UnmodifiableCollection<V> otherAsUnmodifiableCollection = (UnmodifiableCollection<V>)other;
				return equalsCollection( otherAsUnmodifiableCollection );
			}
			assert false;
			return false;
		}

		@Override public int hashCode()
		{
			return calculateHashCode();
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return size() + " elements";
		}
	}

	private final Map<K,V> javaMap;
	private final MutableCollection<Binding<K,V>> entries = new MyEntries();
	private final MutableCollection<K> keys = new MutableMapKeysCollection();
	private final MutableCollection<V> values = new MutableMapValuesCollection();
	private int modificationCount;

	LegacyMutableMapOnJavaMap( Map<K,V> javaMap )
	{
		assert javaMap != null;
		this.javaMap = javaMap;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		throw new MustBeFrozenException( null );
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

	@Override public int hashCode()
	{
		return javaMap.hashCode();
	}

	@Override public int size()
	{
		return javaMap.size();
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		V value = Kit.map.tryGet( javaMap, key );
		if( value == null )
			return Optional.empty();
		return Optional.of( MapEntry.of( key, value ) );
	}

	@Override public Optional<V> tryAdd( K key, V value )
	{
		assert key != null;
		if( !Kit.map.tryAdd( javaMap, key, value ) )
			return Optional.of( Kit.map.get( javaMap, key ) );
		modificationCount++;
		return Optional.empty();
	}

	@Override public boolean tryReplaceValue( K key, V value )
	{
		assert key != null;
		if( !Kit.map.tryReplace( javaMap, key, value ) )
			return false;
		modificationCount++;
		return true;
	}

	@Override public boolean tryRemoveKey( K key )
	{
		assert key != null;
		if( Kit.map.tryRemove( javaMap, key ) == null )
			return false;
		modificationCount++;
		return true;
	}

	@Override public boolean clear()
	{
		if( javaMap.isEmpty() )
			return false;
		javaMap.clear();
		modificationCount++;
		return true;
	}

	@Override public V computeIfAbsent( K key, Function1<? extends V,? super K> mappingFunction )
	{
		assert key != null;
		return javaMap.computeIfAbsent( key, k ->
		{
			modificationCount++;
			return mappingFunction.invoke( k );
		} );
	}

	@Override public boolean equals( Object other )
	{
		return other instanceof UnmodifiableMap<?,?> kin && equals( kin );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return size() + " entries";
	}
}
