package io.github.mikenakis.bytecode.model.attributes.stackmap.verification;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.constants.ClassConstant;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.java_type_model.TypeDescriptor;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * 'Object' {@link VerificationType}.
 *
 * @author michael.gr
 */
public final class ObjectVerificationType extends VerificationType
{
	public static ObjectVerificationType read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		ClassConstant classConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asClassConstant();
		return of( classConstant );
	}

	public static ObjectVerificationType of( TypeDescriptor typeDescriptor )
	{
		ClassConstant classConstant = ClassConstant.of( typeDescriptor );
		return of( classConstant );
	}

	public static ObjectVerificationType of( ClassConstant classConstant )
	{
		return new ObjectVerificationType( classConstant );
	}

	private final ClassConstant classConstant;

	private ObjectVerificationType( ClassConstant classConstant )
	{
		super( tag_Object );
		this.classConstant = classConstant;
	}

	public TypeDescriptor typeDescriptor() { return classConstant.typeDescriptor(); }
	@Deprecated @Override public ObjectVerificationType asObjectVerificationType() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return super.toString() + " classConstant = " + classConstant; }

	@Override public void intern( Interner interner )
	{
		classConstant.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingLocationMap locationMap )
	{
		bufferWriter.writeUnsignedByte( tag );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( classConstant ) );
	}
}
