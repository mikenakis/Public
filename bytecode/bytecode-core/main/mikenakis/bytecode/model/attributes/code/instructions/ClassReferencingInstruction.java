package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.InstructionWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Set;

public final class ClassReferencingInstruction extends Instruction
{
	public static ClassReferencingInstruction read( BufferReader bufferReader, ReadingConstantPool constantPool, boolean wide, int opCode )
	{
		assert !wide;
		ClassConstant classConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asClassConstant();
		return of( opCode, classConstant );
	}

	public static ClassReferencingInstruction of( int opCode, TypeDescriptor targetTypeDescriptor )
	{
		ClassConstant targetClassConstant = ClassConstant.of( targetTypeDescriptor );
		return new ClassReferencingInstruction( opCode, targetClassConstant );
	}

	public static ClassReferencingInstruction of( int opCode, ClassConstant targetClassConstant )
	{
		return new ClassReferencingInstruction( opCode, targetClassConstant );
	}

	private static final Set<Integer> opCodes = Set.of( OpCode.NEW, OpCode.ANEWARRAY, OpCode.CHECKCAST, OpCode.INSTANCEOF );

	public final int opCode;
	private final ClassConstant targetClassConstant;

	private ClassReferencingInstruction( int opCode, ClassConstant targetClassConstant )
	{
		super( groupTag_ClassConstantReferencing );
		assert opCodes.contains( opCode );
		this.opCode = opCode;
		this.targetClassConstant = targetClassConstant;
	}

	public TypeDescriptor target() { return targetClassConstant.typeDescriptor(); }
	@Deprecated @Override public ClassReferencingInstruction asClassReferencingInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( opCode ); }

	@Override public void intern( Interner interner )
	{
		targetClassConstant.intern( interner );
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		instructionWriter.writeUnsignedByte( opCode );
		int constantIndex = instructionWriter.getIndex( targetClassConstant );
		instructionWriter.writeUnsignedShort( constantIndex );
	}
}
