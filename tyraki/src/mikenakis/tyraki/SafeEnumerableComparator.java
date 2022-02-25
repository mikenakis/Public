package mikenakis.tyraki;

import mikenakis.kit.Hasher;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.BooleanFunction2;
import mikenakis.kit.mutation.FreezableMutationContext;
import mikenakis.kit.mutation.TemporaryMutationContext;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.tyraki.mutable.MutableCollections;

import java.util.Objects;

/**
 * A comparator for {@link UnmodifiableEnumerable} which avoids cycles.
 *
 * @param <R>
 */
public class SafeEnumerableComparator<R> extends Mutable
{
	public static <R> boolean compare( BooleanFunction2<R,R> valueComparator, BooleanFunction2<R,R> identityComparator, Hasher<R> identityHasher, //
		UnmodifiableEnumerable<? extends R> enumerableA, UnmodifiableEnumerable<? extends R> enumerableB )
	{
		return Kit.tryGetWith( FreezableMutationContext.of(), mutationContext -> //
		{
			SafeEnumerableComparator<R> safeEnumerableComparator = new SafeEnumerableComparator<>( mutationContext, valueComparator, identityComparator, identityHasher );
			return safeEnumerableComparator.compare( enumerableA, enumerableB );
		} );
	}

	private final BooleanFunction2<R,R> valueComparator;
	private final BooleanFunction2<R,R> identityComparator;
	private final Hasher<R> identityHasher;
	private final MutableCollection<MyComparison> comparisons = MutableCollections.of( mutationContext ).newLinkedHashSet();

	private SafeEnumerableComparator( MutationContext mutationContext, BooleanFunction2<R,R> valueComparator, BooleanFunction2<R,R> identityComparator, Hasher<R> identityHasher )
	{
		super( mutationContext );
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
			MyComparison comparison = new MyComparison( elementA, elementB );
			if( comparisons.tryAdd( comparison ).isEmpty() )
				if( !valueComparator.invoke( elementA, elementB ) )
					return false;
			enumeratorA.moveNext();
			enumeratorB.moveNext();
		}
	}

	private class MyComparison
	{
		final R elementA;
		final R elementB;

		MyComparison( R elementA, R elementB )
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

		public boolean equals( MyComparison other )
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
