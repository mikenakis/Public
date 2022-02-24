package mikenakis.tyraki.mutable;

import mikenakis.kit.mutation.Mutable;

/**
 * A subject of {@link MutableCollections}.
 *
 * @author michael.gr
 */
public abstract class MutableCollectionsSubject extends Mutable
{
	protected final MutableCollections mutableCollections;

	protected MutableCollectionsSubject( MutableCollections mutableCollections )
	{
		super( mutableCollections.mutationContext() );
		assert mutableCollections != null;
		this.mutableCollections = mutableCollections;
	}
}
