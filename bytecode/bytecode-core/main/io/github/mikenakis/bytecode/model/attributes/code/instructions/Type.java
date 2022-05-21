package io.github.mikenakis.bytecode.model.attributes.code.instructions;

import io.github.mikenakis.kit.Kit;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Type
{
	None    /**/( 0 ),
	Boolean /**/( 4 ),  // JVMS 6.5 "Instructions", Table 6.1, "T_BOOLEAN"
	Char    /**/( 5 ),  // JVMS 6.5 "Instructions", Table 6.1, "T_CHAR"
	Float   /**/( 6 ),  // JVMS 6.5 "Instructions", Table 6.1, "T_FLOAT"
	Double  /**/( 7 ),  // JVMS 6.5 "Instructions", Table 6.1, "T_DOUBLE"
	Byte    /**/( 8 ),  // JVMS 6.5 "Instructions", Table 6.1, "T_BYTE"
	Short   /**/( 9 ),  // JVMS 6.5 "Instructions", Table 6.1, "T_SHORT"
	Int     /**/( 10 ), // JVMS 6.5 "Instructions", Table 6.1, "T_INT"
	Long    /**/( 11 ), // JVMS 6.5 "Instructions", Table 6.1, "T_LONG"
	Address /**/( 12 ); // yet to be used.

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

	public boolean isPrimitive()
	{
		return this != Address;
	}
}
