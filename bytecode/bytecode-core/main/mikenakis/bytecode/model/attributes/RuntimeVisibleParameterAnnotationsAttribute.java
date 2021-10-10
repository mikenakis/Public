package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.reading.ReadingConstantPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "RuntimeVisibleParameterAnnotations" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author michael.gr
 */
public final class RuntimeVisibleParameterAnnotationsAttribute extends ParameterAnnotationsAttribute
{
	public static RuntimeVisibleParameterAnnotationsAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		List<ParameterAnnotationSet> entries = readParameterAnnotationsAttributeEntries( bufferReader, constantPool );
		return of( entries );
	}

	public static RuntimeVisibleParameterAnnotationsAttribute of()
	{
		return new RuntimeVisibleParameterAnnotationsAttribute( new ArrayList<>() );
	}

	public static RuntimeVisibleParameterAnnotationsAttribute of( List<ParameterAnnotationSet> entries )
	{
		return new RuntimeVisibleParameterAnnotationsAttribute( entries );
	}

	private RuntimeVisibleParameterAnnotationsAttribute( List<ParameterAnnotationSet> entries )
	{
		super( tag_RuntimeVisibleParameterAnnotations, entries );
	}

	@Deprecated @Override public RuntimeVisibleParameterAnnotationsAttribute asRuntimeVisibleParameterAnnotationsAttribute() { return this; }
}
