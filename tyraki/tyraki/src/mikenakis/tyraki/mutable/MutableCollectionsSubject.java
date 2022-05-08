package mikenakis.tyraki.mutable;

import mikenakis.coherence.AbstractCoherent;
import mikenakis.coherence.FreezableCoherence;
import mikenakis.immutability.ImmutabilitySelfAssessable;

/**
 * A subject of {@link MutableCollections}.
 *
 * @author michael.gr
 */
public abstract class MutableCollectionsSubject extends AbstractCoherent implements ImmutabilitySelfAssessable
{
	protected final MutableCollections mutableCollections;

	protected MutableCollectionsSubject( MutableCollections mutableCollections )
	{
		super( mutableCollections.coherence() );
		assert mutableCollections != null;
		this.mutableCollections = mutableCollections;
	}

	@Override public boolean isImmutable()
	{
		return coherence instanceof FreezableCoherence freezableCoherence && freezableCoherence.isFrozen();
	}
}
