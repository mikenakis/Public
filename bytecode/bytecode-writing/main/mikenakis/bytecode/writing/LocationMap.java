package mikenakis.bytecode.writing;

import mikenakis.bytecode.model.attributes.code.Instruction;

import java.util.Optional;

interface LocationMap
{
	Optional<Instruction> getInstruction( int location ); //returns Optional.empty() if the location is @end.
	int getLocation( Optional<Instruction> instruction );
	int getLocation( Instruction instruction );
	int getOffset( Instruction sourceInstruction, Instruction targetInstruction );
	boolean contains( int location );
}
