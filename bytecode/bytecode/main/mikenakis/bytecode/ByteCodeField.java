package mikenakis.bytecode;

import mikenakis.bytecode.attributes.ConstantValueAttribute;
import mikenakis.bytecode.attributes.DeprecatedAttribute;
import mikenakis.bytecode.attributes.RuntimeInvisibleAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeInvisibleTypeAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeVisibleAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeVisibleTypeAnnotationsAttribute;
import mikenakis.bytecode.attributes.SignatureAttribute;
import mikenakis.bytecode.attributes.SyntheticAttribute;
import mikenakis.bytecode.attributes.UnknownAttribute;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

/**
 * Represents a field.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeField extends ByteCodeMember
{
	/* Access flags */
	public static final int ACC_PUBLIC = 0x0001;
	public static final int ACC_PRIVATE = 0x0002;
	public static final int ACC_PROTECTED = 0x0004;
	public static final int ACC_STATIC = 0x0008;
	public static final int ACC_FINAL = 0x0010;
	public static final int ACC_VOLATILE = 0x0040;
	public static final int ACC_TRANSIENT = 0x0080;
	public static final int ACC_SYNTHETIC = 0x1000;
	public static final int ACC_ENUM = 0x4000;

	public ByteCodeField( Runnable observer, ByteCodeType declaringType )
	{
		super( observer, declaringType, 0, "field", "" );
	}

	public ByteCodeField( Runnable observer, ByteCodeType declaringType, int accessFlags, String name, String descriptor )
	{
		super( observer, declaringType, accessFlags, name, descriptor );
	}

	public ByteCodeField( Runnable observer, ByteCodeType declaringType, BufferReader bufferReader )
	{
		super( observer, declaringType, bufferReader );
	}

	@Override public void intern()
	{
		super.intern();
		attributes.intern( declaringType.constantPool );
	}

	@Override public void write( BufferWriter bufferWriter )
	{
		super.write( bufferWriter );
		attributes.write( declaringType.constantPool, bufferWriter );
	}

	@Override protected Attribute newAttribute( String attributeName, BufferReader bufferReader )
	{
		switch( attributeName )
		{
			//@formatter:off
			case ConstantValueAttribute.NAME:                   return new ConstantValueAttribute                  ( this::markAsDirty, this, bufferReader );
			case DeprecatedAttribute.NAME:                      return new DeprecatedAttribute                     ( this::markAsDirty );
			case RuntimeInvisibleAnnotationsAttribute.NAME:     return new RuntimeInvisibleAnnotationsAttribute    ( this::markAsDirty, declaringType.constantPool, bufferReader );
			case RuntimeVisibleAnnotationsAttribute.NAME:	    return new RuntimeVisibleAnnotationsAttribute      ( this::markAsDirty, declaringType.constantPool, bufferReader );
			case RuntimeInvisibleTypeAnnotationsAttribute.NAME: return new RuntimeInvisibleTypeAnnotationsAttribute( this::markAsDirty, declaringType.constantPool, bufferReader );
			case RuntimeVisibleTypeAnnotationsAttribute.NAME:   return new RuntimeVisibleTypeAnnotationsAttribute  ( this::markAsDirty, declaringType.constantPool, bufferReader );
			case SignatureAttribute.NAME:			            return new SignatureAttribute                      ( this::markAsDirty, declaringType.constantPool, bufferReader );
			case SyntheticAttribute.NAME:                       return new SyntheticAttribute                      ( this::markAsDirty );
			default:		                                    return new UnknownAttribute                        ( this::markAsDirty, attributeName, bufferReader );
			//@formatter:on
		}
	}
}
