package mikenakis.bytecode.printing.twig;

import java.util.List;
import java.util.Map;

public abstract class Twig
{
	public static Twig leaf( String payload )
	{
		return new LeafTwig( payload );
	}

	public static Twig array( String payload, List<Twig> children )
	{
		return new ArrayTwig( payload, children );
	}

	public static Twig array( List<Twig> children )
	{
		return new ArrayTwig( children.size() + " entries", children );
	}

	@SuppressWarnings( "varargs" ) @SafeVarargs public static Twig group( String payload, Map.Entry<String,Twig>... children )
	{
		return group( payload, List.of( children ) );
	}

	public static Twig group( String payload, List<Map.Entry<String,Twig>> children )
	{
		return new StructTwig( payload, children );
	}

	Twig()
	{
	}

	public abstract String text();
	public abstract List<Twig> children();
}
