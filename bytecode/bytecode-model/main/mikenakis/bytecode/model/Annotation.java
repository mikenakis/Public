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
	public static Annotation of( Mutf8Constant typeNameConstant, List<AnnotationParameter> parameters )
	{
		return new Annotation( typeNameConstant, parameters );
	}

	public final Mutf8Constant typeNameConstant;
	public final List<AnnotationParameter> parameters;

	private Annotation( Mutf8Constant typeNameConstant, List<AnnotationParameter> parameters )
	{
		this.typeNameConstant = typeNameConstant;
		this.parameters = parameters;
	}

	public TypeDescriptor typeDescriptor() { return ByteCodeHelpers.typeDescriptorFromDescriptorString( typeNameConstant.stringValue() ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "type = " + typeNameConstant + ", " + parameters.size() + " parameters"; }
}
