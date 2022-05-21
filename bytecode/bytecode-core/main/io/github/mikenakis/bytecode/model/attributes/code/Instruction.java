package io.github.mikenakis.bytecode.model.attributes.code;

import io.github.mikenakis.bytecode.model.attributes.code.instructions.BranchInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.ClassReferencingInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.ConditionalBranchInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.FieldReferencingInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.IIncInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.InvokeDynamicInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.InvokeInterfaceInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.LoadConstantInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.LocalVariableInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.LookupSwitchInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.MethodReferencingInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.MultiANewArrayInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.NewPrimitiveArrayInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.OperandlessInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.TableSwitchInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.TypedOperandlessInstruction;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.InstructionWriter;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a virtual machine instruction.
 *
 * @author michael.gr
 */
public abstract class Instruction
{
	public static Instruction read( BufferReader bufferReader, ReadingConstantPool constantPool, ReadingLocationMap locationMap )
	{
		boolean wide = false;
		int opCode = bufferReader.readUnsignedByte();
		if( opCode == OpCode.WIDE )
		{
			wide = true;
			opCode = bufferReader.readUnsignedByte();
		}
		int instructionGroupTag = groupFromOpCode( opCode );
		return switch( instructionGroupTag )
			{
				case Instruction.groupTag_Operandless -> OperandlessInstruction.read( wide, opCode );
				case Instruction.groupTag_TypedOperandless -> TypedOperandlessInstruction.read( wide, opCode );
				case Instruction.groupTag_LocalVariable -> LocalVariableInstruction.read( bufferReader, wide, opCode );
				case Instruction.groupTag_IInc -> IIncInstruction.read( bufferReader, wide );
				case Instruction.groupTag_ConditionalBranch -> ConditionalBranchInstruction.read( bufferReader, locationMap, wide, opCode );
				case Instruction.groupTag_Branch -> BranchInstruction.read( bufferReader, locationMap, wide, opCode );
				case Instruction.groupTag_TableSwitch -> TableSwitchInstruction.read( bufferReader, locationMap, wide );
				case Instruction.groupTag_LookupSwitch -> LookupSwitchInstruction.read( bufferReader, locationMap, wide, opCode );
				case Instruction.groupTag_ClassConstantReferencing -> ClassReferencingInstruction.read( bufferReader, constantPool, wide, opCode );
				case Instruction.groupTag_FieldConstantReferencing -> FieldReferencingInstruction.read( bufferReader, constantPool, wide, opCode );
				case Instruction.groupTag_MethodConstantReferencing -> MethodReferencingInstruction.read( bufferReader, constantPool, wide, opCode );
				case Instruction.groupTag_InvokeInterface -> InvokeInterfaceInstruction.read( bufferReader, constantPool, wide, opCode );
				case Instruction.groupTag_InvokeDynamic -> InvokeDynamicInstruction.read( bufferReader, constantPool, wide, opCode );
				case Instruction.groupTag_NewPrimitiveArray -> NewPrimitiveArrayInstruction.read( bufferReader, wide, opCode );
				case Instruction.groupTag_MultiANewArray -> MultiANewArrayInstruction.read( bufferReader, constantPool, wide, opCode );
				case Instruction.groupTag_LoadConstant -> LoadConstantInstruction.read( bufferReader, constantPool, wide, opCode );
				default -> throw new AssertionError( instructionGroupTag );
			};
	}

	public static final int groupTag_Branch                    /**/ = 1;
	public static final int groupTag_ConditionalBranch         /**/ = 2;
	public static final int groupTag_ClassConstantReferencing  /**/ = 3;
	public static final int groupTag_FieldConstantReferencing  /**/ = 4;
	public static final int groupTag_IInc                      /**/ = 5;
	public static final int groupTag_InvokeDynamic             /**/ = 6;
	public static final int groupTag_InvokeInterface           /**/ = 7;
	public static final int groupTag_LocalVariable             /**/ = 8;
	public static final int groupTag_LookupSwitch              /**/ = 9;
	public static final int groupTag_MethodConstantReferencing /**/ = 10;
	public static final int groupTag_MultiANewArray            /**/ = 11;
	public static final int groupTag_NewPrimitiveArray         /**/ = 12;
	public static final int groupTag_Operandless               /**/ = 13;
	public static final int groupTag_TypedOperandless          /**/ = 14;
	public static final int groupTag_TableSwitch               /**/ = 15;
	public static final int groupTag_LoadConstant              /**/ = 16;

