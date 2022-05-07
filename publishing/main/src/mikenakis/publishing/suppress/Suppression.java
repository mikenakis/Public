package mikenakis.publishing.suppress;

import mikenakis.coherence.Coherence;
import mikenakis.lifetime.AbstractMortalCoherent;

public class Suppression extends AbstractMortalCoherent
{
	private final Suppressable suppressable;

	public Suppression( Coherence coherence, Suppressable suppressable )
	{
		super( coherence );
		this.suppressable = suppressable;
		suppressable.incrementSuppressionCount();
	}

	@Override protected void onClose()
	{
		suppressable.decrementSuppressionCount();
		super.onClose();
	}
}
