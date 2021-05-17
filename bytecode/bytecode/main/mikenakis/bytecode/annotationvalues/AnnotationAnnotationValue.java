package mikenakis.bytecode.annotationvalues;

import mikenakis.bytecode.AnnotationValue;
import mikenakis.bytecode.ByteCodeAnnotation;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;

/**
 * Represents an annotation {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationAnnotationValue extends AnnotationValue
{
	public static final Kind KIND = new Kind( '@', "annotation" )
	{
		@Override public AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
		{
			return new AnnotationAnnotationValue( observer, constantPool, bufferReader );
		}
	};

	public final ByteCodeAnnotation annotation;

	@SuppressWarnings( "unused" ) public AnnotationAnnotationValue( Runnable observer, ByteCodeAnnotation.Factory annotationFactory )
	{
		super( observer, KIND );
		annotation = annotationFactory.create( this::markAsDirty );
	}

	public AnnotationAnnotationValue( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, KIND );
		annotation = new ByteCodeAnnotation( this::markAsDirty, constantPool, bufferReader );
	}

	@Override public void intern( ConstantPool constantPool )
	{
		annotation.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		super.write( constantPool, bufferWriter );
		annotation.write( constantPool, bufferWriter );
	}

	@Override public Optional<AnnotationAnnotationValue> tryAsAnnotationAnnotationValue()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "annotation = { " );
		annotation.toStringBuilder( builder );
		builder.append( " }" );
	}

	public ByteCodeAnnotation getAnnotation()
	{
		return annotation;
	}
}
