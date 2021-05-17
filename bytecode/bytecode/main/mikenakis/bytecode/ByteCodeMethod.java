package mikenakis.bytecode;

import mikenakis.bytecode.attributes.AnnotationDefaultAttribute;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.DeprecatedAttribute;
import mikenakis.bytecode.attributes.ExceptionsAttribute;
import mikenakis.bytecode.attributes.MethodParametersAttribute;
import mikenakis.bytecode.attributes.RuntimeInvisibleAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeInvisibleParameterAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeInvisibleTypeAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeVisibleAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeVisibleParameterAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeVisibleTypeAnnotationsAttribute;
import mikenakis.bytecode.attributes.SignatureAttribute;
import mikenakis.bytecode.attributes.SyntheticAttribute;
import mikenakis.bytecode.attributes.UnknownAttribute;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;

/**
 * Represents a method.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeMethod extends ByteCodeMember
{
	//@formatter:off
	public static final int ACC_PUBLIC       = 0x0001;
	public static final int ACC_PRIVATE      = 0x0002;
	public static final int ACC_PROTECTED    = 0x0004;
	public static final int ACC_STATIC       = 0x0008;
	public static final int ACC_FINAL        = 0x0010;
	public static final int ACC_SYNCHRONIZED = 0x0020;
	public static final int ACC_BRIDGE       = 0x0040;
	public static final int ACC_VARARGS      = 0x0080;
	public static final int ACC_NATIVE       = 0x0100;
	public static final int ACC_ABSTRACT     = 0x0400;
	public static final int ACC_STRICT       = 0x0800;
	public static final int ACC_SYNTHETIC    = 0x1000;
	//@formatter:on

	public ByteCodeMethod( Runnable observer, ByteCodeType declaringType, int accessFlags, String name, String descriptor )
	{
		super( observer, declaringType, accessFlags, name, descriptor );
	}

	public ByteCodeMethod( Runnable observer, ByteCodeType declaringType )
	{
		super( observer, declaringType, 0, "method", "()V" );
	}

	public ByteCodeMethod( Runnable observer, ByteCodeType declaringType, BufferReader bufferReader )
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
			case AnnotationDefaultAttribute.NAME                    : return new AnnotationDefaultAttribute                   ( this::markAsDirty, declaringType.constantPool, bufferReader );
			case CodeAttribute.NAME                                 : return new CodeAttribute                                ( this::markAsDirty, this, bufferReader );
			case DeprecatedAttribute.NAME                           : return new DeprecatedAttribute                          ( this::markAsDirty );
			case ExceptionsAttribute.NAME                           : return new ExceptionsAttribute                          ( this::markAsDirty, declaringType.constantPool, bufferReader );
			case MethodParametersAttribute.NAME                     : return new MethodParametersAttribute                    ( this::markAsDirty, declaringType.constantPool, bufferReader );
			case RuntimeInvisibleAnnotationsAttribute.NAME          : return new RuntimeInvisibleAnnotationsAttribute         ( this::markAsDirty, declaringType.constantPool, bufferReader );
			case RuntimeVisibleAnnotationsAttribute.NAME            : return new RuntimeVisibleAnnotationsAttribute           ( this::markAsDirty, declaringType.constantPool, bufferReader );
			case RuntimeInvisibleParameterAnnotationsAttribute.NAME : return new RuntimeInvisibleParameterAnnotationsAttribute( this::markAsDirty, declaringType.constantPool, bufferReader );
			case RuntimeVisibleParameterAnnotationsAttribute.NAME   : return new RuntimeVisibleParameterAnnotationsAttribute  ( this::markAsDirty, declaringType.constantPool, bufferReader );
			case RuntimeInvisibleTypeAnnotationsAttribute.NAME      : return new RuntimeInvisibleTypeAnnotationsAttribute     ( this::markAsDirty, declaringType.constantPool, bufferReader );
			case RuntimeVisibleTypeAnnotationsAttribute.NAME        : return new RuntimeVisibleTypeAnnotationsAttribute       ( this::markAsDirty, declaringType.constantPool, bufferReader );
			case SignatureAttribute.NAME                            : return new SignatureAttribute                           ( this::markAsDirty, declaringType.constantPool, bufferReader );
			case SyntheticAttribute.NAME                            : return new SyntheticAttribute                           ( this::markAsDirty );
			default                                                 : return new UnknownAttribute                             ( this::markAsDirty, attributeName, bufferReader );
			//@formatter:on
		}
	}

	public Optional<CodeAttribute> tryGetCodeAttribute()
	{
		return CodeAttribute.tryFrom( attributes );
	}

	public CodeAttribute getCodeAttribute()
	{
		return tryGetCodeAttribute().orElseThrow();
	}

	public Optional<MethodParametersAttribute> tryGetMethodParametersAttribute()
	{
		return MethodParametersAttribute.tryFrom( attributes );
	}

	public Optional<AnnotationDefaultAttribute> tryGetAnnotationDefaultAttribute()
	{
		return AnnotationDefaultAttribute.tryFrom( attributes );
	}
}
