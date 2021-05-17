package mikenakis.bytecode.dumping.twig;

import java.util.Collection;
import java.util.List;

/**
 * Branch (non-leaf) {@link Twig}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class BranchTwig extends LeafTwig
{
	protected final List<Twig> children;

	BranchTwig( String payload, List<Twig> children )
	{
		super( payload );
		assert children.stream().noneMatch( t -> t == null );
		this.children = children;
	}

	@Override public final String toString()
	{
		return payload + " (" + children.size() + " child nodes)";
	}

	@Override public Collection<Twig> getChildren()
	{
		return children;
	}
}
