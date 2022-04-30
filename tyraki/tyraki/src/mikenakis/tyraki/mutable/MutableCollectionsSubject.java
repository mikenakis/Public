package mikenakis.tyraki.mutable;

import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.kit.coherence.AbstractCoherent;
import mikenakis.kit.coherence.FreezableCoherence;

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
