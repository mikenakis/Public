package io.github.mikenakis.bytecode.printing.twig;

import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;
import java.util.stream.IntStream;

final class ArrayTwig extends BranchTwig
{
	private final List<Twig> children;

	ArrayTwig( String text, List<Twig> children )
	{
		super( text );
		this.children = children;
	}

	@Override public List<Twig> children()
	{
		return IntStream.range( 0, children.size() ).mapToObj( i -> //
		{
			Twig child = children.get( i );
			return ConcreteBranchTwig.of( "[" + i + "] " + child.text(), child.children() );
		} ).toList();
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return text() + " (" + children.size() + " children)";
	}
}
