package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an unknown {@link Attribute} of a java class file.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class UnknownAttribute extends Attribute
{
	public static UnknownAttribute of( Mutf8Constant name, Buffer buffer )
	{
		return new UnknownAttribute( name, buffer );
	}

	private final Buffer buffer;

	private UnknownAttribute( Mutf8Constant mutf8name, Buffer buffer )
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
}
