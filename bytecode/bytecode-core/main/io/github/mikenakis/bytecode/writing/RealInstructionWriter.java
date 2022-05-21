package io.github.mikenakis.bytecode.writing;

import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.kit.Helpers;
import io.github.mikenakis.bytecode.model.Constant;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;

public class RealInstructionWriter implements InstructionWriter
{
	private final BufferWriter bufferWriter;
	private final WritingLocationMap locationMap;
	private final WritingConstantPool constantPool;

	public RealInstructionWriter( WritingLocationMap locationMap, WritingConstantPool constantPool )
	{
		bufferWriter = new BufferWriter();
		this.locationMap = locationMap;
		this.constantPool = constantPool;
	}

	@Override public void writeUnsignedByte( int value ) { bufferWriter.writeUnsignedByte( value ); }
	@Override public void writeSignedByte( int value ) { bufferWriter.writeSignedByte( value ); }
	@Override public void writeUnsignedShort( int value ) { bufferWriter.writeUnsignedShort( value ); }
	@Override public void writeSignedShort( int value ) { bufferWriter.writeSignedShort( value ); }
	@Override public void writeInt( int value ) { bufferWriter.writeInt( value ); }
	@Override public void skipToAlign() { bufferWriter.writeZeroBytes( Helpers.padding( bufferWriter.getPosition() ) ); }

	@Override public int getIndex( Constant constant )
	{
		return constantPool.getConstantIndex( constant );
	}

	@Override public int getOffset( Instruction sourceInstruction, Instruction targetInstruction )
	{
		return locationMap.getOffset( sourceInstruction, targetInstruction );
	}

	public byte[] toBytes()
	{
		return bufferWriter.toBytes();
	}
}
