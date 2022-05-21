package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the "LineNumberTable" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link CodeAttribute}
 *
 * @author michael.gr
 */
public final class LineNumberTableAttribute extends KnownAttribute
{
	public static LineNumberTableAttribute read( BufferReader bufferReader, ReadingLocationMap readingLocationMap )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<LineNumberTableEntry> lineNumberTableEntries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
			lineNumberTableEntries.add( LineNumberTableEntry.read( bufferReader, readingLocationMap ) );
		return of( lineNumberTableEntries );
	}

	public static LineNumberTableAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static LineNumberTableAttribute of( List<LineNumberTableEntry> lineNumberTableEntries )
	{
		return new LineNumberTableAttribute( lineNumberTableEntries );
	}

	public final List<LineNumberTableEntry> lineNumberTableEntries;

	private LineNumberTableAttribute( List<LineNumberTableEntry> lineNumberTableEntries )
	{
		super( tag_LineNumberTable );
		this.lineNumberTableEntries = lineNumberTableEntries;
	}

	@Deprecated @Override public LineNumberTableAttribute asLineNumberTableAttribute() { return this; }
	@Override public boolean isOptional() { return true; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return lineNumberTableEntries.size() + " entries"; }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( lineNumberTableEntries.size() );
		for( LineNumberTableEntry lineNumberTableEntry : lineNumberTableEntries )
			lineNumberTableEntry.write( bufferWriter, locationMap.orElseThrow() );
	}
}
