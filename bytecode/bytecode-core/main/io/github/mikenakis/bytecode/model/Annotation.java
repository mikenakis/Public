package io.github.mikenakis.bytecode.model;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.java_type_model.TypeDescriptor;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an annotation.
 *
 * @author michael.gr
 */
public final class Annotation
{
	public static Annotation read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		Mutf8ValueConstant typeNameConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
		List<AnnotationParameter> annotationParameters = readAnnotationParameters( bufferReader, constantPool );
		return of( typeNameConstant, annotationParameters );
	}

	static List<AnnotationParameter> readAnnotationParameters( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		int count = bufferReader.readUnsignedShort();
		List<AnnotationParameter> annotationParameters = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			AnnotationParameter annotationParameter = AnnotationParameter.read( bufferReader, constantPool );
			annotationParameters.add( annotationParameter );
		}
		return annotationParameters;
	}

	private static Annotation of( Mutf8ValueConstant annotationTypeNameConstant, List<AnnotationParameter> parameters )
	{
		return new Annotation( annotationTypeNameConstant, parameters );
	}

	private final Mutf8ValueConstant annotationTypeNameConstant;
	public final List<AnnotationParameter> parameters;

	private Annotation( Mutf8ValueConstant annotationTypeNameConstant, List<AnnotationParameter> parameters )
	{
		this.annotationTypeNameConstant = annotationTypeNameConstant;
		this.parameters = parameters;
	}

	public TypeDescriptor typeDescriptor() { return ByteCodeHelpers.typeDescriptorFromDescriptorStringConstant( annotationTypeNameConstant ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "type = " + annotationTypeNameConstant + ", " + parameters.size() + " parameters"; }

	public void intern( Interner interner )
	{
		annotationTypeNameConstant.intern( interner );
		for( AnnotationParameter annotationParameter : parameters )
			annotationParameter.intern( interner );
	}

	public void write( BufferWriter bufferWriter, WritingConstantPool constantPool )
	{
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( annotationTypeNameConstant ) );
		bufferWriter.writeUnsignedShort( parameters.size() );
		for( AnnotationParameter annotationParameter : parameters )
			annotationParameter.write( bufferWriter, constantPool );
	}
}
