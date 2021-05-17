package mikenakis.bytecode;

import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;

/**
 * Represents the kind of a {@link Constant}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ConstantKind
{
	public final int tag;
	public final String name;

	protected ConstantKind( int tag, String name )
	{
		this.tag = tag;
		this.name = name;
	}

	public abstract Buffer readBuffer( BufferReader bufferReader );

	public abstract Constant parse( ConstantPool constantPool, BufferReader bufferReader );

	public final Constant parse( ConstantPool constantPool, Buffer buffer )
	{
		BufferReader constantBufferReader = new BufferReader( buffer );
		Constant constant = parse( constantPool, constantBufferReader );
		assert constantBufferReader.isAtEnd();
		return constant;
	}

	@Override public final String toString()
	{
		return "tag " + tag + " " + name;
	}
}
