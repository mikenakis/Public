package io.github.mikenakis.bytecode.writing;

import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.model.attributes.stackmap.StackMapFrame;
import io.github.mikenakis.kit.Kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WritingLocationMap
{
	private final List<Instruction> instructionsByLocation = new ArrayList<>();
	private final Map<Instruction,Integer> locationsByInstruction = new HashMap<>();

	public WritingLocationMap()
	{
	}

	public int getLocation( Optional<Instruction> instruction )
	{
		return instruction.isEmpty() ? instructionsByLocation.size() : getLocation( instruction.get() );
	}

	public int getLocation( Instruction instruction )
	{
		return Kit.map.get( locationsByInstruction, instruction );
	}

	public int getLength( Instruction instruction )
	{
		int location = getLocation( instruction );
		int nextLocation;
		for( nextLocation = location + 1; nextLocation < instructionsByLocation.size(); nextLocation++ )
			if( instructionsByLocation.get( nextLocation ) != null )
				break;
		return nextLocation - location;
	}

	public int getOffset( Instruction sourceInstruction, Instruction targetInstruction )
	{
		int sourceLocation = getLocation( sourceInstruction );
		int targetLocation = getLocation( targetInstruction );
		return targetLocation - sourceLocation;
	}

	public void add( Instruction instruction )
	{
		int location = instructionsByLocation.size();
		instructionsByLocation.add( instruction );
		Kit.map.add( locationsByInstruction, instruction, location );
	}

	public void setLength( Instruction instruction, int length )
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

	public void removeBytes( int location, int length )
	{
		assert length > 0;
		instructionsByLocation.subList( location, location + length ).clear();
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

	public int getOffsetDelta( StackMapFrame stackMapFrame, Optional<StackMapFrame> previousFrame )
	{
		int offset = getLocation( Optional.of( stackMapFrame.getTargetInstruction() ) );
		if( previousFrame.isEmpty() )
			return offset;
		int previousLocation = getLocation( Optional.of( previousFrame.get().getTargetInstruction() ) );
		return offset - previousLocation - 1;
	}
}
