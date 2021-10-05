package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.InstructionWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class NewPrimitiveArrayInstruction extends Instruction
{
	public static NewPrimitiveArrayInstruction read( CodeAttributeReader codeAttributeReader, boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.NEWARRAY;
		Type type = Type.fromNumber( codeAttributeReader.readUnsignedByte() );
		return of( type );
	}

	public static NewPrimitiveArrayInstruction of( Type type )
	{
		return new NewPrimitiveArrayInstruction( type );
	}

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

	public final Type type;

	private NewPrimitiveArrayInstruction( Type type )
	{
		super( groupTag_NewPrimitiveArray );
		this.type = type;
	}

	@Deprecated @Override public NewPrimitiveArrayInstruction asNewPrimitiveArrayInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( OpCode.NEWARRAY ); }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		instructionWriter.writeUnsignedByte( OpCode.NEWARRAY );
		instructionWriter.writeUnsignedByte( type.number );
	}
}
