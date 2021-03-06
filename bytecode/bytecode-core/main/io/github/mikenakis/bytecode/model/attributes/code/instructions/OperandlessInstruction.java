package io.github.mikenakis.bytecode.model.attributes.code.instructions;

import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.model.attributes.code.OpCode;
import io.github.mikenakis.bytecode.writing.InstructionWriter;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collection;
import java.util.Set;

//TODO: split in two different forms, one which is type-agnostic, and one which has a type, supplied as a parameter.
//Start with these:
//IALOAD,LALOAD,FALOAD,DALOAD,AALOAD,BALOAD,CALOAD,SALOAD         -> xALOAD(Type)
//IASTORE,LASTORE,FASTORE,DASTORE,AASTORE,BASTORE,CASTORE,SASTORE -> xASTORE(Type)
//IRETURN,LRETURN,FRETURN,DRETURN,ARETURN                         -> xRETURN(Type)
//IADD,LADD,FADD,DADD                                             -> xADD(Type)
//ISUB,LSUB,FSUB,DSUB                                             -> xSUB(Type)
//IMUL,LMUL,FMUL,DMUL                                             -> xMUL(Type)
//IDIV,LDIV,FDIV,DDIV                                             -> xDIV(Type)
//IREM,LREM,FREM,DREM                                             -> xREM(Type)
//INEG,LNEG,FNEG,DNEG                                             -> xNEG(Type)
//Then possibly also do these:
//ISHL,LSHL                                                       -> xSHL(Type)
//ISHR,LSHR                                                       -> xSHR(Type)
//IUSHR,LUSHR                                                     -> xUSHR(Type)
//IAND,LAND                                                       -> xAND(Type)
//IOR,LOR                                                         -> xOR(Type)
//IXOR,LXOR                                                       -> xXOR(Type)
//FCMPL,DCMPL                                                     -> xCMPL(Type)
//FCMPG,DCMPG                                                     -> xCMPG(Type)
//Finally, perhaps split into one more form, which accepts two type parameters:
//I2L,I2F,I2D,L2I,L2F,L2D,F2I,F2L,F2D,D2I,D2L,D2F,I2B,I2C,I2S     -> x2x(Type,Type)
public final class OperandlessInstruction extends Instruction
{
	private static final Collection<Integer> opCodes = Set.of( OpCode.NOP, OpCode.ACONST_NULL, //
		OpCode.POP, OpCode.POP2, OpCode.DUP, OpCode.DUP_X1, OpCode.DUP_X2, OpCode.DUP2, OpCode.DUP2_X1, OpCode.DUP2_X2, OpCode.SWAP, //
//		OpCode.IALOAD, OpCode.LALOAD, OpCode.FALOAD, OpCode.DALOAD, OpCode.AALOAD, OpCode.BALOAD, OpCode.CALOAD, OpCode.SALOAD,
//		OpCode.IASTORE, OpCode.LASTORE, OpCode.FASTORE, OpCode.DASTORE, OpCode.AASTORE, OpCode.BASTORE, OpCode.CASTORE, OpCode.SASTORE,
//		OpCode.IADD, OpCode.LADD, OpCode.FADD, OpCode.DADD, OpCode.ISUB, OpCode.LSUB, OpCode.FSUB, OpCode.DSUB,
//		OpCode.IMUL, OpCode.LMUL, OpCode.FMUL, OpCode.DMUL, OpCode.IDIV, OpCode.LDIV, OpCode.FDIV, OpCode.DDIV,
//		OpCode.IREM, OpCode.LREM, OpCode.FREM, OpCode.DREM, OpCode.INEG, OpCode.LNEG, OpCode.FNEG, OpCode.DNEG,
//		OpCode.IRETURN, OpCode.LRETURN, OpCode.FRETURN, OpCode.DRETURN, OpCode.ARETURN,
		OpCode.ISHL, OpCode.LSHL, OpCode.ISHR, OpCode.LSHR, OpCode.IUSHR, OpCode.LUSHR, //
		OpCode.IAND, OpCode.LAND, OpCode.IOR, OpCode.LOR, OpCode.IXOR, OpCode.LXOR, //
		OpCode.I2L, OpCode.I2F, OpCode.I2D, OpCode.L2I, OpCode.L2F, OpCode.L2D, OpCode.F2I, OpCode.F2L, OpCode.F2D, OpCode.D2I, OpCode.D2L, OpCode.D2F, OpCode.I2B, OpCode.I2C, OpCode.I2S, //
		OpCode.LCMP, OpCode.FCMPL, OpCode.FCMPG, OpCode.DCMPL, OpCode.DCMPG, //
		OpCode.RETURN, OpCode.ARRAYLENGTH, OpCode.ATHROW, OpCode.MONITORENTER, OpCode.MONITOREXIT );

	public static OperandlessInstruction read( boolean wide, int opCode )
	{
		assert !wide;
		return of( opCode );
	}

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

	@Deprecated @Override public OperandlessInstruction asOperandlessInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( opCode ); }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		instructionWriter.writeUnsignedByte( opCode );
	}
}
