package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class SupertypeTarget extends Target // "supertype_target" in jvms-4.7.20.1
{
	public static SupertypeTarget read( BufferReader bufferReader, int targetTag )
	{
		int supertypeIndex = bufferReader.readUnsignedShort();
		return new SupertypeTarget( targetTag, supertypeIndex );
	}

	public final int supertypeIndex; //TODO

	private SupertypeTarget( int tag, int supertypeIndex )
	{
		super( tag );
		assert tag == tag_Supertype;
		this.supertypeIndex = supertypeIndex;
	}

	@Deprecated @Override public SupertypeTarget asSupertypeTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "superTypeIndex = " + supertypeIndex; }

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( supertypeIndex );
	}
}
