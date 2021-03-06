package io.github.mikenakis.bytecode.printing.twig;

import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

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
			String prefix = e.getKey();
			if( !prefix.isEmpty() && !prefix.isBlank() )
				prefix += ": ";
			return ConcreteBranchTwig.of( prefix + child.text(), child.children() );
		} ).toList();
	}

	@ExcludeFromJacocoGeneratedReport @Override public  String toString()
	{
		return text() + " (" + children.size() + " child nodes)";
	}
}
