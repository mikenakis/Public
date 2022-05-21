package io.github.mikenakis.bytecode.reading;

import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.functional.Procedure1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReadingLocationMap
{
	private int topLocation;
	private final Collection<Runnable> fixUps = new ArrayList<>();
	private final Instruction[] instructionsByLocation;
	private final Map<Instruction,Integer> locationsByInstruction = new HashMap<>();

	public ReadingLocationMap( int codeLength )
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

	public int getLocation( Instruction instruction )
	{
		return Kit.map.get( locationsByInstruction, instruction );
	}

	public void add( int location, Instruction instruction, int length )
	{
		assert location == topLocation;
		assert instructionsByLocation[location] == null;
		instructionsByLocation[location] = instruction;
		Kit.map.add( locationsByInstruction, instruction, location );
		topLocation += length;
	}

	public void setRelativeTargetInstruction( Instruction sourceInstruction, int targetInstructionOffset, Procedure1<Instruction> setter )
	{
		assert !locationsByInstruction.containsKey( sourceInstruction );
		int sourceInstructionLocation = topLocation;
		assert sourceInstructionLocation + targetInstructionOffset < instructionsByLocation.length;
		if( targetInstructionOffset < 0 )
			fix( sourceInstructionLocation, targetInstructionOffset, setter );
		else
			fixUps.add( () -> //
			{
				assert sourceInstructionLocation == getLocation( sourceInstruction );
				fix( sourceInstructionLocation, targetInstructionOffset, setter );
			} );
	}

	private void fix( int sourceInstructionLocation, int targetInstructionOffset, Procedure1<Instruction> setter )
	{
		int targetInstructionLocation = sourceInstructionLocation + targetInstructionOffset;
		Instruction targetInstruction = getInstruction( targetInstructionLocation ).orElseThrow();
		setter.invoke( targetInstruction );
	}

	public void runFixUps()
	{
		for( Runnable fixUp : fixUps )
			fixUp.run();
	}
}
