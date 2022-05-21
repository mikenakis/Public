package io.github.mikenakis.bytecode.model;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the "element_value_pair" of JVMS 4.7.16.
 *
 * @author michael.gr
 */
public final class AnnotationParameter
{
	public static AnnotationParameter read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		Mutf8ValueConstant nameConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
		AnnotationValue annotationValue = AnnotationValue.read( bufferReader, constantPool );
		return of( nameConstant, annotationValue );
	}

	private static AnnotationParameter of( Mutf8ValueConstant nameConstant, AnnotationValue annotationValue )
	{
		return new AnnotationParameter( nameConstant, annotationValue );
	}

	private final Mutf8ValueConstant nameConstant;
	public final AnnotationValue annotationValue;

	private AnnotationParameter( Mutf8ValueConstant nameConstant, AnnotationValue annotationValue )
	{
		this.nameConstant = nameConstant;
		this.annotationValue = annotationValue;
	}

	public String name() { return nameConstant.stringValue(); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "name = " + nameConstant + ", value = " + annotationValue;
	}

	public void intern( Interner interner )
	{
		nameConstant.intern( interner );
		annotationValue.intern( interner );
	}

	public void write( BufferWriter bufferWriter, WritingConstantPool constantPool )
	{
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( nameConstant ) );
		annotationValue.write( bufferWriter, constantPool );
	}
}
