package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.CodeConstantWriter;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
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
	public static LocalVariableTableAttribute read( AttributeReader attributeReader )
	{
		CodeAttributeReader codeAttributeReader = attributeReader.asCodeAttributeReader();
		int count = attributeReader.readUnsignedShort();
		List<LocalVariableTableEntry> localVariables = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Instruction startInstruction = codeAttributeReader.readAbsoluteInstruction().orElseThrow();
			int length = attributeReader.readUnsignedShort();
			int endLocation = codeAttributeReader.locationMap.getLocation( startInstruction ) + length;
			Optional<Instruction> endInstruction = codeAttributeReader.locationMap.getInstruction( endLocation );
			Mutf8ValueConstant nameConstant1 = attributeReader.readIndexAndGetConstant().asMutf8ValueConstant();
			Mutf8ValueConstant descriptorConstant = attributeReader.readIndexAndGetConstant().asMutf8ValueConstant();
			int index = attributeReader.readUnsignedShort();
			LocalVariableTableEntry localVariable = LocalVariableTableEntry.of( startInstruction, endInstruction, nameConstant1, descriptorConstant, index );
			localVariables.add( localVariable );
		}
		return of( localVariables );
	}

	public static LocalVariableTableAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static LocalVariableTableAttribute of( List<LocalVariableTableEntry> entrys )
	{
		return new LocalVariableTableAttribute( entrys );
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

	@Override public void write( ConstantWriter constantWriter )
	{
		CodeConstantWriter codeConstantWriter = constantWriter.asCodeConstantWriter();
		codeConstantWriter.writeUnsignedShort( localVariableTableEntries.size() );
		for( LocalVariableTableEntry localVariableTableEntry : localVariableTableEntries )
			localVariableTableEntry.write( codeConstantWriter );
	}
}
