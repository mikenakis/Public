package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.writing.InstructionWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class LocalVariableInstruction extends Instruction
{
	public static LocalVariableInstruction read( BufferReader bufferReader, boolean wide, int opCode )
	{
		OpCodeInfo opCodeInfo = Kit.map.get( opCodeInfosFromOpCodes, opCode );
		IndexType indexType = opCodeInfo.indexType;
		int localVariableIndex;
		if( indexType == IndexType.ByOperand )
			localVariableIndex = wide ? bufferReader.readUnsignedShort() : bufferReader.readUnsignedByte();
		else
		{
			assert !wide;
			localVariableIndex = indexType.index();
		}
		return of( opCodeInfo.genericOpCode, localVariableIndex );
	}

	public static LocalVariableInstruction of( int opCode, int localVariableIndex )
	{
		return new LocalVariableInstruction( opCode, localVariableIndex );
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
		final int genericOpCode;
		final IndexType indexType;

		OpCodeInfo( int opCode, int genericOpCode, IndexType indexType )
		{
			this.opCode = opCode;
			this.genericOpCode = genericOpCode;
			this.indexType = indexType;
		}

		static OpCodeInfo from( int genericOpCode, IndexType indexType )
		{
			for( OpCodeInfo opCodeInfo : opCodeInfosFromOpCodes.values() )
				if( opCodeInfo.genericOpCode == genericOpCode && opCodeInfo.indexType == indexType )
					return opCodeInfo;
			throw new AssertionError( genericOpCode + " " + indexType );
		}
	}

	private static final Map<Integer,OpCodeInfo> opCodeInfosFromOpCodes = Stream.of( //
		new OpCodeInfo( OpCode.ILOAD    /**/, OpCode.ILOAD  /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.ILOAD_0  /**/, OpCode.ILOAD  /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.ILOAD_1  /**/, OpCode.ILOAD  /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.ILOAD_2  /**/, OpCode.ILOAD  /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.ILOAD_3  /**/, OpCode.ILOAD  /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.LLOAD    /**/, OpCode.LLOAD  /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.LLOAD_0  /**/, OpCode.LLOAD  /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.LLOAD_1  /**/, OpCode.LLOAD  /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.LLOAD_2  /**/, OpCode.LLOAD  /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.LLOAD_3  /**/, OpCode.LLOAD  /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.FLOAD    /**/, OpCode.FLOAD  /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.FLOAD_0  /**/, OpCode.FLOAD  /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.FLOAD_1  /**/, OpCode.FLOAD  /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.FLOAD_2  /**/, OpCode.FLOAD  /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.FLOAD_3  /**/, OpCode.FLOAD  /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.DLOAD    /**/, OpCode.DLOAD  /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.DLOAD_0  /**/, OpCode.DLOAD  /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.DLOAD_1  /**/, OpCode.DLOAD  /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.DLOAD_2  /**/, OpCode.DLOAD  /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.DLOAD_3  /**/, OpCode.DLOAD  /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.ALOAD    /**/, OpCode.ALOAD  /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.ALOAD_0  /**/, OpCode.ALOAD  /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.ALOAD_1  /**/, OpCode.ALOAD  /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.ALOAD_2  /**/, OpCode.ALOAD  /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.ALOAD_3  /**/, OpCode.ALOAD  /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.ISTORE   /**/, OpCode.ISTORE /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.ISTORE_0 /**/, OpCode.ISTORE /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.ISTORE_1 /**/, OpCode.ISTORE /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.ISTORE_2 /**/, OpCode.ISTORE /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.ISTORE_3 /**/, OpCode.ISTORE /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.LSTORE   /**/, OpCode.LSTORE /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.LSTORE_0 /**/, OpCode.LSTORE /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.LSTORE_1 /**/, OpCode.LSTORE /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.LSTORE_2 /**/, OpCode.LSTORE /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.LSTORE_3 /**/, OpCode.LSTORE /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.FSTORE   /**/, OpCode.FSTORE /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.FSTORE_0 /**/, OpCode.FSTORE /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.FSTORE_1 /**/, OpCode.FSTORE /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.FSTORE_2 /**/, OpCode.FSTORE /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.FSTORE_3 /**/, OpCode.FSTORE /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.DSTORE   /**/, OpCode.DSTORE /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.DSTORE_0 /**/, OpCode.DSTORE /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.DSTORE_1 /**/, OpCode.DSTORE /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.DSTORE_2 /**/, OpCode.DSTORE /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.DSTORE_3 /**/, OpCode.DSTORE /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.ASTORE   /**/, OpCode.ASTORE /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.ASTORE_0 /**/, OpCode.ASTORE /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.ASTORE_1 /**/, OpCode.ASTORE /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.ASTORE_2 /**/, OpCode.ASTORE /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.ASTORE_3 /**/, OpCode.ASTORE /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.RET      /**/, OpCode.RET    /**/, IndexType.ByOperand )  //
	).collect( Collectors.toMap( opCodeInfo -> opCodeInfo.opCode, opCodeInfo -> opCodeInfo ) );

	public final int genericOpCode;
	public final int localVariableIndex; // TO-maybe-DO: introduce an abstraction of a local variable, then realize a reference to such an abstraction instead of the index here.

	private LocalVariableInstruction( int genericOpCode, int localVariableIndex )
	{
		super( groupTag_LocalVariable );
		assert genericOpCode == Kit.map.get( opCodeInfosFromOpCodes, genericOpCode ).opCode; //must use one of the generic opcodes.
		assert localVariableIndex >= 0;
		this.genericOpCode = genericOpCode;
		this.localVariableIndex = localVariableIndex;
	}

	@Deprecated @Override public LocalVariableInstruction asLocalVariableInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( genericOpCode ); }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		if( !Helpers.isUnsignedByte( localVariableIndex ) )
		{
			instructionWriter.writeUnsignedByte( OpCode.WIDE );
			instructionWriter.writeUnsignedByte( genericOpCode );
			instructionWriter.writeUnsignedShort( localVariableIndex );
		}
		else
		{
			IndexType indexType = IndexType.of( localVariableIndex, genericOpCode );
			int actualOpCode = OpCodeInfo.from( genericOpCode, indexType ).opCode;
			instructionWriter.writeUnsignedByte( actualOpCode );
			if( indexType == IndexType.ByOperand )
				instructionWriter.writeUnsignedByte( localVariableIndex );
		}
	}
}
