package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.descriptors.FieldReference;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.InstructionWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.java_type_model.FieldDescriptor;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Set;

public final class FieldReferencingInstruction extends Instruction
{
	public static FieldReferencingInstruction read( BufferReader bufferReader, ReadingConstantPool constantPool, boolean wide, int opCode )
	{
		assert !wide;
		FieldReferenceConstant fieldReferenceConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asFieldReferenceConstant();
		return of( opCode, fieldReferenceConstant );
	}

	public static FieldReferencingInstruction of( int opCode, FieldReference fieldReference )
	{
		FieldReferenceConstant fieldReferenceConstant = FieldReferenceConstant.of( fieldReference );
		return new FieldReferencingInstruction( opCode, fieldReferenceConstant );
	}

	public static FieldReferencingInstruction of( int opCode, FieldReferenceConstant fieldReferenceConstant )
	{
		return new FieldReferencingInstruction( opCode, fieldReferenceConstant );
	}

	private static final Set<Integer> opCodes = Set.of( OpCode.GETSTATIC, OpCode.PUTSTATIC, OpCode.GETFIELD, OpCode.PUTFIELD );

	public final int opCode;
	private final FieldReferenceConstant fieldReferenceConstant;

	private FieldReferencingInstruction( int opCode, FieldReferenceConstant fieldReferenceConstant )
	{
		super( groupTag_FieldConstantReferencing );
		assert opCodes.contains( opCode );
		this.opCode = opCode;
		this.fieldReferenceConstant = fieldReferenceConstant;
	}

	public TypeDescriptor fieldDeclaringType() { return fieldReferenceConstant.declaringTypeDescriptor(); }
	public String fieldName() { return fieldReferenceConstant.fieldName(); }
	public FieldDescriptor fieldDescriptor() { return fieldReferenceConstant.fieldDescriptor(); }
	public TypeDescriptor fieldType() { return fieldDescriptor().typeDescriptor; }
	@Deprecated @Override public FieldReferencingInstruction asFieldReferencingInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( opCode ); }

	@Override public void intern( Interner interner )
	{
		fieldReferenceConstant.intern( interner );
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		instructionWriter.writeUnsignedByte( opCode );
		int constantIndex = instructionWriter.getIndex( fieldReferenceConstant );
		instructionWriter.writeUnsignedShort( constantIndex );
	}
}
