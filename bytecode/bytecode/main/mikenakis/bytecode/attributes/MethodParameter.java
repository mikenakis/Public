package mikenakis.bytecode.attributes;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;

/**
 * Represents an entry of {@link MethodParametersAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodParameter extends Printable
{
	//@formatter:off
	public static final int ACC_FINAL     = 0x0010;
	public static final int ACC_SYNTHETIC = 0x1000;
	public static final int ACC_MANDATED  = 0x8000;
	//@formatter:on

	public final Utf8Constant nameConstant;
	public final int accessFlags;

	public MethodParameter( ConstantPool constantPool, Utf8Constant nameConstant, int accessFlags )
	{
		this.nameConstant = nameConstant;
		this.accessFlags = accessFlags;
	}

	public MethodParameter( ConstantPool constantPool, BufferReader bufferReader )
	{
		nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		accessFlags = bufferReader.readUnsignedShort();
	}

	public void intern( ConstantPool constantPool )
	{
		nameConstant.intern( constantPool );
	}

	public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		nameConstant.writeIndex( constantPool, bufferWriter );
		bufferWriter.writeUnsignedShort( accessFlags );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "accessFlags = 0x" ).append( Integer.toHexString( accessFlags ) );
		builder.append( ' ' );
		builder.append( nameConstant.getStringValue() );
	}
}
