package mikenakis.bytecode.attributes.code.instructions;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModel;
import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.kit.Kit;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class NewPrimitiveArrayInstruction extends Instruction
{
	public enum Type //(JVMS paragraph 6.5)
	{
		BOOLEAN /**/( 4 ),  // JVMS::T_BOOLEAN
		CHAR    /**/( 5 ),  // JVMS::T_CHAR
		FLOAT   /**/( 6 ),  // JVMS::T_FLOAT
		DOUBLE  /**/( 7 ),  // JVMS::T_DOUBLE
		BYTE    /**/( 8 ),  // JVMS::T_BYTE
		SHORT   /**/( 9 ),  // JVMS::T_SHORT
		INT     /**/( 10 ), // JVMS::T_INT
		LONG    /**/( 11 ); // JVMS::T_LONG

		public static final List<Type> valueList = List.of( values() );
		private static final Map<Integer,Type> valuesFromNumbers = valueList.stream().collect( Collectors.toMap( v -> v.number, v -> v ) );

		public static Type tryFromNumber( int number )
		{
			return Kit.map.get( valuesFromNumbers, number );
		}

		public static Type fromNumber( int number )
		{
			Type result = tryFromNumber( number );
			assert result != null;
			return result;
		}

		public final int number;

		Type( int number )
		{
			this.number = number;
		}
	}

	public static final class Model extends InstructionModel
	{
		public Model()
		{
			super( OpCode.NEWARRAY );
		}

		public NewPrimitiveArrayInstruction newInstruction( Type type )
		{
			return new NewPrimitiveArrayInstruction( this, type );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			assert !wide;
			return new NewPrimitiveArrayInstruction( this, pc, bufferReader );
		}
	}

	public final Model model;
	public final int type;

	private NewPrimitiveArrayInstruction( Model model, Type type )
	{
		super( Optional.empty() );
		this.model = model;
		this.type = type.number;
	}

	private NewPrimitiveArrayInstruction( Model model, int pc, BufferReader bufferReader )
	{
		super( Optional.of( pc ) );
		this.model = model;
		type = bufferReader.readUnsignedByte();
		assert Type.tryFromNumber( type ) != null;
	}

	@Override public Model getModel()
	{
		return model;
	}

	@Override public void intern( ConstantPool constantPool )
	{
		/* nothing to do */
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedByte( model.opCode );
		bufferWriter.writeUnsignedByte( type );
	}

	@Override public Optional<NewPrimitiveArrayInstruction> tryAsNewPrimitiveArrayInstruction()
	{
		return Optional.of( this );
	}
}
