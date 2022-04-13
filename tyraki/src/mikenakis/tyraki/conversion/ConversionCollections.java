package mikenakis.tyraki.conversion;

import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerable;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.PartiallyConvertingEqualityComparator;
import mikenakis.tyraki.TotalConverterWithIndex;
import mikenakis.tyraki.UnmodifiableArrayMap;
import mikenakis.tyraki.UnmodifiableArraySet;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableEnumerable;
import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.tyraki.UnmodifiableList;
import mikenakis.tyraki.UnmodifiableMap;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Conversion Collections.
 *
 * @author michael.gr
 */
public final class ConversionCollections
{
	public static final Object[] ARRAY_OF_ZERO_OBJECTS = new Object[0];

	/**
	 * private constructor to prevent instantiation.
	 */
	private ConversionCollections()
	{
	}

	/**
	 * Creates a filtering {@link UnmodifiableEnumerator}.
	 *
	 * @param enumerator the {@link UnmodifiableEnumerator} to filter.
	 * @param predicate  invoked for each element: returns {@code true} if an element should be included in the enumeration, {@code false} otherwise.
	 * @param <T>        the type of the elements
	 *
	 * @return a new filtering {@link UnmodifiableEnumerator}.
	 */
	public static <T> UnmodifiableEnumerator<T> newFilteringEnumerator( UnmodifiableEnumerator<T> enumerator, Predicate<? super T> predicate )
	{
		return new FilteringEnumerator<>( enumerator, predicate );
	}

	/**
	 * Creates a converting {@link UnmodifiableEnumerator}.
	 *
	 * @param enumerator the {@link UnmodifiableEnumerator} to convert.
	 * @param converter  invoked for each element: returns the converted element.
	 * @param <T>        the type of elements to convert to.
	 * @param <F>        the type of elements to convert from.
	 *
	 * @return a new converting {@link UnmodifiableEnumerator}.
	 */
	public static <T, F> UnmodifiableEnumerator<T> newConvertingEnumerator( UnmodifiableEnumerator<F> enumerator, TotalConverterWithIndex<? extends T,? super F> converter )
	{
		return new ConvertingUnmodifiableEnumerator<>( enumerator, converter );
	}

	/**
	 * Creates a new converting {@link MutableEnumerator}.
	 *
	 * @param enumerator the {@link MutableEnumerator} to convert from.
	 * @param converter  the converter.
	 * @param <T>        the type of elements to convert to.
	 * @param <F>        the type of elements to convert from.
	 *
	 * @return a new {@link MutableEnumerator} converting elements of the given {@link MutableEnumerator} from F to T.
	 */
	public static <T, F> MutableEnumerator<T> newConvertingMutableEnumerator( MutableEnumerator<F> enumerator, Function1<? extends T,? super F> converter )
	{
		return new ConvertingMutableEnumerator<>( enumerator, converter );
	}

