package mikenakis.bytecode.reading;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.attributes.code.instructions.BranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ClassReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ConditionalBranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.FieldReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.IIncInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LocalVariableInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LookupSwitchEntry;
import mikenakis.bytecode.model.attributes.code.instructions.LookupSwitchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MethodReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MultiANewArrayInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.NewPrimitiveArrayInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.OperandlessInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.TableSwitchInstruction;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.DoubleConstant;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.constants.FloatConstant;
import mikenakis.bytecode.model.constants.IntegerConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.LongConstant;
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
				case Instruction.groupTag_LocalVariable -> readLocalVariableInstruction( wide, opCode );
				case Instruction.groupTag_IInc -> readIIncInstruction( wide );
				case Instruction.groupTag_ConditionalBranch -> readConditionalBranchInstruction( wide, opCode );
				case Instruction.groupTag_Branch -> readBranchInstruction( wide, opCode );
				case Instruction.groupTag_TableSwitch -> readTableSwitchInstruction( wide );
				case Instruction.groupTag_LookupSwitch -> readLookupSwitchInstruction( wide, opCode );
				case Instruction.groupTag_ClassConstantReferencing -> readClassReferencingInstruction( wide, opCode );
				case Instruction.groupTag_FieldConstantReferencing -> readFieldReferencingInstruction( wide, opCode );
				case Instruction.groupTag_MethodConstantReferencing -> readMethodReferencingInstruction( wide, opCode );
				case Instruction.groupTag_InvokeInterface -> readInvokeInterfaceInstruction( wide, opCode );
				case Instruction.groupTag_InvokeDynamic -> readInvokeDynamicInstruction( wide, opCode );
				case Instruction.groupTag_NewPrimitiveArray -> readNewPrimitiveArrayInstruction( wide, opCode );
				case Instruction.groupTag_MultiANewArray -> readMultiANewArrayInstruction( wide, opCode );
				case Instruction.groupTag_LoadConstant -> readLoadConstantInstruction( wide, opCode );
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
		MethodReferenceConstant methodReferenceConstant = readIndexAndGetConstant().asMethodReferenceConstant();
		int argumentCount = readUnsignedByte();
		int extraByte = readUnsignedByte(); //one extra byte, unused.
		assert extraByte == 0;
		return InvokeInterfaceInstruction.of( methodReferenceConstant, argumentCount );
	}

	private ClassReferencingInstruction readClassReferencingInstruction( boolean wide, int opCode )
	{
		assert !wide;
		ClassConstant classConstant = readIndexAndGetConstant().asClassConstant();
		return ClassReferencingInstruction.of( opCode, classConstant );
	}

	private FieldReferencingInstruction readFieldReferencingInstruction( boolean wide, int opCode )
	{
		assert !wide;
		FieldReferenceConstant fieldReferenceConstant = readIndexAndGetConstant().asFieldReferenceConstant();
		return FieldReferencingInstruction.of( opCode, fieldReferenceConstant );
	}

	private MethodReferencingInstruction readMethodReferencingInstruction( boolean wide, int opCode )
	{
		assert !wide;
		MethodReferenceConstant methodReferenceConstant = readIndexAndGetConstant().asMethodReferenceConstant();
		return MethodReferencingInstruction.of( opCode, methodReferenceConstant );
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
			setRelativeTargetInstruction( tableSwitchInstruction, targetInstructionOffset, //
				targetInstruction -> tableSwitchInstruction.targetInstructions.add( targetInstruction ) );
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

	private static final Constant iConstM1 = IntegerConstant.of( -1 );
	private static final Constant iConst0 = IntegerConstant.of( 0 );
	private static final Constant iConst1 = IntegerConstant.of( 1 );
	private static final Constant iConst2 = IntegerConstant.of( 2 );
	private static final Constant iConst3 = IntegerConstant.of( 3 );
	private static final Constant iConst4 = IntegerConstant.of( 4 );
	private static final Constant iConst5 = IntegerConstant.of( 5 );
	private static final Constant fConst0 = FloatConstant.of( 0.0f );
	private static final Constant fConst1 = FloatConstant.of( 1.0f );
	private static final Constant fConst2 = FloatConstant.of( 2.0f );
	private static final Constant lConst0 = LongConstant.of( 0L );
	private static final Constant lConst1 = LongConstant.of( 1L );
	private static final Constant dConst0 = DoubleConstant.of( 0.0 );
	private static final Constant dConst1 = DoubleConstant.of( 1.0 );

	private LoadConstantInstruction readLoadConstantInstruction( boolean wide, int opCode )
	{
		assert !wide;
		Constant constant = switch( opCode )
			{
				case OpCode.ICONST_M1 -> iConstM1;
				case OpCode.ICONST_0 -> iConst0;
				case OpCode.ICONST_1 -> iConst1;
				case OpCode.ICONST_2 -> iConst2;
				case OpCode.ICONST_3 -> iConst3;
				case OpCode.ICONST_4 -> iConst4;
				case OpCode.ICONST_5 -> iConst5;
				case OpCode.FCONST_0 -> fConst0;
				case OpCode.FCONST_1 -> fConst1;
				case OpCode.FCONST_2 -> fConst2;
				case OpCode.LCONST_0 -> lConst0;
				case OpCode.LCONST_1 -> lConst1;
				case OpCode.DCONST_0 -> dConst0;
				case OpCode.DCONST_1 -> dConst1;
				case OpCode.BIPUSH -> IntegerConstant.of( readUnsignedByte() );
				case OpCode.SIPUSH -> IntegerConstant.of( readUnsignedShort() );
				case OpCode.LDC -> readLdcConstant();
				case OpCode.LDC_W -> readLdcWConstant();
				case OpCode.LDC2_W -> readLdc2WConstant();
				default -> throw new AssertionError( opCode );
			};
		return LoadConstantInstruction.of( constant );
	}

	private Constant readLdcConstant()
	{
		int constantIndexValue = readUnsignedByte();
		Constant c = getConstant( constantIndexValue );
		assert c.tag == Constant.tag_Integer || c.tag == Constant.tag_Float || c.tag == Constant.tag_String || c.tag == Constant.tag_Class;
		return c;
	}

	private Constant readLdcWConstant()
	{
		int constantIndexValue = readUnsignedShort();
		Constant c = getConstant( constantIndexValue );
		assert c.tag == Constant.tag_Integer || c.tag == Constant.tag_Float || c.tag == Constant.tag_String || c.tag == Constant.tag_Class;
		return c;
	}

	private Constant readLdc2WConstant()
	{
		int constantIndexValue = readUnsignedShort();
		Constant c = getConstant( constantIndexValue );
		assert c.tag == Constant.tag_Long || c.tag == Constant.tag_Double;
		return c;
	}

	private static OperandlessInstruction readOperandlessInstruction( boolean wide, int opCode )
	{
		assert !wide;
		return OperandlessInstruction.of( opCode );
	}
}
