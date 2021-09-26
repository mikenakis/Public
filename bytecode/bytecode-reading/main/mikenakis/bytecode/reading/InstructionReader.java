package mikenakis.bytecode.reading;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.attributes.code.instructions.BranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ClassConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ConditionalBranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.FieldConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.IIncInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ImmediateLoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.IndirectLoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LocalVariableInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LookupSwitchEntry;
import mikenakis.bytecode.model.attributes.code.instructions.LookupSwitchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MethodConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MultiANewArrayInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.NewPrimitiveArrayInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.OperandlessInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.OperandlessLoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.TableSwitchInstruction;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.constants.InterfaceMethodReferenceConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.kit.functional.Procedure1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class InstructionReader
{
	static List<Instruction> readInstructions( BufferReader bufferReader, ReadingLocationMap locationMap, ConstantPool constantPool )
	{
		var instructionReader = new InstructionReader( bufferReader, locationMap, constantPool );
		return instructionReader.readInstructions();
	}

	private final Collection<Runnable> fixUps = new ArrayList<>();
	private final ReadingLocationMap locationMap;
	private final BufferReader bufferReader;
	private final ConstantPool constantPool;

	private InstructionReader( BufferReader bufferReader, ReadingLocationMap locationMap, ConstantPool constantPool )
	{
		this.bufferReader = bufferReader;
		this.locationMap = locationMap;
		this.constantPool = constantPool;
	}

	private List<Instruction> readInstructions()
	{
		List<Instruction> instructions = new ArrayList<>();
		while( !bufferReader.isAtEnd() )
		{
			int startLocation = bufferReader.getPosition();
			Instruction instruction = readInstruction();
			int endLocation = bufferReader.getPosition();
			locationMap.add( startLocation, instruction, endLocation - startLocation );
			instructions.add( instruction );
		}
		for( Runnable fixUp : fixUps )
			fixUp.run();
		return instructions;
	}

	private Constant readIndexAndGetConstant()
	{
		return getConstant( readUnsignedShort() );
	}

	private int readInt() { return bufferReader.readInt(); }
	private int readSignedByte() { return bufferReader.readSignedByte(); }
	private int readSignedShort() { return bufferReader.readSignedShort(); }
	private int readUnsignedByte() { return bufferReader.readUnsignedByte(); }
	private int readUnsignedShort() { return bufferReader.readUnsignedShort(); }
	private void skipToAlign() { bufferReader.skip( Helpers.padding( bufferReader.getPosition() ) ); }

	private void setRelativeTargetInstruction( Instruction sourceInstruction, int targetInstructionOffset, Procedure1<Instruction> setter )
	{
		assert !locationMap.contains( sourceInstruction );
		int sourceInstructionLocation = locationMap.getPosition();
		assert sourceInstructionLocation + targetInstructionOffset < locationMap.size();
		if( targetInstructionOffset < 0 )
			fix( sourceInstructionLocation, targetInstructionOffset, setter );
		else
			fixUps.add( () -> //
				{
					assert sourceInstructionLocation == locationMap.getLocation( sourceInstruction );
					fix( sourceInstructionLocation, targetInstructionOffset, setter );
				} );
	}

	private void fix( int sourceInstructionLocation, int targetInstructionOffset, Procedure1<Instruction> setter )
	{
		int targetInstructionLocation = sourceInstructionLocation + targetInstructionOffset;
		Instruction targetInstruction = locationMap.getInstruction( targetInstructionLocation ).orElseThrow();
		setter.invoke( targetInstruction );
	}

	private Constant getConstant( int index )
	{
		return constantPool.getConstant( index );
	}

	private Instruction readInstruction()
	{
		boolean wide = false;
		int opCode = readUnsignedByte();
		if( opCode == OpCode.WIDE )
		{
			wide = true;
			opCode = readUnsignedByte();
		}
		int instructionGroupTag = Instruction.groupFromOpCode( opCode );
		return switch( instructionGroupTag )
			{
				case Instruction.groupTag_Operandless -> readOperandlessInstruction( wide, opCode );
				case Instruction.groupTag_OperandlessLoadConstant -> readOperandlessLoadConstantInstruction( wide, opCode );
				case Instruction.groupTag_ImmediateLoadConstant -> readImmediateLoadConstantInstruction( wide, opCode );
				case Instruction.groupTag_IndirectLoadConstant -> readIndirectLoadConstantInstruction( wide, opCode );
				case Instruction.groupTag_LocalVariable -> readLocalVariableInstruction( wide, opCode );
				case Instruction.groupTag_IInc -> readIIncInstruction( wide );
				case Instruction.groupTag_ConditionalBranch -> readConditionalBranchInstruction( wide, opCode );
				case Instruction.groupTag_Branch -> readBranchInstruction( wide, opCode );
				case Instruction.groupTag_TableSwitch -> readTableSwitchInstruction( wide );
				case Instruction.groupTag_LookupSwitch -> readLookupSwitchInstruction( wide, opCode );
				case Instruction.groupTag_ClassConstantReferencing -> readClassConstantReferencingInstruction( wide, opCode );
				case Instruction.groupTag_FieldConstantReferencing -> readFieldConstantReferencingInstruction( wide, opCode );
				case Instruction.groupTag_MethodConstantReferencing -> readMethodConstantReferencingInstruction( wide, opCode );
				case Instruction.groupTag_InvokeInterface -> readInvokeInterfaceInstruction( wide, opCode );
				case Instruction.groupTag_InvokeDynamic -> readInvokeDynamicInstruction( wide, opCode );
				case Instruction.groupTag_NewPrimitiveArray -> readNewPrimitiveArrayInstruction( wide, opCode );
				case Instruction.groupTag_MultiANewArray -> readMultiANewArrayInstruction( wide, opCode );
				default -> throw new AssertionError( instructionGroupTag );
			};
	}

	private MultiANewArrayInstruction readMultiANewArrayInstruction( boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.MULTIANEWARRAY;
		ClassConstant constant = readIndexAndGetConstant().asClassConstant();
		int dimensionCount = readUnsignedByte();
		return MultiANewArrayInstruction.of( constant, dimensionCount );
	}

	private NewPrimitiveArrayInstruction readNewPrimitiveArrayInstruction( boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.NEWARRAY;
		NewPrimitiveArrayInstruction.Type type = NewPrimitiveArrayInstruction.Type.fromNumber( readUnsignedByte() );
		return NewPrimitiveArrayInstruction.of( type );
	}

	private InvokeDynamicInstruction readInvokeDynamicInstruction( boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.INVOKEDYNAMIC;
		InvokeDynamicConstant invokeDynamicConstant = readIndexAndGetConstant().asInvokeDynamicConstant();
		int operand2 = readUnsignedShort(); //2 extra bytes, unused.
		assert operand2 == 0;
		return InvokeDynamicInstruction.of( invokeDynamicConstant );
	}

	private InvokeInterfaceInstruction readInvokeInterfaceInstruction( boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.INVOKEINTERFACE;
		InterfaceMethodReferenceConstant constant = readIndexAndGetConstant().asInterfaceMethodReferenceConstant();
		int argumentCount = readUnsignedByte();
		int extraByte = readUnsignedByte(); //one extra byte, unused.
		assert extraByte == 0;
		return InvokeInterfaceInstruction.of( constant, argumentCount );
	}

	private ClassConstantReferencingInstruction readClassConstantReferencingInstruction( boolean wide, int opCode )
	{
		assert !wide;
		ClassConstant classConstant = readIndexAndGetConstant().asClassConstant();
		return ClassConstantReferencingInstruction.of( opCode, classConstant );
	}

	private FieldConstantReferencingInstruction readFieldConstantReferencingInstruction( boolean wide, int opCode )
	{
		assert !wide;
		FieldReferenceConstant fieldReferenceConstant = readIndexAndGetConstant().asFieldReferenceConstant();
		return FieldConstantReferencingInstruction.of( opCode, fieldReferenceConstant );
	}

	private MethodConstantReferencingInstruction readMethodConstantReferencingInstruction( boolean wide, int opCode )
	{
		assert !wide;
		MethodReferenceConstant methodReferenceConstant = readIndexAndGetConstant().asMethodReferenceConstant();
		return MethodConstantReferencingInstruction.of( opCode, methodReferenceConstant );
	}

	private LookupSwitchInstruction readLookupSwitchInstruction( boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.LOOKUPSWITCH;
		skipToAlign();
		int defaultInstructionOffset = readInt();
		int count = readInt();
		assert count > 0;
		LookupSwitchInstruction lookupSwitchInstruction = LookupSwitchInstruction.of( count );
		setRelativeTargetInstruction( lookupSwitchInstruction, defaultInstructionOffset, lookupSwitchInstruction::setDefaultInstruction );
		for( int index = 0; index < count; index++ )
		{
			int value = readInt();
			int entryInstructionOffset = readInt();
			LookupSwitchEntry lookupSwitchEntry = LookupSwitchEntry.of( value );
			setRelativeTargetInstruction( lookupSwitchInstruction, entryInstructionOffset, lookupSwitchEntry::setTargetInstruction );
			lookupSwitchInstruction.entries.add( lookupSwitchEntry );
		}
		return lookupSwitchInstruction;
	}

	private TableSwitchInstruction readTableSwitchInstruction( boolean wide )
	{
		assert !wide;
		skipToAlign();
		int defaultInstructionOffset = readInt();
		int lowValue = readInt();
		int highValue = readInt();
		int entryCount = highValue - lowValue + 1;
		TableSwitchInstruction tableSwitchInstruction = TableSwitchInstruction.of( entryCount, lowValue );
		setRelativeTargetInstruction( tableSwitchInstruction, defaultInstructionOffset, tableSwitchInstruction::setDefaultInstruction );
		for( int index = 0; index < entryCount; index++ )
		{
			int targetInstructionOffset = readInt();
			int targetInstructionIndex = index;
			setRelativeTargetInstruction( tableSwitchInstruction, targetInstructionOffset, //
				targetInstruction -> tableSwitchInstruction.targetInstructions().set( targetInstructionIndex, targetInstruction ) );
		}
		return tableSwitchInstruction;
	}

	private BranchInstruction readBranchInstruction( boolean wide, int opCode )
	{
		assert !wide;
		BranchInstruction branchInstruction = BranchInstruction.of( opCode );
		int targetInstructionOffset = BranchInstruction.isLong( opCode ) ? readInt() : readSignedShort();
		setRelativeTargetInstruction( branchInstruction, targetInstructionOffset, branchInstruction::setTargetInstruction );
		return branchInstruction;
	}

	private ConditionalBranchInstruction readConditionalBranchInstruction( boolean wide, int opCode )
	{
		assert !wide;
		ConditionalBranchInstruction conditionalBranchInstruction = ConditionalBranchInstruction.of( opCode );
		int targetInstructionOffset = readSignedShort();
		setRelativeTargetInstruction( conditionalBranchInstruction, targetInstructionOffset, conditionalBranchInstruction::setTargetInstruction );
		return conditionalBranchInstruction;
	}

	private IIncInstruction readIIncInstruction( boolean wide )
	{
		int index;
		int delta;
		if( wide )
		{
			index = readUnsignedShort();
			delta = readSignedShort();
		}
		else
		{
			index = readUnsignedByte();
			delta = readSignedByte();
		}
		return IIncInstruction.of( wide, index, delta );
	}

	private LocalVariableInstruction readLocalVariableInstruction( boolean wide, int opCode )
	{
		assert LocalVariableInstruction.hasOperand( opCode ) || !wide;
		LocalVariableInstruction.IndexType indexType = LocalVariableInstruction.getIndexType( opCode );
		int index;
		if( indexType == LocalVariableInstruction.IndexType.ByOperand )
		{
			if( wide )
				index = readUnsignedShort();
			else
				index = readUnsignedByte();
		}
		else
		{
			assert !wide;
			index = indexType.index();
		}
		return LocalVariableInstruction.of( opCode, index );
	}

	private IndirectLoadConstantInstruction readIndirectLoadConstantInstruction( boolean wide, int opCode )
	{
		assert !wide;
		int constantIndexValue = IndirectLoadConstantInstruction.isWide( opCode ) ? readUnsignedShort() : readUnsignedByte();
		Constant constant = getConstant( constantIndexValue );
		return IndirectLoadConstantInstruction.of( opCode, constant );
	}

	private ImmediateLoadConstantInstruction readImmediateLoadConstantInstruction( boolean wide, int opCode )
	{
		assert !wide;
		int immediateValue = switch( opCode )
			{
				case OpCode.BIPUSH -> readUnsignedByte();
				case OpCode.SIPUSH -> readUnsignedShort();
				default -> throw new IllegalArgumentException();
			};
		return ImmediateLoadConstantInstruction.of( opCode, immediateValue );
	}

	private static OperandlessLoadConstantInstruction readOperandlessLoadConstantInstruction( boolean wide, int opCode )
	{
		assert !wide;
		return OperandlessLoadConstantInstruction.of( opCode );
	}

	private static OperandlessInstruction readOperandlessInstruction( boolean wide, int opCode )
	{
		assert !wide;
		return OperandlessInstruction.of( opCode );
	}
}
