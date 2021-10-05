package mikenakis.bytecode.model.constants.value;

import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.bytecode.reading.ConstantReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_String_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class StringValueConstant extends ValueConstant
{
	public static StringValueConstant read( ConstantReader constantReader, int constantTag )
	{
		assert constantTag == tag_String;
		StringValueConstant stringConstant = new StringValueConstant();
		constantReader.readIndexAndSetConstant( c -> stringConstant.setValueConstant( c.asMutf8ValueConstant() ) );
		return stringConstant;
	}

	public static StringValueConstant of( String value )
	{
		StringValueConstant stringConstant = new StringValueConstant();
		stringConstant.setValueConstant( Mutf8ValueConstant.of( value ) );
		return stringConstant;
	}

	@SuppressWarnings( "FieldNamingConvention" ) private Mutf8ValueConstant _valueConstant;

	public StringValueConstant()
	{
		super( tag_String );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "value = " + (_valueConstant == null ? "(uninitialized)" : _valueConstant.toString()); }
	@Deprecated @Override public StringValueConstant asStringValueConstant() { return this; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof StringValueConstant kin && equals( kin ); }
	public boolean equals( StringValueConstant other ) { return getValueConstant().equals( other.getValueConstant() ); }
	@Override public int hashCode() { return Objects.hash( tag, getValueConstant() ); }
	public String stringValue() { return getValueConstant().stringValue(); }

	@Override public void intern( Interner interner )
	{
		interner.intern( this );
		getValueConstant().intern( interner );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( tag );
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( getValueConstant() ) );
	}

	private Mutf8ValueConstant getValueConstant()
	{
		assert _valueConstant != null;
		return _valueConstant;
	}

	private void setValueConstant( Mutf8ValueConstant valueConstant )
	{
		assert _valueConstant == null;
		assert valueConstant != null;
		_valueConstant = valueConstant;
	}
}
