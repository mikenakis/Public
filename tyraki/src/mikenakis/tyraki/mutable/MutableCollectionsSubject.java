package mikenakis.tyraki.mutable;

import mikenakis.kit.coherence.AbstractCoherent;

/**
 * A subject of {@link MutableCollections}.
 *
 * @author michael.gr
 */
public abstract class MutableCollectionsSubject extends AbstractCoherent
{
	protected final MutableCollections mutableCollections;

	protected MutableCollectionsSubject( MutableCollections mutableCollections )
	{
		super( mutableCollections.coherence() );
		assert mutableCollections != null;
		this.mutableCollections = mutableCollections;
	}
}
