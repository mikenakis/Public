package mikenakis.bytecode.annotationvalues;

import mikenakis.bytecode.AnnotationValue;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;

/**
 * Represents a class {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ClassAnnotationValue extends AnnotationValue
{
	public static final Kind KIND = new Kind( 'c', "class" )
	{
		@Override public AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
		{
			return new ClassAnnotationValue( observer, constantPool, bufferReader );
		}
	};

	public final Utf8Constant classConstant;

	public ClassAnnotationValue( Runnable observer, String name )
	{
		super( observer, KIND );
		classConstant = new Utf8Constant( name );
	}

	public ClassAnnotationValue( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, KIND );
		classConstant = constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
	}

	@Override public void intern( ConstantPool constantPool )
	{
		classConstant.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		super.write( constantPool, bufferWriter );
		classConstant.writeIndex( constantPool, bufferWriter );
	}

	@Override public Optional<ClassAnnotationValue> tryAsClassAnnotationValue()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "class = " ).append( classConstant.getStringValue() );
	}

	public String getClassName()
	{
		return classConstant.getStringValue();
	}
}
