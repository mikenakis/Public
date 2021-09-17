package mikenakis.bytecode.writing;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;

interface InstructionWriter
{
	void writeUnsignedByte( int value );
	void writeSignedByte( int value );
	void writeUnsignedShort( int value );
	void writeSignedShort( int value );
	void writeInt( int value );
	void skipToAlign();
	int getIndex( Constant constant );
	int getOffset( Instruction sourceInstruction, Instruction targetInstruction );
}
