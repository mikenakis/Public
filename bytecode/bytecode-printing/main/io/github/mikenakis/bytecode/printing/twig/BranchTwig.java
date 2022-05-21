package io.github.mikenakis.bytecode.printing.twig;

abstract class BranchTwig extends Twig
{
	private final String text;

	BranchTwig( String text )
	{
		this.text = text;
	}

	@Override public String text()
	{
		return text;
	}
}
