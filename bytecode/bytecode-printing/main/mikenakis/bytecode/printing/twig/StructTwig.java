package mikenakis.bytecode.printing.twig;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;
import java.util.Map;

final class StructTwig extends BranchTwig
{
	private final List<Map.Entry<String,Twig>> children;

	StructTwig( String text, List<Map.Entry<String,Twig>> children )
	{
		super( text );
		this.children = children;
	}

	@Override public List<Twig> children()
	{
		return children.stream().map( e -> //
		{
			Twig child = e.getValue();
			return ConcreteBranchTwig.of( e.getKey() + ": " + child.text(), child.children() );
		} ).toList();
	}

	@ExcludeFromJacocoGeneratedReport @Override public  String toString()
	{
		return text() + " (" + children.size() + " child nodes)";
	}
}
