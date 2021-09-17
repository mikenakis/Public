package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an enum {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class EnumAnnotationValue extends AnnotationValue
{
	public static final String NAME = "enum";

	public static EnumAnnotationValue of( Utf8Constant typeNameConstant, Utf8Constant valueNameConstant )
	{
		return new EnumAnnotationValue( typeNameConstant, valueNameConstant );
	}

	private final Utf8Constant typeNameConstant;
	private final Utf8Constant valueNameConstant;

	private EnumAnnotationValue( Utf8Constant typeNameConstant, Utf8Constant valueNameConstant )
	{
		super( EnumTag );
		this.typeNameConstant = typeNameConstant;
		this.valueNameConstant = valueNameConstant;
	}

	public Utf8Constant typeNameConstant()
	{
		return typeNameConstant;
	}

	public Utf8Constant valueNameConstant()
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
