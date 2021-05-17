package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.ByteCodeAnnotation;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.ObservableList;
import mikenakis.bytecode.kit.Printable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Common base class for the "RuntimeVisibleParameterAnnotations" and "RuntimeInvisibleParameterAnnotations" {@link Attribute}s of a java class file.
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 *
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ParameterAnnotationsAttribute extends Attribute
{
	public final List<Entry> entries;

	protected ParameterAnnotationsAttribute( Runnable observer, String name )
	{
		super( observer, name );
		entries = new ArrayList<>();
	}

	protected ParameterAnnotationsAttribute( Runnable observer, ConstantPool constantPool, String name, BufferReader bufferReader )
	{
		super( observer, name );
		int count = bufferReader.readUnsignedByte();
		assert count > 0;
		entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Entry entry = new Entry( this::markAsDirty, constantPool, bufferReader );
			entries.add( entry );
		}
	}

	@Override public void intern( ConstantPool constantPool )
	{
		for( Entry entry : entries )
			entry.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedByte( entries.size() );
		for( Entry entry : entries )
			entry.write( constantPool, bufferWriter );
	}

	@Override public Optional<ParameterAnnotationsAttribute> tryAsParameterAnnotationsAttribute()
	{
		return Optional.of( this );
	}

	@Override public final void toStringBuilder( StringBuilder builder )
	{
		builder.append( name ).append( ' ' ).append( entries.size() ).append( " entries" );
	}

	public static final class Entry extends Printable
	{
		private final Runnable observer;
		public final List<ByteCodeAnnotation> annotations;

		public Entry( Runnable observer )
		{
			this.observer = observer;
			annotations = newList( 0 );
		}

		public Entry( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
		{
			this.observer = observer;
			int count = bufferReader.readUnsignedShort();
			annotations = newList( count );
			for( int i = 0; i < count; i++ )
			{
				ByteCodeAnnotation byteCodeAnnotation = new ByteCodeAnnotation( this::markAsDirty, constantPool, bufferReader );
				annotations.add( byteCodeAnnotation );
			}
		}

		public void intern( ConstantPool constantPool )
		{
			for( ByteCodeAnnotation annotation : annotations )
				annotation.intern( constantPool );
		}

		public void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
			bufferWriter.writeUnsignedShort( annotations.size() );
			for( ByteCodeAnnotation annotation : annotations )
				annotation.write( constantPool, bufferWriter );
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( annotations.size() ).append( " entries" );
		}

		private <E> List<E> newList( int capacity )
		{
			List<E> list = new ArrayList<>( capacity );
			return new ObservableList<>( list, this::markAsDirty );
		}

		private void markAsDirty()
		{
			observer.run();
		}
	}
}
