package mikenakis.bytecode.reading;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.kit.Kit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class LocationMap
{
	@SuppressWarnings( "WeakerAccess" ) protected final Instruction[] instructionsByLocation;
	@SuppressWarnings( "WeakerAccess" ) protected final Map<Instruction,Integer> locationsByInstruction = new HashMap<>();

	LocationMap( int codeLength )
	{
		instructionsByLocation = new Instruction[codeLength];
	}

	public Optional<Instruction> getInstruction( int location ) //returns Optional.empty() if the location is @end.
	{
		if( location == instructionsByLocation.length )
			return Optional.empty();
		Instruction instruction = instructionsByLocation[location];
		assert instruction != null;
		return Optional.of( instruction );
	}

	int size()
	{
		return instructionsByLocation.length;
	}

	public int getLocation( Instruction instruction )
	{
		return Kit.map.get( locationsByInstruction, instruction );
	}
}
