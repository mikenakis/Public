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
public final class Annotation
{
	public static Annotation of( Mutf8Constant typeConstant, List<ElementValuePair> parameters )
	{
		return new Annotation( typeConstant, parameters );
	}

	public final Mutf8Constant typeConstant;
	private final List<ElementValuePair> parameters;

	private Annotation( Mutf8Constant typeConstant, List<ElementValuePair> parameters )
	{
		this.typeConstant = typeConstant;
		this.parameters = parameters;
	}

	public ClassDesc typeDescriptor() {	return ClassDesc.ofDescriptor( typeConstant.stringValue() ); }
	public List<ElementValuePair> elementValuePairs() { return parameters; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "type = " + typeConstant + ", " + parameters.size() + " parameters";
	}
}