	public static int groupFromOpCode( int opCode )
	{
		return switch( opCode )
			{
				case OpCode.NOP, OpCode.MONITOREXIT, OpCode.MONITORENTER, OpCode.ATHROW, OpCode.ARRAYLENGTH, OpCode.RETURN, //
					OpCode.DCMPG, OpCode.DCMPL, OpCode.FCMPG, OpCode.FCMPL, OpCode.LCMP, //
					OpCode.I2S, OpCode.I2C, OpCode.I2B, OpCode.D2F, OpCode.D2L, OpCode.D2I, OpCode.F2D, OpCode.F2L, OpCode.F2I, OpCode.L2D, OpCode.L2F, //
					OpCode.L2I, OpCode.I2D, OpCode.I2F, OpCode.I2L, //
					OpCode.LXOR, OpCode.IXOR, OpCode.LOR, OpCode.IOR, OpCode.LAND, OpCode.IAND, //
					OpCode.LUSHR, OpCode.IUSHR, OpCode.LSHR, OpCode.ISHR, OpCode.LSHL, OpCode.ISHL, //
					OpCode.SWAP, OpCode.DUP2_X2, OpCode.DUP2_X1, OpCode.DUP2, OpCode.DUP_X2, OpCode.DUP_X1, OpCode.DUP, OpCode.POP2, OpCode.POP, //
					OpCode.ACONST_NULL -> groupTag_Operandless;
				case OpCode.SALOAD, OpCode.CALOAD, OpCode.BALOAD, OpCode.AALOAD, OpCode.DALOAD, OpCode.FALOAD, OpCode.LALOAD, OpCode.IALOAD, //
					OpCode.SASTORE, OpCode.CASTORE, OpCode.BASTORE, OpCode.AASTORE, OpCode.DASTORE, OpCode.FASTORE, OpCode.LASTORE, OpCode.IASTORE, //
					OpCode.ARETURN, OpCode.DRETURN, OpCode.FRETURN, OpCode.LRETURN, OpCode.IRETURN, //
					OpCode.DADD, OpCode.FADD, OpCode.LADD, OpCode.IADD, //
					OpCode.DSUB, OpCode.FSUB, OpCode.LSUB, OpCode.ISUB, //
					OpCode.DMUL, OpCode.FMUL, OpCode.LMUL, OpCode.IMUL, //
					OpCode.DDIV, OpCode.FDIV, OpCode.LDIV, OpCode.IDIV, //
					OpCode.DREM, OpCode.FREM, OpCode.LREM, OpCode.IREM, //
					OpCode.DNEG, OpCode.FNEG, OpCode.LNEG, OpCode.INEG -> groupTag_TypedOperandless;
				case OpCode.ICONST_M1, OpCode.DCONST_1, OpCode.DCONST_0, OpCode.FCONST_2, OpCode.FCONST_1, OpCode.FCONST_0, OpCode.LCONST_1, OpCode.LCONST_0, //
					OpCode.ICONST_5, OpCode.ICONST_4, OpCode.ICONST_3, OpCode.ICONST_2, OpCode.ICONST_1, OpCode.ICONST_0, OpCode.BIPUSH, OpCode.SIPUSH, //
					OpCode.LDC, OpCode.LDC_W, OpCode.LDC2_W -> groupTag_LoadConstant;
				case OpCode.ILOAD, OpCode.LLOAD, OpCode.ASTORE_3, OpCode.ASTORE_2, OpCode.ASTORE_1, OpCode.ASTORE_0, OpCode.DSTORE_3, OpCode.DSTORE_2, //
					OpCode.DSTORE_1, OpCode.DSTORE_0, OpCode.FSTORE_3, OpCode.FSTORE_2, OpCode.FSTORE_1, OpCode.FSTORE_0, OpCode.LSTORE_3, OpCode.LSTORE_2, //
					OpCode.LSTORE_1, OpCode.LSTORE_0, OpCode.ISTORE_3, OpCode.ISTORE_2, OpCode.ISTORE_1, OpCode.ISTORE_0, OpCode.ASTORE, OpCode.DSTORE, //
					OpCode.FSTORE, OpCode.LSTORE, OpCode.ISTORE, OpCode.ALOAD_3, OpCode.ALOAD_2, OpCode.ALOAD_1, OpCode.ALOAD_0, OpCode.DLOAD_3, //
					OpCode.DLOAD_2, OpCode.DLOAD_1, OpCode.DLOAD_0, OpCode.FLOAD_3, OpCode.FLOAD_2, OpCode.FLOAD_1, OpCode.FLOAD_0, OpCode.LLOAD_3, //
					OpCode.LLOAD_2, OpCode.LLOAD_1, OpCode.LLOAD_0, OpCode.ILOAD_3, OpCode.ILOAD_2, OpCode.ILOAD_1, OpCode.ILOAD_0, OpCode.ALOAD, //
					OpCode.DLOAD, OpCode.FLOAD, OpCode.RET -> groupTag_LocalVariable;
				case OpCode.IINC -> groupTag_IInc;
				case OpCode.IFEQ, OpCode.IFNONNULL, OpCode.IFNULL, OpCode.IF_ACMPNE, OpCode.IF_ACMPEQ, OpCode.IF_ICMPLE, OpCode.IF_ICMPGT, //
					OpCode.IF_ICMPGE, OpCode.IF_ICMPLT, OpCode.IF_ICMPNE, OpCode.IF_ICMPEQ, OpCode.IFLE, OpCode.IFGT, OpCode.IFGE, OpCode.IFLT, //
					OpCode.IFNE -> groupTag_ConditionalBranch;
				case OpCode.GOTO, OpCode.GOTO_W, OpCode.JSR, OpCode.JSR_W -> groupTag_Branch;
				case OpCode.TABLESWITCH -> groupTag_TableSwitch;
				case OpCode.LOOKUPSWITCH -> groupTag_LookupSwitch;
				case OpCode.GETSTATIC, OpCode.PUTFIELD, OpCode.GETFIELD, OpCode.PUTSTATIC -> groupTag_FieldConstantReferencing;
				case OpCode.INSTANCEOF, OpCode.CHECKCAST, OpCode.ANEWARRAY, OpCode.NEW -> groupTag_ClassConstantReferencing;
				case OpCode.INVOKESTATIC, OpCode.INVOKESPECIAL, OpCode.INVOKEVIRTUAL -> groupTag_MethodConstantReferencing;
				case OpCode.INVOKEINTERFACE -> groupTag_InvokeInterface;
				case OpCode.INVOKEDYNAMIC -> groupTag_InvokeDynamic;
				case OpCode.NEWARRAY -> groupTag_NewPrimitiveArray;
				case OpCode.MULTIANEWARRAY -> groupTag_MultiANewArray;
				default -> throw new AssertionError( opCode );
			};
	}

