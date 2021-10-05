package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a class {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ClassAnnotationValue extends AnnotationValue
{
	public static final String NAME = "class";

	public static ClassAnnotationValue of( Mutf8Constant classDescriptorStringConstant )
	{
		return new ClassAnnotationValue( classDescriptorStringConstant );
	}

	public final Mutf8Constant classDescriptorStringConstant;

	private ClassAnnotationValue( Mutf8Constant classDescriptorStringConstant )
	{
		super( tagClass );
		this.classDescriptorStringConstant = classDescriptorStringConstant;
	}

	public TypeDescriptor typeDescriptor() { return ByteCodeHelpers.typeDescriptorFromDescriptorStringConstant( classDescriptorStringConstant ); }
	@Deprecated @Override public ClassAnnotationValue asClassAnnotationValue() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "class = " + classDescriptorStringConstant; }
}
