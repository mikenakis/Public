package mikenakis.bytecode.writing;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.kit.Kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class WritingLocationMap implements LocationMap
{
	private final List<Instruction> instructionsByLocation = new ArrayList<>();
	private final Map<Instruction,Integer> locationsByInstruction = new HashMap<>();

	WritingLocationMap()
	{
	}

	@Override public Optional<Instruction> getInstruction( int location ) //returns Optional.empty() if the location is @end.
	{
		if( location == instructionsByLocation.size() )
			return Optional.empty();
		Instruction instruction = instructionsByLocation.get( location );
		assert instruction != null;
		return Optional.of( instruction );
	}

	@Override public int getLocation( Optional<Instruction> instruction )
	{
		if( instruction.isEmpty() )
			return instructionsByLocation.size();
		return getLocation( instruction.get() );
	}

	@Override public int getLocation( Instruction instruction )
	{
		return Kit.map.get( locationsByInstruction, instruction );
	}

	int getLength( Instruction instruction )
	{
		int location = getLocation( instruction );
		int nextLocation;
		for( nextLocation = location + 1; nextLocation < instructionsByLocation.size(); nextLocation++ )
			if( instructionsByLocation.get( nextLocation ) != null )
				break;
		return nextLocation - location;
	}

	@Override public int getOffset( Instruction sourceInstruction, Instruction targetInstruction )
	{
		int sourceLocation = getLocation( sourceInstruction );
		int targetLocation = getLocation( targetInstruction );
		return targetLocation - sourceLocation;
	}

	void add( Instruction instruction )
	{
		int location = instructionsByLocation.size();
		instructionsByLocation.add( instruction );
		Kit.map.add( locationsByInstruction, instruction, location );
	}

	void setLength( Instruction instruction, int length )
	{
		assert length >= 1;
		assert instruction == instructionsByLocation.get( instructionsByLocation.size() - 1 );
		assert Kit.map.get( locationsByInstruction, instruction ) == instructionsByLocation.size() - 1;
		for( int i = 1; i < length; i++ )
			instructionsByLocation.add( null );
	}

	boolean contains( Instruction instruction )
	{
		return locationsByInstruction.containsKey( instruction );
	}

	@Override public boolean contains( int location )
	{
		return instructionsByLocation.get( location ) != null;
	}

	void removeBytes( int location, int length )
	{
		assert length > 0;
		for( int i = 0;  i < length;  i++ ) //TODO: optimize this by using an array instead of a list, so that we can remove [length] number of bytes in one pass.
			instructionsByLocation.remove( location );
		for( Map.Entry<Instruction,Integer> entry : locationsByInstruction.entrySet() )
		{
			int location2 = entry.getValue();
			if( location2 > location )
			{
				location2 -= length;
				assert location2 >= location;
				entry.setValue( location2 );
			}
		}
	}
}
