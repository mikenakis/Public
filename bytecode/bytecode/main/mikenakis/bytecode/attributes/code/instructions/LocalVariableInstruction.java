package mikenakis.bytecode.attributes.code.instructions;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModel;
import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Helpers;

import java.util.Collection;
import java.util.Optional;

public final class LocalVariableInstruction extends Instruction
{
	public enum Kind
	{
		LOAD,
		STORE,
		RET
	}

	public enum Type
	{
		INT,
		LONG,
		FLOAT,
		DOUBLE,
		REF
	}

	private static class OpCodeInfo
	{
		public final int opCode;
		public final Kind kind;
		public final Type type;
		public final int flavor; //0, 1, 2, 3 for operandless, or -1 for operand.

		OpCodeInfo( int opCode, Kind kind, Type type, int flavor )
		{
			assert opCode == OpCode.RET ? type == Type.INT : type != null;
			assert flavor >= -1;
			assert flavor <= 3;
			this.opCode = opCode;
			this.kind = kind;
			this.type = type;
			this.flavor = flavor;
		}

		private static final OpCodeInfo[] opCodeInfos = {
			new OpCodeInfo( OpCode.ILOAD    /**/, Kind.LOAD  /**/, Type.INT    /**/, -1 ),
			new OpCodeInfo( OpCode.ILOAD_0  /**/, Kind.LOAD  /**/, Type.INT    /**/, 0 ),
			new OpCodeInfo( OpCode.ILOAD_1  /**/, Kind.LOAD  /**/, Type.INT    /**/, 1 ),
			new OpCodeInfo( OpCode.ILOAD_2  /**/, Kind.LOAD  /**/, Type.INT    /**/, 2 ),
			new OpCodeInfo( OpCode.ILOAD_3  /**/, Kind.LOAD  /**/, Type.INT    /**/, 3 ),
			new OpCodeInfo( OpCode.LLOAD    /**/, Kind.LOAD  /**/, Type.LONG   /**/, -1 ),
			new OpCodeInfo( OpCode.LLOAD_0  /**/, Kind.LOAD  /**/, Type.LONG   /**/, 0 ),
			new OpCodeInfo( OpCode.LLOAD_1  /**/, Kind.LOAD  /**/, Type.LONG   /**/, 1 ),
			new OpCodeInfo( OpCode.LLOAD_2  /**/, Kind.LOAD  /**/, Type.LONG   /**/, 2 ),
			new OpCodeInfo( OpCode.LLOAD_3  /**/, Kind.LOAD  /**/, Type.LONG   /**/, 3 ),
			new OpCodeInfo( OpCode.FLOAD    /**/, Kind.LOAD  /**/, Type.FLOAT  /**/, -1 ),
			new OpCodeInfo( OpCode.FLOAD_0  /**/, Kind.LOAD  /**/, Type.FLOAT  /**/, 0 ),
			new OpCodeInfo( OpCode.FLOAD_1  /**/, Kind.LOAD  /**/, Type.FLOAT  /**/, 1 ),
			new OpCodeInfo( OpCode.FLOAD_2  /**/, Kind.LOAD  /**/, Type.FLOAT  /**/, 2 ),
			new OpCodeInfo( OpCode.FLOAD_3  /**/, Kind.LOAD  /**/, Type.FLOAT  /**/, 3 ),
			new OpCodeInfo( OpCode.DLOAD    /**/, Kind.LOAD  /**/, Type.DOUBLE /**/, -1 ),
			new OpCodeInfo( OpCode.DLOAD_0  /**/, Kind.LOAD  /**/, Type.DOUBLE /**/, 0 ),
			new OpCodeInfo( OpCode.DLOAD_1  /**/, Kind.LOAD  /**/, Type.DOUBLE /**/, 1 ),
			new OpCodeInfo( OpCode.DLOAD_2  /**/, Kind.LOAD  /**/, Type.DOUBLE /**/, 2 ),
			new OpCodeInfo( OpCode.DLOAD_3  /**/, Kind.LOAD  /**/, Type.DOUBLE /**/, 3 ),
			new OpCodeInfo( OpCode.ALOAD    /**/, Kind.LOAD  /**/, Type.REF    /**/, -1 ),
			new OpCodeInfo( OpCode.ALOAD_0  /**/, Kind.LOAD  /**/, Type.REF    /**/, 0 ),
			new OpCodeInfo( OpCode.ALOAD_1  /**/, Kind.LOAD  /**/, Type.REF    /**/, 1 ),
			new OpCodeInfo( OpCode.ALOAD_2  /**/, Kind.LOAD  /**/, Type.REF    /**/, 2 ),
			new OpCodeInfo( OpCode.ALOAD_3  /**/, Kind.LOAD  /**/, Type.REF    /**/, 3 ),
			new OpCodeInfo( OpCode.ISTORE   /**/, Kind.STORE /**/, Type.INT    /**/, -1 ),
			new OpCodeInfo( OpCode.ISTORE_0 /**/, Kind.STORE /**/, Type.INT    /**/, 0 ),
			new OpCodeInfo( OpCode.ISTORE_1 /**/, Kind.STORE /**/, Type.INT    /**/, 1 ),
			new OpCodeInfo( OpCode.ISTORE_2 /**/, Kind.STORE /**/, Type.INT    /**/, 2 ),
			new OpCodeInfo( OpCode.ISTORE_3 /**/, Kind.STORE /**/, Type.INT    /**/, 3 ),
			new OpCodeInfo( OpCode.LSTORE   /**/, Kind.STORE /**/, Type.LONG   /**/, -1 ),
			new OpCodeInfo( OpCode.LSTORE_0 /**/, Kind.STORE /**/, Type.LONG   /**/, 0 ),
			new OpCodeInfo( OpCode.LSTORE_1 /**/, Kind.STORE /**/, Type.LONG   /**/, 1 ),
			new OpCodeInfo( OpCode.LSTORE_2 /**/, Kind.STORE /**/, Type.LONG   /**/, 2 ),
			new OpCodeInfo( OpCode.LSTORE_3 /**/, Kind.STORE /**/, Type.LONG   /**/, 3 ),
			new OpCodeInfo( OpCode.FSTORE   /**/, Kind.STORE /**/, Type.FLOAT  /**/, -1 ),
			new OpCodeInfo( OpCode.FSTORE_0 /**/, Kind.STORE /**/, Type.FLOAT  /**/, 0 ),
			new OpCodeInfo( OpCode.FSTORE_1 /**/, Kind.STORE /**/, Type.FLOAT  /**/, 1 ),
			new OpCodeInfo( OpCode.FSTORE_2 /**/, Kind.STORE /**/, Type.FLOAT  /**/, 2 ),
			new OpCodeInfo( OpCode.FSTORE_3 /**/, Kind.STORE /**/, Type.FLOAT  /**/, 3 ),
			new OpCodeInfo( OpCode.DSTORE   /**/, Kind.STORE /**/, Type.DOUBLE /**/, -1 ),
			new OpCodeInfo( OpCode.DSTORE_0 /**/, Kind.STORE /**/, Type.DOUBLE /**/, 0 ),
			new OpCodeInfo( OpCode.DSTORE_1 /**/, Kind.STORE /**/, Type.DOUBLE /**/, 1 ),
			new OpCodeInfo( OpCode.DSTORE_2 /**/, Kind.STORE /**/, Type.DOUBLE /**/, 2 ),
			new OpCodeInfo( OpCode.DSTORE_3 /**/, Kind.STORE /**/, Type.DOUBLE /**/, 3 ),
			new OpCodeInfo( OpCode.ASTORE   /**/, Kind.STORE /**/, Type.REF    /**/, -1 ),
			new OpCodeInfo( OpCode.ASTORE_0 /**/, Kind.STORE /**/, Type.REF    /**/, 0 ),
			new OpCodeInfo( OpCode.ASTORE_1 /**/, Kind.STORE /**/, Type.REF    /**/, 1 ),
			new OpCodeInfo( OpCode.ASTORE_2 /**/, Kind.STORE /**/, Type.REF    /**/, 2 ),
			new OpCodeInfo( OpCode.ASTORE_3 /**/, Kind.STORE /**/, Type.REF    /**/, 3 ),
			new OpCodeInfo( OpCode.RET      /**/, Kind.RET   /**/, Type.INT    /**/, -1 )
		};

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

