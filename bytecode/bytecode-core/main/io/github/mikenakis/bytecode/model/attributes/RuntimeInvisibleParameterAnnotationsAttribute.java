package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "RuntimeInvisibleParameterAnnotations" {@link Attribute} of a java class file.
 * <p>
 * @author michael.gr
 */
public final class RuntimeInvisibleParameterAnnotationsAttribute extends ParameterAnnotationsAttribute
{
	public static RuntimeInvisibleParameterAnnotationsAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		List<ParameterAnnotationSet> entries = readParameterAnnotationsAttributeEntries( bufferReader, constantPool );
		return of( entries );
	}

	public static RuntimeInvisibleParameterAnnotationsAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static RuntimeInvisibleParameterAnnotationsAttribute of( List<ParameterAnnotationSet> entries )
	{
		return new RuntimeInvisibleParameterAnnotationsAttribute( entries );
	}

	private RuntimeInvisibleParameterAnnotationsAttribute( List<ParameterAnnotationSet> entries ) { super( tag_RuntimeInvisibleParameterAnnotations, entries ); }
	@Override public boolean isOptional() { return true; }
	@Deprecated @Override public RuntimeInvisibleParameterAnnotationsAttribute asRuntimeInvisibleParameterAnnotationsAttribute() { return this; }
}
