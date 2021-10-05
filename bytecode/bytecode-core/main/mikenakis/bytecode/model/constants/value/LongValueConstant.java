package mikenakis.bytecode.model.constants.value;

import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.bytecode.reading.ConstantReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_Long_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LongValueConstant extends ValueConstant
{
	public static LongValueConstant read( ConstantReader constantReader, int constantTag )
	{
		assert constantTag == tag_Long;
		long value = constantReader.readLong();
		return of( value );
	}

	public static LongValueConstant of( long value )
	{
		return new LongValueConstant( value );
	}

	public final long value;

	private LongValueConstant( long value )
	{
		super( tag_Long );
		this.value = value;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return value + "L"; }
	@Deprecated @Override public LongValueConstant asLongValueConstant() { return this; }
	@Override public boolean equals( Object other ) { return other instanceof LongValueConstant kin && value == kin.value; }
	public boolean equals( LongValueConstant other ) { return value == other.value; }
	@Override public int hashCode() { return Objects.hash( tag, value ); }

	@Override public void intern( Interner interner )
	{
		interner.intern( this );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( tag );
		constantWriter.writeLong( value );
	}
}