		static Optional<OpCodeInfo> tryFromKindTypeAndFlavor( Kind kind, Type type, int flavor )
		{
			for( OpCodeInfo opCodeInfo : opCodeInfos )
				if( opCodeInfo.kind == kind && opCodeInfo.type == type && opCodeInfo.flavor == flavor )
					return Optional.of( opCodeInfo );
			return Optional.empty();
		}

		static OpCodeInfo fromKindTypeAndFlavor( Kind kind, Type type, int flavor )
		{
			return tryFromKindTypeAndFlavor( kind, type, flavor ).orElseThrow();
		}
	}

	public static final class Model extends InstructionModel
	{
		public final Kind kind;
		public final Type type;
		public final int flavor; //0, 1, 2, 3 for operandless, or -1 for operand.

		public Model( int opCode )
		{
			super( opCode );
			OpCodeInfo opCodeInfo = OpCodeInfo.fromOpCode( opCode );
			kind = opCodeInfo.kind;
			type = opCodeInfo.type;
			flavor = opCodeInfo.flavor;
		}

		public LocalVariableInstruction newInstruction( int localVariableIndex )
		{
			return new LocalVariableInstruction( this, localVariableIndex );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			return new LocalVariableInstruction( this, pc, wide, bufferReader );
		}
	}

	public final Model model;
	public final int localVariableIndex;

	private LocalVariableInstruction( Model model, int localVariableIndex )
	{
		super( Optional.empty() );
		assert localVariableIndex >= 0;
		this.model = model;
		this.localVariableIndex = localVariableIndex;
	}

	private LocalVariableInstruction( Model model, int pc, boolean wide, BufferReader bufferReader )
	{
		super( Optional.of( pc ) );
		assert model.flavor == -1 || !wide;
		this.model = model;
		localVariableIndex = model.flavor == -1 ? bufferReader.readUnsignedByteOrShort( wide ) : model.flavor;
		assert localVariableIndex >= 0;
	}

	@Override public Model getModel()
	{
		return model;
	}

	@Override public void intern( ConstantPool constantPool )
	{
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		int flavor = localVariableIndex <= 3 && model.opCode != OpCode.RET ? localVariableIndex : -1;
		OpCodeInfo opCodeInfo = OpCodeInfo.fromKindTypeAndFlavor( model.kind, model.type, flavor );
		assert opCodeInfo != null;
		boolean wide = !Helpers.isUnsignedByte( localVariableIndex );

		if( wide )
			bufferWriter.writeUnsignedByte( OpCode.WIDE );
		bufferWriter.writeUnsignedByte( opCodeInfo.opCode );
		if( flavor == -1 )
			bufferWriter.writeUnsignedByteOrShort( wide, localVariableIndex );
	}

	@Override public Optional<LocalVariableInstruction> tryAsLocalVariableInstruction()
	{
		return Optional.of( this );
	}
}
