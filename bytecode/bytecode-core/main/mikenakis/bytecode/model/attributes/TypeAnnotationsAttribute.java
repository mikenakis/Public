package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.exceptions.InvalidTargetTagException;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.model.Annotation;
import mikenakis.bytecode.model.AnnotationParameter;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.TypeAnnotation;
import mikenakis.bytecode.model.attributes.target.CatchTarget;
import mikenakis.bytecode.model.attributes.target.EmptyTarget;
import mikenakis.bytecode.model.attributes.target.FormalParameterTarget;
import mikenakis.bytecode.model.attributes.target.LocalVariableTarget;
import mikenakis.bytecode.model.attributes.target.OffsetTarget;
import mikenakis.bytecode.model.attributes.target.SupertypeTarget;
import mikenakis.bytecode.model.attributes.target.Target;
import mikenakis.bytecode.model.attributes.target.ThrowsTarget;
import mikenakis.bytecode.model.attributes.target.TypeArgumentTarget;
import mikenakis.bytecode.model.attributes.target.TypeParameterBoundTarget;
import mikenakis.bytecode.model.attributes.target.TypeParameterTarget;
import mikenakis.bytecode.model.attributes.target.TypePath;
import mikenakis.bytecode.model.attributes.target.TypePathEntry;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Common base class for the "RuntimeVisibleTypeAnnotations" or "RuntimeInvisibleTypeAnnotations" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType} {@link ByteCodeMethod} {@link ByteCodeField} {@link CodeAttribute}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class TypeAnnotationsAttribute extends KnownAttribute
{
	protected static List<TypeAnnotation> readTypeAnnotations( AttributeReader attributeReader )
	{
		int typeAnnotationCount = attributeReader.readUnsignedShort();
		assert typeAnnotationCount > 0;
		List<TypeAnnotation> typeAnnotations = new ArrayList<>( typeAnnotationCount );
		for( int i = 0; i < typeAnnotationCount; i++ )
		{
			TypeAnnotation entry = TypeAnnotation.read( attributeReader );
			typeAnnotations.add( entry );
		}
		return typeAnnotations;
	}

	public final List<TypeAnnotation> typeAnnotations;

	TypeAnnotationsAttribute( int tag, List<TypeAnnotation> typeAnnotations )
	{
		super( tag );
		this.typeAnnotations = typeAnnotations;
	}

	@Deprecated @Override public TypeAnnotationsAttribute asTypeAnnotationsAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return name() + ' ' + typeAnnotations.size() + " entries"; }

	@Override public final void intern( Interner interner )
	{
		for( TypeAnnotation typeAnnotation : typeAnnotations )
			typeAnnotation.intern( interner );
	}

	@Override public final void write( ConstantWriter constantWriter )
	{
		TypeAnnotation.writeTypeAnnotations( constantWriter, typeAnnotations );
	}
}
