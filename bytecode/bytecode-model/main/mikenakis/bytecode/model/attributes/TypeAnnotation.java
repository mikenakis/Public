package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.AnnotationParameter;
import mikenakis.bytecode.model.attributes.target.Target;
import mikenakis.bytecode.model.attributes.target.TypePath;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collections;
import java.util.List;

public final class TypeAnnotation // "type_annotation" in jvms-4.7.20
{
	public static TypeAnnotation of( int targetType, Target target, TypePath targetPath, int typeIndex, List<ElementValuePair> elementValuePairs )
	{
		return new TypeAnnotation( targetType, target, targetPath, typeIndex, elementValuePairs );
	}

	private final int targetType;
	private final Target target;
	private final TypePath typePath;
	private final int typeIndex;
	private final List<ElementValuePair> elementValuePairs;

	private TypeAnnotation( int targetType, Target target, TypePath typePath, int typeIndex, List<ElementValuePair> elementValuePairs )
	{
		this.targetType = targetType;
		this.target = target;
		this.typePath = typePath;
		this.typeIndex = typeIndex;
		this.elementValuePairs = elementValuePairs;
	}

	public int targetType() { return targetType; }
	public Target target() { return target; }
	public TypePath typePath() { return typePath; }
	public int typeIndex() { return typeIndex; }
	public List<ElementValuePair> elementValuePairs() { return Collections.unmodifiableList( elementValuePairs ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "targetType = " + String.format( "0x%02x ", targetType ) + "targetPath = " + typePath + "typeIndex = " + typeIndex + ", " + elementValuePairs.size() + " elementValuePairs";
	}

	public static final class ElementValuePair
	{
		public static ElementValuePair of( int elementNameIndex, AnnotationParameter elementValue )
		{
			return new ElementValuePair( elementNameIndex, elementValue );
		}

		private final int elementNameIndex;
		private final AnnotationParameter elementValue;

		private ElementValuePair( int elementNameIndex, AnnotationParameter elementValue )
		{
			this.elementNameIndex = elementNameIndex;
			this.elementValue = elementValue;
		}

		public int elementNameIndex() { return elementNameIndex; }
		public AnnotationParameter elementValue() { return elementValue; }

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return "elementNameIndex = " + elementNameIndex + ", elementValue = " + elementValue;
		}
	}
}
