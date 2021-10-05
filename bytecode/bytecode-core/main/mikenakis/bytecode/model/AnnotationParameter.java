package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the "element_value_pair" of JVMS 4.7.16.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationParameter
{
	public static AnnotationParameter read( AttributeReader attributeReader )
	{
		Mutf8ValueConstant nameConstant = attributeReader.readIndexAndGetConstant().asMutf8ValueConstant();
		AnnotationValue annotationValue = AnnotationValue.read( attributeReader );
		AnnotationParameter annotationParameter = of( nameConstant, annotationValue );
		return annotationParameter;
	}

	private static AnnotationParameter of( Mutf8ValueConstant nameConstant, AnnotationValue annotationValue )
	{
		return new AnnotationParameter( nameConstant, annotationValue );
	}

	private final Mutf8ValueConstant nameConstant;
	public final AnnotationValue annotationValue;

	private AnnotationParameter( Mutf8ValueConstant nameConstant, AnnotationValue annotationValue )
	{
		this.nameConstant = nameConstant;
		this.annotationValue = annotationValue;
	}

	public String name() { return nameConstant.stringValue(); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "name = " + nameConstant + ", value = " + annotationValue;
	}

	public void intern( Interner interner )
	{
		nameConstant.intern( interner );
		annotationValue.intern( interner );
	}

	public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( nameConstant ) );
		annotationValue.write( constantWriter );
	}
}
