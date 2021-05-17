package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.Attributes;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;

/**
 * Represents the "Source File" {@link Attribute} of a java class file.
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 *
 * {@link ByteCodeType}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SourceFileAttribute extends Attribute
{
	public static final String NAME = "SourceFile";

	public static Optional<SourceFileAttribute> tryFrom( Attributes attributes )
	{
		return Attribute.tryFrom( attributes, NAME ).map( attribute -> attribute.asSourceFileAttribute() );
	}

	public final Utf8Constant valueConstant;

	public SourceFileAttribute( Runnable observer, String sourceFile )
	{
		super( observer, NAME );
		valueConstant = new Utf8Constant( sourceFile );
	}

	public SourceFileAttribute( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, NAME );
		valueConstant = constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
	}

	@Override public void intern( ConstantPool constantPool )
	{
		valueConstant.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		valueConstant.writeIndex( constantPool, bufferWriter );
	}

	@Override public Optional<SourceFileAttribute> tryAsSourceFileAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "value = " );
		valueConstant.toStringBuilder( builder );
	}

	public String getValue()
	{
		return valueConstant.getStringValue();
	}
}
