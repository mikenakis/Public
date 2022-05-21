package io.github.mikenakis.tyraki.legacy;

import io.github.mikenakis.kit.DefaultEqualityComparator;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.functional.Procedure0;
import io.github.mikenakis.tyraki.MutableCollection;
import io.github.mikenakis.tyraki.MutableMap;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.MutableList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Legacy Collections.
 *
 * @author michael.gr
 */
public final class LegacyCollections
{
	private LegacyCollections()
	{
	}

	public static <E> MutableEnumerator<E> newEnumeratorOnJavaIterator( Iterator<E> iterator, Procedure0 modCountIncrementer )
	{
		return new LegacyMutableEnumeratorOnJavaIterator<>( iterator, modCountIncrementer );
	}

	public static <T> MutableCollection<T> newCollectionOnJavaCollection( Collection<T> collection )
	{
		return new LegacyMutableCollectionOnJavaCollection<>( DefaultEqualityComparator.getInstance(), collection );
	}

	public static <T> MutableCollection<T> newCollectionOnJavaCollection( EqualityComparator<T> equalityComparator, Collection<T> collection )
	{
		return new LegacyMutableCollectionOnJavaCollection<>( equalityComparator, collection );
	}

	public static <T> MutableList<T> newMutableListOnJavaList( List<T> list )
	{
		return new LegacyMutableListOnJavaList<>( list );
	}

	public static <K, V> MutableMap<K,V> newMapOnJavaMap( Map<K,V> map )
	{
		return new LegacyMutableMapOnJavaMap<>( map );
	}
}
