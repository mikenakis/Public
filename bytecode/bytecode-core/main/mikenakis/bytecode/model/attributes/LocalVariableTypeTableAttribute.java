package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.CodeConstantWriter;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "LocalVariableTypeTable" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link CodeAttribute}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableTypeTableAttribute extends KnownAttribute
{
	public static LocalVariableTypeTableAttribute read( AttributeReader attributeReader )
	{
		CodeAttributeReader codeAttributeReader = attributeReader.asCodeAttributeReader();
		int count = attributeReader.readUnsignedShort();
		assert count > 0;
		List<LocalVariableTypeTableEntry> localVariableTypeTableEntries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			LocalVariableTypeTableEntry entry = LocalVariableTypeTableEntry.read( codeAttributeReader );
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

	@Override public void write( ConstantWriter constantWriter )
	{
		CodeConstantWriter codeConstantWriter = constantWriter.asCodeConstantWriter();
		codeConstantWriter.writeUnsignedShort( localVariableTypeTableEntries.size() );
		for( LocalVariableTypeTableEntry localVariableTypeTableEntry : localVariableTypeTableEntries )
			localVariableTypeTableEntry.write( codeConstantWriter );
	}
}
