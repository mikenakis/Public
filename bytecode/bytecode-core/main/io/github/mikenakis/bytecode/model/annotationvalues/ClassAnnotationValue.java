package io.github.mikenakis.bytecode.model.annotationvalues;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.AnnotationValue;
import io.github.mikenakis.bytecode.model.ByteCodeHelpers;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.java_type_model.TypeDescriptor;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a class {@link AnnotationValue}.
 *
 * @author michael.gr
 */
public final class ClassAnnotationValue extends AnnotationValue
{
	public static ClassAnnotationValue read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		Mutf8ValueConstant classConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
		return of( classConstant );
	}

	public static final String NAME = "class";

	public static ClassAnnotationValue of( Mutf8ValueConstant classDescriptorStringConstant )
	{
		return new ClassAnnotationValue( classDescriptorStringConstant );
	}

	private final Mutf8ValueConstant classDescriptorStringConstant;

	private ClassAnnotationValue( Mutf8ValueConstant classDescriptorStringConstant )
	{
		super( tagClass );
		this.classDescriptorStringConstant = classDescriptorStringConstant;
	}

	public TypeDescriptor typeDescriptor() { return ByteCodeHelpers.typeDescriptorFromDescriptorStringConstant( classDescriptorStringConstant ); }
	@Deprecated @Override public ClassAnnotationValue asClassAnnotationValue() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "class = " + classDescriptorStringConstant; }

	@Override public void intern( Interner interner )
	{
		classDescriptorStringConstant.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool )
	{
		bufferWriter.writeUnsignedByte( tag );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( classDescriptorStringConstant ) );
	}
}
