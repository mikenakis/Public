package mikenakis.bytecode.printing.twig;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;

final class LeafTwig extends Twig
{
	private final String text;

	LeafTwig( String text )
	{
		this.text = text;
	}

	@Override public String text()
	{
		return text;
	}

	@Override public List<Twig> children()
	{
		return List.of();
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return text;
	}
}
