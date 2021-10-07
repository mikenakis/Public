package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class ThrowsTarget extends Target // "throws_target" in jvms-4.7.20.1
{
	public static ThrowsTarget read( BufferReader bufferReader, int targetTag )
	{
		int throwsTypeIndex = bufferReader.readUnsignedShort();
		// TODO: use throwsTypeIndex to lookup the thrown exception and realize the exception instead of the index.
		return new ThrowsTarget( targetTag, throwsTypeIndex );
	}

	public final int throwsTypeIndex;

	private ThrowsTarget( int tag, int throwsTypeIndex )
	{
		super( tag );
		assert tag == tag_Throws;
		this.throwsTypeIndex = throwsTypeIndex;
	}

	@Deprecated @Override public ThrowsTarget asThrowsTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "throwsTypeIndex = " + throwsTypeIndex; }

	@Override public void intern( Interner interner )
	{
		/* nothing to do */
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( throwsTypeIndex );
	}
}
