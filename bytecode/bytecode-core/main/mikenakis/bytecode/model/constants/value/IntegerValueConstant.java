package mikenakis.bytecode.model.constants.value;

import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.bytecode.reading.ConstantReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_Integer_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class IntegerValueConstant extends ValueConstant
{
	public static IntegerValueConstant read( ConstantReader constantReader, int constantTag )
	{
		assert constantTag == tag_Integer;
		int value = constantReader.readInt();
		return of( value );
	}

	public static IntegerValueConstant of( int value )
	{
		return new IntegerValueConstant( value );
	}

	public final int value;

	private IntegerValueConstant( int value )
	{
		super( tag_Integer );
		this.value = value;
	}

	@Deprecated @Override public IntegerValueConstant asIntegerValueConstant() { return this; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof IntegerValueConstant kin && equals( kin ); }
	public boolean equals( IntegerValueConstant other ) { return value == other.value; }
	@Override public int hashCode() { return Objects.hash( tag, value ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return String.valueOf( value ); }

	@Override public void intern( Interner interner )
	{
		interner.intern( this );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( tag );
		constantWriter.writeInt( value );
	}
}
