package mikenakis.swapi.modeling;

import mikenakis.kit.Kit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Contains a name and a map of {@link TextRow} addressable by id.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class TextTable
{
	public final String name;
	final Map<Integer,TextRow> rowsById = new HashMap<>();

	public TextTable( String name )
	{
		this.name = name;
	}

	public Collection<TextRow> getRows()
	{
		return rowsById.values();
	}

	public TextRow getRowById( Integer id )
	{
		return Kit.map.get( rowsById, id );
	}

	public TextRow getRowByName( String rowName )
	{
		for( TextRow textRow : rowsById.values() )
		{
			Optional<String> thisRowName = textRow.getField( "name" );
			if( thisRowName.isEmpty() )
				continue;
			if( thisRowName.get().equals( rowName ) )
				return textRow;
		}
		assert false;
		return null;
	}

	public void removeRow( TextRow textRow )
	{
		Kit.map.remove( rowsById, textRow.id );
	}

	public void addRow( TextRow textRow )
	{
		Kit.map.add( rowsById, textRow.id, textRow );
	}

	public TextRow addRow()
	{
		int id = 0;
		for( TextRow textRow : rowsById.values() )
			id = Math.max( id, textRow.id );
		id++;
		TextRow textRow = new TextRow( id );
		addRow( textRow );
		return textRow;
	}
}
