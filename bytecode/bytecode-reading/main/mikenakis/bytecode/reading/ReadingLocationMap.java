package mikenakis.bytecode.reading;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.kit.Kit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class ReadingLocationMap implements LocationMap
{
	private final Instruction[] instructionsByLocation;
	private final Map<Instruction,Integer> locationsByInstruction;

	ReadingLocationMap( int codeLength )
	{
		instructionsByLocation = new Instruction[codeLength];
		locationsByInstruction = new HashMap<>();
	}

	@Override public Optional<Instruction> getInstruction( int location ) //returns Optional.empty() if the location is @end.
	{
		if( location == instructionsByLocation.length )
			return Optional.empty();
		Instruction instruction = instructionsByLocation[location];
		assert instruction != null;
		return Optional.of( instruction );
	}

	@Override public int getLocation( Optional<Instruction> instruction )
	{
		if( instruction.isEmpty() )
			return instructionsByLocation.length;
		return getLocation( instruction.get() );
	}

	@Override public int getLocation( Instruction instruction )
	{
		return Kit.map.get( locationsByInstruction, instruction );
	}

	@Override public int getOffset( Instruction sourceInstruction, Instruction targetInstruction )
	{
		assert false;
		return 0;
	}

	private int topLocation;

	void add( int location, Instruction instruction, int length )
	{
		assert location == topLocation;
		assert instructionsByLocation[location] == null;
		instructionsByLocation[location] = instruction;
		Kit.map.add( locationsByInstruction, instruction, location );
		topLocation += length;
	}

	boolean contains( Instruction instruction )
	{
		return locationsByInstruction.containsKey( instruction );
	}

	@Override public boolean contains( int location )
	{
		return instructionsByLocation[location] != null;
	}

	int getPosition()
	{
		return topLocation;
	}
}
