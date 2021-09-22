package mikenakis.bytecode.model;

import mikenakis.bytecode.model.attributes.target.Target;
import mikenakis.bytecode.model.attributes.target.TypePath;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;

public final class TypeAnnotation // "type_annotation" in jvms-4.7.20
{
	public static TypeAnnotation of( Target target, TypePath targetPath, int typeIndex, List<AnnotationParameter> parameters )
	{
		return new TypeAnnotation( target, targetPath, typeIndex, parameters );
	}

	public final Target target;
	public final TypePath typePath;
	public final int typeIndex;
	public final List<AnnotationParameter> parameters;

	private TypeAnnotation( Target target, TypePath typePath, int typeIndex, List<AnnotationParameter> parameters )
	{
		this.target = target;
		this.typePath = typePath;
		this.typeIndex = typeIndex;
		this.parameters = parameters;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "targetType = " + String.format( "0x%02x ", target.tag ) + "targetPath = " + typePath + "typeIndex = " + typeIndex + ", " + parameters.size() + " elementValuePairs";
	}
}
