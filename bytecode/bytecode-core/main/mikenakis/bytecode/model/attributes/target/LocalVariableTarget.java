package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

public final class LocalVariableTarget extends Target // "localvar_target" in jvms-4.7.20.1
{
	public static LocalVariableTarget read( BufferReader bufferReader, int targetTag )
	{
		int entryCount = bufferReader.readUnsignedShort();
		assert entryCount > 0;
		List<LocalVariableTargetEntry> entries = new ArrayList<>( entryCount );
		for( int j = 0; j < entryCount; j++ )
		{
			int startPc = bufferReader.readUnsignedShort();
			int length = bufferReader.readUnsignedShort();
			int index = bufferReader.readUnsignedShort();
			LocalVariableTargetEntry entry = new LocalVariableTargetEntry( startPc, length, index );
			entries.add( entry );
		}
		return new LocalVariableTarget( targetTag, entries );
	}

	public final List<LocalVariableTargetEntry> entries;

	private LocalVariableTarget( int tag, List<LocalVariableTargetEntry> entries )
	{
		super( tag );
		assert tag == tag_LocalVariable || tag == tag_ResourceLocalVariable;
		this.entries = entries;
	}

	@Deprecated @Override public LocalVariableTarget asLocalVariableTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return entries.size() + " entries"; }

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( entries.size() );
		for( LocalVariableTargetEntry entry : entries )
			entry.write( constantWriter );
	}
}
