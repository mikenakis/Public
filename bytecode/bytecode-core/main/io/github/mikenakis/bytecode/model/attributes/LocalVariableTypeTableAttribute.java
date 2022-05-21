package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
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
 * Represents the "LocalVariableTypeTable" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link CodeAttribute}
 *
 * @author michael.gr
 */
public final class LocalVariableTypeTableAttribute extends KnownAttribute
{
	public static LocalVariableTypeTableAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool, ReadingLocationMap locationMap )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<LocalVariableTypeTableEntry> localVariableTypeTableEntries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			LocalVariableTypeTableEntry entry = LocalVariableTypeTableEntry.read( bufferReader, constantPool, locationMap );
			localVariableTypeTableEntries.add( entry );
		}
		return of( localVariableTypeTableEntries );
	}

	public static LocalVariableTypeTableAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static LocalVariableTypeTableAttribute of( List<LocalVariableTypeTableEntry> localVariableTypeTableEntries )
	{
		return new LocalVariableTypeTableAttribute( localVariableTypeTableEntries );
	}

	public final List<LocalVariableTypeTableEntry> localVariableTypeTableEntries;

	private LocalVariableTypeTableAttribute( List<LocalVariableTypeTableEntry> localVariableTypeTableEntries )
	{
		super( tag_LocalVariableTypeTable );
		this.localVariableTypeTableEntries = localVariableTypeTableEntries;
	}

	@Deprecated @Override public LocalVariableTypeTableAttribute asLocalVariableTypeTableAttribute() { return this; }
	@Override public boolean isOptional() { return true; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return localVariableTypeTableEntries.size() + " entries"; }

	@Override public void intern( Interner interner )
	{
		for( LocalVariableTypeTableEntry localVariableTypeTableEntry : localVariableTypeTableEntries )
			localVariableTypeTableEntry.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( localVariableTypeTableEntries.size() );
		for( LocalVariableTypeTableEntry localVariableTypeTableEntry : localVariableTypeTableEntries )
			localVariableTypeTableEntry.write( bufferWriter, constantPool, locationMap.orElseThrow() );
	}
}
