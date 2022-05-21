package io.github.mikenakis.tyraki;

import io.github.mikenakis.kit.EqualityComparator;

/**
 * An equality comparator for {@link Binding}.
 *
 * @param <K> the type of the key.
 * @param <V> the type of the value.
 */
public class BindingEqualityComparator<K, V> implements EqualityComparator<Binding<K,V>>
{
	public final EqualityComparator<? super K> keyEqualityComparator;
	public final EqualityComparator<? super V> valueEqualityComparator;

	public BindingEqualityComparator( EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		this.keyEqualityComparator = keyEqualityComparator;
		this.valueEqualityComparator = valueEqualityComparator;
	}

	@Override public boolean equals( Binding<K,V> a, Binding<K,V> b )
	{
		return keyEqualityComparator.equals( a.getKey(), b.getKey() ) //
			&& valueEqualityComparator.equals( a.getValue(), b.getValue() );
	}
}
