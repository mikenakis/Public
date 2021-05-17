package mikenakis.bytecode.dumping.twig;

import java.util.Collection;
import java.util.Collections;

/**
 * Leaf {@link Twig}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class LeafTwig implements Twig
{
	public final String payload;

	LeafTwig( String payload )
	{
		this.payload = payload;
	}

	@Override public String getPayload()
	{
		return payload;
	}

	@Override public Collection<Twig> getChildren()
	{
		return Collections.emptyList();
	}

	@Override public String toString()
	{
		return payload;
	}
}
