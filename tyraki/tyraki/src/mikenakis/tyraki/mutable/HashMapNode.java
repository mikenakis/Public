package mikenakis.tyraki.mutable;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.UnmodifiableHashMap;

class HashMapNode<K,V> extends HashNode<K,HashMapNode<K,V>> implements Binding<K,V>
{
	private final UnmodifiableHashMap<K,V> hashMap;
	private final K key;
	public V value;
	private int hashCode = 0;

	HashMapNode( UnmodifiableHashMap<K,V> hashMap, K key, V value )
	{
		assert key != null;
		this.hashMap = hashMap;
		this.key = key;
		this.value = value;
	}

	public boolean equals( HashMapNode<K,V> other )
	{
		if( !hashMap.keys().getEqualityComparator().equals( key, other.key ) )
			return false;
		if( !hashMap.values().getEqualityComparator().equals( value, other.value ) )
			return false;
		return true;
	}

	public boolean equals( Binding<K,V> other )
	{
		if( !hashMap.keys().getEqualityComparator().equals( key, other.getKey() ) )
			return false;
		if( !hashMap.values().getEqualityComparator().equals( value, other.getValue() ) )
			return false;
		return true;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof HashMapNode<?,?> otherItem )
			return equals( otherItem );
		if( other instanceof Binding<?,?> )
		{
			@SuppressWarnings( "unchecked" )
			Binding<K,V> otherBinding = (Binding<K,V>)other;
			return equals( otherBinding );
		}
		assert false;
		return false;
	}

	@SuppressWarnings( "NonFinalFieldReferencedInHashCode" )
	@Override public int hashCode()
	{
		if( hashCode == 0 )
			hashCode = hashMap.getKeyHasher().getHashCode( key );
		assert hashMap.getKeyHasher().getHashCode( key ) == hashCode;
		return hashCode;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "{ " + key + " -> " + value + " }";
	}

	@Override public K getKey()
	{
		return key;
	}

	@Override public V getValue()
	{
		return value;
	}

	@Override public boolean keyEquals( K otherKey )
	{
		return hashMap.keys().getEqualityComparator().equals( key, otherKey );
	}
}
