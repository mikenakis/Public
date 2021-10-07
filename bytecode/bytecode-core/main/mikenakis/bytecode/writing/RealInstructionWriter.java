package mikenakis.bytecode.writing;

import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;

public class RealInstructionWriter implements InstructionWriter
{
	private final BufferWriter bufferWriter;
	private final LocationMap locationMap;
	private final ConstantPool constantPool;

	RealInstructionWriter( LocationMap locationMap, ConstantPool constantPool )
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
		return constantPool.getIndex( constant );
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