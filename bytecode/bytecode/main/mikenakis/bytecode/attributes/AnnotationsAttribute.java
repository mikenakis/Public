package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.ByteCodeAnnotation;
import mikenakis.bytecode.ByteCodeField;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Common base class for the "RuntimeVisibleAnnotations" or "RuntimeInvisibleAnnotations" {@link Attribute}s of a java class file.
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 *
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class AnnotationsAttribute extends Attribute
{
	private final List<ByteCodeAnnotation> annotations;

	protected AnnotationsAttribute( Runnable observer, String name )
	{
		super( observer, name );
		annotations = new ArrayList<>();
	}

	protected AnnotationsAttribute( Runnable observer, ConstantPool constantPool, String name, BufferReader bufferReader )
	{
		super( observer, name );
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		annotations = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ByteCodeAnnotation byteCodeAnnotation = new ByteCodeAnnotation( this::markAsDirty, constantPool, bufferReader );
			annotations.add( byteCodeAnnotation );
		}
	}

	@Override public final void intern( ConstantPool constantPool )
	{
		for( ByteCodeAnnotation annotation : annotations )
			annotation.intern( constantPool );
	}

	@Override public final void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( annotations.size() );
		for( ByteCodeAnnotation annotation : annotations )
			annotation.write( constantPool, bufferWriter );
	}

	@Override public Optional<AnnotationsAttribute> tryAsAnnotationsAttribute()
	{
		return Optional.of( this );
	}

	@Override public final void toStringBuilder( StringBuilder builder )
	{
		builder.append( annotations.size() ).append( " entries" );
	}

	public Optional<ByteCodeAnnotation> tryGetAnnotationByName( String annotationName )
	{
		//name = "L" + name + ";";
		for( ByteCodeAnnotation annotation : annotations )
		{
			String annotationTypeName = annotation.getName();
			if( annotationTypeName.equals( annotationName ) )
				return Optional.of( annotation );
		}
		return Optional.empty();
	}

	public ByteCodeAnnotation addAnnotation( ByteCodeAnnotation.Factory annotationFactory )
	{
		ByteCodeAnnotation annotation = annotationFactory.create( this::markAsDirty );
		annotations.add( annotation );
		return annotation;
	}

	public Collection<ByteCodeAnnotation> getAnnotations()
	{
		return Collections.unmodifiableCollection( annotations );
	}
}
