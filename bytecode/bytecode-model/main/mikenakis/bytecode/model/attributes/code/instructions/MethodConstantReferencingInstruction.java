package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.descriptors.MethodReference;
import mikenakis.bytecode.model.descriptors.MethodReferenceKind;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Set;

public final class MethodConstantReferencingInstruction extends Instruction
{
	public static MethodConstantReferencingInstruction of( int opCode, MethodReferenceKind methodReferenceKind, MethodReference methodReference )
	{
		int tag = switch( methodReferenceKind )
			{
				case Plain -> Constant.tag_PlainMethodReference;
				case Interface -> Constant.tag_InterfaceMethodReference;
			};
		ClassConstant declaringTypeConstant = ClassConstant.ofTypeName( methodReference.declaringTypeDescriptor.typeName() );
		NameAndDescriptorConstant nameAndDescriptorConstant = NameAndDescriptorConstant.of( methodReference.methodPrototype.name, ByteCodeHelpers.descriptorStringFromMethodDescriptor( methodReference.methodPrototype.descriptor ) );
		MethodReferenceConstant methodReferenceConstant = MethodReferenceConstant.of( tag, declaringTypeConstant, nameAndDescriptorConstant );
		return new MethodConstantReferencingInstruction( opCode, methodReferenceConstant );
	}

	public static MethodConstantReferencingInstruction of( int opCode, MethodReferenceConstant methodReferenceConstant )
	{
		return new MethodConstantReferencingInstruction( opCode, methodReferenceConstant );
	}

	private static final Set<Integer> opCodes = Set.of( OpCode.INVOKEVIRTUAL, OpCode.INVOKESPECIAL, OpCode.INVOKESTATIC );

	public final int opCode;
	public final MethodReferenceConstant methodReferenceConstant;

	private MethodConstantReferencingInstruction( int opCode, MethodReferenceConstant methodReferenceConstant )
	{
		super( groupTag_MethodConstantReferencing );
		assert methodReferenceConstant.tag == Constant.tag_PlainMethodReference || methodReferenceConstant.tag == Constant.tag_InterfaceMethodReference;
		switch( opCode )
		{
			case OpCode.INVOKEVIRTUAL:
				switch( methodReferenceConstant.tag )
				{
					case Constant.tag_PlainMethodReference: //occurs
						Kit.get( 1 );
						break;
					case Constant.tag_InterfaceMethodReference: //does not occur
						Kit.get( 2 );
						break;
					default:
						assert false;
				}
				break;
			case OpCode.INVOKESPECIAL:
				switch( methodReferenceConstant.tag )
				{
					case Constant.tag_PlainMethodReference: //occurs
						Kit.get( 3 );
						break;
					case Constant.tag_InterfaceMethodReference: //occurs
						Kit.get( 4 );
						break;
					default:
						assert false;
				}
				break;
			case OpCode.INVOKESTATIC:
				switch( methodReferenceConstant.tag )
				{
					case Constant.tag_PlainMethodReference: //occurs
						Kit.get( 5 );
						break;
					case Constant.tag_InterfaceMethodReference: //occurs
						Kit.get( 6 );
						break;
					default:
						assert false;
				}
				break;
		}
		assert opCodes.contains( opCode );
		this.opCode = opCode;
		this.methodReferenceConstant = methodReferenceConstant;
	}

	@Deprecated @Override public MethodConstantReferencingInstruction asMethodConstantReferencingInstruction() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( opCode );
	}
}
