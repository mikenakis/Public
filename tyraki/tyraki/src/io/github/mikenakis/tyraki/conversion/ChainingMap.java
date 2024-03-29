package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableMap;

import java.util.Optional;

/**
 * A chaining {@link UnmodifiableCollection}.
 *
 * @author michael.gr
 */
class ChainingMap<K, V> extends AbstractMap<K,V>
{
	private final UnmodifiableCollection<UnmodifiableMap<K,V>> mapsToChain;
	private final UnmodifiableCollection<Binding<K,V>> entries;
	private final UnmodifiableCollection<K> keys;
	private final UnmodifiableCollection<V> values;

	ChainingMap( UnmodifiableCollection<UnmodifiableMap<K,V>> mapsToChain )
	{
		super( mapsToChain.coherence() );
		this.mapsToChain = mapsToChain.toList();
		assert mapsMustBeCoherentAssertion( this.mapsToChain );
		entries = ConversionCollections.newChainingCollection( this.mapsToChain.map( kvUnmodifiableMap2 -> kvUnmodifiableMap2.entries() ) );
		keys = ConversionCollections.newChainingCollection( this.mapsToChain.map( kvUnmodifiableMap1 -> kvUnmodifiableMap1.keys() ) );
		values = ConversionCollections.newChainingCollection( this.mapsToChain.map( kvUnmodifiableMap -> kvUnmodifiableMap.values() ) );
	}

	private boolean mapsMustBeCoherentAssertion( UnmodifiableCollection<UnmodifiableMap<K,V>> maps )
	{
		for( var map: maps )
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

	@Override public UnmodifiableCollection<Binding<K,V>> entries()
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

	@Override public UnmodifiableCollection<K> keys()
	{
		return keys;
	}

	@Override public UnmodifiableCollection<V> values()
	{
		return values;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return size() + " entries";
	}
}
