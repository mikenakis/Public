package mikenakis.bytecode;

import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Represents a Member. (Field or Method.)
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ByteCodeMember extends Printable implements Annotatable
{
	private final Runnable observer;
	public final ByteCodeType declaringType;
	public final int accessFlags;
	public final Utf8Constant nameConstant;
	public final Utf8Constant descriptorConstant;
	public final Attributes attributes;

	protected ByteCodeMember( Runnable observer, ByteCodeType declaringType, int accessFlags, String memberName, String descriptor )
	{
		this.observer = observer;
		this.declaringType = declaringType;
		this.accessFlags = accessFlags;
		nameConstant = new Utf8Constant( memberName );
		descriptorConstant = new Utf8Constant( descriptor );
		attributes = new Attributes( this::markAsDirty );
	}

	protected ByteCodeMember( Runnable observer, ByteCodeType declaringType, BufferReader bufferReader )
	{
		this.observer = observer;
		this.declaringType = declaringType;
		accessFlags = bufferReader.readUnsignedShort();
		nameConstant = declaringType.constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		descriptorConstant = declaringType.constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		attributes = new Attributes( this::markAsDirty, declaringType.constantPool, this::newAttribute, bufferReader );
	}

	@OverridingMethodsMustInvokeSuper
	protected void intern()
	{
		nameConstant.intern( declaringType.constantPool );
		descriptorConstant.intern( declaringType.constantPool );
	}

	@OverridingMethodsMustInvokeSuper
	protected void write( BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( accessFlags );
		nameConstant.writeIndex( declaringType.constantPool, bufferWriter );
		descriptorConstant.writeIndex( declaringType.constantPool, bufferWriter );
	}

	public boolean hasAccessFlag( int flag )
	{
		assert Integer.bitCount( flag ) == 1;
		return hasAnyAccessFlag( flag );
	}

	public boolean hasAnyAccessFlag( int flag )
	{
		assert flag != 0;
		return (accessFlags & flag) != 0;
	}

	public String getName()
	{
		return nameConstant.getStringValue();
	}

	public String getDescriptorString()
	{
		return descriptorConstant.getStringValue();
	}

	public Descriptor getDescriptor()
	{
		return Descriptor.from( descriptorConstant.getStringValue() );
	}

	protected abstract Attribute newAttribute( String attributeName, BufferReader bufferReader );

	@Override public Attributes getAttributes()
	{
		return attributes;
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "accessFlags = 0x" ).append( Integer.toHexString( accessFlags ) );
		builder.append( ", name = " ).append( nameConstant.getStringValue() );
		builder.append( ", descriptor = " ).append( descriptorConstant.getStringValue() );
	}

	@Override public void markAsDirty()
	{
		observer.run();
	}
}
