package io.github.mikenakis.bytecode.model.attributes.code.instructions;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.model.Constant;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.model.attributes.code.OpCode;
import io.github.mikenakis.bytecode.model.constants.ClassConstant;
import io.github.mikenakis.bytecode.model.constants.MethodReferenceConstant;
import io.github.mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import io.github.mikenakis.bytecode.model.descriptors.MethodReference;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.InstructionWriter;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class InvokeInterfaceInstruction extends Instruction
{
	public static InvokeInterfaceInstruction read( BufferReader bufferReader, ReadingConstantPool constantPool, boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.INVOKEINTERFACE;
		MethodReferenceConstant methodReferenceConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMethodReferenceConstant();
		int argumentCount = bufferReader.readUnsignedByte();
		int extraByte = bufferReader.readUnsignedByte(); //one extra byte, unused.
		assert extraByte == 0;
		return of( methodReferenceConstant, argumentCount );
	}

	public static InvokeInterfaceInstruction of( MethodReference methodReference, int argumentCount )
	{
		ClassConstant declaringTypeConstant = ClassConstant.of( methodReference.declaringTypeDescriptor );
		NameAndDescriptorConstant nameAndDescriptorConstant = NameAndDescriptorConstant.of( methodReference.methodPrototype );
		MethodReferenceConstant methodReferenceConstant = MethodReferenceConstant.of( methodReference.kind, declaringTypeConstant, nameAndDescriptorConstant );
		return of( methodReferenceConstant, argumentCount );
	}

	public static InvokeInterfaceInstruction of( MethodReferenceConstant methodReferenceConstant, int argumentCount )
	{
		return new InvokeInterfaceInstruction( methodReferenceConstant, argumentCount );
	}

	private final MethodReferenceConstant methodReferenceConstant;
	public final int argumentCount;

	private InvokeInterfaceInstruction( MethodReferenceConstant methodReferenceConstant, int argumentCount )
	{
		super( groupTag_InvokeInterface );
		assert methodReferenceConstant.tag == Constant.tag_InterfaceMethodReference;
		this.methodReferenceConstant = methodReferenceConstant;
		this.argumentCount = argumentCount;
	}

	@Deprecated @Override public InvokeInterfaceInstruction asInvokeInterfaceInstruction() { return this; }
	public MethodReference methodReference() { return methodReferenceConstant.methodReference(); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( OpCode.INVOKEINTERFACE ) + " " + methodReferenceConstant.toString(); }

	@Override public void intern( Interner interner )
	{
		methodReferenceConstant.intern( interner );
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		int constantIndex = instructionWriter.getIndex( methodReferenceConstant );
		instructionWriter.writeUnsignedByte( OpCode.INVOKEINTERFACE );
		instructionWriter.writeUnsignedShort( constantIndex );
		instructionWriter.writeUnsignedByte( argumentCount );
		instructionWriter.writeUnsignedByte( 0 );
	}
}
