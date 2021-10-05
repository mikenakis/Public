package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
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
		{
			TypePathEntry typePathEntry = readTypePathEntry( attributeReader.bufferReader );
			entries.add( typePathEntry );
		}
		return new TypePath( entries );
	}

	private static TypePathEntry readTypePathEntry( BufferReader bufferReader )
	{
		int pathKind = bufferReader.readUnsignedByte();
		int argumentIndex = bufferReader.readUnsignedByte();
		return new TypePathEntry( pathKind, argumentIndex );
	}

	public final List<TypePathEntry> entries;

	public TypePath( List<TypePathEntry> entries )
	{
		this.entries = entries;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return entries.size() + " entries"; }

	public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( entries.size() );
		for( TypePathEntry entry : entries )
		{
			constantWriter.writeUnsignedByte( entry.pathKind );
			constantWriter.writeUnsignedByte( entry.argumentIndex );
		}
	}
}
