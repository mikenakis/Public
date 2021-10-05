package mikenakis.bytecode.printing.twig;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;

final class ConcreteBranchTwig extends BranchTwig
{
	static Twig of( String text, List<Twig> children )
	{
		return new ConcreteBranchTwig( text, children );
	}

	private final List<Twig> children;

	private ConcreteBranchTwig( String text, List<Twig> children )
	{
		super( text );
		this.children = children;
	}

	@Override public List<Twig> children()
	{
		return children;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return text() + " (" + children.size() + " children)";
	}
}
