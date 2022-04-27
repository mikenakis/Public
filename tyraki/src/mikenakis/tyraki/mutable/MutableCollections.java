package mikenakis.tyraki.mutable;

import mikenakis.kit.DefaultComparator;
import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;
import mikenakis.kit.IdentityEqualityComparator;
import mikenakis.kit.ObjectHasher;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.buffer.CaseInsensitiveBufferEqualityComparator;
import mikenakis.kit.buffer.CaseInsensitiveBufferHasher;
import mikenakis.kit.coherence.AbstractCoherent;
import mikenakis.kit.coherence.Coherence;
import mikenakis.kit.functional.Procedure1;
import mikenakis.tyraki.CaseInsensitiveStringEqualityComparator;
import mikenakis.tyraki.CaseInsensitiveStringHasher;
import mikenakis.tyraki.IdentityHasher;
import mikenakis.tyraki.MutableArrayHashMap;
import mikenakis.tyraki.MutableArrayHashSet;
import mikenakis.tyraki.MutableArrayMap;
import mikenakis.tyraki.MutableArraySet;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerable;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableHashMap;
import mikenakis.tyraki.MutableHashSet;
import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.MutableMap;
import mikenakis.tyraki.Queue;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableEnumerable;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Comparator;

/**
 * Mutable Collections.
 *
 * TODO: introduce rigid (mutable but structurally immutable) collections.
 *
 * @author michael.gr
 */
public final class MutableCollections extends AbstractCoherent
{
	public static MutableCollections of( Coherence coherence )
	{
		return new MutableCollections( coherence );
	}

	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	private static final float DEFAULT_FILL_FACTOR = 0.75f;

	private MutableCollections( Coherence coherence  )
	{
		super( coherence );
	}

	/**
	 * Creates a new {@link MutableHashSet}.
	 *
	 * @param initialCapacity    the initial capacity of the set.
	 * @param fillFactor         the fill factor to maintain.
	 * @param hasher             the {@link Hasher} to use for hashing items.
	 * @param equalityComparator the {@link EqualityComparator} to use for comparing items.
	 * @param <T>                the type of the items.
	 *
	 * @return a new {@link MutableCollection} backed by a HashSet.
	 */
	public <T> MutableHashSet<T> newHashSet( int initialCapacity, float fillFactor, Hasher<? super T> hasher, EqualityComparator<? super T> equalityComparator )
	{
		return new ConcreteMutableHashSet<>( this, initialCapacity, fillFactor, hasher, equalityComparator );
	}

	public <T> MutableArrayHashSet<T> newArrayHashSet( int initialCapacity, float fillFactor, Hasher<? super T> hasher, EqualityComparator<? super T> equalityComparator )
	{
		return new ConcreteMutableArrayHashSet<>( this, initialCapacity, fillFactor, hasher, equalityComparator );
	}

	public <T> MutableHashSet<T> newLinkedHashSet( int initialCapacity, float fillFactor, Hasher<? super T> hasher, EqualityComparator<? super T> equalityComparator )
	{
		return new ConcreteMutableLinkedHashSet<>( this, initialCapacity, fillFactor, hasher, equalityComparator );
	}

	public <T> MutableHashSet<T> newLinkedHashSet()
	{
		return newLinkedHashSet( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR );
	}

	/**
	 * Creates a new array list.
	 *
	 * @param <T>                the type of elements of the list.
	 * @param initialCapacity    the initial capacity of the list.
	 * @param equalityComparator the {@link EqualityComparator} to use.
	 *
	 * @return a new array list.
	 */
	public <T> MutableList<T> newArrayList( int initialCapacity, EqualityComparator<? super T> equalityComparator )
	{
		return new ConcreteMutableArrayList<>( this, equalityComparator, initialCapacity );
	}

