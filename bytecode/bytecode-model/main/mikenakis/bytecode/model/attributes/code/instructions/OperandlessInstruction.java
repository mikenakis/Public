package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collection;
import java.util.Set;

public final class OperandlessInstruction extends Instruction
{
	private static final Collection<Integer> opCodes = Set.of( OpCode.NOP, OpCode.ACONST_NULL, OpCode.IALOAD, OpCode.LALOAD, OpCode.FALOAD, OpCode.DALOAD, //
		OpCode.AALOAD, OpCode.BALOAD, OpCode.CALOAD, OpCode.SALOAD, OpCode.IASTORE, OpCode.LASTORE, OpCode.FASTORE, OpCode.DASTORE, OpCode.AASTORE, //
		OpCode.BASTORE, OpCode.CASTORE, OpCode.SASTORE, OpCode.POP, OpCode.POP2, OpCode.DUP, OpCode.DUP_X1, OpCode.DUP_X2, OpCode.DUP2, OpCode.DUP2_X1, //
		OpCode.DUP2_X2, OpCode.SWAP, OpCode.IADD, OpCode.LADD, OpCode.FADD, OpCode.DADD, OpCode.ISUB, OpCode.LSUB, OpCode.FSUB, OpCode.DSUB, OpCode.IMUL, //
		OpCode.LMUL, OpCode.FMUL, OpCode.DMUL, OpCode.IDIV, OpCode.LDIV, OpCode.FDIV, OpCode.DDIV, OpCode.IREM, OpCode.LREM, OpCode.FREM, OpCode.DREM, //
		OpCode.INEG, OpCode.LNEG, OpCode.FNEG, OpCode.DNEG, OpCode.ISHL, OpCode.LSHL, OpCode.ISHR, OpCode.LSHR, OpCode.IUSHR, OpCode.LUSHR, OpCode.IAND, //
		OpCode.LAND, OpCode.IOR, OpCode.LOR, OpCode.IXOR, OpCode.LXOR, OpCode.I2L, OpCode.I2F, OpCode.I2D, OpCode.L2I, OpCode.L2F, OpCode.L2D, OpCode.F2I, //
		OpCode.F2L, OpCode.F2D, OpCode.D2I, OpCode.D2L, OpCode.D2F, OpCode.I2B, OpCode.I2C, OpCode.I2S, OpCode.LCMP, OpCode.FCMPL, OpCode.FCMPG, OpCode.DCMPL, //
		OpCode.DCMPG, OpCode.IRETURN, OpCode.LRETURN, OpCode.FRETURN, OpCode.DRETURN, OpCode.ARETURN, OpCode.RETURN, OpCode.ARRAYLENGTH, OpCode.ATHROW, //
		OpCode.MONITORENTER, OpCode.MONITOREXIT );

	public static OperandlessInstruction of( int opCode )
	{
		return new OperandlessInstruction( opCode );
	}

	public final int opCode;

	private OperandlessInstruction( int opCode )
	{
		super( groupTag_Operandless );
		assert opCodes.contains( opCode );
		this.opCode = opCode;
	}

	public int getOpCode()
	{
		return opCode;
	}

	@Deprecated @Override public OperandlessInstruction asOperandlessInstruction()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( opCode );
	}
}
