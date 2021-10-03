package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an enum {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class EnumAnnotationValue extends AnnotationValue
{
	public static final String NAME = "enum";

	public static EnumAnnotationValue of( Mutf8Constant enumClassDescriptorStringConstant, Mutf8Constant enumValueNameConstant )
	{
		return new EnumAnnotationValue( enumClassDescriptorStringConstant, enumValueNameConstant );
	}

	public final Mutf8Constant enumClassDescriptorStringConstant;
	public final Mutf8Constant enumValueNameConstant;

	private EnumAnnotationValue( Mutf8Constant enumClassDescriptorStringConstant, Mutf8Constant enumValueNameConstant )
	{
		super( tagEnum );
		this.enumClassDescriptorStringConstant = enumClassDescriptorStringConstant;
		this.enumValueNameConstant = enumValueNameConstant;
	}

	public TerminalTypeDescriptor typeDescriptor() { return ByteCodeHelpers.typeDescriptorFromDescriptorString( enumClassDescriptorStringConstant.stringValue() ).asTerminalTypeDescriptor(); }
	public String valueName() { return enumValueNameConstant.stringValue(); }
	@Deprecated @Override public EnumAnnotationValue asEnumAnnotationValue() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "type = " + enumClassDescriptorStringConstant + ", value = " + enumValueNameConstant; }
}
