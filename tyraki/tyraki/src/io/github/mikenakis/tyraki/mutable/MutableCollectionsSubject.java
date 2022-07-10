package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.bathyscaphe.ImmutabilitySelfAssessable;
import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.coherence.FreezableCoherence;

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
		return coherence() instanceof FreezableCoherence freezableCoherence && freezableCoherence.isFrozen();
	}
}
