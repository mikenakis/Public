package io.github.mikenakis.tyraki;

import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.implementation.ConcreteFreezableCoherence;
import io.github.mikenakis.kit.Hasher;
import io.github.mikenakis.kit.functional.BooleanFunction2;
import io.github.mikenakis.live.Mortal;
import io.github.mikenakis.tyraki.mutable.MutableCollections;

import java.util.Objects;

/**
 * A comparator for {@link UnmodifiableEnumerable} which avoids cycles.
 *
 * @param <R>
 */
public class SafeEnumerableComparator<R> extends AbstractCoherent
{
	public static <R> boolean compare( BooleanFunction2<R,R> valueComparator, BooleanFunction2<R,R> identityComparator, Hasher<R> identityHasher, //
		UnmodifiableEnumerable<? extends R> enumerableA, UnmodifiableEnumerable<? extends R> enumerableB )
	{
		return Mortal.tryGetWith( ConcreteFreezableCoherence.create(), coherence -> //
		{
			SafeEnumerableComparator<R> safeEnumerableComparator = new SafeEnumerableComparator<>( coherence, valueComparator, identityComparator, identityHasher );
			return safeEnumerableComparator.compare( enumerableA, enumerableB );
		} );
	}

	private final BooleanFunction2<R,R> valueComparator;
	private final BooleanFunction2<R,R> identityComparator;
	private final Hasher<R> identityHasher;
	private final MutableCollection<MyComparison> comparisons = MutableCollections.of( coherence() ).newLinkedHashSet();

	private SafeEnumerableComparator( Coherence coherence, BooleanFunction2<R,R> valueComparator, BooleanFunction2<R,R> identityComparator, Hasher<R> identityHasher )
	{
		super( coherence );
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
			R elementA = enumeratorA.current();
			R elementB = enumeratorB.current();
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

		@SuppressWarnings( "unchecked" ) @Deprecated @Override public boolean equals( Object other )
		{
			return other instanceof SafeEnumerableComparator<?>.MyComparison kin && equals( (SafeEnumerableComparator<R>.MyComparison)kin );
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
