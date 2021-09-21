package mikenakis.bytecode.model;

import mikenakis.bytecode.model.attributes.target.Target;
import mikenakis.bytecode.model.attributes.target.TypePath;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collections;
import java.util.List;

public final class TypeAnnotation // "type_annotation" in jvms-4.7.20
{
	public static TypeAnnotation of( Target target, TypePath targetPath, int typeIndex, List<ElementValuePair> elementValuePairs )
	{
		return new TypeAnnotation( target, targetPath, typeIndex, elementValuePairs );
	}

	private final Target target;
	private final TypePath typePath;
	private final int typeIndex;
	private final List<ElementValuePair> elementValuePairs;

	private TypeAnnotation( Target target, TypePath typePath, int typeIndex, List<ElementValuePair> elementValuePairs )
	{
		this.target = target;
		this.typePath = typePath;
		this.typeIndex = typeIndex;
		this.elementValuePairs = elementValuePairs;
	}

	public Target target() { return target; }
	public TypePath typePath() { return typePath; }
	public int typeIndex() { return typeIndex; }
	public List<ElementValuePair> elementValuePairs() { return Collections.unmodifiableList( elementValuePairs ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "targetType = " + String.format( "0x%02x ", target.type.number ) + "targetPath = " + typePath + "typeIndex = " + typeIndex + ", " + elementValuePairs.size() + " elementValuePairs";
	}
}
