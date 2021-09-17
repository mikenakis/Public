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

	@Override public int getOffset( Instruction sourceInstruction, Instruction targetInstruction )
	{
		int sourceLocation = getLocation( sourceInstruction );
		int targetLocation = getLocation( targetInstruction );
		return targetLocation - sourceLocation;
	}

	void add( Instruction instruction, int length )
	{
		assert length >= 1;
		int location = instructionsByLocation.size();
		instructionsByLocation.add( instruction );
		for( int i = 1;  i < length;  i++ )
			instructionsByLocation.add( null );
		Kit.map.add( locationsByInstruction, instruction, location );
	}

	boolean contains( Instruction instruction )
	{
		return locationsByInstruction.containsKey( instruction );
	}

	@Override public boolean contains( int location )
	{
		return instructionsByLocation.get( location ) != null;
	}
}
