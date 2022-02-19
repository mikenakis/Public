package mikenakis.tyraki;

import mikenakis.kit.Hasher;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.BooleanFunction2;
import mikenakis.tyraki.mutable.SingleThreadedMutableCollections;

import java.util.Objects;

/**
 * A comparator for {@link UnmodifiableEnumerable} which avoids cycles.
 *
 * @param <R>
 */
public class SafeEnumerableComparator<R>
{
	private final BooleanFunction2<R,R> valueComparator;
	private final BooleanFunction2<R,R> identityComparator;
	private final Hasher<R> identityHasher;
	private final MutableCollection<RowComparison> comparisons = SingleThreadedMutableCollections.instance().newLinkedHashSet();

	public SafeEnumerableComparator( BooleanFunction2<R,R> valueComparator, BooleanFunction2<R,R> identityComparator, Hasher<R> identityHasher )
	{
		this.valueComparator = valueComparator;
		this.identityComparator = identityComparator;
		this.identityHasher = identityHasher;
	}

	public boolean compare( UnmodifiableEnumerable<? extends R> enumerableA, UnmodifiableEnumerable<? extends R> enumerableB )
	{
		UnmodifiableEnumerator<? extends R> enumeratorA = enumerableA.newUnmodifiableEnumerator();
		UnmodifiableEnumerator<? extends R> enumeratorB = enumerableB.newUnmodifiableEnumerator();
		for( ; ; )
		{
			if( enumeratorA.isFinished() || enumeratorB.isFinished() )
				return enumeratorA.isFinished() == enumeratorB.isFinished();
			R elementA = enumeratorA.getCurrent();
			R elementB = enumeratorB.getCurrent();
			RowComparison comparison = new RowComparison( elementA, elementB );
			if( comparisons.tryAdd( comparison ).isEmpty() )
				if( !valueComparator.invoke( elementA, elementB ) )
					return false;
			enumeratorA.moveNext();
			enumeratorB.moveNext();
		}
	}

	private class RowComparison
	{
		final R elementA;
		final R elementB;

		RowComparison( R elementA, R elementB )
		{
			this.elementA = elementA;
			this.elementB = elementB;
		}

		@Override public boolean equals( Object other )
		{
			if( getClass() == other.getClass() )
				return equals( Kit.upCast( other ) );
			assert false;
			return false;
		}

		public boolean equals( RowComparison other )
		{
			if( this == other )
				return true;
			if( !identityComparator.invoke( elementA, other.elementA ) )
				return false;
			if( !identityComparator.invoke( elementB, other.elementB ) )
				return false;
			return true;
		}

		@Override public int hashCode()
		{
			int hashA = identityHasher.getHashCode( elementA );
			int hashB = identityHasher.getHashCode( elementB );
			return Objects.hash( hashA, hashB );
		}
	}
}
