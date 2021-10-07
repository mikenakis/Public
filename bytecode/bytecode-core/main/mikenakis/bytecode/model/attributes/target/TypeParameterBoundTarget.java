package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class TypeParameterBoundTarget extends Target // "type_parameter_bound_target" in jvms-4.7.20.1
{
	public static TypeParameterBoundTarget read( BufferReader bufferReader, int targetTag )
	{
		int typeParameterIndex = bufferReader.readUnsignedByte();
		int boundIndex = bufferReader.readUnsignedByte();
		return new TypeParameterBoundTarget( targetTag, typeParameterIndex, boundIndex );
	}

	public final int typeParameterIndex;
	public final int boundIndex;

	private TypeParameterBoundTarget( int tag, int typeParameterIndex, int boundIndex )
	{
		super( tag );
		assert tag == tag_ClassTypeBound ||
			tag == tag_MethodTypeBound;
		this.typeParameterIndex = typeParameterIndex;
		this.boundIndex = boundIndex;
	}

	@Deprecated @Override public TypeParameterBoundTarget asTypeParameterBoundTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "typeParameterIndex = " + typeParameterIndex + ", boundIndex = " + boundIndex; }

	@Override public void intern( Interner interner )
	{
		/* nothing to do */
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedByte( typeParameterIndex );
		bufferWriter.writeUnsignedByte( boundIndex );
	}
}
