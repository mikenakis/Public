package mikenakis.swapi.test;

import mikenakis.swapi.Swapi;
import mikenakis.swapi.SwapiFixer;
import mikenakis.swapi.SwapiLoader;
import mikenakis.swapi.modeling.TextRow;
import mikenakis.swapi.modeling.TextTable;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 * test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
@SuppressWarnings( "SpellCheckingInspection" )
public class T010_Swapi
{
	public T010_Swapi()
	{
	}

	private static String getPintableField( TextRow textRow, String fieldName )
	{
		return textRow.getField( fieldName ).orElse( "N/A" );
	}

	@Test
	public void Load_The_Model_And_Dump_Everything()
	{
		Swapi swapi = new Swapi();
		new SwapiLoader( swapi ).run();
		new SwapiFixer( swapi ).run();

		System.out.println();
		System.out.println( "Films" );
		{
			var builder = new StringBuilder();
			format( builder, "%-23s", "name" );
			format( builder, "%-16s", "director" );
			format( builder, "%-48s", "producers" );
			format( builder, "%-30s", "openingCrawl" );
			format( builder, "%-9s", "episodeId" );
			format( builder, "%-11s", "releaseDate" );
			System.out.println( builder.toString() );
		}
		for( TextRow film : swapi.getFilms().getRows() )
		{
			var builder = new StringBuilder();
			format( builder, "%-23s", getPintableField( film, "name" ) );
			format( builder, "%-16s", getPintableField( film, "director" ) );
			format( builder, "%-48s", getPintableField( film, "producer" ) );
			format( builder, "%-30s", getPrefix( film.getField( "opening_crawl" ).orElseThrow(), 30 ) );
			format( builder, "%9s", getPintableField( film, "episode_id" ) );
			format( builder, "%11s", LocalDateTime.ofInstant( film.getInstant( "release_date" ).orElseThrow(), ZoneOffset.UTC ).format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) ) );
			appendCollection( builder, " Persons", swapi.getPeople(), film.getList( "characters" ) );
			appendCollection( builder, " Planets", swapi.getPlanets(), film.getList( "planets" ) );
			appendCollection( builder, " Species", swapi.getSpecies(), film.getList( "species" ) );
			appendCollection( builder, " Starships", swapi.getStarships(), film.getList( "starships" ) );
			appendCollection( builder, " Vehicles", swapi.getVehicles(), film.getList( "vehicles" ) );
			System.out.println( builder.toString() );
		}

		System.out.println();
		System.out.println( "people" );
		{
			var builder = new StringBuilder();
			format( builder, "%-21s", "name" );
			format( builder, "%-9s", "birthYear" );
			format( builder, "%-14s", "eyeColor" );
			format( builder, "%-15s", "gender" );
			format( builder, "%-14s", "hairColor" );
			format( builder, "%-6s", "height" );
			format( builder, "%-16s", "homeWorld" );
			format( builder, "%-19s", "skinColor" );
			format( builder, "%-16s", "species" );
			System.out.println( builder.toString() );
		}
		for( TextRow person : swapi.getPeople().getRows() )
		{
			var builder = new StringBuilder();
			format( builder, "%-21s", getPintableField( person, "name" ) );
			format( builder, "%9s", getPintableField( person, "birth_year" ) );
			format( builder, "%-14s", getPintableField( person, "eye_color" ) );
			format( builder, "%-15s", getPintableField( person, "gender" ) );
			format( builder, "%-14s", getPintableField( person, "hair_color" ) );
			format( builder, "%6s", getPintableField( person, "height" ) );
			format( builder, "%-16s", getPintableField( person, "homeworld" ) );
			format( builder, "%-19s", getPintableField( person, "skin_color" ) );
			format( builder, "%-16s", getPintableField( person, "species" ) );
			appendCollection( builder, " Starships", swapi.getStarships(), person.getList( "starships" ) );
			appendCollection( builder, " Vehicles", swapi.getVehicles(), person.getList( "vehicles" ) );
			appendCollection( builder, " Films", swapi.getFilms(), person.getList( "films" ) );
			System.out.println( builder.toString() );
		}

		System.out.println();
		System.out.println( "Planets" );
		{
			var builder = new StringBuilder();
			format( builder, "%-16s", "name" );
			format( builder, "%-8s", "diameter" );
			format( builder, "%-7s", "gravity" );
			format( builder, "%-13s", "orbitalPeriod" );
			format( builder, "%-14s", "rotationPeriod" );
			format( builder, "%-12s", "surfaceWater" );
			format( builder, "%-13s", "population" );
			format( builder, "%-25s", "climate" );
			format( builder, "%-40s", "terrain" );
			System.out.println( builder.toString() );
		}
		for( TextRow planet : swapi.getPlanets().getRows() )
		{
			var builder = new StringBuilder();
			format( builder, "%-16s", getPintableField( planet, "name" ) );
			format( builder, "%8s", getPintableField( planet, "diameter" ) );
			format( builder, "%7s", getPintableField( planet, "gravity" ) );
			format( builder, "%13s", getPintableField( planet, "orbital_period" ) );
			format( builder, "%14s", getPintableField( planet, "rotation_period" ) );
			format( builder, "%12s", getPintableField( planet, "surface_water" ) );
			format( builder, "%13s", getPintableField( planet, "population" ) );
			format( builder, "%-25s", getPintableField( planet, "climate" ) );
			format( builder, "%-40s", getPintableField( planet, "terrain" ) );
			appendCollection( builder, " Films", swapi.getFilms(), planet.getList( "films" ) );
			appendCollection( builder, " Residents", swapi.getPeople(), planet.getList( "residents" ) );
			System.out.println( builder.toString() );
		}

		System.out.println();
		System.out.println( "Species" );
		{
			var builder = new StringBuilder();
			format( builder, "%-16s", "name" );
			format( builder, "%-13s", "averageHeight" );
			format( builder, "%-15s", "averageLifespan" );
			format( builder, "%-14s", "classification" );
			format( builder, "%-11s", "designation" );
			format( builder, "%-40s", "eyeColors" );
			format( builder, "%-30s", "hairColors" );
			format( builder, "%-32s", "homeWorld" );
			format( builder, "%-20s", "language" );
			format( builder, "%-47s", "skinColors" );
			System.out.println( builder.toString() );
		}
		for( TextRow specie : swapi.getSpecies().getRows() )
		{
			var builder = new StringBuilder();
			format( builder, "%-16s", getPintableField( specie, "name" ) );
			format( builder, "%13s", getPintableField( specie, "average_height" ) );
			format( builder, "%15s", getPintableField( specie, "average_lifespan" ) );
			format( builder, "%-14s", getPintableField( specie, "classification" ) );
			format( builder, "%-11s", getPintableField( specie, "designation" ) );
			format( builder, "%-40s", getPintableField( specie, "eye_colors" ) );
			format( builder, "%-30s", getPintableField( specie, "hair_colors" ) );
			format( builder, "%-32s", getPintableField( specie, "homeworld" ) );
			format( builder, "%-20s", getPintableField( specie, "language" ) );
			format( builder, "%-47s", getPintableField( specie, "skin_colors" ) );
			appendCollection( builder, " Films", swapi.getFilms(), specie.getList( "films" ) );
			appendCollection( builder, " People", swapi.getPeople(), specie.getList( "people" ) );
			System.out.println( builder.toString() );
		}

		System.out.println();
		System.out.println( "Starships" );
		{
			var builder = new StringBuilder();
			format( builder, "%-29s", "name" );
			format( builder, "%-8s", "length" );
			format( builder, "%-11s", "consumables" );
			format( builder, "%-62s", "manufacturer" );
			format( builder, "%-19s", "maxAtmosphericSpeed" );
			format( builder, "%-41s", "modelName" );
			format( builder, "%-10s", "passengers" );
			format( builder, "%-31s", "className" );
			format( builder, "%-13s", "cargoCapacity" );
			format( builder, "%-13s", "costInCredits" );
			format( builder, "%-6s", "crew" );
			format( builder, "%-5s", "speed" );
			format( builder, "%-16s", "hyperdriveRating" );
			System.out.println( builder.toString() );
		}
		for( TextRow starship : swapi.getStarships().getRows() )
		{
			var builder = new StringBuilder();
			format( builder, "%-29s", getPintableField( starship, "name" ) );
			format( builder, "%8s", getPintableField( starship, "length" ) );
			format( builder, "%-11s", getPintableField( starship, "consumables" ) );
			format( builder, "%-62s", getPintableField( starship, "manufacturer" ) );
			format( builder, "%19s", getPintableField( starship, "max_atmosphering_speed" ) ); // --> max_air_speed
			format( builder, "%-41s", getPintableField( starship, "model" ) );
			format( builder, "%10s", getPintableField( starship, "passengers" ) );
			format( builder, "%-31s", getPintableField( starship, "starship_class" ) );
			format( builder, "%13s", getPintableField( starship, "cargo_capacity" ) );
			format( builder, "%13s", getPintableField( starship, "cost_in_credits" ) ); // --> cost
			format( builder, "%6s", getPintableField( starship, "crew" ) );
			format( builder, "%5s", getPintableField( starship, "MGLT" ) ); // --> max_space_speed
			format( builder, "%16s", getPintableField( starship, "hyperdrive_rating" ) );
			appendCollection( builder, " Films", swapi.getFilms(), starship.getList( "films" ) );
			appendCollection( builder, " Pilots", swapi.getPeople(), starship.getList( "pilots" ) );
			System.out.println( builder.toString() );
		}

		System.out.println();
		System.out.println( "Vehicles" );
		{
			var builder = new StringBuilder();
			format( builder, "%-30s", "name" );
			format( builder, "%-6s", "length" );
			format( builder, "%-15s", "consumables" );
			format( builder, "%-58s", "manufacturer" );
			format( builder, "%-19s", "maxAtmosphericSpeed" );
			format( builder, "%-40s", "modelName" );
			format( builder, "%-10s", "passengers" );
			format( builder, "%-27s", "className" );
			format( builder, "%-13s", "cargoCapacity" );
			format( builder, "%-13s", "costInCredits" );
			format( builder, "%-4s", "crew" );
			System.out.println( builder.toString() );
		}
		for( TextRow vehicle : swapi.getVehicles().getRows() )
		{
			var builder = new StringBuilder();
			format( builder, "%-30s", getPintableField( vehicle, "name" ) );
			format( builder, "%6s", getPintableField( vehicle, "length" ) );
			format( builder, "%-15s", getPintableField( vehicle, "consumables" ) );
			format( builder, "%-58s", getPintableField( vehicle, "manufacturer" ) );
			format( builder, "%19s", getPintableField( vehicle, "max_atmosphering_speed" ) ); // --> max_air_speed
			format( builder, "%-40s", getPintableField( vehicle, "model" ) );
			format( builder, "%10s", getPintableField( vehicle, "passengers" ) );
			format( builder, "%-27s", getPintableField( vehicle, "vehicle_class" ) );
			format( builder, "%13s", getPintableField( vehicle, "cargo_capacity" ) );
			format( builder, "%13s", getPintableField( vehicle, "cost_in_credits" ) ); // --> cost
			format( builder, "%4s", getPintableField( vehicle, "crew" ) );
			appendCollection( builder, " Films", swapi.getFilms(), vehicle.getList( "films" ) );
			appendCollection( builder, " Pilots", swapi.getPeople(), vehicle.getList( "pilots" ) );
			System.out.println( builder.toString() );
		}

		System.out.println( "." );
		System.out.flush();
	}

	private static void appendCollection( StringBuilder stringBuilder, String name, TextTable textTable, Collection<Integer> ids )
	{
		stringBuilder.append( name ).append( ": [" );
		boolean first = true;
		for( Integer id : ids )
		{
			if( first )
				first = false;
			else
				stringBuilder.append( ", " );
			TextRow textRow = textTable.getRowById( id );
			stringBuilder.append( textRow.getField( "name" ).orElseThrow() );
		}
		stringBuilder.append( "]" );
	}

	private static String getPrefix( String whole, int length )
	{
		whole = whole.replace( "\r", "" );
		whole = whole.replace( "\n", " " );
		whole = whole.substring( 0, Math.min( whole.length(), 30 ) );
		return whole;
	}

	private static void format( StringBuilder stringBuilder, String formatString, String value )
	{
		String text = String.format( formatString, value ) + " | ";
		stringBuilder.append( text );
	}

	@Test
	public void Prove_That_Entity_Collections_Of_Film_Are_Redundant()
	{
		Swapi swapi = new Swapi();
		new SwapiLoader( swapi ).run();
		new SwapiFixer( swapi ).run();

		for( TextRow film : swapi.getFilms().getRows() )
		{
			for( TextRow person : swapi.getPeople().getRows() )
				assert person.getList( "films" ).contains( film.id ) == film.getList( "characters" ).contains( person.id );
			for( TextRow planet : swapi.getPlanets().getRows() )
				assert planet.getList( "films" ).contains( film.id ) == film.getList( "planets" ).contains( planet.id );
			for( TextRow species : swapi.getSpecies().getRows() )
				assert species.getList( "films" ).contains( film.id ) == film.getList( "species" ).contains( species.id );
			for( TextRow starship : swapi.getStarships().getRows() )
				assert starship.getList( "films" ).contains( film.id ) == film.getList( "starships" ).contains( starship.id );
			for( TextRow vehicle : swapi.getVehicles().getRows() )
				assert vehicle.getList( "films" ).contains( film.id ) == film.getList( "vehicles" ).contains( vehicle.id );
		}
	}

	@Test
	public void Prove_That_Entity_Collections_Of_Person_Are_Redundant()
	{
		Swapi swapi = new Swapi();
		new SwapiLoader( swapi ).run();
		new SwapiFixer( swapi ).run();

		for( TextRow person : swapi.getPeople().getRows() )
		{
			for( TextRow vehicle : swapi.getVehicles().getRows() )
				assert person.getList( "vehicles" ).contains( vehicle.id ) == vehicle.getList( "pilots" ).contains( person.id );
			for( TextRow starship : swapi.getStarships().getRows() )
				assert person.getList( "starships" ).contains( starship.id ) == starship.getList( "pilots" ).contains( person.id );
		}
	}
}
