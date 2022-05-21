package io.github.mikenakis.swapi.modeling;

import io.github.mikenakis.kit.Kit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains a map of {@link TextTable} addressable by name.
 *
 * @author michael.gr
 */
public class TextDomain
{
	private final Map<String,TextTable> tablesByName = new HashMap<>();

	public TextDomain( Collection<String> tableNames )
	{
		for( String entityTypeName : tableNames )
		{
			TextTable textTable = new TextTable( entityTypeName );
			Kit.map.add( tablesByName, entityTypeName, textTable );
		}
	}

	public TextTable getTableByName( String name )
	{
		return Kit.map.get( tablesByName, name );
	}

	public Iterable<TextTable> getTables()
	{
		return tablesByName.values();
	}
}
