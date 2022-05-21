package io.github.mikenakis.bytecode.model.attributes.code.instructions;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.model.attributes.BootstrapMethod;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.model.attributes.code.OpCode;
import io.github.mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import io.github.mikenakis.bytecode.model.descriptors.MethodPrototype;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.InstructionWriter;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class InvokeDynamicInstruction extends Instruction
{
	public static InvokeDynamicInstruction read( BufferReader bufferReader, ReadingConstantPool constantPool, boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.INVOKEDYNAMIC;
		InvokeDynamicConstant invokeDynamicConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asInvokeDynamicConstant();
		int operand2 = bufferReader.readUnsignedShort(); //2 extra bytes, unused.
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
