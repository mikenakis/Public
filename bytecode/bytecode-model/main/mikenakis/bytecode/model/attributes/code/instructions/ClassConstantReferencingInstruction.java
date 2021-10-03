package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Set;

public final class ClassConstantReferencingInstruction extends Instruction
{
	public static ClassConstantReferencingInstruction of( int opCode, ClassConstant targetClassConstant )
	{
		return new ClassConstantReferencingInstruction( opCode, targetClassConstant );
	}

	private static final Set<Integer> opCodes = Set.of( OpCode.NEW, OpCode.ANEWARRAY, OpCode.CHECKCAST, OpCode.INSTANCEOF );

	public final int opCode;
	public final ClassConstant targetClassConstant;

	private ClassConstantReferencingInstruction( int opCode, ClassConstant targetClassConstant )
	{
		super( groupTag_ClassConstantReferencing );
		assert opCodes.contains( opCode );
		this.opCode = opCode;
		this.targetClassConstant = targetClassConstant;
	}

	public TypeDescriptor target() { return targetClassConstant.typeDescriptor(); }
	@Deprecated @Override public ClassConstantReferencingInstruction asClassConstantReferencingInstruction() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( opCode );
	}
}
