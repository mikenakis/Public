package mikenakis.rumination;

import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.Descriptor;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.LocalVariableTableAttribute;
import mikenakis.bytecode.attributes.LocalVariableTypeTableAttribute;
import mikenakis.bytecode.attributes.StackMapTableAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModels;
import mikenakis.bytecode.attributes.code.Instructions;
import mikenakis.bytecode.attributes.code.instructions.LocalVariableInstruction;
import mikenakis.bytecode.constants.ClassConstant;
import mikenakis.bytecode.constants.FieldReferenceConstant;
import mikenakis.bytecode.constants.MethodReferenceConstant;
import mikenakis.bytecode.constants.NameAndTypeConstant;
import mikenakis.bytecode.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.constants.StringConstant;
import mikenakis.rumination.annotations.Ruminant;

import java.util.List;

/**
 * Intercepts class loading and performs bytecode manipulation to add rumination capabilities to objects marked with @{@link Ruminant}.
 * <p>
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class Ruminizer
{
	private static final ClassConstant objectsClassConstant = new ClassConstant( "java/util/Objects" );
	private static final NameAndTypeConstant equalsMethodNameAndType = new NameAndTypeConstant( "equals", "(L" + "java/lang/Object;Ljava/lang/Object;)Z" );
	private static final MethodReferenceConstant objectsEqualsMethodReferenceConstant = new PlainMethodReferenceConstant( objectsClassConstant, equalsMethodNameAndType );

	public Ruminizer()
	{
	}

	public boolean ruminize( ByteCodeType byteCodeType, String ruminatorMethodName )
	{
		NameAndTypeConstant ruminatorMethodNameAndTypeConstant = new NameAndTypeConstant( ruminatorMethodName, "(L" + "java/lang/String;)V" );
		boolean fixed = false;
		for( ByteCodeMethod byteCodeMethod : byteCodeType.methods )
			fixed |= fixByteCodeMethod( byteCodeMethod, ruminatorMethodNameAndTypeConstant );
		return fixed;
	}

	private static boolean fixByteCodeMethod( ByteCodeMethod byteCodeMethod, NameAndTypeConstant ruminatorMethodNameAndTypeConstant )
	{
		String methodName = byteCodeMethod.getName();
		if( !methodName.startsWith( "set" ) )
			return false;
		Descriptor methodDescriptor = byteCodeMethod.getDescriptor();
		if( !methodDescriptor.typeName.equals( "void" ) )
			return false;
		if( methodDescriptor.argumentTypeNames.size() != 1 )
			return false;
		char parameterType = getParameterType( methodDescriptor.argumentTypeNames.get( 0 ) );
		LocalVariableInstruction.Model load1Model = getLoad1Model( parameterType );
		CodeAttribute codeAttribute = byteCodeMethod.getCodeAttribute();
		List<Instruction> instructions = codeAttribute.instructions;
		int n = instructions.size();
		if( n < 4 )
			return false;
		int pc = n - 4;
		if( instructions.get( pc ).getModel() != InstructionModels.ALOAD_0 )
			return false;
		if( instructions.get( pc + 1 ).getModel() != load1Model )
			return false;
		Instruction putFieldInstruction = instructions.get( pc + 2 );
		if( putFieldInstruction.getModel() != InstructionModels.PUTFIELD )
			return false;
		Instruction returnInstruction = instructions.get( pc + 3 );
		if( returnInstruction.getModel() != InstructionModels.RETURN )
			return false;

		/** skip the {@link InstructionModels.ALOAD_0} instruction (need to keep it because it may be the target of previous instructions.) */
		pc++;

		//emit bytecode to get the existing field value
		FieldReferenceConstant fieldReferenceConstant = putFieldInstruction.asConstantReferencingInstruction().constant.asFieldReferenceConstant();
		assert fieldReferenceConstant.nameAndTypeConstant.getDescriptor().typeName.equals( byteCodeMethod.getDescriptor().argumentTypeNames.get( 0 ) );
		instructions.add( pc++, Instructions.newGetField( fieldReferenceConstant ) );

		//emit bytecode to get the new value
		instructions.add( pc++, load1Model.newInstruction( 1 ) );

		//emit bytecode to compare existing with new value and jump to the return instruction if equal
		switch( parameterType )
		{
			case 'I':
				instructions.add( pc++, Instructions.newIfICmpEQ( returnInstruction ) );
				break;
			case 'F':
				instructions.add( pc++, Instructions.newFCmpL() );
				instructions.add( pc++, Instructions.newIfEQ( returnInstruction ) );
				break;
			case 'J':
				instructions.add( pc++, Instructions.newLCmp() );
				instructions.add( pc++, Instructions.newIfEQ( returnInstruction ) );
				codeAttribute.maxStack++;
				break;
			case 'D':
				instructions.add( pc++, Instructions.newDCmpL() );
				instructions.add( pc++, Instructions.newIfEQ( returnInstruction ) );
				codeAttribute.maxStack++;
				break;
			case 'L':
				instructions.add( pc++, Instructions.newInvokeStatic( objectsEqualsMethodReferenceConstant ) );
				instructions.add( pc++, Instructions.newIfNE( returnInstruction ) );
				break;
			default:
				assert false;
		}

		//skip bytecode that stores the value into the existing field
		instructions.add( pc++, Instructions.newALoad( 0 ) );
		assert instructions.get( pc ).getModel() == load1Model;
		assert instructions.get( pc + 1 ).getModel() == InstructionModels.PUTFIELD;
		pc += 2;

		//emit bytecode to invoke the 'member changed' method
		instructions.add( pc++, Instructions.newALoad( 0 ) );
		instructions.add( pc++, Instructions.newLdc( new StringConstant( fieldReferenceConstant.nameAndTypeConstant.nameConstant ) ) );
		instructions.add( pc++,
			Instructions.newInvokeVirtual( new PlainMethodReferenceConstant( byteCodeMethod.declaringType.thisClassConstant, ruminatorMethodNameAndTypeConstant ) ) );

		//ensure we have reached the return instruction.
		assert instructions.get( pc ) == returnInstruction;

		//remove local variable tables as they are not valid anymore
		codeAttribute.attributes.removeAttributeByNameIfPresent( LocalVariableTableAttribute.NAME );
		codeAttribute.attributes.removeAttributeByNameIfPresent( LocalVariableTypeTableAttribute.NAME );
		codeAttribute.intern( codeAttribute.method.declaringType.constantPool );

		//fix the stack map table attribute
		StackMapTableAttribute stackMapTableAttribute = byteCodeMethod.getCodeAttribute().getOrCreateStackMapTableAttribute();
		stackMapTableAttribute.addSameFrame( returnInstruction );

		//indicate that the method was modified.
		return true;
	}

	private static char getParameterType( String parameterTypeName )
	{
		if( parameterTypeName.endsWith( "[]" ) )
			return 'L';
		switch( parameterTypeName )
		{
			case "boolean":
			case "byte":
			case "short":
			case "char":
			case "int":
				return 'I';
			case "float":
				return 'F';
			case "long":
				return 'J';
			case "double":
				return 'D';
			case "void":
				assert false;
				return '\0';
			default:
				return 'L';
		}
	}

	private static LocalVariableInstruction.Model getLoad1Model( char type )
	{
		switch( type )
		{
			case 'I':
				return InstructionModels.ILOAD_1;
			case 'F':
				return InstructionModels.FLOAD_1;
			case 'J':
				return InstructionModels.LLOAD_1;
			case 'D':
				return InstructionModels.DLOAD_1;
			case 'L':
				return InstructionModels.ALOAD_1;
			default:
				throw new AssertionError();
		}
	}
}
