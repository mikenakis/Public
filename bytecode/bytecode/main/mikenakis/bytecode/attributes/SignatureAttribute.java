package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.ByteCodeField;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;

/**
 * Represents the "Signature" {@link Attribute} of a java class file.
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 *
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SignatureAttribute extends Attribute
{
	public static final String NAME = "Signature";

	public final Utf8Constant signatureConstant;

	public SignatureAttribute( Runnable observer, String signature )
	{
		super( observer, NAME );
		signatureConstant = new Utf8Constant( signature );
	}

	public SignatureAttribute( Runnable observer )
	{
		this( observer, "" );
	}

	public SignatureAttribute( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, NAME );
		signatureConstant = constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
	}

	@Override public void intern( ConstantPool constantPool )
	{
		signatureConstant.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		signatureConstant.writeIndex( constantPool, bufferWriter );
	}

	@Override public Optional<SignatureAttribute> tryAsSignatureAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "signature = " ).append( signatureConstant.getStringValue() );
	}

	public String getSignature()
	{
		return signatureConstant.getStringValue();
	}
}
