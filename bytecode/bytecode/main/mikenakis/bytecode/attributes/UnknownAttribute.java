package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;

/**
 * Represents an unknown {@link Attribute} of a java class file.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class UnknownAttribute extends Attribute
{
	public final Buffer buffer;

	public UnknownAttribute( Runnable observer, String name, Buffer buffer )
	{
		super( observer, name );
		this.buffer = buffer;
	}

	public UnknownAttribute( Runnable observer, String name, BufferReader bufferReader )
	{
		super( observer, name );
		buffer = bufferReader.readBuffer();
	}

	@Override public void intern( ConstantPool constantPool )
	{
		/* nothing to do */
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeBuffer( buffer );
	}

	@Override public Optional<UnknownAttribute> tryAsUnknownAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( buffer.getLength() ).append( " bytes" );
	}
}
