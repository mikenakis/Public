package mikenakis.bytecode.annotationvalues;

import mikenakis.bytecode.AnnotationValue;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;

/**
 * Represents an enum {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class EnumAnnotationValue extends AnnotationValue
{
	public static final Kind KIND = new Kind( 'e', "enum" )
	{
		@Override public AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
		{
			return new EnumAnnotationValue( observer, constantPool, bufferReader );
		}
	};

	public final Utf8Constant typeNameConstant;
	public final Utf8Constant valueNameConstant;

	public EnumAnnotationValue( Runnable observer, Utf8Constant typeNameConstant, Utf8Constant valueNameConstant )
	{
		super( observer, KIND );
		this.typeNameConstant = typeNameConstant;
		this.valueNameConstant = valueNameConstant;
	}

	public EnumAnnotationValue( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, KIND );
		typeNameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		valueNameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
	}

	@Override public void intern( ConstantPool constantPool )
	{
		typeNameConstant.intern( constantPool );
		valueNameConstant.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		super.write( constantPool, bufferWriter );
		typeNameConstant.writeIndex( constantPool, bufferWriter );
		valueNameConstant.writeIndex( constantPool, bufferWriter );
	}

	@Override public Optional<EnumAnnotationValue> tryAsEnumAnnotationValue()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "type = " ).append( typeNameConstant.getStringValue() );
		builder.append( ", value = " ).append( valueNameConstant.getStringValue() );
	}
}
