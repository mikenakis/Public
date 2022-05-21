package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.model.Annotation;
import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "RuntimeVisibleAnnotations" {@link Attribute} of a java class file.
 * <p>
 * See JVMS 4.7.17 "The RuntimeInvisibleAnnotations attribute", <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.17">https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.17</a>
 *
 * @author michael.gr
 */
public final class RuntimeVisibleAnnotationsAttribute extends AnnotationsAttribute
{
	public static RuntimeVisibleAnnotationsAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		List<Annotation> annotations = readAnnotations( bufferReader, constantPool );
		return of( annotations );
	}

	public static RuntimeVisibleAnnotationsAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static RuntimeVisibleAnnotationsAttribute of( List<Annotation> annotations )
	{
		return new RuntimeVisibleAnnotationsAttribute( annotations );
	}

	private RuntimeVisibleAnnotationsAttribute( List<Annotation> annotations )
	{
		super( tag_RuntimeVisibleAnnotations, annotations );
	}

	@Deprecated @Override public RuntimeVisibleAnnotationsAttribute asRuntimeVisibleAnnotationsAttribute() { return this; }
}
