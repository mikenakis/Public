package io.github.mikenakis.lifetime;

import io.github.mikenakis.coherence.Coherence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiCloser extends AbstractMortalCoherent
{
	private final List<Mortal> mortals = new ArrayList<>();

	public MultiCloser( Coherence coherence, Mortal... mortals )
	{
		super( coherence );
		this.mortals.addAll( Arrays.asList( mortals ) );
	}

	public <T extends Mortal> T add( T mortal )
	{
		mortals.add( mortal );
		return mortal;
	}

	@Override protected void onClose()
	{
		super.onClose();
		for( int i = mortals.size() - 1;  i >= 0;  i-- )
			mortals.get( i ).close();
	}
}
