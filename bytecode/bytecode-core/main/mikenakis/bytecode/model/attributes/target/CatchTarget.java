package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class CatchTarget extends Target // "catch_target" in jvms-4.7.20.1
{
	public static CatchTarget read( BufferReader bufferReader, int targetTag )
	{
		int exceptionTableIndex = bufferReader.readUnsignedShort();
		//TO-maybe-DO: lookup the actual exception-info and realize a reference to it instead of realizing this index.
		return new CatchTarget( targetTag, exceptionTableIndex );
	}

	public final int exceptionTableIndex;

	private CatchTarget( int tag, int exceptionTableIndex )
	{
		super( tag );
		assert tag == tag_Catch;
		this.exceptionTableIndex = exceptionTableIndex;
	}

	@Deprecated @Override public CatchTarget asCatchTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "exceptionTableIndex = " + exceptionTableIndex; }

	@Override public void intern( Interner interner )
	{
		/* nothing to do */
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( exceptionTableIndex );
	}
}
