package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.writing.InstructionWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class IIncInstruction extends Instruction
{
	public static IIncInstruction read( BufferReader bufferReader, boolean wide )
	{
		int index;
		int delta;
		if( wide )
		{
			index = bufferReader.readUnsignedShort();
			delta = bufferReader.readSignedShort();
		}
		else
		{
			index = bufferReader.readUnsignedByte();
			delta = bufferReader.readSignedByte();
		}
		return of( index, delta );
	}

	public static IIncInstruction of( int index, int delta )
	{
		return new IIncInstruction( index, delta );
	}

	public final int index;
	public final int delta;

	private IIncInstruction( int index, int delta )
	{
		super( groupTag_IInc );
		this.index = index;
		this.delta = delta;
	}

	@Deprecated @Override public IIncInstruction asIIncInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( OpCode.IINC ); }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		boolean wide = !Helpers.isUnsignedByte( index ) || !Helpers.isSignedByte( delta );
		if( wide )
			instructionWriter.writeUnsignedByte( OpCode.WIDE );
		instructionWriter.writeUnsignedByte( OpCode.IINC );
		if( wide )
		{
			instructionWriter.writeUnsignedShort( index );
			instructionWriter.writeSignedShort( delta );
		}
		else
		{
			instructionWriter.writeUnsignedByte( index );
			instructionWriter.writeSignedByte( delta );
		}
	}
}
