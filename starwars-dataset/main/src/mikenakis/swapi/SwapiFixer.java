package mikenakis.swapi;

import mikenakis.kit.Kit;
import mikenakis.swapi.modeling.TextRow;
import mikenakis.swapi.modeling.TextTable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Presents the data-set of SWAPI as a list of named tables, where each table is a list of rows addressable by id, where each row is a map of values addressable by field name.
 *
 * @author Michael Belivanakis (michael.gr)
 */
@SuppressWarnings( "SpellCheckingInspection" )
public final class SwapiFixer
{
	private final Swapi swapi;

	/**
	 * Constructor.
	 */
	public SwapiFixer( Swapi swapi )
	{
		this.swapi = swapi;
	}

	public void run()
	{
		/* get a few rows that we are going to need. */
		TextRow unknownPlanetRow = swapi.getPlanets().getRowByName( "unknown" );
		TextRow humanSpeciesRow = swapi.getSpecies().getRowByName( "Human" );
		TextRow droidSpeciesRow = swapi.getSpecies().getRowByName( "Droid" );

		/* move some vehicles that are actually starships to starships */
		for( String name : Arrays.asList( "Droid tri-fighter", "TIE/LN starfighter", "TIE bomber", "TIE/IN interceptor", "Vulture Droid", "Geonosian starfighter" ) )
		{
			TextRow textRow = swapi.getVehicles().getRowByName( name );
			moveVehicleToStarships( textRow );
		}

		/* fix films */
		for( TextRow textRow : swapi.getFilms().getRows() )
		{
			/* remove the 'title' of the movie and re-insert as 'name'. */
			Optional<String> title = textRow.extractField( "title" );
			textRow.addField( "name", title );

			/* fix newlines in the opening crawl. */
			String openingCrawl = textRow.getField( "opening_crawl" ).orElseThrow();
			openingCrawl = Kit.string.replaceAll( openingCrawl, "\r\n", "\n" );
			openingCrawl = Kit.string.replaceAll( openingCrawl, "\n\r", "\n" );
			openingCrawl = Kit.string.replaceAll( openingCrawl, "\r", "\n" );
			textRow.putField( "opening_crawl", Optional.of( openingCrawl ) );
		}

		/* fix some fields. */
		//fixFieldInTable( swapi.getFilms(), "The Empire Strikes Back", "producer", "Gary Kutz, Rick McCallum", "Gary Kurtz, Rick McCallum" );
		fixFieldInTable( swapi.getSpecies(), "Rodian", "classification", Optional.of( "sentient" ), "reptilian" );
		fixFieldInTable( swapi.getSpecies(), "Rodian", "designation", Optional.of( "reptilian" ), "sentient" );
		fixFieldInTable( swapi.getSpecies(), "Twi'lek", "classification", Optional.of( "mammals" ), "mammal" );

		/* fix species. */
		for( TextRow textRow : swapi.getSpecies().getRows() )
		{
			fixFieldInRow( textRow, "homeworld", "null", Optional.empty() );
			fixFieldInRow( textRow, "classification", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "average_height", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "average_height", "n/a", Optional.empty() );
			fixFieldInRow( textRow, "average_lifespan", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "average_lifespan", "indefinite", Optional.of( String.valueOf( Integer.MAX_VALUE ) ) );

			/* replace homeworld url with homeworld id, and also replace homeworld "unknown" with a null id. */
			Optional<String> homeworldUrl = textRow.getField( "homeworld" );
			Optional<Integer> homeworldId = homeworldUrl.isEmpty() ? Optional.empty() : Optional.of( SwapiLoader.getIdFromUrl( Optional.of( "planets" ), homeworldUrl.get() ) );
			if( homeworldId.isPresent() && homeworldId.get() == unknownPlanetRow.id )
				homeworldId = Optional.empty();
			textRow.putField( "homeworld", homeworldId.map( i -> i.toString() ) );
		}

		/* add missing species */
		TextRow umbaraPlanetRow = swapi.getPlanets().getRowByName( "Umbara" );
		TextRow umbaranSpeciesRow = swapi.getSpecies().addRow();
		umbaranSpeciesRow.addField( "name", Optional.of( "Umbaran" ) );
		umbaranSpeciesRow.addField( "average_height", Optional.empty() );
		umbaranSpeciesRow.addField( "average_lifespan", Optional.empty() );
		umbaranSpeciesRow.addField( "classification", Optional.of( "mammal" ) );
		umbaranSpeciesRow.addField( "designation", Optional.of( "sentient" ) );
		umbaranSpeciesRow.addField( "eye_colors", Optional.of( "" ) );
		umbaranSpeciesRow.addField( "hair_colors", Optional.of( "" ) );
		umbaranSpeciesRow.addField( "homeworld", Optional.of( Integer.toString( umbaraPlanetRow.id ) ) );
		umbaranSpeciesRow.addField( "language", Optional.of( "umbaran" ) );
		umbaranSpeciesRow.addField( "skin_colors", Optional.of( "" ) );
		umbaranSpeciesRow.addField( "url", Optional.empty() );
		umbaranSpeciesRow.addField( "created", Optional.empty() );
		umbaranSpeciesRow.addField( "edited", Optional.empty() );
		umbaranSpeciesRow.addList( "films", Collections.emptyList() );
		umbaranSpeciesRow.addList( "people", Collections.emptyList() );

		/* fix people */
		for( TextRow textRow : swapi.getPeople().getRows() )
		{
			/* replace the single-element list of species with a single species. */
			List<Integer> entityIds = textRow.extractList( "species" );
			if( entityIds.isEmpty() )
				textRow.addField( "species", Optional.empty() );
			else
			{
				assert entityIds.size() == 1;
				String species = entityIds.get( 0 ).toString();
				textRow.addField( "species", Optional.of( species ) );
			}

			fixFieldInRow( textRow, "mass", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "height", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "birth_year", "unknown", Optional.empty() );

			/* replace homeworld url with homeworld id and also replace homeworld "unknown" with a null id. */
			String homeworldUrl = textRow.getField( "homeworld" ).orElseThrow();
			Optional<Integer> homeworldId = Optional.of( SwapiLoader.getIdFromUrl( Optional.of( "planets" ), homeworldUrl ) );
			if( homeworldId.get() == unknownPlanetRow.id )
				homeworldId = Optional.empty();
			textRow.putField( "homeworld", homeworldId.map( i -> i.toString() ) );

			/* fix the birth_year to be a negative integer if BBY, non-negative if ABY. */
			Optional<String> birthYearString = textRow.getField( "birth_year" );
			if( birthYearString.isPresent() )
			{
				assert birthYearString.get().endsWith( "BBY" ) || birthYearString.get().endsWith( "ABY" );
				float birthYear = Float.parseFloat( birthYearString.get().substring( 0, birthYearString.get().length() - 3 ) );
				if( birthYearString.get().endsWith( "BBY" ) )
					birthYear = -birthYear;
				textRow.putField( "birth_year", Optional.of( String.valueOf( birthYear ) ) );
			}
		}

		fixFieldInTable( swapi.getPeople(), "Jabba Desilijic Tiure", "mass", Optional.of( "1,358" ), "1358" );
		fixFieldInTable( swapi.getPeople(), "Ric Olié", "species", Optional.empty(), String.valueOf( humanSpeciesRow.id ) );
		fixFieldInTable( swapi.getPeople(), "Quarsh Panaka", "species", Optional.empty(), String.valueOf( humanSpeciesRow.id ) );
		fixFieldInTable( swapi.getPeople(), "R4-P17", "species", Optional.empty(), String.valueOf( droidSpeciesRow.id ) );
		fixFieldInTable( swapi.getPeople(), "R4-P17", "gender", Optional.of( "female" ), "n/a" ); //Yes, Arfour had a female personality; nonetheless, it is a droid.
		fixFieldInTable( swapi.getPeople(), "Sly Moore", "species", Optional.empty(), String.valueOf( umbaranSpeciesRow.id ) );

		/* fix planets. */
		for( TextRow textRow : swapi.getPlanets().getRows() )
		{
			fixFieldInRow( textRow, "diameter", "0", Optional.empty() );
			fixFieldInRow( textRow, "population", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "surface_water", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "orbital_period", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "rotation_period", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "diameter", "unknown", Optional.empty() );

			/* fix gravity to be strictly a float: map "N/A" to Float.NaN, and "unknown" to null, and strip trailing garbage. */
			Optional<String> gravityString = textRow.getField( "gravity" );
			if( gravityString.isPresent() )
			{
				Optional<Float> gravity;
				if( gravityString.get().equals( "N/A" ) || gravityString.get().equals( "unknown" ) )
					gravity = Optional.empty();
				else if( gravityString.get().contains( "standard" ) || gravityString.get().contains( "(surface)" ) )
					gravity = Optional.of( Float.valueOf( gravityString.get().substring( 0, gravityString.get().indexOf( ' ' ) ) ) );
				else
					gravity = Optional.of( Float.valueOf( gravityString.get() ) );
				textRow.putField( "gravity", gravity.map( f -> f.toString() ) );
			}
		}

		addField( swapi.getStarships(), "Droid tri-fighter", "MGLT", Optional.empty() );
		addField( swapi.getStarships(), "Droid tri-fighter", "hyperdrive_rating", Optional.empty() );
		addField( swapi.getStarships(), "TIE/LN starfighter", "MGLT", Optional.empty() );
		addField( swapi.getStarships(), "TIE/LN starfighter", "hyperdrive_rating", Optional.empty() );
		addField( swapi.getStarships(), "TIE bomber", "MGLT", Optional.empty() );
		addField( swapi.getStarships(), "TIE bomber", "hyperdrive_rating", Optional.empty() );
		addField( swapi.getStarships(), "TIE/IN interceptor", "MGLT", Optional.empty() );
		addField( swapi.getStarships(), "TIE/IN interceptor", "hyperdrive_rating", Optional.empty() );
		addField( swapi.getStarships(), "Vulture Droid", "MGLT", Optional.empty() );
		addField( swapi.getStarships(), "Vulture Droid", "hyperdrive_rating", Optional.empty() );
		addField( swapi.getStarships(), "Geonosian starfighter", "MGLT", Optional.empty() );
		addField( swapi.getStarships(), "Geonosian starfighter", "hyperdrive_rating", Optional.empty() );
		fixFieldInTable( swapi.getStarships(), "Star Destroyer", "length", Optional.of( "1,600" ), "1600" );
		fixFieldInTable( swapi.getStarships(), "Naboo star skiff", "manufacturer", Optional.of( "Theed Palace Space Vessel Engineering Corps/Nubia Star Drives, Incorporated" ),
			"Theed Palace Space Vessel Engineering Corps, Nubia Star Drives" );
		fixFieldInTable( swapi.getStarships(), "arc-170", "model", Optional.of( "Aggressive Reconnaissance-170 starfighte" ), "Aggressive Reconnaissance-170 starfighter" );
		fixFieldInTable( swapi.getStarships(), "Y-wing", "max_atmosphering_speed", Optional.of( "1000km" ), "1000" );
		//fixFieldInTable( swapi.getStarships(), "Banking clan frigte", "name", "Banking clan frigte", "Banking clan frigate" );

		/* fix starships */
		for( TextRow textRow : swapi.getStarships().getRows() )
		{
			fixFieldInRow( textRow, "MGLT", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "cargo_capacity", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "cargo_capacity", "none", Optional.of( "0" ) );
			fixFieldInRow( textRow, "cost_in_credits", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "passengers", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "max_atmosphering_speed", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "max_atmosphering_speed", "n/a", Optional.of( String.valueOf( Integer.MAX_VALUE ) ) );
			fixFieldInRow( textRow, "length", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "hyperdrive_rating", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "crew", "unknown", Optional.empty() );
		}

		/* fix vehicles */
		for( TextRow textRow : swapi.getVehicles().getRows() )
		{
			fixFieldInRow( textRow, "cargo_capacity", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "cargo_capacity", "none", Optional.of( "0" ) );
			fixFieldInRow( textRow, "cost_in_credits", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "length", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "passengers", "unknown", Optional.empty() );
			fixFieldInRow( textRow, "max_atmosphering_speed", "unknown", Optional.empty() );
		}

		/* remove the "unknown" planet. */
		swapi.getPlanets().removeRow( unknownPlanetRow );

		//More fixes 2019-08
		fixFieldInTable( swapi.getStarships(), "Executor", "length", Optional.of( "19,000" ), "19000" );
	}

