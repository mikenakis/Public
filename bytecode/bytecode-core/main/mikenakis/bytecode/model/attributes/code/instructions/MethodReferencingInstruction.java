package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.bytecode.model.descriptors.MethodReference;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.InstructionWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Set;

public final class MethodReferencingInstruction extends Instruction
{
	public static MethodReferencingInstruction read( CodeAttributeReader codeAttributeReader, boolean wide, int opCode )
	{
		assert !wide;
		MethodReferenceConstant methodReferenceConstant = codeAttributeReader.readIndexAndGetConstant().asMethodReferenceConstant();
		return of( opCode, methodReferenceConstant );
	}

	public static MethodReferencingInstruction of( int opCode, MethodReference methodReference )
	{
		MethodReferenceConstant methodReferenceConstant = MethodReferenceConstant.of( methodReference );
		return new MethodReferencingInstruction( opCode, methodReferenceConstant );
	}

	public static MethodReferencingInstruction of( int opCode, MethodReferenceConstant methodReferenceConstant )
	{
		return new MethodReferencingInstruction( opCode, methodReferenceConstant );
	}

	private static final Set<Integer> opCodes = Set.of( OpCode.INVOKEVIRTUAL, OpCode.INVOKESPECIAL, OpCode.INVOKESTATIC );

	public final int opCode;
	private final MethodReferenceConstant methodReferenceConstant;

	private MethodReferencingInstruction( int opCode, MethodReferenceConstant methodReferenceConstant )
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

	@Deprecated @Override public MethodReferencingInstruction asMethodReferencingInstruction() { return this; }
	public MethodReference methodReference() { return methodReferenceConstant.methodReference(); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( opCode ); }

	@Override public void intern( Interner interner )
	{
		methodReferenceConstant.intern( interner );
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		instructionWriter.writeUnsignedByte( opCode );
		int constantIndex = instructionWriter.getIndex( methodReferenceConstant );
		instructionWriter.writeUnsignedShort( constantIndex );
	}
}
