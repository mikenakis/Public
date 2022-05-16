package mikenakis.tyraki;

import io.github.mikenakis.bathyscaphe.ImmutabilitySelfAssessable;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Function0;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for debugging.
 *
 * @author michael.gr
 */
@ExcludeFromJacocoGeneratedReport
public final class DebugView extends AbstractList<Object> implements ImmutabilitySelfAssessable
{
	public static <E> DebugView create( Function0<UnmodifiableEnumerable<E>> enumerableSupplier )
	{
		if( !Kit.areAssertionsEnabled() )
			return null;
		return new DebugView( enumerableSupplier );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private final Function0<? extends UnmodifiableEnumerable<?>> enumerableSupplier;
	private int lastModificationCount;
	private final List<Object> values = new ArrayList<>();

	private DebugView( Function0<? extends UnmodifiableEnumerable<?>> enumerableSupplier )
	{
		assert enumerableSupplier != null;
		this.enumerableSupplier = enumerableSupplier;
		lastModificationCount = -1;
	}

	@Override public int size()
	{
		UnmodifiableEnumerable<?> enumerable = enumerableSupplier.invoke();
		return enumerable.countElements();
	}

	@Override public Object get( int index )
	{
		UnmodifiableEnumerable<?> enumerable = enumerableSupplier.invoke();
		if( enumerable.getModificationCount() != lastModificationCount )
		{
			lastModificationCount = enumerable.getModificationCount();
			values.clear();
			enumerable.iterator().forEachRemaining( values::add );
		}
		return values.get( index );
	}

	@Override public boolean isImmutable()
	{
		return true; //we are not really immutable, but we are for all practical purposes.
	}
}
