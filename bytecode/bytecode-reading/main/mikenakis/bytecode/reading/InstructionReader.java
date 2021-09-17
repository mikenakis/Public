package mikenakis.bytecode.reading;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.kit.functional.Procedure1;

interface InstructionReader
{
	int readInt();
	int readSignedByte();
	int readSignedShort();
	int readUnsignedByte();
	int readUnsignedShort();
	void skipToAlign();
	Constant getConstant( int index );
	void setRelativeTargetInstruction( Instruction sourceInstruction, int targetInstructionOffset, Procedure1<Instruction> setter );
}
