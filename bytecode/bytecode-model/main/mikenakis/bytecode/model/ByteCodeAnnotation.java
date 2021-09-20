package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ClassDesc;
import java.util.List;

/**
 * Represents an annotation.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeAnnotation
{
	public static ByteCodeAnnotation of( Mutf8Constant typeConstant, List<AnnotationParameter> parameters )
	{
		return new ByteCodeAnnotation( typeConstant, parameters );
	}

	public final Mutf8Constant typeConstant;
	private final List<AnnotationParameter> parameters;

	private ByteCodeAnnotation( Mutf8Constant typeConstant, List<AnnotationParameter> parameters )
	{
		this.typeConstant = typeConstant;
		this.parameters = parameters;
	}

	public ClassDesc typeDescriptor() {	return ClassDesc.ofDescriptor( typeConstant.stringValue() ); }
	public List<AnnotationParameter> parameters() { return parameters; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "type = " + typeConstant + ", " + parameters.size() + " parameters";
	}
}
