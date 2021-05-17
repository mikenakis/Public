package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.Attributes;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Represents the "LineNumberTable" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link CodeAttribute}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LineNumberTableAttribute extends Attribute
{
	public static final String NAME = "LineNumberTable";

	public static Optional<LineNumberTableAttribute> tryFrom( Attributes attributes )
	{
		return Attribute.tryFrom( attributes, NAME ).map( attribute -> attribute.asLineNumberTableAttribute() );
	}

	public final ArrayList<LineNumber> entries = new ArrayList<>();

	public LineNumberTableAttribute( Runnable observer )
	{
		super( observer, NAME );
	}

	public LineNumberTableAttribute( Runnable observer, CodeAttribute codeAttribute, BufferReader bufferReader )
	{
		super( observer, NAME );
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		entries.clear();
		entries.ensureCapacity( count );
		for( int i = 0; i < count; i++ )
			entries.add( new LineNumber( codeAttribute, bufferReader ) );
	}

	@Override public void intern( ConstantPool constantPool )
	{
		/* nothing to do */
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( entries.size() );
		for( LineNumber entry : entries )
			entry.write( bufferWriter );
	}

	@Override public Optional<LineNumberTableAttribute> tryAsLineNumberTableAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( entries.size() ).append( " entries" );
	}
}
