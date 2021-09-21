package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.ElementValue;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ClassDesc;

/**
 * Represents an enum {@link ElementValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class EnumElementValue extends ElementValue
{
	public static final String NAME = "enum";

	public static EnumElementValue of( Mutf8Constant typeNameConstant, Mutf8Constant valueNameConstant )
	{
		return new EnumElementValue( typeNameConstant, valueNameConstant );
	}

	private final Mutf8Constant typeNameConstant;
	private final Mutf8Constant valueNameConstant;

	private EnumElementValue( Mutf8Constant typeNameConstant, Mutf8Constant valueNameConstant )
	{
		super( Tag.Enum );
		this.typeNameConstant = typeNameConstant;
		this.valueNameConstant = valueNameConstant;
	}

	public Mutf8Constant typeNameConstant()	{ return typeNameConstant; }
	public ClassDesc typeDescriptor() { return ClassDesc.ofDescriptor( typeNameConstant.stringValue() ); }
	public Mutf8Constant valueNameConstant() { return valueNameConstant; }
	public String valueName() { return valueNameConstant.stringValue(); }

	@Deprecated @Override public EnumElementValue asEnumAnnotationValue()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "type = " + typeNameConstant + ", value = " + valueNameConstant;
	}
}
