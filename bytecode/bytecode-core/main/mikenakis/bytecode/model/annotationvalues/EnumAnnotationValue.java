package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an enum {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class EnumAnnotationValue extends AnnotationValue
{
	public static EnumAnnotationValue read( AttributeReader attributeReader )
	{
		Mutf8ValueConstant typeNameConstant = attributeReader.readIndexAndGetConstant().asMutf8ValueConstant();
		Mutf8ValueConstant valueNameConstant = attributeReader.readIndexAndGetConstant().asMutf8ValueConstant();
		return of( typeNameConstant, valueNameConstant );
	}

	public static final String NAME = "enum";

	public static EnumAnnotationValue of( Mutf8ValueConstant enumClassDescriptorStringConstant, Mutf8ValueConstant enumValueNameConstant )
	{
		return new EnumAnnotationValue( enumClassDescriptorStringConstant, enumValueNameConstant );
	}

	private final Mutf8ValueConstant enumClassDescriptorStringConstant;
	private final Mutf8ValueConstant enumValueNameConstant;

	private EnumAnnotationValue( Mutf8ValueConstant enumClassDescriptorStringConstant, Mutf8ValueConstant enumValueNameConstant )
	{
		super( tagEnum );
		this.enumClassDescriptorStringConstant = enumClassDescriptorStringConstant;
		this.enumValueNameConstant = enumValueNameConstant;
	}

	public TerminalTypeDescriptor typeDescriptor() { return ByteCodeHelpers.typeDescriptorFromDescriptorStringConstant( enumClassDescriptorStringConstant ).asTerminalTypeDescriptor(); }
	public String valueName() { return enumValueNameConstant.stringValue(); }
	@Deprecated @Override public EnumAnnotationValue asEnumAnnotationValue() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "type = " + enumClassDescriptorStringConstant + ", value = " + enumValueNameConstant; }

	@Override public void intern( Interner interner )
	{
		enumClassDescriptorStringConstant.intern( interner );
		enumValueNameConstant.intern( interner );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( tag );
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( enumClassDescriptorStringConstant ) );
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( enumValueNameConstant ) );
	}
}
