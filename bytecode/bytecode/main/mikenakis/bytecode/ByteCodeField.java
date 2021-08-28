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
import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.EnumSet;
import java.util.Map;

/**
 * Represents a field.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeField extends ByteCodeMember
{
	public enum Access
	{
		Public    ,
		Private   ,
		Protected ,
		Static    ,
		Final     ,
		Volatile  ,
		Transient ,
		Synthetic ,
		Enum
	}

	public static final FlagEnum<Access> accessFlagEnum = FlagEnum.of( Access.class,
		Map.entry( Access.Public    , 0x0001 ), // ACC_PUBLIC    = 0x0001;
		Map.entry( Access.Private   , 0x0002 ), // ACC_PRIVATE   = 0x0002;
		Map.entry( Access.Protected , 0x0004 ), // ACC_PROTECTED = 0x0004;
		Map.entry( Access.Static    , 0x0008 ), // ACC_STATIC    = 0x0008;
		Map.entry( Access.Final     , 0x0010 ), // ACC_FINAL     = 0x0010;
		Map.entry( Access.Volatile  , 0x0040 ), // ACC_VOLATILE  = 0x0040;
		Map.entry( Access.Transient , 0x0080 ), // ACC_TRANSIENT = 0x0080;
		Map.entry( Access.Synthetic , 0x1000 ), // ACC_SYNTHETIC = 0x1000;
		Map.entry( Access.Enum      , 0x4000 ) ); // ACC_ENUM      = 0x4000;

	public static ByteCodeField read( Runnable observer, ByteCodeType declaringType, BufferReader bufferReader )
	{
		EnumSet<Access> access = accessFlagEnum.fromInt( bufferReader.readUnsignedShort() );
		Utf8Constant nameConstant = declaringType.constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		Utf8Constant descriptorConstant = declaringType.constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		Attributes attributes = Attributes.read( observer, declaringType.constantPool, (attributeName, r) -> newAttribute( observer, declaringType.constantPool, attributeName, r ), bufferReader );
		ByteCodeField byteCodeField = new ByteCodeField( observer, declaringType, access, nameConstant, descriptorConstant, attributes );
		return byteCodeField;
	}

	public final EnumSet<Access> access;

	public ByteCodeField( Runnable observer, ByteCodeType declaringType, EnumSet<Access> access, String name, String descriptor )
	{
		this( observer, declaringType, access, new Utf8Constant( name ), new Utf8Constant( descriptor ), new Attributes( observer ) );
	}

	private ByteCodeField( Runnable observer, ByteCodeType declaringType, EnumSet<Access> access, Utf8Constant nameConstant, Utf8Constant descriptorConstant, Attributes attributes )
	{
		super( observer, declaringType, nameConstant, descriptorConstant, attributes );
		this.access = access;
	}

	public ByteCodeField( Runnable observer, ByteCodeType declaringType )
	{
		this( observer, declaringType, EnumSet.noneOf( Access.class ), "field", "" );
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
			case ConstantValueAttribute.NAME:                   return new ConstantValueAttribute                  ( observer, constantPool, bufferReader );
			case DeprecatedAttribute.NAME:                      return new DeprecatedAttribute                     ( observer );
			case RuntimeInvisibleAnnotationsAttribute.NAME:     return new RuntimeInvisibleAnnotationsAttribute    ( observer, constantPool, bufferReader );
			case RuntimeVisibleAnnotationsAttribute.NAME:	    return new RuntimeVisibleAnnotationsAttribute      ( observer, constantPool, bufferReader );
			case RuntimeInvisibleTypeAnnotationsAttribute.NAME: return new RuntimeInvisibleTypeAnnotationsAttribute( observer, constantPool, bufferReader );
			case RuntimeVisibleTypeAnnotationsAttribute.NAME:   return new RuntimeVisibleTypeAnnotationsAttribute  ( observer, constantPool, bufferReader );
			case SignatureAttribute.NAME:			            return new SignatureAttribute                      ( observer, constantPool, bufferReader );
			case SyntheticAttribute.NAME:                       return new SyntheticAttribute                      ( observer );
			default:		                                    return new UnknownAttribute                        ( observer, attributeName, bufferReader );
			//@formatter:on
		}
	}
}
