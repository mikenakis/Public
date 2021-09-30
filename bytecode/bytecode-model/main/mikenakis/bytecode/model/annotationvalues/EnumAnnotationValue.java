package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.descriptors.TerminalTypeDescriptor;
import mikenakis.bytecode.model.descriptors.TypeDescriptor;
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

	public final Mutf8Constant typeNameConstant;
	public final Mutf8Constant valueNameConstant;

	private EnumAnnotationValue( Mutf8Constant typeNameConstant, Mutf8Constant valueNameConstant )
	{
		super( tagEnum );
		this.typeNameConstant = typeNameConstant;
		this.valueNameConstant = valueNameConstant;
	}

	public String typeName() { return typeDescriptor().name(); }
	public TypeDescriptor typeDescriptor() { return TerminalTypeDescriptor.ofDescriptorString( typeNameConstant.stringValue() ); }
	public String valueName() { return valueNameConstant.stringValue(); }

	@Deprecated @Override public EnumAnnotationValue asEnumAnnotationValue()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "type = " + typeNameConstant + ", value = " + valueNameConstant;
	}
}
