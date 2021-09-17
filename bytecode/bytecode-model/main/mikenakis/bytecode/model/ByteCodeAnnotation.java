package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an annotation.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeAnnotation
{
	public static ByteCodeAnnotation of( String name )
	{
		String descriptorName = ByteCodeHelpers.getDescriptorTypeNameFromJavaTypeName( name );
		Utf8Constant nameConstant = Utf8Constant.of( descriptorName );
		List<AnnotationParameter> annotationParameters = new ArrayList<>();
		return of( nameConstant, annotationParameters );
	}

	public static ByteCodeAnnotation of( Utf8Constant nameConstant, List<AnnotationParameter> annotationParameters )
	{
		return new ByteCodeAnnotation( nameConstant, annotationParameters );
	}

	private final Utf8Constant nameConstant;
	private final List<AnnotationParameter> annotationParameters;

	private ByteCodeAnnotation( Utf8Constant nameConstant, List<AnnotationParameter> annotationParameters )
	{
		this.nameConstant = nameConstant;
		this.annotationParameters = annotationParameters;
	}

	public Utf8Constant nameConstant() { return nameConstant; }
	public String name() { return nameConstant.value(); }
	public List<AnnotationParameter> annotationParameters() { return annotationParameters; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "name = " + nameConstant + ", " + annotationParameters.size() + " entries";
	}
}
