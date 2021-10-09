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

//TODO: introduce a 'type' parameter, get rid of all the different forms!
//ILOAD,LLOAD,FLOAD,DLOAD,ALOAD -> xLOAD(Type)
//ISTORE,LSTORE,FSTORE,DSTORE,ASTORE -> xSTORE(Type)
//public LocalVariableInstruction xLOAD( Type type, int localVariableIndex ) { return add( LocalVariableInstruction.of( OpCode.LOAD, type, localVariableIndex ) ); }
//public LocalVariableInstruction xSTORE( Type type, int localVariableIndex ) { return add( LocalVariableInstruction.of( OpCode.STORE, type, localVariableIndex ) ); }
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
		return of( opCodeInfo.operation, opCodeInfo.type, localVariableIndex );
	}

	public static LocalVariableInstruction of( Operation operation, Type type, int localVariableIndex )
	{
		return new LocalVariableInstruction( operation, type, localVariableIndex );
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

		public static IndexType of( int index )
		{
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

	public enum Operation
	{
		Load,
		Store,
		Ret
	}

	private static final class OpCodeInfo
	{
		final int opCode;
		final Operation operation;
		final Type type;
		final IndexType indexType;

		OpCodeInfo( int opCode, Operation operation, Type type, IndexType indexType )
		{
			this.opCode = opCode;
			this.operation = operation;
			this.type = type;
			this.indexType = indexType;
		}

		static OpCodeInfo from( Operation operation, Type type, IndexType indexType )
		{
			for( OpCodeInfo opCodeInfo : opCodeInfosFromOpCodes.values() )
				if( opCodeInfo.operation == operation && opCodeInfo.type == type && opCodeInfo.indexType == indexType )
					return opCodeInfo;
			throw new AssertionError( operation + " " + type + " " + indexType );
		}
	}

	private static final Map<Integer,OpCodeInfo> opCodeInfosFromOpCodes = Stream.of( //
		new OpCodeInfo( OpCode.ILOAD    /**/, Operation.Load  /**/, Type.Int     /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.ILOAD_0  /**/, Operation.Load  /**/, Type.Int     /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.ILOAD_1  /**/, Operation.Load  /**/, Type.Int     /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.ILOAD_2  /**/, Operation.Load  /**/, Type.Int     /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.ILOAD_3  /**/, Operation.Load  /**/, Type.Int     /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.LLOAD    /**/, Operation.Load  /**/, Type.Long    /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.LLOAD_0  /**/, Operation.Load  /**/, Type.Long    /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.LLOAD_1  /**/, Operation.Load  /**/, Type.Long    /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.LLOAD_2  /**/, Operation.Load  /**/, Type.Long    /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.LLOAD_3  /**/, Operation.Load  /**/, Type.Long    /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.FLOAD    /**/, Operation.Load  /**/, Type.Float   /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.FLOAD_0  /**/, Operation.Load  /**/, Type.Float   /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.FLOAD_1  /**/, Operation.Load  /**/, Type.Float   /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.FLOAD_2  /**/, Operation.Load  /**/, Type.Float   /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.FLOAD_3  /**/, Operation.Load  /**/, Type.Float   /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.DLOAD    /**/, Operation.Load  /**/, Type.Double  /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.DLOAD_0  /**/, Operation.Load  /**/, Type.Double  /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.DLOAD_1  /**/, Operation.Load  /**/, Type.Double  /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.DLOAD_2  /**/, Operation.Load  /**/, Type.Double  /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.DLOAD_3  /**/, Operation.Load  /**/, Type.Double  /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.ALOAD    /**/, Operation.Load  /**/, Type.Address /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.ALOAD_0  /**/, Operation.Load  /**/, Type.Address /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.ALOAD_1  /**/, Operation.Load  /**/, Type.Address /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.ALOAD_2  /**/, Operation.Load  /**/, Type.Address /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.ALOAD_3  /**/, Operation.Load  /**/, Type.Address /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.ISTORE   /**/, Operation.Store /**/, Type.Int     /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.ISTORE_0 /**/, Operation.Store /**/, Type.Int     /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.ISTORE_1 /**/, Operation.Store /**/, Type.Int     /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.ISTORE_2 /**/, Operation.Store /**/, Type.Int     /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.ISTORE_3 /**/, Operation.Store /**/, Type.Int     /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.LSTORE   /**/, Operation.Store /**/, Type.Long    /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.LSTORE_0 /**/, Operation.Store /**/, Type.Long    /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.LSTORE_1 /**/, Operation.Store /**/, Type.Long    /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.LSTORE_2 /**/, Operation.Store /**/, Type.Long    /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.LSTORE_3 /**/, Operation.Store /**/, Type.Long    /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.FSTORE   /**/, Operation.Store /**/, Type.Float   /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.FSTORE_0 /**/, Operation.Store /**/, Type.Float   /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.FSTORE_1 /**/, Operation.Store /**/, Type.Float   /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.FSTORE_2 /**/, Operation.Store /**/, Type.Float   /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.FSTORE_3 /**/, Operation.Store /**/, Type.Float   /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.DSTORE   /**/, Operation.Store /**/, Type.Double  /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.DSTORE_0 /**/, Operation.Store /**/, Type.Double  /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.DSTORE_1 /**/, Operation.Store /**/, Type.Double  /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.DSTORE_2 /**/, Operation.Store /**/, Type.Double  /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.DSTORE_3 /**/, Operation.Store /**/, Type.Double  /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.ASTORE   /**/, Operation.Store /**/, Type.Address /**/, IndexType.ByOperand ), //
		new OpCodeInfo( OpCode.ASTORE_0 /**/, Operation.Store /**/, Type.Address /**/, IndexType.AtIndex0 ),  //
		new OpCodeInfo( OpCode.ASTORE_1 /**/, Operation.Store /**/, Type.Address /**/, IndexType.AtIndex1 ),  //
		new OpCodeInfo( OpCode.ASTORE_2 /**/, Operation.Store /**/, Type.Address /**/, IndexType.AtIndex2 ),  //
		new OpCodeInfo( OpCode.ASTORE_3 /**/, Operation.Store /**/, Type.Address /**/, IndexType.AtIndex3 ),  //
		new OpCodeInfo( OpCode.RET      /**/, Operation.Ret    /**/, Type.None    /**/, IndexType.ByOperand )  //
	).collect( Collectors.toMap( opCodeInfo -> opCodeInfo.opCode, opCodeInfo -> opCodeInfo ) );

	public final Operation operation;
	public final Type type;
	public final int localVariableIndex; // TO-maybe-DO: introduce an abstraction of a local variable, then realize a reference to such an abstraction instead of the index here.

	private LocalVariableInstruction( Operation operation, Type type, int localVariableIndex )
	{
		super( groupTag_LocalVariable );
		assert operation == Operation.Ret ? type == Type.None : (type == Type.Int || type == Type.Long || type == Type.Float || type == Type.Double || type == Type.Address);
		assert localVariableIndex >= 0;
		this.operation = operation;
		this.type = type;
		this.localVariableIndex = localVariableIndex;
	}

	public int opCode() { return getGenericOpCode( operation, type ); }
	@Deprecated @Override public LocalVariableInstruction asLocalVariableInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( opCode() ); }

	private static int getGenericOpCode( Operation operation, Type type )
	{
		return OpCodeInfo.from( operation, type, IndexType.ByOperand ).opCode;
	}

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		if( !Helpers.isUnsignedByte( localVariableIndex ) )
		{
			instructionWriter.writeUnsignedByte( OpCode.WIDE );
			instructionWriter.writeUnsignedByte( OpCodeInfo.from( operation, type, IndexType.ByOperand ).opCode );
			instructionWriter.writeUnsignedShort( localVariableIndex );
		}
		else
		{
			int genericOpCode = opCode();
			IndexType indexType = genericOpCode == OpCode.RET ? IndexType.ByOperand : IndexType.of( localVariableIndex );
			int actualOpCode = OpCodeInfo.from( operation, type, indexType ).opCode;
			instructionWriter.writeUnsignedByte( actualOpCode );
			if( indexType == IndexType.ByOperand )
				instructionWriter.writeUnsignedByte( localVariableIndex );
		}
	}
}
