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
import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a method.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeMethod extends ByteCodeMember
{
	public enum Access
	{
		Public       ,
		Private      ,
		Protected    ,
		Static       ,
		Final        ,
		Synchronized ,
		Bridge       ,
		Varargs      ,
		Native       ,
		Abstract     ,
		Strict       ,
		Synthetic
	}

	public static final FlagEnum<Access> accessFlagEnum = FlagEnum.of( Access.class,
		Map.entry( Access.Public       , 0x0001 ), // ACC_PUBLIC       = 0x0001
		Map.entry( Access.Private      , 0x0002 ), // ACC_PRIVATE      = 0x0002
		Map.entry( Access.Protected    , 0x0004 ), // ACC_PROTECTED    = 0x0004
		Map.entry( Access.Static       , 0x0008 ), // ACC_STATIC       = 0x0008
		Map.entry( Access.Final        , 0x0010 ), // ACC_FINAL        = 0x0010
		Map.entry( Access.Synchronized , 0x0020 ), // ACC_SYNCHRONIZED = 0x0020
		Map.entry( Access.Bridge       , 0x0040 ), // ACC_BRIDGE       = 0x0040
		Map.entry( Access.Varargs      , 0x0080 ), // ACC_VARARGS      = 0x0080
		Map.entry( Access.Native       , 0x0100 ), // ACC_NATIVE       = 0x0100
		Map.entry( Access.Abstract     , 0x0400 ), // ACC_ABSTRACT     = 0x0400
		Map.entry( Access.Strict       , 0x0800 ), // ACC_STRICT       = 0x0800
		Map.entry( Access.Synthetic    , 0x1000 ) ); // ACC_SYNTHETIC    = 0x1000

	public static ByteCodeMethod of( Runnable observer, ByteCodeType declaringType, BufferReader bufferReader )
	{
		EnumSet<Access> access = accessFlagEnum.fromInt( bufferReader.readUnsignedShort() );
		Utf8Constant nameConstant = declaringType.constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		Utf8Constant descriptorConstant = declaringType.constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		Attributes attributes = Attributes.read( observer, declaringType.constantPool, (attributeName, r) -> newAttribute( observer, declaringType.constantPool, attributeName, r ), bufferReader );
		return new ByteCodeMethod( observer, declaringType, access, nameConstant, descriptorConstant, attributes );
	}

	public final EnumSet<Access> access;

	public ByteCodeMethod( Runnable observer, ByteCodeType declaringType )
	{
		this( observer, declaringType, EnumSet.noneOf( Access.class ), "method", "()V" );
	}

	public ByteCodeMethod( Runnable observer, ByteCodeType declaringType, EnumSet<Access> access, String name, String descriptor )
	{
		this( observer, declaringType, access, new Utf8Constant( name ), new Utf8Constant( descriptor ), new Attributes( observer ) );
	}

	private ByteCodeMethod( Runnable observer, ByteCodeType declaringType, EnumSet<Access> access, Utf8Constant nameConstant, Utf8Constant descriptorConstant, Attributes attributes )
	{
		super( observer, declaringType, nameConstant, descriptorConstant, attributes );
		this.access = access;
	}

	@Override public void intern()
	{
		super.intern();
		attributes.intern( declaringType.constantPool );
	}

	@Override public int accessAsInt()
	{
		return accessFlagEnum.toInt( access );
	}

	@Override public void accessToStringBuilder( StringBuilder stringBuilder )
	{
		accessFlagEnum.toStringBuilder( stringBuilder, access );
	}

	public void write( BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( accessFlagEnum.toInt( access ) );
		nameConstant.writeIndex( declaringType.constantPool, bufferWriter );
		descriptorConstant.writeIndex( declaringType.constantPool, bufferWriter );
		attributes.write( declaringType.constantPool, bufferWriter );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "accessFlags = 0x" ).append( Integer.toHexString( accessFlagEnum.toInt( access ) ) );
		builder.append( " (" );
		accessFlagEnum.toStringBuilder( builder, access );
		builder.append( ")" ) ;
		builder.append( ", name = " ).append( nameConstant.getStringValue() );
		builder.append( ", descriptor = " ).append( descriptorConstant.getStringValue() );
	}

	private static Attribute newAttribute( Runnable observer, ConstantPool constantPool, String attributeName, BufferReader bufferReader )
	{
		switch( attributeName )
		{
			//@formatter:off
			case AnnotationDefaultAttribute.NAME                    : return new AnnotationDefaultAttribute                   ( observer, constantPool, bufferReader );
			case CodeAttribute.NAME                                 : return new CodeAttribute                                ( observer, constantPool, bufferReader );
			case DeprecatedAttribute.NAME                           : return new DeprecatedAttribute                          ( observer );
			case ExceptionsAttribute.NAME                           : return new ExceptionsAttribute                          ( observer, constantPool, bufferReader );
			case MethodParametersAttribute.NAME                     : return new MethodParametersAttribute                    ( observer, constantPool, bufferReader );
			case RuntimeInvisibleAnnotationsAttribute.NAME          : return new RuntimeInvisibleAnnotationsAttribute         ( observer, constantPool, bufferReader );
			case RuntimeVisibleAnnotationsAttribute.NAME            : return new RuntimeVisibleAnnotationsAttribute           ( observer, constantPool, bufferReader );
			case RuntimeInvisibleParameterAnnotationsAttribute.NAME : return new RuntimeInvisibleParameterAnnotationsAttribute( observer, constantPool, bufferReader );
			case RuntimeVisibleParameterAnnotationsAttribute.NAME   : return new RuntimeVisibleParameterAnnotationsAttribute  ( observer, constantPool, bufferReader );
			case RuntimeInvisibleTypeAnnotationsAttribute.NAME      : return new RuntimeInvisibleTypeAnnotationsAttribute     ( observer, constantPool, bufferReader );
			case RuntimeVisibleTypeAnnotationsAttribute.NAME        : return new RuntimeVisibleTypeAnnotationsAttribute       ( observer, constantPool, bufferReader );
			case SignatureAttribute.NAME                            : return new SignatureAttribute                           ( observer, constantPool, bufferReader );
			case SyntheticAttribute.NAME                            : return new SyntheticAttribute                           ( observer );
			default                                                 : return new UnknownAttribute                             ( observer, attributeName, bufferReader );
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