	public <K, V> MutableHashMap<K,V> newHashMap( int initialCapacity, float loadFactor, Hasher<? super K> keyHasher, EqualityComparator<? super K> keyEqualityComparator,
		EqualityComparator<? super V> valueEqualityComparator )
	{
		return new ConcreteMutableHashMap<>( this, initialCapacity, loadFactor, keyHasher, keyEqualityComparator, valueEqualityComparator );
		//return new ConcreteMutableLinkedHashMap<>( this, initialCapacity, loadFactor, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <K, V> MutableHashMap<K,V> newKeyReferencingHashMap( ReferencingMethod referencingMethod, Hasher<? super K> keyHasher,
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		return new KeyReferencingHashMap<>( this, DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, referencingMethod, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <K, V> MutableHashMap<K,V> newValueReferencingHashMap( ReferencingMethod referencingMethod, Hasher<? super K> keyHasher,
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		return new ValueReferencingHashMap<>( this, DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, referencingMethod, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <K, V> MutableArrayHashMap<K,V> newArrayHashMap( int initialCapacity, float fillFactor, Hasher<? super K> keyHasher,
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		return new ConcreteMutableArrayHashMap<>( this, initialCapacity, fillFactor, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <K, V> MutableHashMap<K,V> newLinkedHashMap( int initialCapacity, float fillFactor, Hasher<? super K> keyHasher,
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		return new ConcreteMutableLinkedHashMap<>( this, initialCapacity, fillFactor, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <K, V> MutableHashMap<K,V> newLinkedHashMap()
	{
		return newLinkedHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR );
	}

	public <K, V> MutableHashMap<K,V> newIdentityLinkedHashMap()
	{
		return newLinkedHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	/**
	 * Creates a new array map. An array map stores bindings in an array, so they can also be accessed by index.
	 */
	public <K, V> MutableArrayMap<K,V> newArrayMap()
	{
		return new ConcreteMutableArrayMap<>( this, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <V> MutableMap<Integer,V> newMapOnList( MutableList<V> list )
	{
		return new MutableMapOnMutableList<>( this, list );
	}

	public <V> MutableList<V> newListOnMap( MutableMap<Integer,V> map )
	{
		return new MutableListOnMutableMap<>( this, map );
	}

	/**
	 * Returns a new array set.
	 *
	 * @param <T>                the type of the elements.
	 *
	 * @param initialCapacity    the initial capacity of the backing array list.
	 * @param equalityComparator the {@link EqualityComparator} to use for element comparisons.
	 * @return a new {@link MutableCollection} behaving as a set and backed by an array.
	 */
	public <T> MutableArraySet<T> newArraySet( int initialCapacity, EqualityComparator<? super T> equalityComparator )
	{
		return new ConcreteMutableArraySet<>( this, equalityComparator, initialCapacity );
	}

	public <K, V> MutableArrayMap<K,V> newCachingArrayMap( int capacity, EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		return new CachingArrayMap<>( this, capacity, keyEqualityComparator, valueEqualityComparator );
	}

	public <K, V> MutableMap<K,V> newCachingHashMap( int capacity, Hasher<? super K> keyHasher, EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		return new CachingHashMap<>( this, capacity, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <K, V> MutableMap<K,V> newCachingIdentityHashMap( int capacity )
	{
		var keyHasher = IdentityHasher.getInstance();
		var keyEqualityComparator = IdentityEqualityComparator.getInstance();
		var valueEqualityComparator = DefaultEqualityComparator.getInstance();
		return new CachingHashMap<>( this, capacity, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <E> MutableCollection<E> newCachingHashSet( int capacity, Hasher<? super E> hasher, EqualityComparator<? super E> equalityComparator )
	{
		return new CachingHashSet<>( this, capacity, hasher, equalityComparator );
	}

	public <E> MutableList<E> newSingleElementMutableList( E element )
	{
		return new SingleElementList<>( this, DefaultEqualityComparator.getInstance(), element );
	}

	public <K, V> MutableHashMap<K,V> newTreeMap( Hasher<? super K> keyHasher, Comparator<? super K> keyComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		return new ConcreteMutableTreeMap<>( this, keyHasher, keyComparator, valueEqualityComparator );
	}

	public <T> MutableHashSet<T> newTreeSet( Hasher<? super T> hasher, Comparator<? super T> comparator )
	{
		return new ConcreteMutableTreeSet<>( this, hasher, comparator );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public MutableHashSet<String> newCaseInsensitiveStringHashSet()
	{
		return newHashSet( CaseInsensitiveStringHasher.INSTANCE, CaseInsensitiveStringEqualityComparator.INSTANCE );
	}

	public MutableHashSet<Buffer> newCaseInsensitiveBufferHashSet()
	{
		return newHashSet( CaseInsensitiveBufferHasher.INSTANCE, CaseInsensitiveBufferEqualityComparator.INSTANCE );
	}

	/**
	 * Creates a new {@link MutableHashSet} with set semantics, implemented using hashing.
	 *
	 * @param <T> the type of the elements in the set.
	 *
	 * @return a new {@link MutableHashSet} with set semantics.
	 */
	public <T> MutableHashSet<T> newHashSet()
	{
		return newHashSet( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	/**
	 * Creates a new {@link MutableHashSet}.
	 *
	 * @param initialCapacity the initial capacity of the set.
	 * @param fillFactor      the fill factor to maintain.
	 * @param <T>             the type of the items.
	 *
	 * @return a new {@link MutableCollection} backed by a HashSet.
	 */
	public <T> MutableHashSet<T> newHashSet( int initialCapacity, float fillFactor )
	{
		return newHashSet( initialCapacity, fillFactor, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	/**
	 * Creates a new {@link MutableHashSet}.
	 *
	 * @param hasher             the {@link Hasher} to use for hashing items.
	 * @param equalityComparator the {@link EqualityComparator} to use for comparing items.
	 * @param <T>                the type of the items.
	 *
	 * @return a new {@link MutableCollection} backed by a HashSet.
	 */
	public <T> MutableHashSet<T> newHashSet( Hasher<? super T> hasher, EqualityComparator<? super T> equalityComparator )
	{
		return newHashSet( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, hasher, equalityComparator );
	}

	/**
	 * Creates a new {@link MutableCollection} with identity set semantics, implemented using hashing.
	 *
	 * @param <T> the type of the elements in the set.
	 *
	 * @return a new {@link MutableCollection} with identity set semantics.
	 */
	public <T> MutableHashSet<T> newIdentityHashSet()
	{
		return newHashSet( IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance() );
	}

	public <T> MutableArrayHashSet<T> newIdentityArrayHashSet()
	{
		return newArrayHashSet( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance() );
	}

	public <T> MutableHashSet<T> newIdentityLinkedHashSet()
	{
		return newLinkedHashSet( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance() );
	}

	public <E> MutableArrayHashSet<E> newArrayHashSet( Hasher<? super E> hasher, EqualityComparator<? super E> equalityComparator )
	{
		return newArrayHashSet( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, hasher, equalityComparator );
	}

	public <E> MutableArrayHashSet<E> newArrayHashSet( int initialCapacity, float fillFactor )
	{
		return newArrayHashSet( initialCapacity, fillFactor, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	public <E> MutableHashSet<E> newLinkedHashSet( int initialCapacity, float fillFactor )
	{
		return newLinkedHashSet( initialCapacity, fillFactor, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	public <E> MutableArrayHashSet<E> newArrayHashSet()
	{
		return newArrayHashSet( 16, 0.75f );
	}

	public <T> MutableList<T> newArrayList( EqualityComparator<? super T> equalityComparator )
	{
		return newArrayList( 0, equalityComparator );
	}

	public <T> MutableList<T> newArrayList()
	{
		return newArrayList( 0, DefaultEqualityComparator.getInstance() );
	}

	public <T> MutableList<T> newIdentityArrayList()
	{
		return newArrayList( 0, IdentityEqualityComparator.getInstance() );
	}

	public <T> MutableList<T> newArrayList( int capacity )
	{
		return newArrayList( capacity, DefaultEqualityComparator.getInstance() );
	}

	public <T> MutableList<T> newArrayList( UnmodifiableEnumerable<? extends T> items )
	{
		MutableList<T> list = newArrayList();
		list.addAll( items );
		return list;
	}

	public <T> MutableList<T> newArrayList( UnmodifiableCollection<? extends T> items )
	{
		MutableList<T> list = newArrayList( items.size() );
		list.addAll( items );
		return list;
	}

	public <K, V> MutableHashMap<K,V> newIdentityHashMap()
	{
		return newHashMap( IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance(), IdentityEqualityComparator.getInstance() );
	}

	public <K, V> MutableHashMap<K,V> newHashMap()
	{
		return newHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> MutableHashMap<K,V> newHashMap( Hasher<? super K> keyHasher, EqualityComparator<? super K> keyEqualityComparator,
		EqualityComparator<? super V> valueEqualityComparator )
	{
		return newHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <K, V> MutableHashMap<K,V> newHashMap( int initialCapacity, float loadFactor )
	{
		return newHashMap( initialCapacity, loadFactor, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> MutableArrayHashMap<K,V> newArrayHashMap()
	{
		return newArrayHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K,V> MutableArrayHashMap<K,V> newIdentityArrayHashMap()
	{
		return newArrayHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> MutableArrayHashMap<K,V> newArrayHashMap( int initialCount, float fillFactor )
	{
		return newArrayHashMap( initialCount, fillFactor, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> MutableArrayHashMap<K,V> newArrayHashMap( Hasher<? super K> keyHasher, EqualityComparator<? super K> keyEqualityComparator,
		EqualityComparator<? super V> valueEqualityComparator )
	{
		return newArrayHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <K, V> MutableHashMap<K,V> newLinkedHashMap( int initialCount, float fillFactor )
	{
		return newLinkedHashMap( initialCount, fillFactor, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> MutableHashMap<K,V> newLinkedHashMap( Hasher<? super K> keyHasher, EqualityComparator<? super K> keyEqualityComparator,
		EqualityComparator<? super V> valueEqualityComparator )
	{
		return newLinkedHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <V> MutableHashMap<String,V> newCaseInsensitiveStringKeyHashMap()
	{
		return newHashMap( CaseInsensitiveStringHasher.INSTANCE, CaseInsensitiveStringEqualityComparator.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	public <V> MutableHashMap<Buffer,V> newCaseInsensitiveBufferKeyHashMap()
	{
		return newHashMap( CaseInsensitiveBufferHasher.INSTANCE, CaseInsensitiveBufferEqualityComparator.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	public <V> MutableArrayHashMap<String,V> newCaseInsensitiveStringKeyArrayHashMap()
	{
		return newArrayHashMap( CaseInsensitiveStringHasher.INSTANCE, CaseInsensitiveStringEqualityComparator.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	public <V> MutableHashMap<String,V> newCaseInsensitiveStringKeyLinkedHashMap()
	{
		return newLinkedHashMap( CaseInsensitiveStringHasher.INSTANCE, CaseInsensitiveStringEqualityComparator.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	public <V> MutableArrayHashMap<Buffer,V> newCaseInsensitiveBufferKeyArrayHashMap()
	{
		return newArrayHashMap( CaseInsensitiveBufferHasher.INSTANCE, CaseInsensitiveBufferEqualityComparator.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	public <K, V> MutableBiHashMap<K,V> newBiHashMap( int initialCapacity, float fillFactor, Hasher<? super K> keyHasher, Hasher<? super V> valueHasher,
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		MutableBiHashMap<K,V> forward = new MutableBiHashMap<>( this, initialCapacity, fillFactor, keyHasher, keyEqualityComparator, valueEqualityComparator );
		MutableBiHashMap<V,K> reverse = new MutableBiHashMap<>( this, initialCapacity, fillFactor, valueHasher, valueEqualityComparator, keyEqualityComparator );
		forward.setReverse( reverse );
		reverse.setReverse( forward );
		return forward;
	}

	public <K, V> MutableBiHashMap<K,V> newBiHashMap()
	{
		return newBiHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, ObjectHasher.INSTANCE, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(),
			DefaultEqualityComparator.getInstance() );
	}

	/**
	 * Creates a new array set.
	 *
	 * @param <T> the type of the elements.
	 *
	 * @return a new array set.
	 */
	public <T> MutableArraySet<T> newArraySet()
	{
		return newArraySet( 0, DefaultEqualityComparator.getInstance() );
	}

	/**
	 * Creates a new array set.
	 *
	 * @param equalityComparator the {@link EqualityComparator}.
	 * @param <T>                the type of the elements.
	 *
	 * @return a new array set.
	 */
	public <T> MutableArraySet<T> newArraySet( EqualityComparator<T> equalityComparator )
	{
		return newArraySet( 0, equalityComparator );
	}

	public <K, V> MutableArrayMap<K,V> newCachingArrayMap( int capacity )
	{
		return newCachingArrayMap( capacity, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> MutableMap<K,V> newCachingHashMap( int capacity )
	{
		return newCachingHashMap( capacity, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <E> MutableCollection<E> newCachingHashSet( int capacity )
	{
		return newCachingHashSet( capacity, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	public <K, V> MutableHashMap<K,V> newTreeMap()
	{
		return newTreeMap( ObjectHasher.INSTANCE, DefaultComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> MutableMap<K,V> newKeyReferencingHashMap( ReferencingMethod referencingMethod )
	{
		return newKeyReferencingHashMap( referencingMethod, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> MutableMap<K,V> newWeakKeyHashMap()
	{
		return newKeyReferencingHashMap( ReferencingMethod.WEAK );
	}

	public <K, V> MutableMap<K,V> newSoftKeyHashMap()
	{
		return newKeyReferencingHashMap( ReferencingMethod.SOFT );
	}

	public <K, V> MutableHashMap<K,V> newValueReferencingHashMap( ReferencingMethod referencingMethod )
	{
		return newValueReferencingHashMap( referencingMethod, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> MutableHashMap<K,V> newWeakValueHashMap()
	{
		return newValueReferencingHashMap( ReferencingMethod.WEAK, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> MutableMap<K,V> newWeakKeyIdentityHashMap()
	{
		return newKeyReferencingHashMap( ReferencingMethod.WEAK, IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <T> MutableEnumerable<T> newMutableEnumerableOnUnmodifiableEnumerable( UnmodifiableEnumerable<T> unmodifiableEnumerable, Procedure1<T> deleter )
	{
		return new MutableEnumerableOnUnmodifiableEnumerable<>( this, unmodifiableEnumerable, deleter );
	}

	public <T> MutableEnumerator<T> newMutableEnumeratorOnUnmodifiableEnumerator( UnmodifiableEnumerator<T> unmodifiableEnumerator, Procedure1<T> deleter )
	{
		return new MutableEnumeratorOnUnmodifiableEnumerator<>( this, unmodifiableEnumerator, deleter );
	}

	public <E> Queue<E> newLinkedQueue()
	{
		return new LinkedQueue<>( this );
	}
}
