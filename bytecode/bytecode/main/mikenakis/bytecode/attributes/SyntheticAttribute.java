package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.ByteCodeField;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;

/**
 * Represents the "Synthetic" {@link Attribute} of a java class file.
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 *
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SyntheticAttribute extends Attribute
{
	public static final String NAME = "Synthetic";

	public SyntheticAttribute( Runnable observer )
	{
		super( observer, NAME );
	}

	@Override public void intern( ConstantPool constantPool )
	{
		/* nothing to do */
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		/* nothing to do */
	}

	@Override public Optional<SyntheticAttribute> tryAsSyntheticAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		/* nothing to do */
	}
}
