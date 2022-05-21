package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.model.Annotation;
import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "RuntimeInvisibleAnnotations" {@link Attribute} of a java class file.
 * <p>
 * See <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.16">JVMS 4.7.16 "The RuntimeVisibleAnnotations attribute"</a>
 *
 * @author michael.gr
 */
public final class RuntimeInvisibleAnnotationsAttribute extends AnnotationsAttribute
{
	public static RuntimeInvisibleAnnotationsAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		List<Annotation> annotations = readAnnotations( bufferReader, constantPool );
		return of( annotations );
	}

	public static RuntimeInvisibleAnnotationsAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static RuntimeInvisibleAnnotationsAttribute of( List<Annotation> annotations )
	{
		return new RuntimeInvisibleAnnotationsAttribute( annotations );
	}

	private RuntimeInvisibleAnnotationsAttribute( List<Annotation> annotations )
	{
		super( tag_RuntimeInvisibleAnnotations, annotations );
	}

	@Deprecated @Override public RuntimeInvisibleAnnotationsAttribute asRuntimeInvisibleAnnotationsAttribute() { return this; }
}
