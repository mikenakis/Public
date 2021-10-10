package mikenakis.swapi.modeling;

import mikenakis.kit.Kit;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contains an id, a map of field values addressable by name, and a map of lists of integer addressable by name.
 *
 * @author michael.gr
 */
public final class TextRow
{
	public final int id;

	public TextRow( int id )
	{
		this.id = id;
	}

	private final Map<String,Optional<String>> valuesFromFieldNames = new HashMap<>();
	private final Map<String,List<Integer>> entityIdsFromFieldNames = new HashMap<>();

	public boolean containsField( String name )
	{
		assert name != null;
		return valuesFromFieldNames.containsKey( name );
	}

	public Optional<String> getField( String name )
	{
		return Kit.map.get( valuesFromFieldNames, name );
	}

	public Optional<String> extractField( String name )
	{
		return Kit.map.remove( valuesFromFieldNames, name );
	}

	public void putField( String name, Optional<String> value )
	{
		Kit.map.replace( valuesFromFieldNames, name, value );
	}

	public void addField( String name, Optional<String> value )
	{
		Kit.map.add( valuesFromFieldNames, name, value );
	}

	public List<Integer> extractList( String name )
	{
		return Kit.map.remove( entityIdsFromFieldNames, name );
	}

	public List<Integer> getList( String name )
	{
		return Kit.map.get( entityIdsFromFieldNames, name );
	}

	public void addList( String name, List<Integer> list )
	{
		Kit.map.add( entityIdsFromFieldNames, name, list );
	}

	public boolean isEmpty()
	{
		if( !valuesFromFieldNames.isEmpty() )
			return false;
		if( !entityIdsFromFieldNames.isEmpty() )
			return false;
		return true;
	}

	public Optional<Integer> extractInteger( String name )
	{
		Optional<String> stringValue = extractField( name );
		if( stringValue.isEmpty() )
			return Optional.empty();
		return Optional.of( Kit.unchecked( () -> Integer.valueOf( stringValue.get() ) ) );
	}

	public Optional<Long> extractLong( String name )
	{
		Optional<String> stringValue = extractField( name );
		if( stringValue.isEmpty() )
			return Optional.empty();
		return Optional.of( Kit.unchecked( () -> Long.valueOf( stringValue.get() ) ) );
	}

	public Optional<Float> extractFloat( String name )
	{
		Optional<String> stringValue = extractField( name );
		if( stringValue.isEmpty() )
			return Optional.empty();
		return Optional.of( Kit.unchecked( () -> Float.valueOf( stringValue.get() ) ) );
	}

	public Optional<Instant> extractInstant( String name )
	{
		Optional<String> stringValue = extractField( name );
		return Optional.of( Instant.parse( stringValue.orElseThrow() + "T00:00:00.00Z" ) );
	}

	public Optional<Instant> getInstant( String name )
	{
		Optional<String> stringValue = getField( name );
		return Optional.of( Instant.parse( stringValue.orElseThrow() + "T00:00:00.00Z" ) );
	}
}
