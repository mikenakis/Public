package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an unknown {@link Attribute} of a java class file.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class UnknownAttribute extends Attribute
{
	public static UnknownAttribute of( Utf8Constant name, Buffer buffer )
	{
		return new UnknownAttribute( name, buffer );
	}

	private final Buffer buffer;

	private UnknownAttribute( Utf8Constant name, Buffer buffer )
	{
		super( new Kind( name.value() ) );
		this.buffer = buffer;
	}

	public Buffer buffer()
	{
		return buffer;
	}

	@Deprecated @Override public UnknownAttribute asUnknownAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return buffer.length() + " bytes";
	}
}