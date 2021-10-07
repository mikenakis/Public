package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

public final class TypePath // "type_path" in jvms-4.7.20.2
{
	public static TypePath read( AttributeReader attributeReader )
	{
		int entryCount = attributeReader.bufferReader.readUnsignedByte();
		List<TypePathEntry> entries = new ArrayList<>( entryCount );
		for( int i = 0; i < entryCount; i++ )
			entries.add( TypePathEntry.read( attributeReader.bufferReader ) );
		return new TypePath( entries );
	}

	public final List<TypePathEntry> entries;

	public TypePath( List<TypePathEntry> entries )
	{
		this.entries = entries;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return entries.size() + " entries"; }

	public void intern( Interner interner )
	{
		for( TypePathEntry entry : entries )
			entry.intern( interner );
	}

	public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( entries.size() );
		for( TypePathEntry entry : entries )
			entry.write( constantWriter );
	}
}
