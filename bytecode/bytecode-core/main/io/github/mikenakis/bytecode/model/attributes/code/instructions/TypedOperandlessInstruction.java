package io.github.mikenakis.bytecode.model.attributes.code.instructions;

import io.github.mikenakis.bytecode.writing.InstructionWriter;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.model.attributes.code.OpCode;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//TODO: possibly move these here from "OperandlessInstruction":
//ISHL,LSHL    -> xSHL(Type)
//ISHR,LSHR    -> xSHR(Type)
//IUSHR,LUSHR  -> xUSHR(Type)
//IAND,LAND    -> xAND(Type)
//IOR,LOR      -> xOR(Type)
//IXOR,LXOR    -> xXOR(Type)
//FCMPL,DCMPL  -> xCMPL(Type)
//FCMPG,DCMPG  -> xCMPG(Type)
//TODO: perhaps split into one more form, which accepts two type parameters:
//I2L,I2F,I2D,L2I,L2F,L2D,F2I,F2L,F2D,D2I,D2L,D2F,I2B,I2C,I2S -> x2x(Type,Type)
public final class TypedOperandlessInstruction extends Instruction
{
	public enum Operation
	{
		Load, Store, Return, Add, Sub, Mul, Div, Rem, Neg
	}

	private static class OpCodeInfo
	{
		final Operation operation;
		final Type type;
		final int opCode;

		private OpCodeInfo( Operation operation, Type type, int opCode )
		{
			this.operation = operation;
			this.type = type;
			this.opCode = opCode;
		}

		static OpCodeInfo from( Operation operation, Type type )
		{
			for( OpCodeInfo opCodeInfo : opCodeInfoMap.values() )
				if( opCodeInfo.operation == operation && opCodeInfo.type == type )
					return opCodeInfo;
			throw new AssertionError( operation + " " + type );
		}
	}

