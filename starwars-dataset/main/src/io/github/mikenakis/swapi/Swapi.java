package io.github.mikenakis.swapi;

import io.github.mikenakis.swapi.modeling.TextDomain;
import io.github.mikenakis.swapi.modeling.TextTable;

import java.util.Arrays;

/**
 * Presents the data-set of SWAPI as a list of named tables, where each table is a list of rows addressable by id, where each row is a map of values addressable by field name.
 *
 * @author michael.gr
 */
public final class Swapi
{
	private final TextDomain textDomain = new TextDomain( Arrays.asList( "films", "people", "planets", "species", "starships", "vehicles" ) );

	public Swapi()
	{
	}

	public Iterable<TextTable> getTables()
	{
		return textDomain.getTables();
	}

	public TextTable getFilms()
	{
		return textDomain.getTableByName( "films" );
	}

	public TextTable getPeople()
	{
		return textDomain.getTableByName( "people" );
	}

	public TextTable getPlanets()
	{
		return textDomain.getTableByName( "planets" );
	}

	public TextTable getSpecies()
	{
		return textDomain.getTableByName( "species" );
	}

	public TextTable getStarships()
	{
		return textDomain.getTableByName( "starships" );
	}

	public TextTable getVehicles()
	{
		return textDomain.getTableByName( "vehicles" );
	}
}
