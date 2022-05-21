package io.github.mikenakis.bytecode.printing.twig;

import java.util.List;
import java.util.Map;

public abstract class Twig
{
	public static Twig leaf( String text )
	{
		return new LeafTwig( text );
	}

	public static Twig array( String header, List<Twig> children )
	{
		return array( header, children, "items" );
	}

	public static Twig array( List<Twig> children )
	{
		return array( "", children );
	}

	public static Twig array( List<Twig> children, String itemName )
	{
		return array( "", children, itemName );
	}

	public static Twig array( String header, List<Twig> children, String itemName )
	{
		return new ArrayTwig( (header.isEmpty() ? "" : (header + "; ")) + children.size() + " " + itemName, children );
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
