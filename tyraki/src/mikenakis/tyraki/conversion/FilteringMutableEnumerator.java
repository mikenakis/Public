package mikenakis.tyraki.conversion;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.function.Predicate;

/**
 * Filtering {@link MutableEnumerator}.
 *
 * @author michael.gr
 */
final class FilteringMutableEnumerator<E> implements MutableEnumerator.Defaults<E>
{
	private final MutableEnumerator<E> enumeratorToFilter;
	private final Predicate<? super E> predicate;

	FilteringMutableEnumerator( MutableEnumerator<E> enumeratorToFilter, Predicate<? super E> predicate )
	{
		assert enumeratorToFilter != null;
		assert predicate != null;
		this.enumeratorToFilter = enumeratorToFilter;
		this.predicate = predicate;
		skipToMatch();
	}

	@Override public void deleteCurrent()
	{
		enumeratorToFilter.deleteCurrent();
	}

	@Override public boolean isFinished()
	{
		return enumeratorToFilter.isFinished();
	}

	@Override public E getCurrent()
	{
		return enumeratorToFilter.getCurrent();
	}

	@Override public UnmodifiableEnumerator<E> moveNext()
	{
		enumeratorToFilter.moveNext();
		skipToMatch();
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		var builder = new StringBuilder();
		unmodifiableEnumeratorToStringBuilder( builder );
		return builder.toString();
	}

	private void skipToMatch()
	{
		while( !enumeratorToFilter.isFinished() && !predicate.test( enumeratorToFilter.getCurrent() ) )
			enumeratorToFilter.moveNext();
	}
}
