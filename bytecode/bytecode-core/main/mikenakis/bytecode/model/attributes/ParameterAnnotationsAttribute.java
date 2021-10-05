package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Annotation;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Common base class for the "RuntimeVisibleParameterAnnotations" and "RuntimeInvisibleParameterAnnotations" {@link Attribute}s of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ParameterAnnotationsAttribute extends KnownAttribute
{
	protected static List<ParameterAnnotationSet> readParameterAnnotationsAttributeEntries( AttributeReader attributeReader )
	{
		int entryCount = attributeReader.readUnsignedByte();
		assert entryCount > 0;
		List<ParameterAnnotationSet> entries = new ArrayList<>( entryCount );
		for( int i = 0; i < entryCount; i++ )
		{
			int annotationCount = attributeReader.readUnsignedShort();
			assert annotationCount >= 0;
			List<Annotation> annotations = new ArrayList<>( annotationCount );
			for( int i1 = 0; i1 < annotationCount; i1++ )
			{
				Annotation byteCodeAnnotation = Annotation.read( attributeReader );
				annotations.add( byteCodeAnnotation );
			}
			ParameterAnnotationSet entry = ParameterAnnotationSet.of( annotations );
			entries.add( entry );
		}
		return entries;
	}

	public final List<ParameterAnnotationSet> parameterAnnotationSets;

	ParameterAnnotationsAttribute( int tag, List<ParameterAnnotationSet> parameterAnnotationSets )
	{
		super( tag );
		this.parameterAnnotationSets = parameterAnnotationSets;
	}

	@Deprecated @Override public ParameterAnnotationsAttribute asParameterAnnotationsAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return name() + " " + parameterAnnotationSets.size() + " entries"; }

	@Override public final void intern( Interner interner )
	{
		for( ParameterAnnotationSet entry : parameterAnnotationSets )
			for( Annotation annotation : entry.annotations )
				annotation.intern( interner );
	}

	@Override public final void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( ((Collection<ParameterAnnotationSet>)parameterAnnotationSets).size() );
		for( ParameterAnnotationSet parameterAnnotationSet : parameterAnnotationSets )
		{
			constantWriter.writeUnsignedShort( ((Collection<Annotation>)parameterAnnotationSet.annotations).size() );
			for( Annotation annotation : parameterAnnotationSet.annotations )
				annotation.write( constantWriter );
		}
	}
}
