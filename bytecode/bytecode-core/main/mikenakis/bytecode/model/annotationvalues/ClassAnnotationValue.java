package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a class {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ClassAnnotationValue extends AnnotationValue
{
	public static ClassAnnotationValue read( AttributeReader attributeReader )
	{
		Mutf8ValueConstant classConstant = attributeReader.readIndexAndGetConstant().asMutf8ValueConstant();
		return of( classConstant );
	}

	public static final String NAME = "class";

	public static ClassAnnotationValue of( Mutf8ValueConstant classDescriptorStringConstant )
	{
		return new ClassAnnotationValue( classDescriptorStringConstant );
	}

	private final Mutf8ValueConstant classDescriptorStringConstant;

	private ClassAnnotationValue( Mutf8ValueConstant classDescriptorStringConstant )
	{
		super( tagClass );
		this.classDescriptorStringConstant = classDescriptorStringConstant;
	}

	public TypeDescriptor typeDescriptor() { return ByteCodeHelpers.typeDescriptorFromDescriptorStringConstant( classDescriptorStringConstant ); }
	@Deprecated @Override public ClassAnnotationValue asClassAnnotationValue() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "class = " + classDescriptorStringConstant; }

	@Override public void intern( Interner interner )
	{
		classDescriptorStringConstant.intern( interner );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( tag );
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( classDescriptorStringConstant ) );
	}
}
