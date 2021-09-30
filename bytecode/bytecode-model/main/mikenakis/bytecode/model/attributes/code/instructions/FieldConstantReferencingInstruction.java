package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.descriptors.FieldDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

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

	public String fieldDeclaringTypeName() { return fieldReferenceConstant.declaringTypeName(); }
	public String fieldName() { return fieldReferenceConstant.fieldName(); }
	public FieldDescriptor fieldDescriptor() { return fieldReferenceConstant.fieldDescriptor(); }
	public String fieldTypeName() { return fieldDescriptor().typeDescriptor.name(); }

	@Deprecated @Override public FieldConstantReferencingInstruction asFieldConstantReferencingInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( opCode ); }
}
