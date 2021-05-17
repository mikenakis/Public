package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.Attributes;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the "MethodParameters" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodParametersAttribute extends Attribute
{
	public static final String NAME = "MethodParameters";

	public static Optional<MethodParametersAttribute> tryFrom( Attributes attributes )
	{
		return Attribute.tryFrom( attributes, NAME ).map( attribute -> attribute.asMethodParametersAttribute() );
	}

	public final List<MethodParameter> entries;

	public MethodParametersAttribute( Runnable observer )
	{
		super( observer, NAME );
		entries = new ArrayList<>();
	}

	public MethodParametersAttribute( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, NAME );
		int count = bufferReader.readUnsignedByte();
		assert count > 0;
		entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			MethodParameter entry = new MethodParameter( constantPool, bufferReader );
			entries.add( entry );
		}
	}

	@Override public void intern( ConstantPool constantPool )
	{
		for( MethodParameter entry : entries )
			entry.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedByte( entries.size() );
		for( MethodParameter entry : entries )
			entry.write( constantPool, bufferWriter );
	}

	@Override public Optional<MethodParametersAttribute> tryAsMethodParametersAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( entries.size() ).append( " entries" );
	}
}
