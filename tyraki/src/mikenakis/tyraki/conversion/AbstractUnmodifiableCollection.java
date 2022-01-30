package mikenakis.tyraki.conversion;

import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.DebugView;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableEnumerable;
import mikenakis.kit.EqualityComparator;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Base class in this package for classes implementing {@link UnmodifiableCollection}.
 *
 * @author michael.gr
 */
abstract class AbstractUnmodifiableCollection<E> extends AbstractUnmodifiableEnumerable<E> implements UnmodifiableCollection.Defaults<E>
{
	@SuppressWarnings( { "unused", "FieldNamingConvention" } )
	protected final DebugView _debugView = DebugView.create( () -> this );

	private final EqualityComparator<? super E> equalityComparator;

	AbstractUnmodifiableCollection( EqualityComparator<? super E> equalityComparator )
	{
		this.equalityComparator = equalityComparator;
	}

	@Override public final EqualityComparator<? super E> getEqualityComparator()
	{
		return equalityComparator;
	}

	@SuppressWarnings( "deprecation" ) @Deprecated @Override public final boolean equals( Object other )
	{
		if( other instanceof UnmodifiableCollection )
			return equalsCollection( Kit.upCast( other ) );
		if( other instanceof UnmodifiableEnumerable )
			return equalsEnumerable( Kit.upCast( other ) );
		assert false;
		return false;
	}

	@ExcludeFromJacocoGeneratedReport @OverridingMethodsMustInvokeSuper
	@Override public final String toString()
	{
		return size() + " elements";
	}
}
