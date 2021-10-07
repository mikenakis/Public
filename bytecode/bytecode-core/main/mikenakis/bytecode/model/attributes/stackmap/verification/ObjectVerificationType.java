package mikenakis.bytecode.model.attributes.stackmap.verification;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * 'Object' {@link VerificationType}.
 *
 * @author Michael Belivanakis (michael.gr)
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
