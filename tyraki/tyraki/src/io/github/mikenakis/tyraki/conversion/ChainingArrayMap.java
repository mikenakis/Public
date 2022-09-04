package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.UnmodifiableArrayMap;
import io.github.mikenakis.tyraki.UnmodifiableArraySet;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableList;
import io.github.mikenakis.tyraki.UnmodifiableMap;

import java.util.Optional;

/**
 * A chaining {@link UnmodifiableCollection}.
 *
 * @author michael.gr
 */
class ChainingArrayMap<K, V> extends AbstractMap<K,V> implements UnmodifiableArrayMap.Defaults<K,V>
{
	final UnmodifiableCollection<UnmodifiableArrayMap<K,V>> mapsToChain;
	final UnmodifiableList<Binding<K,V>> entries;
	final UnmodifiableArraySet<K> keys;
	final UnmodifiableList<V> values;

	ChainingArrayMap( UnmodifiableCollection<UnmodifiableArrayMap<K,V>> mapsToChain )
	{
		super( mapsToChain.coherence() );
		assert mapsMustBeCoherentAssertion( mapsToChain );
		this.mapsToChain = mapsToChain.toList();
		entries = ConversionCollections.newChainingList( this.mapsToChain.map( UnmodifiableArrayMap::entries ) );
		keys = ConversionCollections.newChainingArraySet( this.mapsToChain.map( UnmodifiableArrayMap::keys ) );
		values = ConversionCollections.newChainingList( this.mapsToChain.map( UnmodifiableArrayMap::values ) );
	}

	private boolean mapsMustBeCoherentAssertion( UnmodifiableCollection<UnmodifiableArrayMap<K,V>> maps )
	{
		for( var map : maps )
			assert map.coherence().mustBeReadableAssertion();
		return true;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return mapsToChain.mustBeImmutableAssertion() && mapsToChain.trueForAll( m -> m.mustBeImmutableAssertion() );
	}

	@Override public int size()
	{
		int sum = 0;
		for( UnmodifiableMap<K,V> map : mapsToChain )
			sum += map.size();
		return sum;
	}

	@Override public UnmodifiableList<Binding<K,V>> entries()
	{
		return entries;
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		for( UnmodifiableMap<K,V> map : mapsToChain )
		{
			Optional<Binding<K,V>> binding = map.tryGetBindingByKey( key );
			if( binding.isPresent() )
				return binding;
		}
		return Optional.empty();
	}

	@Override public UnmodifiableArraySet<K> keys()
	{
		return keys;
	}

	@Override public UnmodifiableList<V> values()
	{
		return values;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return size() + " entries";
	}
}
