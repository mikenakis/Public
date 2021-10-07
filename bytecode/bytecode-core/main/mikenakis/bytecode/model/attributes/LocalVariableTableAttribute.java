package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.reading.ReadingLocationMap;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the "LocalVariableTable" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link CodeAttribute}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableTableAttribute extends KnownAttribute
{
	public static LocalVariableTableAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool, ReadingLocationMap locationMap )
	{
		int count = bufferReader.readUnsignedShort();
		List<LocalVariableTableEntry> localVariables = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Instruction startInstruction = locationMap.getInstruction( bufferReader.readUnsignedShort() ).orElseThrow();
			int length = bufferReader.readUnsignedShort();
			int endLocation = locationMap.getLocation( startInstruction ) + length;
			Optional<Instruction> endInstruction = locationMap.getInstruction( endLocation );
			Mutf8ValueConstant nameConstant1 = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
			Mutf8ValueConstant descriptorConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
			int index = bufferReader.readUnsignedShort();
			LocalVariableTableEntry localVariableTableEntry = LocalVariableTableEntry.of( startInstruction, endInstruction, nameConstant1, descriptorConstant, index );
			localVariables.add( localVariableTableEntry );
		}
		return of( localVariables );
	}

	public static LocalVariableTableAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static LocalVariableTableAttribute of( List<LocalVariableTableEntry> localVariableTableEntries )
	{
		return new LocalVariableTableAttribute( localVariableTableEntries );
	}

	public final List<LocalVariableTableEntry> localVariableTableEntries;

	private LocalVariableTableAttribute( List<LocalVariableTableEntry> localVariableTableEntries )
	{
		super( tag_LocalVariableTable );
		this.localVariableTableEntries = localVariableTableEntries;
	}

	@Deprecated @Override public LocalVariableTableAttribute asLocalVariableTableAttribute() { return this; }
	@Override public boolean isOptional() { return true; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return localVariableTableEntries.size() + " entries"; }

	@Override public void intern( Interner interner )
	{
		for( LocalVariableTableEntry localVariableTableEntry : localVariableTableEntries )
			localVariableTableEntry.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( localVariableTableEntries.size() );
		for( LocalVariableTableEntry localVariableTableEntry : localVariableTableEntries )
			localVariableTableEntry.write( bufferWriter, constantPool, locationMap.orElseThrow() );
	}
}
