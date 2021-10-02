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

	public TypeDescriptor typeDescriptor()
	{
		String descriptorString = typeNameConstant.stringValue();
		assert descriptorString.charAt( 0 ) == 'L' && descriptorString.charAt( descriptorString.length() - 1 ) == ';';
		String internalName = descriptorString.substring( 1, descriptorString.length() - 1 );
		return TerminalTypeDescriptor.ofInternalName( internalName );
	}

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
