package mikenakis.bytecode.model.constants.value;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingBootstrapPool;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_Float_info structure.
 *
 * @author michael.gr
 */
public final class FloatValueConstant extends ValueConstant
{
	public static FloatValueConstant read( BufferReader bufferReader, int constantTag )
	{
		assert constantTag == tag_Float;
		float value = bufferReader.readFloat();
		return of( value );
	}

	public static FloatValueConstant of( float value )
	{
		return new FloatValueConstant( value );
	}

	public final float value;

	private FloatValueConstant( float value )
	{
		super( tag_Float );
		this.value = value;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return value + "f"; }
	@Deprecated @Override public FloatValueConstant asFloatValueConstant() { return this; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof FloatValueConstant kin && equals( kin ); }
	public boolean equals( FloatValueConstant otherFloatConstant ) { return Float.floatToIntBits( value ) == Float.floatToIntBits( otherFloatConstant.value ); }
	@Override public int hashCode() { return Objects.hash( tag, value ); }

	@Override public void intern( Interner interner )
	{
		interner.intern( this );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingBootstrapPool bootstrapPool )
	{
		bufferWriter.writeUnsignedByte( tag );
		bufferWriter.writeFloat( value );
	}
}
