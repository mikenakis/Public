package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.InstructionWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class IIncInstruction extends Instruction
{
	public static IIncInstruction read( CodeAttributeReader codeAttributeReader, boolean wide )
	{
		int index;
		int delta;
		if( wide )
		{
			index = codeAttributeReader.readUnsignedShort();
			delta = codeAttributeReader.readSignedShort();
		}
		else
		{
			index = codeAttributeReader.readUnsignedByte();
			delta = codeAttributeReader.readSignedByte();
		}
		return of( wide, index, delta );
	}

	public static IIncInstruction of( int index, int delta )
	{
		return of( false, index, delta );
	}

	public static IIncInstruction of( boolean wide, int index, int delta )
	{
		return new IIncInstruction( wide, index, delta );
	}

	private boolean wide; //TODO
	public final int index;
	public final int delta;

	private IIncInstruction( boolean wide, int index, int delta )
	{
		super( groupTag_IInc );
		this.wide = wide;
		this.index = index;
		this.delta = delta;
	}

	public boolean isWide()
	{
		return wide;
	}

	// TODO:
	//	void intern( ConstantPool constantPool )
	//	{
	//		wide = !(Helpers.isUnsignedByte( index ) && Helpers.isSignedByte( delta ));
	//	}

	@Deprecated @Override public IIncInstruction asIIncInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( OpCode.IINC ); }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		boolean wide = isWide();
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
