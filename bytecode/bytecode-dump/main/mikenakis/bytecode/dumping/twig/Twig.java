package mikenakis.bytecode.dumping.twig;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Represents a node in a tree of strings.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface Twig
{
	String getPayload();

	Collection<Twig> getChildren();

	static Twig of( String payload )
	{
		return new LeafTwig( payload );
	}

	static Twig of( String payload, Twig... arrayOfChildren )
	{
		return of( payload, Arrays.asList( arrayOfChildren ) );
	}

	static Twig of( String payload, List<Twig> children )
	{
		return new BranchTwig( payload, children );
	}
}
