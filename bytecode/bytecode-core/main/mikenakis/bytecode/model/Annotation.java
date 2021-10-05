package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;

/**
 * Represents an annotation.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class Annotation
{
	public static Annotation of( Mutf8Constant annotationTypeDescriptorStringConstant, List<AnnotationParameter> parameters )
	{
		return new Annotation( annotationTypeDescriptorStringConstant, parameters );
	}

	public final Mutf8Constant annotationTypeDescriptorStringConstant;
	public final List<AnnotationParameter> parameters;

	private Annotation( Mutf8Constant annotationTypeDescriptorStringConstant, List<AnnotationParameter> parameters )
	{
		this.annotationTypeDescriptorStringConstant = annotationTypeDescriptorStringConstant;
		this.parameters = parameters;
	}

	public TypeDescriptor typeDescriptor() { return ByteCodeHelpers.typeDescriptorFromDescriptorStringConstant( annotationTypeDescriptorStringConstant ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "type = " + annotationTypeDescriptorStringConstant + ", " + parameters.size() + " parameters"; }
}
