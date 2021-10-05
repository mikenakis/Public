package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.InstructionWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class InvokeDynamicInstruction extends Instruction
{
	public static InvokeDynamicInstruction read( CodeAttributeReader codeAttributeReader, boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.INVOKEDYNAMIC;
		InvokeDynamicConstant invokeDynamicConstant = codeAttributeReader.readIndexAndGetConstant().asInvokeDynamicConstant();
		int operand2 = codeAttributeReader.readUnsignedShort(); //2 extra bytes, unused.
		assert operand2 == 0;
		return of( invokeDynamicConstant );
	}

	public static InvokeDynamicInstruction of( InvokeDynamicConstant invokeDynamicConstant )
	{
		return new InvokeDynamicInstruction( invokeDynamicConstant );
	}

	private final InvokeDynamicConstant invokeDynamicConstant;

	private InvokeDynamicInstruction( InvokeDynamicConstant invokeDynamicConstant )
	{
		super( groupTag_InvokeDynamic );
		this.invokeDynamicConstant = invokeDynamicConstant;
	}

	public MethodPrototype methodPrototype() { return invokeDynamicConstant.methodPrototype(); }
	public BootstrapMethod bootstrapMethod() { return invokeDynamicConstant.getBootstrapMethod(); }
	@Deprecated @Override public InvokeDynamicInstruction asInvokeDynamicInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( OpCode.INVOKEDYNAMIC ); }

	@Override public void intern( Interner interner )
	{
		invokeDynamicConstant.intern( interner );
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		int constantIndex = instructionWriter.getIndex( invokeDynamicConstant );
		instructionWriter.writeUnsignedByte( OpCode.INVOKEDYNAMIC );
		instructionWriter.writeUnsignedShort( constantIndex );
		instructionWriter.writeUnsignedByte( 0 );
		instructionWriter.writeUnsignedByte( 0 );
	}
}
