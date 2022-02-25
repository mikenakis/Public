package mikenakis.tyraki.mutable;

import mikenakis.tyraki.Binding;
import mikenakis.tyraki.BindingEqualityComparator;
import mikenakis.tyraki.DebugView;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.MutableMap;
import mikenakis.tyraki.UnmodifiableMap;
import mikenakis.kit.EqualityComparator;

import java.util.Optional;

/**
 * Abstract {@link UnmodifiableMap}.
 *
 * @param <K> the type of the keys.
 * @param <V> the type of the values.
 *
 * @author michael.gr
 */
abstract class AbstractMutableMap<K, V> extends MutableCollectionsSubject implements MutableMap.Defaults<K,V>
{
	protected abstract class AbstractMutableEntries extends AbstractMutableCollection<Binding<K,V>>
	{
		AbstractMutableEntries( MutableCollections mutableCollections, EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( mutableCollections, new BindingEqualityComparator<>( keyEqualityComparator, valueEqualityComparator ) );
		}

		@Override public final int size()
		{
			return getUnmodifiableMap().size();
		}

		@Override public Optional<Binding<K,V>> tryGet( Binding<K,V> element )
		{
			Optional<Binding<K,V>> binding = getUnmodifiableMap().tryGetBindingByKey( element.getKey() );
			if( binding.isEmpty() )
				return Optional.empty();
			if( !getUnmodifiableMap().values().getEqualityComparator().equals( binding.get().getValue(), element.getValue() ) )
				return Optional.empty();
			return binding;
		}

		final UnmodifiableMap<K,V> getUnmodifiableMap()
		{
			return AbstractMutableMap.this;
		}

		@Override public Optional<Binding<K,V>> tryAdd( Binding<K,V> element )
		{
			return AbstractMutableMap.this.tryAdd( element.getKey(), element.getValue() ).map( previous -> MapEntry.of( element.getKey(), previous ) );

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
			return AbstractMutableMap.this.clear();
		}
	}

	@SuppressWarnings( { "unused", "FieldNamingConvention" } )
	private final DebugView _debugView = DebugView.create( this::entries );

	protected AbstractMutableMap( MutableCollections mutableCollections )
	{
		super( mutableCollections );
	}

	@Override public boolean isFrozenAssertion()
	{
		return mutationContext.isFrozenAssertion();
	}

	@Override public boolean equals( Object other )
	{
		return other instanceof UnmodifiableMap<?,?> kin && equals( kin );
	}

	@Override public int hashCode()
	{
		return entries().calculateHashCode();
	}

	@Override public final String toString()
	{
		return size() + " entries";
	}
}
