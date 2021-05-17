package mikenakis.bytecode.attributes.code;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.attributes.code.instructions.BranchInstruction;
import mikenakis.bytecode.attributes.code.instructions.ConditionalBranchInstruction;
import mikenakis.bytecode.attributes.code.instructions.ConstantReferencingInstruction;
import mikenakis.bytecode.attributes.code.instructions.IIncInstruction;
import mikenakis.bytecode.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.attributes.code.instructions.LoadConstantInstruction;
import mikenakis.bytecode.attributes.code.instructions.LocalVariableInstruction;
import mikenakis.bytecode.attributes.code.instructions.LookupSwitchInstruction;
import mikenakis.bytecode.attributes.code.instructions.MultiANewArrayInstruction;
import mikenakis.bytecode.attributes.code.instructions.NewPrimitiveArrayInstruction;
import mikenakis.bytecode.attributes.code.instructions.OperandlessInstruction;
import mikenakis.bytecode.attributes.code.instructions.TableSwitchInstruction;
import mikenakis.bytecode.constants.ClassConstant;
import mikenakis.bytecode.constants.FieldReferenceConstant;
import mikenakis.bytecode.constants.InvokeDynamicConstant;
import mikenakis.bytecode.constants.MethodReferenceConstant;

/**
 * ByteCode Instructions.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class Instructions
{
	private Instructions()
	{
	}

	//@formatter:off
	public static LocalVariableInstruction       newILoad           ( int localVariableIndex )                 { return newLocalVariableInstruction       ( OpCode.ILOAD, localVariableIndex ); }
	public static LocalVariableInstruction       newLLoad           ( int localVariableIndex )                 { return newLocalVariableInstruction       ( OpCode.LLOAD, localVariableIndex ); }
	public static LocalVariableInstruction       newFLoad           ( int localVariableIndex )                 { return newLocalVariableInstruction       ( OpCode.FLOAD, localVariableIndex ); }
	public static LocalVariableInstruction       newDLoad           ( int localVariableIndex )                 { return newLocalVariableInstruction       ( OpCode.DLOAD, localVariableIndex ); }
	public static LocalVariableInstruction       newALoad           ( int localVariableIndex )                 { return newLocalVariableInstruction       ( OpCode.ALOAD, localVariableIndex ); }
	public static LocalVariableInstruction       newIStore          ( int localVariableIndex )                 { return newLocalVariableInstruction       ( OpCode.ISTORE, localVariableIndex ); }
	public static LocalVariableInstruction       newLStore          ( int localVariableIndex )                 { return newLocalVariableInstruction       ( OpCode.LSTORE, localVariableIndex ); }
	public static LocalVariableInstruction       newFStore          ( int localVariableIndex )                 { return newLocalVariableInstruction       ( OpCode.FSTORE, localVariableIndex ); }
	public static LocalVariableInstruction       newDStore          ( int localVariableIndex )                 { return newLocalVariableInstruction       ( OpCode.DSTORE, localVariableIndex ); }
	public static LocalVariableInstruction       newAStore          ( int localVariableIndex )                 { return newLocalVariableInstruction       ( OpCode.ASTORE, localVariableIndex ); }
	public static LoadConstantInstruction        newLdc             ( Constant constant )                      { return newLoadConstantInstruction        ( constant ); }
	public static ConditionalBranchInstruction   newIfEQ            ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IFEQ, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfNE            ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IFNE, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfLT            ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IFLT, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfGE            ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IFGE, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfGT            ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IFGT, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfLE            ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IFLE, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfICmpEQ        ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IF_ICMPEQ, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfICmpNE        ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IF_ICMPNE, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfICmpLT        ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IF_ICMPLT, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfICmpGE        ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IF_ICMPGE, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfICmpGT        ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IF_ICMPGT, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfICmpLE        ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IF_ICMPLE, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfNull          ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IFNULL, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfNonNull       ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IFNONNULL, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfACmpEQ        ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IF_ACMPEQ, targetInstruction ); }
	public static ConditionalBranchInstruction   newIfACmpNE        ( Instruction targetInstruction )          { return newConditionalBranchInstruction   ( OpCode.IF_ACMPNE, targetInstruction ); }
	public static BranchInstruction              newGoto            ( Instruction targetInstruction )          { return InstructionModels.GOTO.newInstruction( targetInstruction ); }
	public static BranchInstruction              newJsr             ( Instruction targetInstruction )          { return InstructionModels.JSR.newInstruction( targetInstruction ); }
	public static IIncInstruction                newIInc            ( int localVariableIndex, int delta )      { return InstructionModels.IINC.newInstruction( localVariableIndex, delta ); }
	public static InvokeInterfaceInstruction     newInvokeInterface ( Constant constant, int argumentCount )   { return InstructionModels.INVOKEINTERFACE.newInstruction( constant, argumentCount ); }
	public static InvokeDynamicInstruction       newInvokeDynamic   ( InvokeDynamicConstant constant )         { return InstructionModels.INVOKEDYNAMIC.newInstruction( constant ); }
	public static MultiANewArrayInstruction      newMultiANewArray  ( Constant constant, int dimensionCount )  { return InstructionModels.MULTIANEWARRAY.newInstruction( constant, dimensionCount ); }
	public static TableSwitchInstruction         newTableSwitch     ()                                         { return InstructionModels.TABLESWITCH.newInstruction(); }
	public static LookupSwitchInstruction        newLookupSwitch    ()                                         { return InstructionModels.LOOKUPSWITCH.newInstruction(); }
	public static NewPrimitiveArrayInstruction   newNewArray        ( NewPrimitiveArrayInstruction.Type type ) { return InstructionModels.NEWARRAY.newInstruction( type ); }
	public static LocalVariableInstruction       newRet             ( byte localVariableIndex )                { return newLocalVariableInstruction       ( OpCode.RET, localVariableIndex ); }
	public static ConstantReferencingInstruction newGetStatic       ( FieldReferenceConstant constant )        { return newConstantReferencingInstruction ( OpCode.GETSTATIC, constant ); }
	public static ConstantReferencingInstruction newPutStatic       ( FieldReferenceConstant constant )        { return newConstantReferencingInstruction ( OpCode.PUTSTATIC, constant ); }
	public static ConstantReferencingInstruction newGetField        ( FieldReferenceConstant constant )        { return newConstantReferencingInstruction ( OpCode.GETFIELD, constant ); }
	public static ConstantReferencingInstruction newPutField        ( FieldReferenceConstant constant )        { return newConstantReferencingInstruction ( OpCode.PUTFIELD, constant ); }
	public static ConstantReferencingInstruction newInvokeVirtual   ( MethodReferenceConstant constant )       { return newConstantReferencingInstruction ( OpCode.INVOKEVIRTUAL, constant ); }
	public static ConstantReferencingInstruction newInvokeSpecial   ( MethodReferenceConstant constant )       { return newConstantReferencingInstruction ( OpCode.INVOKESPECIAL, constant ); }
	public static ConstantReferencingInstruction newInvokeStatic    ( MethodReferenceConstant constant )       { return newConstantReferencingInstruction ( OpCode.INVOKESTATIC, constant ); }
	public static ConstantReferencingInstruction newNew             ( ClassConstant constant )                 { return newConstantReferencingInstruction ( OpCode.NEW, constant ); }
	public static ConstantReferencingInstruction newANewArray       ( ClassConstant constant )                 { return newConstantReferencingInstruction ( OpCode.ANEWARRAY, constant ); }
	public static ConstantReferencingInstruction newCheckCast       ( ClassConstant constant )                 { return newConstantReferencingInstruction ( OpCode.CHECKCAST, constant ); }
	public static ConstantReferencingInstruction newInstanceOf      ( ClassConstant constant )                 { return newConstantReferencingInstruction ( OpCode.INSTANCEOF, constant ); }
	public static OperandlessInstruction         newNop             ()                                         { return newOperandless( OpCode.NOP ); }
	public static OperandlessInstruction         newAConstNull      ()                                         { return newOperandless( OpCode.ACONST_NULL ); }
	public static OperandlessInstruction         newIALoad          ()                                         { return newOperandless( OpCode.IALOAD ); }
	public static OperandlessInstruction         newLALoad          ()                                         { return newOperandless( OpCode.LALOAD ); }
	public static OperandlessInstruction         newFALoad          ()                                         { return newOperandless( OpCode.FALOAD ); }
	public static OperandlessInstruction         newDALoad          ()                                         { return newOperandless( OpCode.DALOAD ); }
	public static OperandlessInstruction         newAALoad          ()                                         { return newOperandless( OpCode.AALOAD ); }
	public static OperandlessInstruction         newBALoad          ()                                         { return newOperandless( OpCode.BALOAD ); }
	public static OperandlessInstruction         newCALoad          ()                                         { return newOperandless( OpCode.CALOAD ); }
	public static OperandlessInstruction         newSALoad          ()                                         { return newOperandless( OpCode.SALOAD ); }
	public static OperandlessInstruction         newIAStore         ()                                         { return newOperandless( OpCode.IASTORE ); }
	public static OperandlessInstruction         newLAStore         ()                                         { return newOperandless( OpCode.LASTORE ); }
	public static OperandlessInstruction         newFAStore         ()                                         { return newOperandless( OpCode.FASTORE ); }
	public static OperandlessInstruction         newDAStore         ()                                         { return newOperandless( OpCode.DASTORE ); }
	public static OperandlessInstruction         newAAStore         ()                                         { return newOperandless( OpCode.AASTORE ); }
	public static OperandlessInstruction         newBAStore         ()                                         { return newOperandless( OpCode.BASTORE ); }
	public static OperandlessInstruction         newCAStore         ()                                         { return newOperandless( OpCode.CASTORE ); }
	public static OperandlessInstruction         newSAStore         ()                                         { return newOperandless( OpCode.SASTORE ); }
	public static OperandlessInstruction         newPop             ()                                         { return newOperandless( OpCode.POP ); }
	public static OperandlessInstruction         newPop2            ()                                         { return newOperandless( OpCode.POP2 ); }
	public static OperandlessInstruction         newDup             ()                                         { return newOperandless( OpCode.DUP ); }
	public static OperandlessInstruction         newDupX1           ()                                         { return newOperandless( OpCode.DUP_X1 ); }
	public static OperandlessInstruction         newDupX2           ()                                         { return newOperandless( OpCode.DUP_X2 ); }
	public static OperandlessInstruction         newDup2            ()                                         { return newOperandless( OpCode.DUP2 ); }
	public static OperandlessInstruction         newDup2X1          ()                                         { return newOperandless( OpCode.DUP2_X1 ); }
	public static OperandlessInstruction         newDup2X2          ()                                         { return newOperandless( OpCode.DUP2_X2 ); }
	public static OperandlessInstruction         newSwap            ()                                         { return newOperandless( OpCode.SWAP ); }
	public static OperandlessInstruction         newIAdd            ()                                         { return newOperandless( OpCode.IADD ); }
	public static OperandlessInstruction         newLAdd            ()                                         { return newOperandless( OpCode.LADD ); }
	public static OperandlessInstruction         newFAdd            ()                                         { return newOperandless( OpCode.FADD ); }
	public static OperandlessInstruction         newDAdd            ()                                         { return newOperandless( OpCode.DADD ); }
	public static OperandlessInstruction         newISub            ()                                         { return newOperandless( OpCode.ISUB ); }
	public static OperandlessInstruction         newLSub            ()                                         { return newOperandless( OpCode.LSUB ); }
	public static OperandlessInstruction         newFSub            ()                                         { return newOperandless( OpCode.FSUB ); }
	public static OperandlessInstruction         newDSub            ()                                         { return newOperandless( OpCode.DSUB ); }
	public static OperandlessInstruction         newIMul            ()                                         { return newOperandless( OpCode.IMUL ); }
	public static OperandlessInstruction         newLMul            ()                                         { return newOperandless( OpCode.LMUL ); }
	public static OperandlessInstruction         newFMul            ()                                         { return newOperandless( OpCode.FMUL ); }
	public static OperandlessInstruction         newDMul            ()                                         { return newOperandless( OpCode.DMUL ); }
	public static OperandlessInstruction         newIDiv            ()                                         { return newOperandless( OpCode.IDIV ); }
	public static OperandlessInstruction         newLDiv            ()                                         { return newOperandless( OpCode.LDIV ); }
	public static OperandlessInstruction         newFDiv            ()                                         { return newOperandless( OpCode.FDIV ); }
	public static OperandlessInstruction         newDDiv            ()                                         { return newOperandless( OpCode.DDIV ); }
	public static OperandlessInstruction         newIRem            ()                                         { return newOperandless( OpCode.IREM ); }
	public static OperandlessInstruction         newLRem            ()                                         { return newOperandless( OpCode.LREM ); }
	public static OperandlessInstruction         newFRem            ()                                         { return newOperandless( OpCode.FREM ); }
	public static OperandlessInstruction         newDRem            ()                                         { return newOperandless( OpCode.DREM ); }
	public static OperandlessInstruction         newINeg            ()                                         { return newOperandless( OpCode.INEG ); }
	public static OperandlessInstruction         newLNeg            ()                                         { return newOperandless( OpCode.LNEG ); }
	public static OperandlessInstruction         newFNeg            ()                                         { return newOperandless( OpCode.FNEG ); }
	public static OperandlessInstruction         newDNeg            ()                                         { return newOperandless( OpCode.DNEG ); }
	public static OperandlessInstruction         newIShl            ()                                         { return newOperandless( OpCode.ISHL ); }
	public static OperandlessInstruction         newLShl            ()                                         { return newOperandless( OpCode.LSHL ); }
	public static OperandlessInstruction         newIShr            ()                                         { return newOperandless( OpCode.ISHR ); }
	public static OperandlessInstruction         newLShr            ()                                         { return newOperandless( OpCode.LSHR ); }
	public static OperandlessInstruction         newIUShr           ()                                         { return newOperandless( OpCode.IUSHR ); }
	public static OperandlessInstruction         newLUShr           ()                                         { return newOperandless( OpCode.LUSHR ); }
	public static OperandlessInstruction         newIAnd            ()                                         { return newOperandless( OpCode.IAND ); }
	public static OperandlessInstruction         newLAnd            ()                                         { return newOperandless( OpCode.LAND ); }
	public static OperandlessInstruction         newIOr             ()                                         { return newOperandless( OpCode.IOR ); }
	public static OperandlessInstruction         newLOr             ()                                         { return newOperandless( OpCode.LOR ); }
	public static OperandlessInstruction         newIXor            ()                                         { return newOperandless( OpCode.IXOR ); }
	public static OperandlessInstruction         newLXor            ()                                         { return newOperandless( OpCode.LXOR ); }
	public static OperandlessInstruction         newI2L             ()                                         { return newOperandless( OpCode.I2L ); }
	public static OperandlessInstruction         newI2F             ()                                         { return newOperandless( OpCode.I2F ); }
	public static OperandlessInstruction         newI2D             ()                                         { return newOperandless( OpCode.I2D ); }
	public static OperandlessInstruction         newL2I             ()                                         { return newOperandless( OpCode.L2I ); }
	public static OperandlessInstruction         newL2F             ()                                         { return newOperandless( OpCode.L2F ); }
	public static OperandlessInstruction         newL2D             ()                                         { return newOperandless( OpCode.L2D ); }
	public static OperandlessInstruction         newF2I             ()                                         { return newOperandless( OpCode.F2I ); }
	public static OperandlessInstruction         newF2L             ()                                         { return newOperandless( OpCode.F2L ); }
	public static OperandlessInstruction         newF2D             ()                                         { return newOperandless( OpCode.F2D ); }
	public static OperandlessInstruction         newD2I             ()                                         { return newOperandless( OpCode.D2I ); }
	public static OperandlessInstruction         newD2L             ()                                         { return newOperandless( OpCode.D2L ); }
	public static OperandlessInstruction         newD2F             ()                                         { return newOperandless( OpCode.D2F ); }
	public static OperandlessInstruction         newI2B             ()                                         { return newOperandless( OpCode.I2B ); }
	public static OperandlessInstruction         newI2C             ()                                         { return newOperandless( OpCode.I2C ); }
	public static OperandlessInstruction         newI2S             ()                                         { return newOperandless( OpCode.I2S ); }
	public static OperandlessInstruction         newLCmp            ()                                         { return newOperandless( OpCode.LCMP ); }
	public static OperandlessInstruction         newFCmpL           ()                                         { return newOperandless( OpCode.FCMPL ); }
	public static OperandlessInstruction         newFCmpG           ()                                         { return newOperandless( OpCode.FCMPG ); }
	public static OperandlessInstruction         newDCmpL           ()                                         { return newOperandless( OpCode.DCMPL ); }
	public static OperandlessInstruction         newDCmpG           ()                                         { return newOperandless( OpCode.DCMPG ); }
	public static OperandlessInstruction         newIReturn         ()                                         { return newOperandless( OpCode.IRETURN ); }
	public static OperandlessInstruction         newLReturn         ()                                         { return newOperandless( OpCode.LRETURN ); }
	public static OperandlessInstruction         newFReturn         ()                                         { return newOperandless( OpCode.FRETURN ); }
	public static OperandlessInstruction         newDReturn         ()                                         { return newOperandless( OpCode.DRETURN ); }
	public static OperandlessInstruction         newAReturn         ()                                         { return newOperandless( OpCode.ARETURN ); }
	public static OperandlessInstruction         newReturn          ()                                         { return newOperandless( OpCode.RETURN ); }
	public static OperandlessInstruction         newArrayLength     ()                                         { return newOperandless( OpCode.ARRAYLENGTH ); }
	public static OperandlessInstruction         newAThrow          ()                                         { return newOperandless( OpCode.ATHROW ); }
	public static OperandlessInstruction         newMonitorEnter    ()                                         { return newOperandless( OpCode.MONITORENTER ); }
	public static OperandlessInstruction         newMonitorExit     ()                                         { return newOperandless( OpCode.MONITOREXIT ); }
	//@formatter:on

	private static OperandlessInstruction newOperandless( int opCode )
	{
		OperandlessInstruction.Model instructionModel = (OperandlessInstruction.Model)InstructionModels.getInstructionModel( opCode );
		return instructionModel.newInstruction();
	}

	private static ConditionalBranchInstruction newConditionalBranchInstruction( int opCode, Instruction targetInstruction )
	{
		ConditionalBranchInstruction.Model instructionModel = (ConditionalBranchInstruction.Model)InstructionModels.getInstructionModel( opCode );
		return instructionModel.newInstruction( targetInstruction );
	}

	private static LocalVariableInstruction newLocalVariableInstruction( int opCode, int localVariableIndex )
	{
		LocalVariableInstruction.Model instructionModel = (LocalVariableInstruction.Model)InstructionModels.getInstructionModel( opCode );
		return instructionModel.newInstruction( localVariableIndex );
	}

	private static LoadConstantInstruction newLoadConstantInstruction( Constant constant )
	{
		LoadConstantInstruction.Model model = LoadConstantInstruction.Model.forConstant( constant );
		return model.newInstruction( constant );
	}

	private static ConstantReferencingInstruction newConstantReferencingInstruction( int opCode, Constant constant )
	{
		ConstantReferencingInstruction.Model instructionModel = (ConstantReferencingInstruction.Model)InstructionModels.getInstructionModel( opCode );
		return instructionModel.newInstruction( constant );
	}
}
