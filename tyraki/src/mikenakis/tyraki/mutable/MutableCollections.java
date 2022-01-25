package mikenakis.tyraki.mutable;

import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.kit.Hasher;
import mikenakis.kit.IdentityEqualityComparator;
import mikenakis.kit.ObjectHasher;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.buffer.CaseInsensitiveBufferEqualityComparator;
import mikenakis.kit.buffer.CaseInsensitiveBufferHasher;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.tyraki.CaseInsensitiveStringEqualityComparator;
import mikenakis.tyraki.CaseInsensitiveStringHasher;
import mikenakis.tyraki.FreezableArrayHashMap;
import mikenakis.tyraki.FreezableArrayHashSet;
import mikenakis.tyraki.FreezableArrayMap;
import mikenakis.tyraki.FreezableArraySet;
import mikenakis.tyraki.FreezableHashMap;
import mikenakis.tyraki.FreezableHashSet;
import mikenakis.tyraki.FreezableList;
import mikenakis.tyraki.IdentityHasher;
import mikenakis.tyraki.MutableArrayMap;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableHashMap;
import mikenakis.tyraki.MutableHashSet;
import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.MutableMap;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableEnumerable;
import mikenakis.kit.DefaultComparator;
import mikenakis.kit.EqualityComparator;

import java.util.Comparator;

/**
 * Mutable Collections.
 *
 * TODO: introduce rigid (mutable but structurally immutable) collections.
 *
 * @author michael.gr
 */
public final class MutableCollections extends Mutable
{
	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	private static final float DEFAULT_FILL_FACTOR = 0.75f;

	public MutableCollections( MutationContext mutationContext  )
	{
		super( mutationContext );
	}

	public boolean assertCoherence() //FIXME XXX TODO inline this!
	{
		return inContextAssertion();
	}

	/**
	 * Creates a new {@link FreezableHashSet}.
	 *
	 * @param initialCapacity    the initial capacity of the set.
	 * @param fillFactor         the fill factor to maintain.
	 * @param hasher             the {@link Hasher} to use for hashing items.
	 * @param equalityComparator the {@link EqualityComparator} to use for comparing items.
	 * @param <T>                the type of the items.
	 *
	 * @return a new {@link MutableCollection} backed by a HashSet.
	 */
	public <T> FreezableHashSet<T> newHashSet( int initialCapacity, float fillFactor, Hasher<? super T> hasher, EqualityComparator<? super T> equalityComparator )
	{
		return new FreezableMutableHashSet<>( this, initialCapacity, fillFactor, hasher, equalityComparator );
	}

	public <T> FreezableArrayHashSet<T> newArrayHashSet( int initialCapacity, float fillFactor, Hasher<? super T> hasher, EqualityComparator<? super T> equalityComparator )
	{
		return new FreezableMutableArrayHashSet<>( this, initialCapacity, fillFactor, hasher, equalityComparator );
	}

	public <T> FreezableHashSet<T> newLinkedHashSet( int initialCapacity, float fillFactor, Hasher<? super T> hasher, EqualityComparator<? super T> equalityComparator )
	{
		return new FreezableMutableLinkedHashSet<>( this, initialCapacity, fillFactor, hasher, equalityComparator );
	}

	public <T> FreezableHashSet<T> newLinkedHashSet()
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
	public <T> FreezableList<T> newArrayList( int initialCapacity, EqualityComparator<? super T> equalityComparator )
	{
		return new FreezableMutableArrayList<>( this, equalityComparator, initialCapacity );
	}

