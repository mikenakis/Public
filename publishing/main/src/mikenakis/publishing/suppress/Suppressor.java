package mikenakis.publishing.suppress;

import mikenakis.kit.mutation.AbstractCoherent;
import mikenakis.kit.mutation.Coherence;

/**
 * Base class for suppressors.
 *
 * @author michael.gr
 */
public abstract class Suppressor extends AbstractCoherent implements Suppressable
{
	private int suppressionCount = 0;

	protected Suppressor( Coherence coherence )
	{
		super( coherence );
	}

	@Override public Suppression newSuppression()
	{
		return new Suppression( coherence, this );
	}

	@Override public void incrementSuppressionCount()
	{
		suppressionCount++;
		if( suppressionCount == 1 )
			onSuppress();
	}

	@Override public void decrementSuppressionCount()
	{
		assert suppressionCount > 0;
		suppressionCount--;
		if( suppressionCount == 0 )
			onRelease();
	}

	public boolean isSuppressing()
	{
		return suppressionCount > 0;
	}

	protected abstract void onSuppress();

	protected abstract void onRelease();
}