	private static final Collection<OpCodeInfo> opCodeInfos = List.of( //
		new OpCodeInfo( Operation.Load,   /**/ Type.Int     /**/, OpCode.IALOAD ),  //
		new OpCodeInfo( Operation.Load,   /**/ Type.Long    /**/, OpCode.LALOAD ),  //
		new OpCodeInfo( Operation.Load,   /**/ Type.Float   /**/, OpCode.FALOAD ),  //
		new OpCodeInfo( Operation.Load,   /**/ Type.Double  /**/, OpCode.DALOAD ),  //
		new OpCodeInfo( Operation.Load,   /**/ Type.Address /**/, OpCode.AALOAD ),  //
		new OpCodeInfo( Operation.Load,   /**/ Type.Byte    /**/, OpCode.BALOAD ),  //
		//new OpCodeInfo( Operation.Load,   /**/ Type.BOOLEAN /**/, OpCode.BALOAD ),  //
		new OpCodeInfo( Operation.Load,   /**/ Type.Char    /**/, OpCode.CALOAD ),  //
		new OpCodeInfo( Operation.Load,   /**/ Type.Short   /**/, OpCode.SALOAD ),  //
		new OpCodeInfo( Operation.Store,  /**/ Type.Int     /**/, OpCode.IASTORE ), //
		new OpCodeInfo( Operation.Store,  /**/ Type.Long    /**/, OpCode.LASTORE ), //
		new OpCodeInfo( Operation.Store,  /**/ Type.Float   /**/, OpCode.FASTORE ), //
		new OpCodeInfo( Operation.Store,  /**/ Type.Double  /**/, OpCode.DASTORE ), //
		new OpCodeInfo( Operation.Store,  /**/ Type.Address /**/, OpCode.AASTORE ), //
		new OpCodeInfo( Operation.Store,  /**/ Type.Byte    /**/, OpCode.BASTORE ), //
		//new OpCodeInfo( Operation.Store,  /**/ Type.BOOLEAN /**/, OpCode.BASTORE ), //
		new OpCodeInfo( Operation.Store,  /**/ Type.Char    /**/, OpCode.CASTORE ), //
		new OpCodeInfo( Operation.Store,  /**/ Type.Short   /**/, OpCode.SASTORE ), //
		new OpCodeInfo( Operation.Return, /**/ Type.Int     /**/, OpCode.IRETURN ), //
		new OpCodeInfo( Operation.Return, /**/ Type.Long    /**/, OpCode.LRETURN ), //
		new OpCodeInfo( Operation.Return, /**/ Type.Float   /**/, OpCode.FRETURN ), //
		new OpCodeInfo( Operation.Return, /**/ Type.Double  /**/, OpCode.DRETURN ), //
		new OpCodeInfo( Operation.Return, /**/ Type.Address /**/, OpCode.ARETURN ), //
		new OpCodeInfo( Operation.Add,    /**/ Type.Int     /**/, OpCode.IADD ),    //
		new OpCodeInfo( Operation.Add,    /**/ Type.Long    /**/, OpCode.LADD ),    //
		new OpCodeInfo( Operation.Add,    /**/ Type.Float   /**/, OpCode.FADD ),    //
		new OpCodeInfo( Operation.Add,    /**/ Type.Double  /**/, OpCode.DADD ),    //
		new OpCodeInfo( Operation.Sub,    /**/ Type.Int     /**/, OpCode.ISUB ),    //
		new OpCodeInfo( Operation.Sub,    /**/ Type.Long    /**/, OpCode.LSUB ),    //
		new OpCodeInfo( Operation.Sub,    /**/ Type.Float   /**/, OpCode.FSUB ),    //
		new OpCodeInfo( Operation.Sub,    /**/ Type.Double  /**/, OpCode.DSUB ),    //
		new OpCodeInfo( Operation.Mul,    /**/ Type.Int     /**/, OpCode.IMUL ),    //
		new OpCodeInfo( Operation.Mul,    /**/ Type.Long    /**/, OpCode.LMUL ),    //
		new OpCodeInfo( Operation.Mul,    /**/ Type.Float   /**/, OpCode.FMUL ),    //
		new OpCodeInfo( Operation.Mul,    /**/ Type.Double  /**/, OpCode.DMUL ),    //
		new OpCodeInfo( Operation.Div,    /**/ Type.Int     /**/, OpCode.IDIV ),    //
		new OpCodeInfo( Operation.Div,    /**/ Type.Long    /**/, OpCode.LDIV ),    //
		new OpCodeInfo( Operation.Div,    /**/ Type.Float   /**/, OpCode.FDIV ),    //
		new OpCodeInfo( Operation.Div,    /**/ Type.Double  /**/, OpCode.DDIV ),    //
		new OpCodeInfo( Operation.Rem,    /**/ Type.Int     /**/, OpCode.IREM ),    //
		new OpCodeInfo( Operation.Rem,    /**/ Type.Long    /**/, OpCode.LREM ),    //
		new OpCodeInfo( Operation.Rem,    /**/ Type.Float   /**/, OpCode.FREM ),    //
		new OpCodeInfo( Operation.Rem,    /**/ Type.Double  /**/, OpCode.DREM ),    //
		new OpCodeInfo( Operation.Neg,    /**/ Type.Int     /**/, OpCode.INEG ),    //
		new OpCodeInfo( Operation.Neg,    /**/ Type.Long    /**/, OpCode.LNEG ),    //
		new OpCodeInfo( Operation.Neg,    /**/ Type.Float   /**/, OpCode.FNEG ),    //
		new OpCodeInfo( Operation.Neg,    /**/ Type.Double  /**/, OpCode.DNEG ) );
	private static final Map<Integer,OpCodeInfo> opCodeInfoMap = opCodeInfos.stream().collect( Collectors.toMap( i -> i.opCode, i -> i ) );

	public static TypedOperandlessInstruction read( boolean wide, int opCode )
	{
		assert !wide;
		assert opCodeInfoMap.containsKey( opCode );
		return new TypedOperandlessInstruction( opCode );
	}

	public static TypedOperandlessInstruction of( Operation operation, Type type )
	{
		OpCodeInfo opCodeInfo = OpCodeInfo.from( operation, type );
		return new TypedOperandlessInstruction( opCodeInfo.opCode );
	}

	public final int opCode;

	private TypedOperandlessInstruction( int opCode )
	{
		super( groupTag_TypedOperandless );
		this.opCode = opCode;
	}

	@Deprecated @Override public TypedOperandlessInstruction asTypedOperandlessInstruction() { return this; }
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
