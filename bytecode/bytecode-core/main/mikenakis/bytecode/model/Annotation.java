package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an annotation.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class Annotation
{
	public static Annotation read( AttributeReader attributeReader )
	{
		Mutf8ValueConstant typeNameConstant = attributeReader.readIndexAndGetConstant().asMutf8ValueConstant();
		List<AnnotationParameter> annotationParameters = readAnnotationParameters( attributeReader );
		return of( typeNameConstant, annotationParameters );
	}

	public static List<AnnotationParameter> readAnnotationParameters( AttributeReader attributeReader )
	{
		int count = attributeReader.readUnsignedShort();
		List<AnnotationParameter> annotationParameters = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			AnnotationParameter annotationParameter = AnnotationParameter.read( attributeReader );
			annotationParameters.add( annotationParameter );
		}
		return annotationParameters;
	}

	private static Annotation of( Mutf8ValueConstant annotationTypeNameConstant, List<AnnotationParameter> parameters )
	{
		return new Annotation( annotationTypeNameConstant, parameters );
	}

	private final Mutf8ValueConstant annotationTypeNameConstant;
	public final List<AnnotationParameter> parameters;

	private Annotation( Mutf8ValueConstant annotationTypeNameConstant, List<AnnotationParameter> parameters )
	{
		this.annotationTypeNameConstant = annotationTypeNameConstant;
		this.parameters = parameters;
	}

	public TypeDescriptor typeDescriptor() { return ByteCodeHelpers.typeDescriptorFromDescriptorStringConstant( annotationTypeNameConstant ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "type = " + annotationTypeNameConstant + ", " + parameters.size() + " parameters"; }

	public void intern( Interner interner )
	{
		annotationTypeNameConstant.intern( interner );
		for( AnnotationParameter annotationParameter : parameters )
			annotationParameter.intern( interner );
	}

	public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( annotationTypeNameConstant ) );
		constantWriter.writeUnsignedShort( parameters.size() );
		for( AnnotationParameter annotationParameter : parameters )
			annotationParameter.write( constantWriter );
	}
}
