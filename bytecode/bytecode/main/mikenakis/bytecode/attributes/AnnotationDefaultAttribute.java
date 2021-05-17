package mikenakis.bytecode.attributes;

import mikenakis.bytecode.AnnotationValue;
import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.Attributes;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;

/**
 * Represents the "AnnotationDefault" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationDefaultAttribute extends Attribute
{
	public static final String NAME = "AnnotationDefault";

	public static Optional<AnnotationDefaultAttribute> tryFrom( Attributes attributes )
	{
		return Attribute.tryFrom( attributes, NAME ).map( attribute -> attribute.asAnnotationDefaultAttribute() );
	}

	public final AnnotationValue annotationValue;

	@SuppressWarnings( "unused" ) public AnnotationDefaultAttribute( Runnable observer, AnnotationValue annotationValue )
	{
		super( observer, NAME );
		this.annotationValue = annotationValue;
	}

	public AnnotationDefaultAttribute( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, NAME );
		annotationValue = AnnotationValue.parse( observer, constantPool, bufferReader );
	}

	@Override public void intern( ConstantPool constantPool )
	{
		annotationValue.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		annotationValue.write( constantPool, bufferWriter );
	}

	@Override public Optional<AnnotationDefaultAttribute> tryAsAnnotationDefaultAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "value = " );
		annotationValue.toStringBuilder( builder );
	}
}
