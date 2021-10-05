package mikenakis.bytecode.reading;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.kit.Kit;

class ReadingLocationMap extends LocationMap
{
	private int topLocation;

	ReadingLocationMap( int codeLength )
	{
		super( codeLength );
	}

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

	int getPosition()
	{
		return topLocation;
	}
}
