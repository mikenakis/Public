package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an enum {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class EnumAnnotationValue extends AnnotationValue
{
	public static final String NAME = "enum";

	public static EnumAnnotationValue of( Mutf8Constant typeNameConstant, Mutf8Constant valueNameConstant )
	{
		return new EnumAnnotationValue( typeNameConstant, valueNameConstant );
	}

	private final Mutf8Constant typeNameConstant;
	private final Mutf8Constant valueNameConstant;

	private EnumAnnotationValue( Mutf8Constant typeNameConstant, Mutf8Constant valueNameConstant )
	{
		super( EnumTag );
		this.typeNameConstant = typeNameConstant;
		this.valueNameConstant = valueNameConstant;
	}

	public Mutf8Constant typeNameConstant()
	{
		return typeNameConstant;
	}

	public Mutf8Constant valueNameConstant()
	{
		return valueNameConstant;
	}

	@Deprecated @Override public EnumAnnotationValue asEnumAnnotationValue()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "type = " + typeNameConstant + ", value = " + valueNameConstant;
	}
}
