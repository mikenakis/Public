package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class TypeArgumentTarget extends Target // "type_argument_target" in jvms-4.7.20.1
{
	public static TypeArgumentTarget read( BufferReader bufferReader, int targetTag )
	{
		int offset = bufferReader.readUnsignedShort();
		int typeArgumentIndex = bufferReader.readUnsignedByte();
		return new TypeArgumentTarget( targetTag, offset, typeArgumentIndex );
	}

	public final int offset; //TODO what kind of offset is this?
	public final int typeArgumentIndex;

	private TypeArgumentTarget( int tag, int offset, int typeArgumentIndex )
	{
		super( tag );
		assert tag == tag_CastArgument || //
			tag == tag_ConstructorArgument || //
			tag == tag_MethodArgument || //
			tag == tag_NewMethodArgument || //
			tag == tag_IdentifierMethodArgument;
		this.offset = offset;
		this.typeArgumentIndex = typeArgumentIndex;
	}

	@Deprecated @Override public TypeArgumentTarget asTypeArgumentTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "offset = " + offset + ", typeArgumentIndex = " + typeArgumentIndex; }

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( offset );
		constantWriter.writeUnsignedByte( typeArgumentIndex );
	}
}
