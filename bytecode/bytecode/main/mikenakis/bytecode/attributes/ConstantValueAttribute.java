package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.ByteCodeField;
import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.constants.IntegerConstant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;

/**
 * Represents the "ConstantValue" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeField}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ConstantValueAttribute extends Attribute
{
	public static final String NAME = "ConstantValue";

	public final Constant valueConstant;

	public ConstantValueAttribute( Runnable observer, Constant valueConstant )
	{
		super( observer, NAME );
		this.valueConstant = valueConstant;
	}

	public ConstantValueAttribute( Runnable observer )
	{
		super( observer, NAME );
		valueConstant = new IntegerConstant( 0 );
	}

	public ConstantValueAttribute( Runnable observer, ByteCodeField field, BufferReader bufferReader )
	{
		super( observer, NAME );
		valueConstant = field.declaringType.constantPool.readIndexAndGetConstant( bufferReader );
	}

	@Override public void intern( ConstantPool constantPool )
	{
		valueConstant.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		valueConstant.writeIndex( constantPool, bufferWriter );
	}

	@Override public Optional<ConstantValueAttribute> tryAsConstantValueAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "value = " );
		valueConstant.toStringBuilder( builder );
	}
}