	public <K, V> FreezableHashMap<K,V> newHashMap( int initialCapacity, float loadFactor, Hasher<? super K> keyHasher, EqualityComparator<? super K> keyEqualityComparator,
		EqualityComparator<? super V> valueEqualityComparator )
	{
		return new FreezableMutableHashMap<>( this, initialCapacity, loadFactor, keyHasher, keyEqualityComparator, valueEqualityComparator );
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

	public <K, V> FreezableArrayHashMap<K,V> newArrayHashMap( int initialCapacity, float fillFactor, Hasher<? super K> keyHasher,
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		return new FreezableMutableArrayHashMap<>( this, initialCapacity, fillFactor, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <K, V> FreezableHashMap<K,V> newLinkedHashMap( int initialCapacity, float fillFactor, Hasher<? super K> keyHasher,
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		return new FreezableMutableLinkedHashMap<>( this, initialCapacity, fillFactor, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <K, V> FreezableHashMap<K,V> newLinkedHashMap()
	{
		return newLinkedHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR );
	}

	public <K, V> FreezableHashMap<K,V> newIdentityLinkedHashMap()
	{
		return newLinkedHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	/**
	 * Creates a new array map. An array map stores bindings in an array, so they can also be accessed by index.
	 */
	public <K, V> FreezableArrayMap<K,V> newArrayMap()
	{
		return new FreezableMutableArrayMap<>( this, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
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
	public <T> FreezableArraySet<T> newArraySet( int initialCapacity, EqualityComparator<? super T> equalityComparator )
	{
		return new FreezableMutableArraySet<>( this, equalityComparator, initialCapacity );
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

	public <K, V> FreezableHashMap<K,V> newTreeMap( Hasher<? super K> keyHasher, Comparator<? super K> keyComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		return new FreezableMutableTreeMap<>( this, keyHasher, keyComparator, valueEqualityComparator );
	}

	public <T> FreezableHashSet<T> newTreeSet( Hasher<? super T> hasher, Comparator<? super T> comparator )
	{
		return new FreezableMutableTreeSet<>( this, hasher, comparator );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public FreezableHashSet<String> newCaseInsensitiveStringHashSet()
	{
		return newHashSet( CaseInsensitiveStringHasher.INSTANCE, CaseInsensitiveStringEqualityComparator.INSTANCE );
	}

	public FreezableHashSet<Buffer> newCaseInsensitiveBufferHashSet()
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
	public <T> FreezableHashSet<T> newHashSet()
	{
		return newHashSet( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	/**
	 * Creates a new {@link FreezableHashSet}.
	 *
	 * @param initialCapacity the initial capacity of the set.
	 * @param fillFactor      the fill factor to maintain.
	 * @param <T>             the type of the items.
	 *
	 * @return a new {@link MutableCollection} backed by a HashSet.
	 */
	public <T> FreezableHashSet<T> newHashSet( int initialCapacity, float fillFactor )
	{
		return newHashSet( initialCapacity, fillFactor, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	/**
	 * Creates a new {@link FreezableHashSet}.
	 *
	 * @param hasher             the {@link Hasher} to use for hashing items.
	 * @param equalityComparator the {@link EqualityComparator} to use for comparing items.
	 * @param <T>                the type of the items.
	 *
	 * @return a new {@link MutableCollection} backed by a HashSet.
	 */
	public <T> FreezableHashSet<T> newHashSet( Hasher<? super T> hasher, EqualityComparator<? super T> equalityComparator )
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
	public <T> FreezableHashSet<T> newIdentityHashSet()
	{
		return newHashSet( IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance() );
	}

	public <T> FreezableArrayHashSet<T> newIdentityArrayHashSet()
	{
		return newArrayHashSet( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance() );
	}

	public <T> FreezableHashSet<T> newIdentityLinkedHashSet()
	{
		return newLinkedHashSet( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance() );
	}

	public <E> FreezableArrayHashSet<E> newArrayHashSet( Hasher<? super E> hasher, EqualityComparator<? super E> equalityComparator )
	{
		return newArrayHashSet( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, hasher, equalityComparator );
	}

	public <E> FreezableArrayHashSet<E> newArrayHashSet( int initialCapacity, float fillFactor )
	{
		return newArrayHashSet( initialCapacity, fillFactor, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	public <E> FreezableHashSet<E> newLinkedHashSet( int initialCapacity, float fillFactor )
	{
		return newLinkedHashSet( initialCapacity, fillFactor, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	public <E> FreezableArrayHashSet<E> newArrayHashSet()
	{
		return newArrayHashSet( 16, 0.75f );
	}

	public <T> FreezableList<T> newArrayList( EqualityComparator<? super T> equalityComparator )
	{
		return newArrayList( 0, equalityComparator );
	}

	public <T> FreezableList<T> newArrayList()
	{
		return newArrayList( 0, DefaultEqualityComparator.getInstance() );
	}

	public <T> FreezableList<T> newIdentityArrayList()
	{
		return newArrayList( 0, IdentityEqualityComparator.getInstance() );
	}

	public <T> FreezableList<T> newArrayList( int capacity )
	{
		return newArrayList( capacity, DefaultEqualityComparator.getInstance() );
	}

	public <T> FreezableList<T> newArrayList( UnmodifiableEnumerable<? extends T> items )
	{
		FreezableList<T> list = newArrayList();
		list.addAll( items );
		return list;
	}

	public <T> FreezableList<T> newArrayList( UnmodifiableCollection<? extends T> items )
	{
		FreezableList<T> list = newArrayList( items.size() );
		list.addAll( items );
		return list;
	}

	public <K, V> FreezableHashMap<K,V> newIdentityHashMap()
	{
		return newHashMap( IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance(), IdentityEqualityComparator.getInstance() );
	}

	public <K, V> FreezableHashMap<K,V> newHashMap()
	{
		return newHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> FreezableHashMap<K,V> newHashMap( Hasher<? super K> keyHasher, EqualityComparator<? super K> keyEqualityComparator,
		EqualityComparator<? super V> valueEqualityComparator )
	{
		return newHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <K, V> FreezableHashMap<K,V> newHashMap( int initialCapacity, float loadFactor )
	{
		return newHashMap( initialCapacity, loadFactor, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> FreezableArrayHashMap<K,V> newArrayHashMap()
	{
		return newArrayHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K,V> FreezableArrayHashMap<K,V> newIdentityArrayHashMap()
	{
		return newArrayHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> FreezableArrayHashMap<K,V> newArrayHashMap( int initialCount, float fillFactor )
	{
		return newArrayHashMap( initialCount, fillFactor, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> FreezableArrayHashMap<K,V> newArrayHashMap( Hasher<? super K> keyHasher, EqualityComparator<? super K> keyEqualityComparator,
		EqualityComparator<? super V> valueEqualityComparator )
	{
		return newArrayHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <K, V> FreezableHashMap<K,V> newLinkedHashMap( int initialCount, float fillFactor )
	{
		return newLinkedHashMap( initialCount, fillFactor, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	public <K, V> FreezableHashMap<K,V> newLinkedHashMap( Hasher<? super K> keyHasher, EqualityComparator<? super K> keyEqualityComparator,
		EqualityComparator<? super V> valueEqualityComparator )
	{
		return newLinkedHashMap( DEFAULT_INITIAL_CAPACITY, DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	public <V> FreezableHashMap<String,V> newCaseInsensitiveStringKeyHashMap()
	{
		return newHashMap( CaseInsensitiveStringHasher.INSTANCE, CaseInsensitiveStringEqualityComparator.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	public <V> FreezableHashMap<Buffer,V> newCaseInsensitiveBufferKeyHashMap()
	{
		return newHashMap( CaseInsensitiveBufferHasher.INSTANCE, CaseInsensitiveBufferEqualityComparator.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	public <V> FreezableArrayHashMap<String,V> newCaseInsensitiveStringKeyArrayHashMap()
	{
		return newArrayHashMap( CaseInsensitiveStringHasher.INSTANCE, CaseInsensitiveStringEqualityComparator.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	public <V> FreezableHashMap<String,V> newCaseInsensitiveStringKeyLinkedHashMap()
	{
		return newLinkedHashMap( CaseInsensitiveStringHasher.INSTANCE, CaseInsensitiveStringEqualityComparator.INSTANCE, DefaultEqualityComparator.getInstance() );
	}

	public <V> FreezableArrayHashMap<Buffer,V> newCaseInsensitiveBufferKeyArrayHashMap()
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
	public <T> FreezableArraySet<T> newArraySet()
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
	public <T> FreezableArraySet<T> newArraySet( EqualityComparator<T> equalityComparator )
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

	public <K, V> FreezableHashMap<K,V> newTreeMap()
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
}
