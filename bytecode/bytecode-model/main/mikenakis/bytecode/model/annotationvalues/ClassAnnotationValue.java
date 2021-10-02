package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.descriptors.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a class {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ClassAnnotationValue extends AnnotationValue
{
	public static final String NAME = "class";

	public static ClassAnnotationValue of( String name )
	{
		Mutf8Constant classConstant = Mutf8Constant.of( name );
		return of( classConstant );
	}

	public static ClassAnnotationValue of( Mutf8Constant nameConstant )
	{
		return new ClassAnnotationValue( nameConstant );
	}

	public final Mutf8Constant nameConstant;

	private ClassAnnotationValue( Mutf8Constant nameConstant )
	{
		super( tagClass );
		this.nameConstant = nameConstant;
	}

	public TypeDescriptor typeDescriptor() { return TypeDescriptor.ofDescriptorString( nameConstant.stringValue() ); }

	@Deprecated @Override public ClassAnnotationValue asClassAnnotationValue()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "class = " + nameConstant;
	}
}
