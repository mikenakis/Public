package io.github.mikenakis.tyraki.mutable;

/**
 * A Hash Index Node.
 *
 * @author michael.gr
 */
public abstract class HashNode<K, T extends HashNode<K,T>>
{
	T prev;
	T next;

	HashNode()
	{
	}

	public abstract K getKey();

	public abstract boolean keyEquals( K key );

	@Override public abstract int hashCode();
}
