package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the "InnerClasses" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InnerClassesAttribute extends Attribute
{
	public static final String NAME = "InnerClasses";

	public final List<InnerClass> innerClasses;

	public InnerClassesAttribute( Runnable observer )
	{
		super( observer, NAME );
		innerClasses = new ArrayList<>();
	}

	public InnerClassesAttribute( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, NAME );
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		innerClasses = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			InnerClass innerClass = new InnerClass( constantPool, bufferReader );
			innerClasses.add( innerClass );
		}
	}

	@Override public void intern( ConstantPool constantPool )
	{
		for( InnerClass innerClass : innerClasses )
			innerClass.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( innerClasses.size() );
		for( InnerClass innerClass : innerClasses )
			innerClass.write( constantPool, bufferWriter );
	}

	@Override public Optional<InnerClassesAttribute> tryAsInnerClassesAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( innerClasses.size() ).append( " entries" );
	}
}
