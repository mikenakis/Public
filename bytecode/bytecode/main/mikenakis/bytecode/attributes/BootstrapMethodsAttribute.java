package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.Attributes;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the "BootstrapMethods" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class BootstrapMethodsAttribute extends Attribute
{
	public static final String NAME = "BootstrapMethods";

	public static Optional<BootstrapMethodsAttribute> tryFrom( Attributes attributes )
	{
		return Attribute.tryFrom( attributes, NAME ).map( attribute -> attribute.asBootstrapMethodsAttribute() );
	}

	public final List<BootstrapMethod> entries;

	public BootstrapMethodsAttribute( Runnable observer, ConstantPool constantPool )
	{
		super( observer, NAME );
		entries = new ArrayList<>();
	}

	public BootstrapMethodsAttribute( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, NAME );
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			BootstrapMethod entry = new BootstrapMethod( constantPool, bufferReader );
			entries.add( entry );
		}
	}

	@Override public void intern( ConstantPool constantPool )
	{
		for( BootstrapMethod entry : entries )
			entry.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( entries.size() );
		for( BootstrapMethod entry : entries )
			entry.write( constantPool, bufferWriter );
	}

	@Override public Optional<BootstrapMethodsAttribute> tryAsBootstrapMethodsAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( entries.size() ).append( " entries" );
	}

	public BootstrapMethod getBootstrapMethodByIndex( int index )
	{
		return entries.get( index );
	}

	public int getIndexOfBootstrapMethod( BootstrapMethod entry )
	{
		int index = entries.indexOf( entry );
		if( index == -1 )
		{
			index = entries.size();
			entries.add( entry );
		}
		return index;
	}
}
