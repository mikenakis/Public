package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.writing.InstructionWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class NewPrimitiveArrayInstruction extends Instruction
{
	public static NewPrimitiveArrayInstruction read( BufferReader bufferReader, boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.NEWARRAY;
		Type type = Type.fromNumber( bufferReader.readUnsignedByte() );
		return of( type );
	}

	public static NewPrimitiveArrayInstruction of( Type type )
	{
		return new NewPrimitiveArrayInstruction( type );
	}

	public final Type type;

	private NewPrimitiveArrayInstruction( Type type )
	{
		super( groupTag_NewPrimitiveArray );
		assert type.isPrimitive();
		this.type = type;
	}

	@Deprecated @Override public NewPrimitiveArrayInstruction asNewPrimitiveArrayInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( OpCode.NEWARRAY ); }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		instructionWriter.writeUnsignedByte( OpCode.NEWARRAY );
		instructionWriter.writeUnsignedByte( type.number );
	}
}
