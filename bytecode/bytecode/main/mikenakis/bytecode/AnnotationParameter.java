package mikenakis.bytecode;

import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;

/**
 * Represents an annotation parameter.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationParameter extends Printable // "element_value" in the jvms
{
	public interface Factory
	{
		AnnotationParameter create( Runnable observer );
	}

	private final Runnable observer;
	public final Utf8Constant nameConstant;
	public final AnnotationValue annotationValue;

	public AnnotationParameter( Runnable observer, String name, AnnotationValue.Factory annotationValueFactory )
	{
		this.observer = observer;
		nameConstant = new Utf8Constant( name );
		annotationValue = annotationValueFactory.create( this::markAsDirty );
	}

	public AnnotationParameter( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		this.observer = observer;
		nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
		annotationValue = AnnotationValue.parse( this::markAsDirty, constantPool, bufferReader );
	}

	public void intern( ConstantPool constantPool )
	{
		nameConstant.intern( constantPool );
		annotationValue.intern( constantPool );
	}

	public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		nameConstant.writeIndex( constantPool, bufferWriter );
		annotationValue.write( constantPool, bufferWriter );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "name = " );
		nameConstant.toStringBuilder( builder );
		builder.append( ", value = " );
		annotationValue.toStringBuilder( builder );
	}

	private void markAsDirty()
	{
		observer.run();
	}
}
