package io.github.mikenakis.bytecode.model.constants.value;

import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingBootstrapPool;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.constants.ValueConstant;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_String_info structure.
 *
 * @author michael.gr
 */
public final class StringValueConstant extends ValueConstant
{
	public static StringValueConstant read( BufferReader bufferReader, ReadingConstantPool constantPool, int constantTag )
	{
		assert constantTag == tag_String;
		StringValueConstant stringConstant = new StringValueConstant();
		constantPool.setConstant( bufferReader.readUnsignedShort(), c -> stringConstant.setValueConstant( c.asMutf8ValueConstant() ) );
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

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingBootstrapPool bootstrapPool )
	{
		bufferWriter.writeUnsignedByte( tag );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( getValueConstant() ) );
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
