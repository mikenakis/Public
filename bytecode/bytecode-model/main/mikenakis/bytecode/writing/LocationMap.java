package mikenakis.bytecode.writing;

import mikenakis.bytecode.model.attributes.code.Instruction;

import java.util.Optional;

interface LocationMap
{
	int getLocation( Optional<Instruction> instruction );
	int getLocation( Instruction instruction );
	int getOffset( Instruction sourceInstruction, Instruction targetInstruction );
}
