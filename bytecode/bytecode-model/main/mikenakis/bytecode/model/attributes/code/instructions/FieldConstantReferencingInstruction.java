package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ClassDesc;
import java.util.Set;

public final class FieldConstantReferencingInstruction extends Instruction
{
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

	public String fieldDeclaringTypeName() { return fieldReferenceConstant.getDeclaringTypeConstant().typeName(); }
	public String fieldName() { return fieldReferenceConstant.getNameAndDescriptorConstant().getNameConstant().stringValue(); }

	public String fieldTypeName()
	{
		String descriptorString = fieldReferenceConstant.getNameAndDescriptorConstant().getDescriptorConstant().stringValue();
		ClassDesc classDesc = ClassDesc.ofDescriptor( descriptorString );
		return ByteCodeHelpers.typeNameFromClassDesc( classDesc );
	}

	@Deprecated @Override public FieldConstantReferencingInstruction asFieldConstantReferencingInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( opCode ); }
}
