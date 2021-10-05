package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class ThrowsTarget extends Target // "throws_target" in jvms-4.7.20.1
{
	public static ThrowsTarget read( BufferReader bufferReader, int targetTag )
	{
		int throwsTypeIndex = bufferReader.readUnsignedShort();
		return new ThrowsTarget( targetTag, throwsTypeIndex );
	}

	public final int throwsTypeIndex; //TODO

	private ThrowsTarget( int tag, int throwsTypeIndex )
	{
		super( tag );
		assert tag == tag_Throws;
		this.throwsTypeIndex = throwsTypeIndex;
	}

	@Deprecated @Override public ThrowsTarget asThrowsTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "throwsTypeIndex = " + throwsTypeIndex; }

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( throwsTypeIndex );
	}
}
