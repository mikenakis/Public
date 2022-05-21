package io.github.mikenakis.bytecode.writing;

import io.github.mikenakis.bytecode.model.Constant;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;

public interface InstructionWriter
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
