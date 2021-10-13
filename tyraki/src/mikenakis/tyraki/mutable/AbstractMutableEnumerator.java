package mikenakis.tyraki.mutable;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.MutableEnumerator;

/**
 * Abstract {@link MutableEnumerator}.
 *
 * @author michael.gr
 */
abstract class AbstractMutableEnumerator<E> extends MutableCollectionsSubject implements MutableEnumerator.Defaults<E>
{
	protected AbstractMutableEnumerator( MutableCollections mutableCollections )
	{
		super( mutableCollections );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		var builder = new StringBuilder();
		unmodifiableEnumeratorToStringBuilder( builder );
		return builder.toString();
	}
}
