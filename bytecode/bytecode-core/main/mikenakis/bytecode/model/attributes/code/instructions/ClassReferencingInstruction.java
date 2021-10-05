package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Set;

public final class ClassReferencingInstruction extends Instruction
{
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
	public final ClassConstant targetClassConstant;

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
}
