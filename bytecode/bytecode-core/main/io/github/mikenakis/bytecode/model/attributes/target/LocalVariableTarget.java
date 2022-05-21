package io.github.mikenakis.bytecode.model.attributes.target;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class LocalVariableTarget extends Target // "localvar_target" in jvms-4.7.20.1
{
	public static LocalVariableTarget read( BufferReader bufferReader, ReadingLocationMap locationMap, int targetTag )
	{
		int entryCount = bufferReader.readUnsignedShort();
		assert entryCount > 0;
		List<LocalVariableTargetEntry> entries = new ArrayList<>( entryCount );
		for( int i = 0; i < entryCount; i++ )
			entries.add( LocalVariableTargetEntry.read( bufferReader, locationMap ) );
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

	@Override public void intern( Interner interner )
	{
		for( LocalVariableTargetEntry entry : entries )
			entry.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( entries.size() );
		for( LocalVariableTargetEntry entry : entries )
			entry.write( bufferWriter, locationMap.orElseThrow() );
	}
}