	private void moveVehicleToStarships( TextRow textRow )
	{
		/* remove the "vehicle_class" and put it back as "starship_class" */
		Optional<String> className = textRow.extractField( "vehicle_class" );
		textRow.addField( "starship_class", className );

		/* remove the row from the vehicles table and add it to the starships table. */
		swapi.getVehicles().removeRow( textRow );
		swapi.getStarships().addRow( textRow );

		/* go through all films and replace any reference to the vehicle with a reference to the starship. */
		for( TextRow filmTextRow : swapi.getFilms().getRows() )
		{
			if( filmTextRow.getList( "vehicles" ).remove( (Integer)textRow.id ) )
				filmTextRow.getList( "starships" ).add( textRow.id );
		}

		/* go through all persons and replace any reference to the vehicle with a reference to the starship. */
		for( TextRow personTextRow : swapi.getPeople().getRows() )
		{
			if( personTextRow.getList( "vehicles" ).remove( (Integer)textRow.id ) )
				personTextRow.getList( "starships" ).add( textRow.id );
		}
	}

	private static void fixFieldInRow( TextRow textRow, String fieldName, String oldValue, Optional<String> newValue )
	{
		if( Objects.equals( textRow.getField( fieldName ), Optional.of( oldValue ) ) )
			textRow.putField( fieldName, newValue );
	}

	private static void fixFieldInTable( TextTable textTable, String rowName, String fieldName, Optional<String> oldValue, String newValue )
	{
		TextRow textRow = textTable.getRowByName( rowName );
		assert !Objects.equals( textRow.getField( fieldName ), Optional.of( newValue ) );
		assert Objects.equals( textRow.getField( fieldName ), oldValue );
		textRow.putField( fieldName, Optional.of( newValue ) );
	}

	private static void addField( TextTable textTable, String rowName, String fieldName, Optional<String> newValue )
	{
		TextRow textRow = textTable.getRowByName( rowName );
		assert !textRow.containsField( fieldName );
		textRow.addField( fieldName, newValue );
	}
}