	public final int groupTag;

	protected Instruction( int groupTag )
	{
		this.groupTag = groupTag;
	}

	@ExcludeFromJacocoGeneratedReport public BranchInstruction            /**/ asBranchInstruction            /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ConditionalBranchInstruction /**/ asConditionalBranchInstruction /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ClassReferencingInstruction  /**/ asClassReferencingInstruction  /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public FieldReferencingInstruction  /**/ asFieldReferencingInstruction  /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public IIncInstruction              /**/ asIIncInstruction              /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public InvokeDynamicInstruction     /**/ asInvokeDynamicInstruction     /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public InvokeInterfaceInstruction   /**/ asInvokeInterfaceInstruction   /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public LocalVariableInstruction     /**/ asLocalVariableInstruction     /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public LookupSwitchInstruction      /**/ asLookupSwitchInstruction      /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodReferencingInstruction /**/ asMethodReferencingInstruction /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MultiANewArrayInstruction    /**/ asMultiANewArrayInstruction    /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public NewPrimitiveArrayInstruction /**/ asNewPrimitiveArrayInstruction /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public OperandlessInstruction       /**/ asOperandlessInstruction       /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public TypedOperandlessInstruction  /**/ asTypedOperandlessInstruction  /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public TableSwitchInstruction       /**/ asTableSwitchInstruction       /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public LoadConstantInstruction      /**/ asLoadConstantInstruction      /**/() { return Kit.fail(); }

	@Override public final int hashCode() { return super.hashCode(); }
	@Override public final boolean equals( Object other ) { return this == other; }
	@Override public abstract String toString();

	public abstract void intern( Interner interner );
	public abstract void write( InstructionWriter instructionWriter );
}
