package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.descriptors.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;

/**
 * Represents an annotation.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class Annotation
{
	public static Annotation of( Mutf8Constant typeConstant, List<AnnotationParameter> parameters )
	{
		return new Annotation( typeConstant, parameters );
	}

	public final Mutf8Constant typeConstant;
	public final List<AnnotationParameter> parameters;

	private Annotation( Mutf8Constant typeConstant, List<AnnotationParameter> parameters )
	{
		this.typeConstant = typeConstant;
		this.parameters = parameters;
	}

	public String typeName() { return typeDescriptor().name(); }
	public TypeDescriptor typeDescriptor() { return TypeDescriptor.ofDescriptorString( typeConstant.stringValue() ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "type = " + typeConstant + ", " + parameters.size() + " parameters";
	}
}
