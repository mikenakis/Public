package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class LocalVariableInstruction extends Instruction
{
	public static LocalVariableInstruction of( int opCode, int index )
	{
		return new LocalVariableInstruction( opCode, index );
	}

	public enum IndexType
	{
		ByOperand( -1 ), //
		AtIndex0( 0 ),   //
		AtIndex1( 1 ),   //
		AtIndex2( 2 ),   //
		AtIndex3( 3 );

		private final int index;

		IndexType( int index )
		{
			assert index >= -1;
			assert index <= 3;
			this.index = index;
		}

		public int index()
		{
			assert index != -1;
			return index;
		}

		public static IndexType of( int index, int opCode )
		{
			if( opCode == OpCode.RET )
				return ByOperand;
			return switch( index )
				{
					case 0 -> AtIndex0;
					case 1 -> AtIndex1;
					case 2 -> AtIndex2;
					case 3 -> AtIndex3;
					default -> ByOperand;
				};
		}
	}

	private static final class OpCodeInfo
	{
		final int opCode;
		final PseudoOpCode pseudoOpCode;
		final IndexType indexType;

		OpCodeInfo( int opCode, PseudoOpCode pseudoOpCode, IndexType indexType )
		{
			this.opCode = opCode;
			this.pseudoOpCode = pseudoOpCode;
			this.indexType = indexType;
		}

		static Optional<OpCodeInfo> tryFromOpCode( int opCode )
		{
			for( OpCodeInfo opCodeInfo : opCodeInfos )
				if( opCodeInfo.opCode == opCode )
					return Optional.of( opCodeInfo );
			return Optional.empty();
		}

		static OpCodeInfo fromOpCode( int opCode )
		{
			return tryFromOpCode( opCode ).orElseThrow();
		}

		static Optional<OpCodeInfo> tryFrom( PseudoOpCode pseudoOpCode, IndexType indexType )
		{
			for( OpCodeInfo opCodeInfo : opCodeInfos )
				if( opCodeInfo.pseudoOpCode == pseudoOpCode && opCodeInfo.indexType == indexType )
					return Optional.of( opCodeInfo );
			return Optional.empty();
		}

		static OpCodeInfo from( PseudoOpCode pseudoOpCode, IndexType indexType )
		{
			return tryFrom( pseudoOpCode, indexType ).orElseThrow();
		}
	}

	private enum PseudoOpCode
	{
		ILoad( OpCode.ILOAD ),   /**/
		LLoad( OpCode.LLOAD ),   /**/
		FLoad( OpCode.FLOAD ),   /**/
		DLoad( OpCode.DLOAD ),   /**/
		ALoad( OpCode.ALOAD ),   /**/
		IStore( OpCode.ISTORE ), /**/
		LStore( OpCode.LSTORE ), /**/
		FStore( OpCode.FSTORE ), /**/
		DStore( OpCode.DSTORE ), /**/
		AStore( OpCode.ASTORE ), /**/
		Ret( OpCode.RET );

		final int posterOpCode;

		PseudoOpCode( int posterOpCode )
		{
			this.posterOpCode = posterOpCode;
		}
	}

	private static final Collection<OpCodeInfo> opCodeInfos = List.of( //
		new OpCodeInfo( OpCode.ILOAD    /**/, PseudoOpCode.ILoad  /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.ILOAD_0  /**/, PseudoOpCode.ILoad  /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.ILOAD_1  /**/, PseudoOpCode.ILoad  /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.ILOAD_2  /**/, PseudoOpCode.ILoad  /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.ILOAD_3  /**/, PseudoOpCode.ILoad  /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.LLOAD    /**/, PseudoOpCode.LLoad  /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.LLOAD_0  /**/, PseudoOpCode.LLoad  /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.LLOAD_1  /**/, PseudoOpCode.LLoad  /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.LLOAD_2  /**/, PseudoOpCode.LLoad  /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.LLOAD_3  /**/, PseudoOpCode.LLoad  /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.FLOAD    /**/, PseudoOpCode.FLoad  /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.FLOAD_0  /**/, PseudoOpCode.FLoad  /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.FLOAD_1  /**/, PseudoOpCode.FLoad  /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.FLOAD_2  /**/, PseudoOpCode.FLoad  /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.FLOAD_3  /**/, PseudoOpCode.FLoad  /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.DLOAD    /**/, PseudoOpCode.DLoad  /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.DLOAD_0  /**/, PseudoOpCode.DLoad  /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.DLOAD_1  /**/, PseudoOpCode.DLoad  /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.DLOAD_2  /**/, PseudoOpCode.DLoad  /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.DLOAD_3  /**/, PseudoOpCode.DLoad  /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.ALOAD    /**/, PseudoOpCode.ALoad  /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.ALOAD_0  /**/, PseudoOpCode.ALoad  /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.ALOAD_1  /**/, PseudoOpCode.ALoad  /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.ALOAD_2  /**/, PseudoOpCode.ALoad  /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.ALOAD_3  /**/, PseudoOpCode.ALoad  /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.ISTORE   /**/, PseudoOpCode.IStore /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.ISTORE_0 /**/, PseudoOpCode.IStore /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.ISTORE_1 /**/, PseudoOpCode.IStore /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.ISTORE_2 /**/, PseudoOpCode.IStore /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.ISTORE_3 /**/, PseudoOpCode.IStore /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.LSTORE   /**/, PseudoOpCode.LStore /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.LSTORE_0 /**/, PseudoOpCode.LStore /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.LSTORE_1 /**/, PseudoOpCode.LStore /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.LSTORE_2 /**/, PseudoOpCode.LStore /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.LSTORE_3 /**/, PseudoOpCode.LStore /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.FSTORE   /**/, PseudoOpCode.FStore /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.FSTORE_0 /**/, PseudoOpCode.FStore /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.FSTORE_1 /**/, PseudoOpCode.FStore /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.FSTORE_2 /**/, PseudoOpCode.FStore /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.FSTORE_3 /**/, PseudoOpCode.FStore /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.DSTORE   /**/, PseudoOpCode.DStore /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.DSTORE_0 /**/, PseudoOpCode.DStore /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.DSTORE_1 /**/, PseudoOpCode.DStore /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.DSTORE_2 /**/, PseudoOpCode.DStore /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.DSTORE_3 /**/, PseudoOpCode.DStore /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.ASTORE   /**/, PseudoOpCode.AStore /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.ASTORE_0 /**/, PseudoOpCode.AStore /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.ASTORE_1 /**/, PseudoOpCode.AStore /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.ASTORE_2 /**/, PseudoOpCode.AStore /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.ASTORE_3 /**/, PseudoOpCode.AStore /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.RET      /**/, PseudoOpCode.Ret    /**/, IndexType.ByOperand )  //
	);

	private static final Set<Integer> opCodes = opCodeInfos.stream().map( c -> c.opCode ).collect( Collectors.toSet() );

	private static final class Model
	{
		final int opCode;
		final OpCodeInfo opCodeInfo;

		Model( int opCode )
		{
			this.opCode = opCode;
			opCodeInfo = OpCodeInfo.fromOpCode( opCode );
		}
	}

	private static final Map<Integer,Model> modelsFromOpCodes = Kit.iterable.toMap( opCodeInfos, i -> i.opCode, i -> new Model( i.opCode ) );

	public static IndexType getIndexType( int opCode )
	{
		Model model = Kit.map.get( modelsFromOpCodes, opCode );
		return model.opCodeInfo.indexType;
	}

	public static boolean hasOperand( int opCode )
	{
		IndexType indexType = getIndexType( opCode );
		return indexType == IndexType.ByOperand;
	}

	public static int getIndex( int opCode )
	{
		IndexType indexType = getIndexType( opCode );
		return indexType.index();
	}

	private final int opCode;
	public final int index;

	private LocalVariableInstruction( int opCode, int index )
	{
		super( Group.LocalVariable );
		assert opCodes.contains( opCode );
		assert index >= 0;
		this.opCode = opCode;
		this.index = index;
	}

	public int getOpCode()
	{
		return opCode;
	}

	public int getActualOpcode()
	{
		IndexType indexType = IndexType.of( index, opCode );
		Model model = Kit.map.get( modelsFromOpCodes, opCode );
		OpCodeInfo opCodeInfo = OpCodeInfo.from( model.opCodeInfo.pseudoOpCode, indexType );
		return opCodeInfo.opCode;
	}

	public boolean isWide()
	{
		return !Helpers.isUnsignedByte( index );
	}

	public boolean hasOperand()
	{
		IndexType selector = IndexType.of( index, opCode );
		return selector == IndexType.ByOperand;
	}

	@Deprecated @Override public LocalVariableInstruction asLocalVariableInstruction()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( opCode );
	}
}
