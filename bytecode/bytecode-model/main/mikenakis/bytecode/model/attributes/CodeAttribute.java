package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.InstructionList;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.attributes.code.instructions.BranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ConditionalBranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.IIncInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LocalVariableInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LookupSwitchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MultiANewArrayInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.NewPrimitiveArrayInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.OperandlessInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.TableSwitchInstruction;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.constants.InterfaceMethodReferenceConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "Code" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class CodeAttribute extends KnownAttribute
{
	public static final String END_LABEL = "@end";

	public static CodeAttribute of( int maxStack, int maxLocals )
	{
		return of( maxStack, maxLocals, new ArrayList<>(), new ArrayList<>(), AttributeSet.of() );
	}

	public static CodeAttribute of( int maxStack, int maxLocals, List<Instruction> instructions, List<ExceptionInfo> exceptionInfos, AttributeSet attributeSet )
	{
		return new CodeAttribute( maxStack, maxLocals, instructions, exceptionInfos, attributeSet );
	}

	private int maxStack;
	private int maxLocals;
	private final List<ExceptionInfo> exceptionInfos;
	public final AttributeSet attributeSet;
	private final InstructionList instructions;

	private CodeAttribute( int maxStack, int maxLocals, List<Instruction> instructions, List<ExceptionInfo> exceptionInfos, AttributeSet attributeSet )
	{
		super( tagCode );
		this.maxStack = maxStack;
		this.maxLocals = maxLocals;
		this.instructions = InstructionList.of( instructions );
		this.exceptionInfos = exceptionInfos;
		this.attributeSet = attributeSet;
	}

	public int getMaxStack()
	{
		return maxStack;
	}

	public void setMaxStack( int value )
	{
		maxStack = value;
	}

	public int getMaxLocals()
	{
		return maxLocals;
	}

	public void setMaxLocals( int value )
	{
		maxLocals = value;
	}

	public List<ExceptionInfo> exceptionInfos()
	{
		return exceptionInfos;
	}

	public InstructionList instructions()
	{
		return instructions;
	}

	@Deprecated @Override public CodeAttribute asCodeAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "maxStack = " + maxStack + ", maxLocals = " + maxLocals + ", " + instructions.size() + " instructions, " + //
			exceptionInfos.size() + " exceptionInfos, " + attributeSet.size() + " attributes";
	}

	//@formatter:off
	public LocalVariableInstruction       addILoad           ( int localVariableIndex )                                       { return add( LocalVariableInstruction.of( OpCode.ILOAD, localVariableIndex ) ); }
	public LocalVariableInstruction       addLLoad           ( int localVariableIndex )                                       { return add( LocalVariableInstruction.of( OpCode.LLOAD, localVariableIndex ) ); }
	public LocalVariableInstruction       addFLoad           ( int localVariableIndex )                                       { return add( LocalVariableInstruction.of( OpCode.FLOAD, localVariableIndex ) ); }
	public LocalVariableInstruction       addDLoad           ( int localVariableIndex )                                       { return add( LocalVariableInstruction.of( OpCode.DLOAD, localVariableIndex ) ); }
	public LocalVariableInstruction       addALoad           ( int localVariableIndex )                                       { return add( LocalVariableInstruction.of( OpCode.ALOAD, localVariableIndex ) ); }
	public LocalVariableInstruction       addIStore          ( int localVariableIndex )                                       { return add( LocalVariableInstruction.of( OpCode.ISTORE, localVariableIndex ) ); }
	public LocalVariableInstruction       addLStore          ( int localVariableIndex )                                       { return add( LocalVariableInstruction.of( OpCode.LSTORE, localVariableIndex ) ); }
	public LocalVariableInstruction       addFStore          ( int localVariableIndex )                                       { return add( LocalVariableInstruction.of( OpCode.FSTORE, localVariableIndex ) ); }
	public LocalVariableInstruction       addDStore          ( int localVariableIndex )                                       { return add( LocalVariableInstruction.of( OpCode.DSTORE, localVariableIndex ) ); }
	public LocalVariableInstruction       addAStore          ( int localVariableIndex )                                       { return add( LocalVariableInstruction.of( OpCode.ASTORE, localVariableIndex ) ); }
	public LoadConstantInstruction        addLdc             ( boolean constant )                                             { return add( LoadConstantInstruction.of( constant ) ); }
	public LoadConstantInstruction        addLdc             ( int constant )                                                 { return add( LoadConstantInstruction.of( constant ) );	}
	public LoadConstantInstruction        addLdc             ( long constant )                                                { return add( LoadConstantInstruction.of( constant ) );	}
	public LoadConstantInstruction        addLdc             ( float constant )                                               { return add( LoadConstantInstruction.of( constant ) );	}
	public LoadConstantInstruction        addLdc             ( double constant )                                              { return add( LoadConstantInstruction.of( constant ) );	}
	public LoadConstantInstruction        addLdc             ( String constant )                                              { return add( LoadConstantInstruction.of( constant ) );	}
	public ConditionalBranchInstruction   addIfEQ            ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IFEQ, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfEQ            ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IFEQ ) ); }
	public ConditionalBranchInstruction   addIfNE            ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IFNE, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfNE            ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IFNE ) ); }
	public ConditionalBranchInstruction   addIfLT            ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IFLT, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfLT            ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IFLT ) ); }
	public ConditionalBranchInstruction   addIfGE            ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IFGE, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfGE            ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IFGE ) ); }
	public ConditionalBranchInstruction   addIfGT            ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IFGT, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfGT            ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IFGT ) ); }
	public ConditionalBranchInstruction   addIfLE            ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IFLE, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfLE            ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IFLE ) ); }
	public ConditionalBranchInstruction   addIfICmpEQ        ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPEQ, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfICmpEQ        ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPEQ ) ); }
	public ConditionalBranchInstruction   addIfICmpNE        ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPNE, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfICmpNE        ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPNE ) ); }
	public ConditionalBranchInstruction   addIfICmpLT        ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPLT, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfICmpLT        ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPLT ) ); }
	public ConditionalBranchInstruction   addIfICmpGE        ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPGE, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfICmpGE        ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPGE ) ); }
	public ConditionalBranchInstruction   addIfICmpGT        ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPGT, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfICmpGT        ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPGT ) ); }
	public ConditionalBranchInstruction   addIfICmpLE        ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPLE, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfICmpLE        ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPLE ) ); }
	public ConditionalBranchInstruction   addIfNull          ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IFNULL, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfNull          ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IFNULL ) ); }
	public ConditionalBranchInstruction   addIfNonNull       ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IFNONNULL, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfNonNull       ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IFNONNULL ) ); }
	public ConditionalBranchInstruction   addIfACmpEQ        ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IF_ACMPEQ, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfACmpEQ        ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IF_ACMPEQ ) ); }
	public ConditionalBranchInstruction   addIfACmpNE        ( Instruction targetInstruction )                                { return add( ConditionalBranchInstruction.of( OpCode.IF_ACMPNE, targetInstruction ) ); }
	public ConditionalBranchInstruction   addIfACmpNE        ()                                                               { return add( ConditionalBranchInstruction.of( OpCode.IF_ACMPNE ) ); }
	public BranchInstruction              addGoto            ( Instruction targetInstruction )                                { return add( BranchInstruction.of( OpCode.GOTO, targetInstruction ) ); }
	public BranchInstruction              addGoto            ()                                                               { return add( BranchInstruction.of( OpCode.GOTO ) ); }
	public BranchInstruction              addJsr             ( Instruction targetInstruction )                                { return add( BranchInstruction.of( OpCode.JSR, targetInstruction ) ); }
	public BranchInstruction              addJsr             ()                                                               { return add( BranchInstruction.of( OpCode.JSR ) ); }
	public IIncInstruction                addIInc            ( int localVariableIndex, int delta )                            { return add( IIncInstruction.of( localVariableIndex, delta ) ); }
	public InvokeInterfaceInstruction     addInvokeInterface ( InterfaceMethodReferenceConstant constant, int argumentCount ) { return add( InvokeInterfaceInstruction.of( constant, argumentCount ) ); }
	public InvokeDynamicInstruction       addInvokeDynamic   ( InvokeDynamicConstant constant )                               { return add( InvokeDynamicInstruction.of( constant ) ); }
	public MultiANewArrayInstruction      addMultiANewArray  ( ClassConstant constant, int dimensionCount )                   { return add( MultiANewArrayInstruction.of( constant, dimensionCount ) ); }
	public TableSwitchInstruction         addTableSwitch     ( int lowValue )                                                 { return add( TableSwitchInstruction.of( 0, lowValue ) ); }
	public LookupSwitchInstruction        addLookupSwitch    ()                                                               { return add( LookupSwitchInstruction.of() ); }
	public NewPrimitiveArrayInstruction   addNewArray        ( NewPrimitiveArrayInstruction.Type type )                       { return add( NewPrimitiveArrayInstruction.of( type ) ); }
	public LocalVariableInstruction       addRet             ( int localVariableIndex )                                      { return add( LocalVariableInstruction.of( OpCode.RET, localVariableIndex ) ); }
	public ConstantReferencingInstruction addGetStatic       ( FieldReferenceConstant constant )                              { return add( ConstantReferencingInstruction.of ( OpCode.GETSTATIC, constant ) ); }
	public ConstantReferencingInstruction addPutStatic       ( FieldReferenceConstant constant )                              { return add( ConstantReferencingInstruction.of ( OpCode.PUTSTATIC, constant ) ); }
	public ConstantReferencingInstruction addGetField        ( FieldReferenceConstant constant )                              { return add( ConstantReferencingInstruction.of ( OpCode.GETFIELD, constant ) ); }
	public ConstantReferencingInstruction addPutField        ( FieldReferenceConstant constant )                              { return add( ConstantReferencingInstruction.of ( OpCode.PUTFIELD, constant ) ); }
	public ConstantReferencingInstruction addInvokeVirtual   ( MethodReferenceConstant constant )                             { return add( ConstantReferencingInstruction.of ( OpCode.INVOKEVIRTUAL, constant ) ); }
	public ConstantReferencingInstruction addInvokeSpecial   ( MethodReferenceConstant constant )                             { return add( ConstantReferencingInstruction.of ( OpCode.INVOKESPECIAL, constant ) ); }
	public ConstantReferencingInstruction addInvokeStatic    ( MethodReferenceConstant constant )                             { return add( ConstantReferencingInstruction.of ( OpCode.INVOKESTATIC, constant ) ); }
	public ConstantReferencingInstruction addNew             ( ClassConstant constant )                                       { return add( ConstantReferencingInstruction.of ( OpCode.NEW, constant ) ); }
	public ConstantReferencingInstruction addANewArray       ( ClassConstant constant )                                       { return add( ConstantReferencingInstruction.of ( OpCode.ANEWARRAY, constant ) ); }
	public ConstantReferencingInstruction addCheckCast       ( ClassConstant constant )                                       { return add( ConstantReferencingInstruction.of ( OpCode.CHECKCAST, constant ) ); }
	public ConstantReferencingInstruction addInstanceOf      ( ClassConstant constant )                                       { return add( ConstantReferencingInstruction.of ( OpCode.INSTANCEOF, constant ) ); }
	public OperandlessInstruction         addNop             ()                                                               { return add( OperandlessInstruction.of( OpCode.NOP ) ); }
	public OperandlessInstruction         addAConstNull      ()                                                               { return add( OperandlessInstruction.of( OpCode.ACONST_NULL ) ); }
	public OperandlessInstruction         addIALoad          ()                                                               { return add( OperandlessInstruction.of( OpCode.IALOAD ) ); }
	public OperandlessInstruction         addLALoad          ()                                                               { return add( OperandlessInstruction.of( OpCode.LALOAD ) ); }
	public OperandlessInstruction         addFALoad          ()                                                               { return add( OperandlessInstruction.of( OpCode.FALOAD ) ); }
	public OperandlessInstruction         addDALoad          ()                                                               { return add( OperandlessInstruction.of( OpCode.DALOAD ) ); }
	public OperandlessInstruction         addAALoad          ()                                                               { return add( OperandlessInstruction.of( OpCode.AALOAD ) ); }
	public OperandlessInstruction         addBALoad          ()                                                               { return add( OperandlessInstruction.of( OpCode.BALOAD ) ); }
	public OperandlessInstruction         addCALoad          ()                                                               { return add( OperandlessInstruction.of( OpCode.CALOAD ) ); }
	public OperandlessInstruction         addSALoad          ()                                                               { return add( OperandlessInstruction.of( OpCode.SALOAD ) ); }
	public OperandlessInstruction         addIAStore         ()                                                               { return add( OperandlessInstruction.of( OpCode.IASTORE ) ); }
	public OperandlessInstruction         addLAStore         ()                                                               { return add( OperandlessInstruction.of( OpCode.LASTORE ) ); }
	public OperandlessInstruction         addFAStore         ()                                                               { return add( OperandlessInstruction.of( OpCode.FASTORE ) ); }
	public OperandlessInstruction         addDAStore         ()                                                               { return add( OperandlessInstruction.of( OpCode.DASTORE ) ); }
	public OperandlessInstruction         addAAStore         ()                                                               { return add( OperandlessInstruction.of( OpCode.AASTORE ) ); }
	public OperandlessInstruction         addBAStore         ()                                                               { return add( OperandlessInstruction.of( OpCode.BASTORE ) ); }
	public OperandlessInstruction         addCAStore         ()                                                               { return add( OperandlessInstruction.of( OpCode.CASTORE ) ); }
	public OperandlessInstruction         addSAStore         ()                                                               { return add( OperandlessInstruction.of( OpCode.SASTORE ) ); }
	public OperandlessInstruction         addPop             ()                                                               { return add( OperandlessInstruction.of( OpCode.POP ) ); }
	public OperandlessInstruction         addPop2            ()                                                               { return add( OperandlessInstruction.of( OpCode.POP2 ) ); }
	public OperandlessInstruction         addDup             ()                                                               { return add( OperandlessInstruction.of( OpCode.DUP ) ); }
	public OperandlessInstruction         addDupX1           ()                                                               { return add( OperandlessInstruction.of( OpCode.DUP_X1 ) ); }
	public OperandlessInstruction         addDupX2           ()                                                               { return add( OperandlessInstruction.of( OpCode.DUP_X2 ) ); }
	public OperandlessInstruction         addDup2            ()                                                               { return add( OperandlessInstruction.of( OpCode.DUP2 ) ); }
	public OperandlessInstruction         addDup2X1          ()                                                               { return add( OperandlessInstruction.of( OpCode.DUP2_X1 ) ); }
	public OperandlessInstruction         addDup2X2          ()                                                               { return add( OperandlessInstruction.of( OpCode.DUP2_X2 ) ); }
	public OperandlessInstruction         addSwap            ()                                                               { return add( OperandlessInstruction.of( OpCode.SWAP ) ); }
	public OperandlessInstruction         addIAdd            ()                                                               { return add( OperandlessInstruction.of( OpCode.IADD ) ); }
	public OperandlessInstruction         addLAdd            ()                                                               { return add( OperandlessInstruction.of( OpCode.LADD ) ); }
	public OperandlessInstruction         addFAdd            ()                                                               { return add( OperandlessInstruction.of( OpCode.FADD ) ); }
	public OperandlessInstruction         addDAdd            ()                                                               { return add( OperandlessInstruction.of( OpCode.DADD ) ); }
	public OperandlessInstruction         addISub            ()                                                               { return add( OperandlessInstruction.of( OpCode.ISUB ) ); }
	public OperandlessInstruction         addLSub            ()                                                               { return add( OperandlessInstruction.of( OpCode.LSUB ) ); }
	public OperandlessInstruction         addFSub            ()                                                               { return add( OperandlessInstruction.of( OpCode.FSUB ) ); }
	public OperandlessInstruction         addDSub            ()                                                               { return add( OperandlessInstruction.of( OpCode.DSUB ) ); }
	public OperandlessInstruction         addIMul            ()                                                               { return add( OperandlessInstruction.of( OpCode.IMUL ) ); }
	public OperandlessInstruction         addLMul            ()                                                               { return add( OperandlessInstruction.of( OpCode.LMUL ) ); }
	public OperandlessInstruction         addFMul            ()                                                               { return add( OperandlessInstruction.of( OpCode.FMUL ) ); }
	public OperandlessInstruction         addDMul            ()                                                               { return add( OperandlessInstruction.of( OpCode.DMUL ) ); }
	public OperandlessInstruction         addIDiv            ()                                                               { return add( OperandlessInstruction.of( OpCode.IDIV ) ); }
	public OperandlessInstruction         addLDiv            ()                                                               { return add( OperandlessInstruction.of( OpCode.LDIV ) ); }
	public OperandlessInstruction         addFDiv            ()                                                               { return add( OperandlessInstruction.of( OpCode.FDIV ) ); }
	public OperandlessInstruction         addDDiv            ()                                                               { return add( OperandlessInstruction.of( OpCode.DDIV ) ); }
	public OperandlessInstruction         addIRem            ()                                                               { return add( OperandlessInstruction.of( OpCode.IREM ) ); }
	public OperandlessInstruction         addLRem            ()                                                               { return add( OperandlessInstruction.of( OpCode.LREM ) ); }
	public OperandlessInstruction         addFRem            ()                                                               { return add( OperandlessInstruction.of( OpCode.FREM ) ); }
	public OperandlessInstruction         addDRem            ()                                                               { return add( OperandlessInstruction.of( OpCode.DREM ) ); }
	public OperandlessInstruction         addINeg            ()                                                               { return add( OperandlessInstruction.of( OpCode.INEG ) ); }
	public OperandlessInstruction         addLNeg            ()                                                               { return add( OperandlessInstruction.of( OpCode.LNEG ) ); }
	public OperandlessInstruction         addFNeg            ()                                                               { return add( OperandlessInstruction.of( OpCode.FNEG ) ); }
	public OperandlessInstruction         addDNeg            ()                                                               { return add( OperandlessInstruction.of( OpCode.DNEG ) ); }
	public OperandlessInstruction         addIShl            ()                                                               { return add( OperandlessInstruction.of( OpCode.ISHL ) ); }
	public OperandlessInstruction         addLShl            ()                                                               { return add( OperandlessInstruction.of( OpCode.LSHL ) ); }
	public OperandlessInstruction         addIShr            ()                                                               { return add( OperandlessInstruction.of( OpCode.ISHR ) ); }
	public OperandlessInstruction         addLShr            ()                                                               { return add( OperandlessInstruction.of( OpCode.LSHR ) ); }
	public OperandlessInstruction         addIUShr           ()                                                               { return add( OperandlessInstruction.of( OpCode.IUSHR ) ); }
	public OperandlessInstruction         addLUShr           ()                                                               { return add( OperandlessInstruction.of( OpCode.LUSHR ) ); }
	public OperandlessInstruction         addIAnd            ()                                                               { return add( OperandlessInstruction.of( OpCode.IAND ) ); }
	public OperandlessInstruction         addLAnd            ()                                                               { return add( OperandlessInstruction.of( OpCode.LAND ) ); }
	public OperandlessInstruction         addIOr             ()                                                               { return add( OperandlessInstruction.of( OpCode.IOR ) ); }
	public OperandlessInstruction         addLOr             ()                                                               { return add( OperandlessInstruction.of( OpCode.LOR ) ); }
	public OperandlessInstruction         addIXor            ()                                                               { return add( OperandlessInstruction.of( OpCode.IXOR ) ); }
	public OperandlessInstruction         addLXor            ()                                                               { return add( OperandlessInstruction.of( OpCode.LXOR ) ); }
	public OperandlessInstruction         addI2L             ()                                                               { return add( OperandlessInstruction.of( OpCode.I2L ) ); }
	public OperandlessInstruction         addI2F             ()                                                               { return add( OperandlessInstruction.of( OpCode.I2F ) ); }
	public OperandlessInstruction         addI2D             ()                                                               { return add( OperandlessInstruction.of( OpCode.I2D ) ); }
	public OperandlessInstruction         addL2I             ()                                                               { return add( OperandlessInstruction.of( OpCode.L2I ) ); }
	public OperandlessInstruction         addL2F             ()                                                               { return add( OperandlessInstruction.of( OpCode.L2F ) ); }
	public OperandlessInstruction         addL2D             ()                                                               { return add( OperandlessInstruction.of( OpCode.L2D ) ); }
	public OperandlessInstruction         addF2I             ()                                                               { return add( OperandlessInstruction.of( OpCode.F2I ) ); }
	public OperandlessInstruction         addF2L             ()                                                               { return add( OperandlessInstruction.of( OpCode.F2L ) ); }
	public OperandlessInstruction         addF2D             ()                                                               { return add( OperandlessInstruction.of( OpCode.F2D ) ); }
	public OperandlessInstruction         addD2I             ()                                                               { return add( OperandlessInstruction.of( OpCode.D2I ) ); }
	public OperandlessInstruction         addD2L             ()                                                               { return add( OperandlessInstruction.of( OpCode.D2L ) ); }
	public OperandlessInstruction         addD2F             ()                                                               { return add( OperandlessInstruction.of( OpCode.D2F ) ); }
	public OperandlessInstruction         addI2B             ()                                                               { return add( OperandlessInstruction.of( OpCode.I2B ) ); }
	public OperandlessInstruction         addI2C             ()                                                               { return add( OperandlessInstruction.of( OpCode.I2C ) ); }
	public OperandlessInstruction         addI2S             ()                                                               { return add( OperandlessInstruction.of( OpCode.I2S ) ); }
	public OperandlessInstruction         addLCmp            ()                                                               { return add( OperandlessInstruction.of( OpCode.LCMP ) ); }
	public OperandlessInstruction         addFCmpL           ()                                                               { return add( OperandlessInstruction.of( OpCode.FCMPL ) ); }
	public OperandlessInstruction         addFCmpG           ()                                                               { return add( OperandlessInstruction.of( OpCode.FCMPG ) ); }
	public OperandlessInstruction         addDCmpL           ()                                                               { return add( OperandlessInstruction.of( OpCode.DCMPL ) ); }
	public OperandlessInstruction         addDCmpG           ()                                                               { return add( OperandlessInstruction.of( OpCode.DCMPG ) ); }
	public OperandlessInstruction         addIReturn         ()                                                               { return add( OperandlessInstruction.of( OpCode.IRETURN ) ); }
	public OperandlessInstruction         addLReturn         ()                                                               { return add( OperandlessInstruction.of( OpCode.LRETURN ) ); }
	public OperandlessInstruction         addFReturn         ()                                                               { return add( OperandlessInstruction.of( OpCode.FRETURN ) ); }
	public OperandlessInstruction         addDReturn         ()                                                               { return add( OperandlessInstruction.of( OpCode.DRETURN ) ); }
	public OperandlessInstruction         addAReturn         ()                                                               { return add( OperandlessInstruction.of( OpCode.ARETURN ) ); }
	public OperandlessInstruction         addReturn          ()                                                               { return add( OperandlessInstruction.of( OpCode.RETURN ) ); }
	public OperandlessInstruction         addArrayLength     ()                                                               { return add( OperandlessInstruction.of( OpCode.ARRAYLENGTH ) ); }
	public OperandlessInstruction         addAThrow          ()                                                               { return add( OperandlessInstruction.of( OpCode.ATHROW ) ); }
	public OperandlessInstruction         addMonitorEnter    ()                                                               { return add( OperandlessInstruction.of( OpCode.MONITORENTER ) ); }
	public OperandlessInstruction         addMonitorExit     ()                                                               { return add( OperandlessInstruction.of( OpCode.MONITOREXIT ) ); }
	//@formatter:on

	private <T extends Instruction> T add( T instruction )
	{
		return instruction;
	}
}
