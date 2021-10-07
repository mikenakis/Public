package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class TypeParameterTarget extends Target // "type_parameter_target" in jvms-4.7.20.1
{
	public static TypeParameterTarget read( BufferReader bufferReader, int targetTag )
	{
		int typeParameterIndex = bufferReader.readUnsignedByte();
		return new TypeParameterTarget( targetTag, typeParameterIndex );
	}

	public final int typeParameterIndex;

	private TypeParameterTarget( int tag, int typeParameterIndex )
	{
		super( tag );
		assert tag == tag_ClassTypeParameter || tag == tag_MethodTypeParameter;
		this.typeParameterIndex = typeParameterIndex;
	}

	@Deprecated @Override public TypeParameterTarget asTypeParameterTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "typeParameterIndex = " + typeParameterIndex; }

	@Override public void intern( Interner interner )
	{
		/* nothing to do */
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( typeParameterIndex );
	}
}
