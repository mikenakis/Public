package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.descriptors.MethodReference;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class InvokeInterfaceInstruction extends Instruction
{
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

	public final MethodReferenceConstant methodReferenceConstant;
	public final int argumentCount;

	private InvokeInterfaceInstruction( MethodReferenceConstant methodReferenceConstant, int argumentCount )
	{
		super( groupTag_InvokeInterface );
		assert methodReferenceConstant.tag == Constant.tag_InterfaceMethodReference;
		this.methodReferenceConstant = methodReferenceConstant;
		this.argumentCount = argumentCount;
	}

	@Deprecated @Override public InvokeInterfaceInstruction asInvokeInterfaceInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( OpCode.INVOKEINTERFACE ) + " " + methodReferenceConstant.toString(); }
}
