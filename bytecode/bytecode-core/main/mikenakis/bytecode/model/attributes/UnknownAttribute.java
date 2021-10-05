package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an unknown {@link Attribute} of a java class file.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class UnknownAttribute extends Attribute
{
	public static UnknownAttribute of( Mutf8ValueConstant name, Buffer buffer )
	{
		return new UnknownAttribute( name, buffer );
	}

	private final Buffer buffer;

	private UnknownAttribute( Mutf8ValueConstant mutf8name, Buffer buffer )
	{
		super( mutf8name );
		this.buffer = buffer;
	}

	public Buffer buffer()
	{
		return buffer;
	}

	@Deprecated @Override public boolean isKnown() { return false; }
	@Deprecated @Override public UnknownAttribute asUnknownAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return buffer.length() + " bytes"; }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeBuffer( buffer );
	}
}
