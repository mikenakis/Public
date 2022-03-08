package mikenakis.publishing.suppress;

import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

/**
 * Base class for suppressors.
 *
 * @author michael.gr
 */
public abstract class Suppressor extends Mutable implements Suppressable
{
	private int suppressionCount = 0;

	protected Suppressor( MutationContext mutationContext )
	{
		super( mutationContext );
	}

	@Override public Suppression newSuppression()
	{
		return new Suppression( mutationContext, this );
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
