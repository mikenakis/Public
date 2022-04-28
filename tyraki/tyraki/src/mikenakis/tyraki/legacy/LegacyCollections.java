package mikenakis.tyraki.legacy;

import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.functional.Procedure0;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.MutableMap;

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