	/**
	 * Creates a new filtering {@link MutableEnumerator}.
	 *
	 * @param enumerator the {@link MutableEnumerator} to filter from.
	 * @param predicate  the {@link Predicate}.
	 * @param <T>        the type of elements to filter.
	 *
	 * @return a new {@link MutableEnumerator} converting elements of the given {@link MutableEnumerator} from F to T.
	 */
	public static <T> MutableEnumerator<T> newFilteringMutableEnumerator( MutableEnumerator<T> enumerator, Predicate<T> predicate )
	{
		return new FilteringMutableEnumerator<>( enumerator, predicate );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a filtering {@link UnmodifiableEnumerable}.
	 *
	 * @param enumerable the {@link UnmodifiableEnumerable} to filter.
	 * @param predicate  invoked for each element: returns {@code true} if an element should be included in the enumeration, {@code false} otherwise.
	 * @param <T>        the type of the elements
	 *
	 * @return a new filtering {@link UnmodifiableEnumerator}.
	 */
	public static <T> UnmodifiableEnumerable<T> newFilteringEnumerable( UnmodifiableEnumerable<T> enumerable, Predicate<? super T> predicate )
	{
		return new FilteringEnumerable<>( enumerable, predicate );
	}

	/**
	 * Creates a chaining {@link UnmodifiableEnumerable}.
	 *
	 * @param enumerables the {@link UnmodifiableEnumerable}s to chain.
	 * @param <T>         the type of the elements.
	 *
	 * @return a new chaining {@link UnmodifiableEnumerable}.
	 */
	public static <T> UnmodifiableEnumerable<T> newChainingEnumerable( UnmodifiableCollection<UnmodifiableEnumerable<T>> enumerables )
	{
		if( enumerables.isEmpty() )
			return UnmodifiableEnumerable.of();
		if( enumerables.size() == 1 )
			return enumerables.fetchFirstElement();
		return new ChainingEnumerable<>( enumerables );
	}

	/**
	 * Creates a chaining {@link UnmodifiableEnumerable}.
	 *
	 * @param arrayOfEnumerables the {@link UnmodifiableEnumerable}s to chain.
	 * @param <T>                the type of the elements.
	 *
	 * @return a new chaining {@link UnmodifiableEnumerable}.
	 */
	@SafeVarargs
	@SuppressWarnings( "varargs" ) //for -Xlint
	public static <T> UnmodifiableEnumerable<T> newChainingEnumerableOf( UnmodifiableEnumerable<T>... arrayOfEnumerables )
	{
		var enumerables = newArrayWrapper( arrayOfEnumerables );
		return newChainingEnumerable( enumerables );
	}

	/**
	 * Creates a converting {@link MutableEnumerable}.
	 *
	 * @param enumerable the {@link MutableEnumerable} to convert.
	 * @param converter  invoked for each element: returns the converted element.
	 * @param <T>        the type of elements to convert to.
	 * @param <F>        the type of elements to convert from.
	 *
	 * @return a new converting {@link UnmodifiableEnumerator}.
	 */
	public static <T, F> MutableEnumerable<T> newConvertingMutableEnumerable( MutableEnumerable<F> enumerable, Function1<? extends T,? super F> converter )
	{
		return new ConvertingMutableEnumerable<>( enumerable, converter );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a chaining {@link UnmodifiableCollection}.
	 *
	 * @param arrayOfCollections the {@link UnmodifiableCollection}s to chain.
	 * @param <T>                the type of the elements.
	 *
	 * @return a new chaining {@link UnmodifiableCollection}.
	 */
	@SafeVarargs @SuppressWarnings( "varargs" ) //for -Xlint
	public static <T> UnmodifiableCollection<T> newChainingCollectionOf( UnmodifiableCollection<T>... arrayOfCollections )
	{
		var wrappedCollections = newArrayWrapper( arrayOfCollections );
		return newChainingCollection( wrappedCollections );
	}

	/**
	 * Creates a chaining {@link UnmodifiableCollection}.
	 *
	 * @param collections the {@link UnmodifiableCollection}s to chain.
	 * @param <T>         the type of the elements.
	 *
	 * @return a new chaining {@link UnmodifiableCollection}.
	 */
	public static <T> UnmodifiableCollection<T> newChainingCollection( UnmodifiableCollection<UnmodifiableCollection<T>> collections )
	{
		return new ChainingCollection<>( collections, DefaultEqualityComparator.getInstance() );
	}

	/**
	 * Creates a new {@link UnmodifiableCollection} which contains each item of another {@link UnmodifiableCollection} converted by a conversion function.
	 *
	 * @param collection the {@link UnmodifiableCollection} to convert from.
	 * @param converter  a converter of T from F.
	 * @param reverter   a converter of F from T.
	 * @param <T>        the type of elements to convert to.
	 * @param <F>        the type of elements to convert from.
	 *
	 * @return a new {@link UnmodifiableCollection} converting elements of the given {@link UnmodifiableCollection} from F to T.
	 */
	public static <T, F> UnmodifiableCollection<T> newConvertingCollection( UnmodifiableCollection<F> collection, Function1<? extends T,? super F> converter, //
		Function1<Optional<? extends F>,? super T> reverter )
	{
		EqualityComparator<T> equalityComparator = new PartiallyConvertingEqualityComparator<>( reverter );
		return newConvertingCollection( collection, converter, reverter, equalityComparator );
	}

	/**
	 * Creates a new {@link UnmodifiableCollection} which contains each item of another {@link UnmodifiableCollection} converted by a conversion function.
	 *
	 * @param collection         the {@link UnmodifiableCollection} to convert from.
	 * @param converter          a converter of T from F.
	 * @param reverter           a converter of F from T.
	 * @param equalityComparator an {@link EqualityComparator} of T.
	 * @param <T>                the type of elements to convert to.
	 * @param <F>                the type of elements to convert from.
	 *
	 * @return a new {@link UnmodifiableCollection} converting elements of the given {@link UnmodifiableCollection} from F to T.
	 */
	public static <T, F> UnmodifiableCollection<T> newConvertingCollection( UnmodifiableCollection<F> collection, Function1<? extends T,? super F> converter, //
		Function1<Optional<? extends F>,? super T> reverter, EqualityComparator<? super T> equalityComparator )
	{
		return new ConvertingUnmodifiableCollection<>( collection, converter, reverter, equalityComparator );
	}

	/**
	 * Creates a new {@link UnmodifiableCollection} which converts items using a {@link Function1}.
	 *
	 * @param collection the {@link UnmodifiableCollection} to convert from.
	 * @param converter  a converter of T from F.
	 * @param <T>        the type of elements to convert to.
	 * @param <F>        the type of elements to convert from.
	 *
	 * @return a new {@link UnmodifiableCollection} converting elements of the given {@link UnmodifiableCollection} from F to T.
	 */
	public static <T, F> UnmodifiableCollection<T> newConvertingCollection( UnmodifiableCollection<F> collection, Function1<? extends T,? super F> converter )
	{
		Function1<Optional<? extends F>,? super T> reverter = newReversingConverter( collection, converter );
		return newConvertingCollection( collection, converter, reverter );
	}

	/**
	 * Creates a new {@link UnmodifiableCollection} which contains each item of another {@link UnmodifiableCollection} converted by a conversion function.
	 *
	 * @param collection         the {@link UnmodifiableCollection} to convert from.
	 * @param converter          a converter of T from F.
	 * @param reverter           a converter of F from T.
	 * @param equalityComparator an {@link EqualityComparator} of T.
	 * @param <T>                the type of elements to convert to.
	 * @param <F>                the type of elements to convert from.
	 *
	 * @return a new {@link UnmodifiableCollection} converting elements of the given {@link UnmodifiableCollection} from F to T.
	 */
	public static <T, F> MutableCollection<T> newConvertingMutableCollection( MutableCollection<F> collection, Function1<? extends T,? super F> converter, //
		Function1<F,? super T> reverter, EqualityComparator<? super T> equalityComparator )
	{
		return new ConvertingMutableCollection<>( collection, converter, reverter, equalityComparator );
	}

	/**
	 * Creates a new {@link UnmodifiableCollection} representing a subset of the elements of the given {@link UnmodifiableCollection}.
	 *
	 * @param collection the {@link UnmodifiableCollection} whose contents are to be filtered.
	 * @param <T>        the type of elements.
	 *
	 * @return a new modifiable {@link UnmodifiableCollection} representing a subset of the elements of the given {@link UnmodifiableCollection}.
	 */
	public static <T> UnmodifiableCollection<T> newFilteringCollection( UnmodifiableCollection<T> collection, Predicate<T> predicate )
	{
		return new FilteringCollection<>( collection, predicate );
	}

	/**
	 * Creates a new {@link UnmodifiableCollection} out of the given elements, containing each element only once.
	 *
	 * @param collections the elements to put in the {@link UnmodifiableCollection}.
	 * @param <T>         the type of elements.
	 *
	 * @return an {@link UnmodifiableList} containing the given elements.
	 */
	public static <T> UnmodifiableCollection<T> newChainingSet( UnmodifiableCollection<UnmodifiableCollection<T>> collections )
	{
		return new ChainingSet<>( collections, DefaultEqualityComparator.getInstance() );
	}

	/**
	 * Creates a new {@link UnmodifiableCollection} out of the given elements, containing each element only once.
	 *
	 * @param arrayOfCollections the elements to put in the {@link UnmodifiableCollection}.
	 * @param <T>                the type of elements.
	 *
	 * @return an {@link UnmodifiableList} containing the given elements.
	 */
	@SafeVarargs
	@SuppressWarnings( "varargs" ) //for -Xlint
	public static <T> UnmodifiableCollection<T> newChainingSetOf( UnmodifiableCollection<T>... arrayOfCollections )
	{
		var collectionOfCollections = newArrayWrapper( arrayOfCollections );
		return newChainingSet( collectionOfCollections );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//

	/**
	 * Creates a new {@link UnmodifiableList} representing the items of some given {@link UnmodifiableList}s chained together.
	 *
	 * @param lists the {@link UnmodifiableList}s to chain.
	 * @param <T>   the type of items of the {@link UnmodifiableList}s.
	 *
	 * @return a {@link UnmodifiableList} representing the items of all given {@link UnmodifiableList}s chained together.
	 */
	public static <T> UnmodifiableList<T> newChainingList( UnmodifiableCollection<? extends UnmodifiableList<T>> lists )
	{
		return new ChainingList<>( lists, DefaultEqualityComparator.getInstance() );
	}

	/**
	 * Creates a new {@link UnmodifiableList} representing the items of some given {@link UnmodifiableList}s chained together.
	 *
	 * @param arrayOfLists the {@link UnmodifiableList}s to chain.
	 * @param <T>          the type of items of the {@link UnmodifiableList}s.
	 *
	 * @return a {@link UnmodifiableList} representing the items of all given {@link UnmodifiableList}s chained together.
	 */
	@SafeVarargs
	@SuppressWarnings( "varargs" ) //for -Xlint
	public static <T> UnmodifiableList<T> newChainingListOf( UnmodifiableList<T>... arrayOfLists )
	{
		var collectionOfLists = newArrayWrapper( arrayOfLists );
		return newChainingList( collectionOfLists );
	}

	/**
	 * Creates a new converting {@link UnmodifiableList}.
	 *
	 * @param list      the {@link UnmodifiableList} to convert from.
	 * @param converter the converter.
	 * @param reverter  the reverter.
	 * @param <T>       the type of elements to convert to.
	 * @param <F>       the type of elements to convert from.
	 *
	 * @return a new {@link UnmodifiableList} converting elements of the given {@link UnmodifiableList} from F to T.
	 */
	public static <T, F> UnmodifiableList<T> newConvertingList( UnmodifiableList<F> list, TotalConverterWithIndex<? extends T,? super F> converter,
		Function1<Optional<? extends F>,? super T> reverter, EqualityComparator<? super T> equalityComparator )
	{
		return new ConvertingList<>( list, converter, reverter, equalityComparator );
	}

	/**
	 * Creates a new reversing {@link UnmodifiableList}.
	 *
	 * @param list the {@link UnmodifiableList} to reverse.
	 *
	 * @return a new {@link UnmodifiableList} reversing the given {@link UnmodifiableList}.
	 */
	public static <T> UnmodifiableList<T> newReversingList( UnmodifiableList<T> list )
	{
		return new ReversingList<>( list );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//

	/**
	 * Creates a new {@link UnmodifiableList} representing the items of some given {@link UnmodifiableList}s chained together.
	 *
	 * @param lists the {@link UnmodifiableList}s to chain.
	 * @param <T>   the type of items of the {@link UnmodifiableList}s.
	 *
	 * @return a {@link UnmodifiableList} representing the items of all given {@link UnmodifiableList}s chained together.
	 */
	public static <T> UnmodifiableArraySet<T> newChainingArraySet( UnmodifiableCollection<? extends UnmodifiableArraySet<T>> lists )
	{
		return new ChainingArraySet<>( lists, DefaultEqualityComparator.getInstance() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//

	/**
	 * Creates a new {@link UnmodifiableMap} which filters bindings using a given predicate.
	 *
	 * @param map the {@link UnmodifiableMap} to filter.
	 *
	 * @return a new {@link UnmodifiableMap}.
	 */
	public static <K, V> UnmodifiableMap<K,V> newFilteringMap( UnmodifiableMap<K,V> map, Predicate<Binding<K,V>> predicate )
	{
		return new FilteringMap<>( map, predicate );
	}

	/**
	 * Creates a new {@link UnmodifiableMap} representing each entry of the given {@link UnmodifiableMap} where the keys are converted to the given type.
	 *
	 * @param map       the {@link UnmodifiableMap} to convert from.
	 * @param <TK>      the key type to convert to.
	 * @param <SK>      the value type to convert from.
	 * @param <V>       the value type.
	 * @param converter key converter.
	 * @param reverter  reverse key converter.
	 *
	 * @return a new {@link UnmodifiableMap} representing all pairs of the given {@link UnmodifiableMap} where keys have been converted from FV to TV.
	 */
	public static <TK, V, SK> UnmodifiableMap<TK,V> newKeyConvertingMap( UnmodifiableMap<SK,V> map, Function1<? extends TK,? super SK> converter, //
		Function1<Optional<? extends SK>,? super TK> reverter )
	{
		return new KeyConvertingMap<>( map, converter, reverter );
	}

	/**
	 * Creates a new {@link UnmodifiableMap} representing each entry of the given {@link UnmodifiableMap} where the keys are converted to the given type.
	 *
	 * @param map       the {@link UnmodifiableMap} to convert from.
	 * @param <TK>      the key type to convert to.
	 * @param <SK>      the value type to convert from.
	 * @param <V>       the value type.
	 * @param converter key converter.
	 *
	 * @return a new {@link UnmodifiableMap} representing all pairs of the given {@link UnmodifiableMap} where keys have been converted from FV to TV.
	 */
	public static <TK, V, SK> UnmodifiableMap<TK,V> newKeyConvertingAndFilteringMap( UnmodifiableMap<SK,V> map, Function1<? extends TK,? super SK> converter )
	{
		return newKeyConvertingMap( map, converter, t -> Kit.fail() );
	}

	/**
	 * Creates a new {@link UnmodifiableMap} representing each entry of the given {@link UnmodifiableMap} where the values are converted to the given type.
	 *
	 * @param map               the {@link UnmodifiableMap} to convert from.
	 * @param <K>               the key type.
	 * @param <TV>              the value type to convert to.
	 * @param <SV>              the value type to convert from.
	 * @param tvFromSvConverter value converter.
	 * @param svFromTvConverter reverse value converter.
	 *
	 * @return a new {@link UnmodifiableMap} representing all pairs of the given {@link UnmodifiableMap} where values have been converted from FV to TV.
	 */
	public static <K, TV, SV> UnmodifiableMap<K,TV> newValueConvertingAndFilteringMap( UnmodifiableMap<K,SV> map, //
		Function1<? extends TV,? super SV> tvFromSvConverter, Function1<Optional<? extends SV>,? super TV> svFromTvConverter )
	{
		return new ValueConvertingMap<>( map, tvFromSvConverter, svFromTvConverter );
	}

	/**
	 * Creates a new {@link UnmodifiableMap} representing each entry of the given {@link UnmodifiableMap} where the values are converted to the given type.
	 *
	 * @param map               the {@link UnmodifiableMap} to convert from.
	 * @param <K>               the key type.
	 * @param <TV>              the value type to convert to.
	 * @param <SV>              the value type to convert from.
	 * @param tvFromSvConverter value converter.
	 *
	 * @return a new {@link UnmodifiableMap} representing all pairs of the given {@link UnmodifiableMap} where values have been converted from FV to TV.
	 */
	public static <K, TV, SV> UnmodifiableMap<K,TV> newValueConvertingAndFilteringMap( UnmodifiableMap<K,SV> map, Function1<? extends TV,? super SV> tvFromSvConverter )
	{
		return newValueConvertingAndFilteringMap( map, tvFromSvConverter, t -> Kit.fail() );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new {@link UnmodifiableEnumerable} representing each element of the given {@link UnmodifiableEnumerable} converted using a given converter.
	 *
	 * @param enumerable the {@link UnmodifiableEnumerable} to cast from.
	 * @param converter  the {@link Function1} to use.
	 * @param <T>        the type of elements to cast to.
	 * @param <F>        the type of elements to cast from.
	 *
	 * @return a new, possibly modifiable, {@link UnmodifiableEnumerable} representing all elements in the given {@link UnmodifiableEnumerable} cast down from F to T.
	 */
	public static <T, F> UnmodifiableEnumerable<T> newConvertingUnmodifiableEnumerable( UnmodifiableEnumerable<F> enumerable, TotalConverterWithIndex<? extends T,? super F> converter )
	{
		return new ConvertingEnumerable<>( enumerable, converter );
	}

	/**
	 * Creates an {@link UnmodifiableMap} on a {@link UnmodifiableCollection} of values using a {@link Function1} to obtain keys from values.
	 *
	 * @param collection the {@link UnmodifiableCollection}.
	 * @param converter  the {@link Function1} of values to keys.
	 * @param <K>        the type of the keys.
	 * @param <V>        the type of the values.
	 *
	 * @return an {@link UnmodifiableMap} on the given {@link UnmodifiableCollection} using the given {@link Function1}.
	 */
	public static <K, V> UnmodifiableMap<K,V> newMapOnValueCollection( UnmodifiableCollection<V> collection, Function1<? extends K,? super V> converter )
	{
		return new MapOnValueCollection<>( collection, converter );
	}

	/**
	 * Creates an {@link UnmodifiableMap} on a {@link UnmodifiableCollection} of keys using a {@link Function1} to obtain values from keys.
	 *
	 * @param collection the {@link UnmodifiableCollection}.
	 * @param converter  the {@link Function1} of keys to values.
	 * @param <K>        the type of the keys.
	 * @param <V>        the type of the values.
	 *
	 * @return an {@link UnmodifiableMap} on the given {@link UnmodifiableCollection} using the given {@link Function1}.
	 */
	public static <K, V> UnmodifiableMap<K,V> newMapOnKeyCollection( UnmodifiableCollection<K> collection, Function1<? extends V,? super K> converter )
	{
		return new MapOnKeyCollection<>( collection, converter );
	}

	/**
	 * Creates an {@link UnmodifiableMap} of {@link Integer} keys on a {@link UnmodifiableList}.
	 *
	 * @param list the {@link UnmodifiableList} to convert.
	 * @param <V>  the type of the elements in the list. (They are represented as values in the map.)
	 *
	 * @return a new {@link UnmodifiableMap} of {@link Integer} keys on the given {@link UnmodifiableList}.
	 */
	public static <V> UnmodifiableMap<Integer,V> newIndexingMapOnList( UnmodifiableList<V> list )
	{
		return new IndexingMapOnList<>( list );
	}

	/**
	 * Creates an {@link UnmodifiableEnumerable} which starts with a given element and computes subsequent elements by applying a {@link Function1} to the current element.
	 *
	 * @param firstElement         the first element to yield.
	 * @param nextElementProducer  the function which, given an element, calculates the next element.
	 * @param <E>                  the type of the elements.
	 *
	 * @return a new {@link UnmodifiableEnumerable}.
	 */
	public static <E> UnmodifiableEnumerable<E> newEnumerable( E firstElement, Function1<Optional<E>,E> nextElementProducer )
	{
		return new EnumerableOnFunction<>( firstElement, nextElementProducer );
	}

	public static <E> UnmodifiableEnumerable<E> newSingleElementEnumerable( E element )
	{
		return new SingleElementEnumerable<>( element );
	}

	public static <E> UnmodifiableList<E> newArrayWrapper( E[] array )
	{
		return newArrayWrapper( array, false );
	}

	public static <E> UnmodifiableList<E> newArrayWrapper( E[] array, boolean frozen )
	{
		assert array != null;
		if( array.length == 0 )
			return UnmodifiableList.of();
		EqualityComparator<? super E> equalityComparator = DefaultEqualityComparator.getInstance();
		return newArrayWrapper( array, equalityComparator, frozen );
	}

	public static <E> UnmodifiableList<E> newArrayWrapper( E[] array, EqualityComparator<? super E> equalityComparator, boolean frozen )
	{
		if( array.length == 0 )
			return UnmodifiableList.of();
		return new ListOnArray<>( equalityComparator, array, frozen );
	}

	public static <K, V> UnmodifiableCollection<K> newUnmodifiableMapKeysCollection( UnmodifiableMap<K,V> map, EqualityComparator<? super K> keyEqualityComparator )
	{
		return new MapKeysCollection<>( map, keyEqualityComparator );
	}

	public static <K, V> UnmodifiableCollection<V> newUnmodifiableMapValuesCollection( UnmodifiableMap<K,V> map, EqualityComparator<? super V> valueEqualityComparator )
	{
		return new MapValuesCollection<>( map, valueEqualityComparator );
	}

	public static <E> UnmodifiableCollection<E> newUnmodifiableCollectionOnUnmodifiableEnumerable( UnmodifiableEnumerable<E> enumerable, EqualityComparator<E> equalityComparator )
	{
		return new CollectionOnEnumerable<>( enumerable, equalityComparator );
	}

	public static <E> UnmodifiableEnumerator<E> newUnmodifiableEnumeratorOnUnmodifiableList( UnmodifiableList<E> list )
	{
		return new EnumeratorOnList<>( list );
	}

	public static <F, T> Function1<Optional<? extends F>,T> newReversingConverter( UnmodifiableCollection<F> collection, Function1<? extends T,? super F> forwardConverter )
	{
		return new ReversingConverter<>( collection, forwardConverter );
	}

	public static <K, V> UnmodifiableMap<K,V> newChainingMap( UnmodifiableCollection<UnmodifiableMap<K,V>> mapsToChain )
	{
		return new ChainingMap<>( mapsToChain );
	}

	public static <K, V> UnmodifiableArrayMap<K,V> newChainingArrayMap( UnmodifiableList<UnmodifiableArrayMap<K,V>> mapsToChain )
	{
		return new ChainingArrayMap<>( mapsToChain );
	}

	@SafeVarargs
	@SuppressWarnings( "varargs" ) //for -Xlint
	public static <K, V> UnmodifiableMap<K,V> newChainingMapOf( UnmodifiableMap<K,V>... mapsToChain )
	{
		var collectionOfMaps = newArrayWrapper( mapsToChain );
		return newChainingMap( collectionOfMaps );
	}

	@SafeVarargs
	@SuppressWarnings( "varargs" ) //for -Xlint
	public static <K, V> UnmodifiableArrayMap<K,V> newChainingArrayMapOf( UnmodifiableArrayMap<K,V>... mapsToChain )
	{
		var collectionOfMaps = newArrayWrapper( mapsToChain );
		return newChainingArrayMap( collectionOfMaps );
	}

	public static <E> UnmodifiableList<E> newSubList( UnmodifiableList<E> list, int startOffset, int endOffset )
	{
		if( endOffset - startOffset == 0 )
			return UnmodifiableList.of();
		return new SubList<>( list, startOffset, endOffset );
	}

	public static <E> E[] arrayOfObjectFromIterable( Iterable<E> iterable, int lengthHint )
	{
		Object[] elements = lengthHint == 0 ? ARRAY_OF_ZERO_OBJECTS : new Object[lengthHint];
		int length = 0;
		for( Object element : iterable )
		{
			if( length >= elements.length )
				elements = expand( elements );
			elements[length++] = element;
		}
		//noinspection ArrayEquality
		elements = elements == ARRAY_OF_ZERO_OBJECTS ? elements : Arrays.copyOf( elements, length );
		@SuppressWarnings( { "unchecked", "SuspiciousArrayCast" } )
		E[] result = (E[])elements;
		return result;
	}

	private static Object[] expand( Object[] elements )
	{
		int newLength = elements.length == 0 ? 16 : elements.length * 2;
		Object[] newElements = new Object[newLength];
		System.arraycopy( elements, 0, newElements, 0, elements.length );
		return newElements;
	}

	public static Integer[] newArray( int[] array )
	{
		Integer[] arrayOfInteger = new Integer[array.length];
		for( int i = 0; i < arrayOfInteger.length; i++ )
			arrayOfInteger[i] = array[i];
		return arrayOfInteger;
	}
}
