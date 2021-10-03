package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.descriptors.FieldReference;
import mikenakis.java_type_model.FieldDescriptor;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Set;

public final class FieldConstantReferencingInstruction extends Instruction
{
	public static FieldConstantReferencingInstruction of( int opCode, FieldReference fieldReference )
	{
		ClassConstant declaringTypeConstant = ClassConstant.ofTypeName( fieldReference.declaringTypeDescriptor.typeName() );
		NameAndDescriptorConstant nameAndDescriptorConstant = NameAndDescriptorConstant.of( fieldReference.fieldPrototype.fieldName, fieldReference.fieldPrototype.descriptor.typeDescriptor );
		FieldReferenceConstant fieldReferenceConstant = FieldReferenceConstant.of( declaringTypeConstant, nameAndDescriptorConstant );
		return new FieldConstantReferencingInstruction( opCode, fieldReferenceConstant );
	}

	public static FieldConstantReferencingInstruction of( int opCode, FieldReferenceConstant fieldReferenceConstant )
	{
		return new FieldConstantReferencingInstruction( opCode, fieldReferenceConstant );
	}

	private static final Set<Integer> opCodes = Set.of( OpCode.GETSTATIC, OpCode.PUTSTATIC, OpCode.GETFIELD, OpCode.PUTFIELD );

	public final int opCode;
	public final FieldReferenceConstant fieldReferenceConstant;

	private FieldConstantReferencingInstruction( int opCode, FieldReferenceConstant fieldReferenceConstant )
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

	@Deprecated @Override public FieldConstantReferencingInstruction asFieldConstantReferencingInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( opCode ); }
}
