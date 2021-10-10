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
 * Represents the JVMS::CONSTANT_Double_info structure.
 *
 * @author michael.gr
 */
public final class DoubleValueConstant extends ValueConstant
{
	public static DoubleValueConstant read( BufferReader bufferReader, int constantTag )
	{
		assert constantTag == tag_Double;
		double value = bufferReader.readDouble();
		return of( value );
	}

	public static DoubleValueConstant of( double value )
	{
		return new DoubleValueConstant( value );
	}

	public final double value;

	private DoubleValueConstant( double value )
	{
		super( tag_Double );
		this.value = value;
	}

	@Deprecated @Override public DoubleValueConstant asDoubleValueConstant() { return this; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof DoubleValueConstant kin && equals( kin ); }
	public boolean equals( DoubleValueConstant other ) { return Double.doubleToLongBits( value ) == Double.doubleToLongBits( other.value ); }
	@Override public int hashCode() { return Objects.hash( tag, value ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return String.valueOf( value ); }

	@Override public void intern( Interner interner )
	{
		interner.intern( this );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingBootstrapPool bootstrapPool )
	{
		bufferWriter.writeUnsignedByte( tag );
		bufferWriter.writeDouble( value );
	}
}
