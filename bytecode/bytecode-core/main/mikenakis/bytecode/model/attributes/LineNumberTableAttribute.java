package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.CodeConstantWriter;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "LineNumberTable" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link CodeAttribute}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LineNumberTableAttribute extends KnownAttribute
{
	public static LineNumberTableAttribute read( AttributeReader attributeReader )
	{
		CodeAttributeReader codeAttributeReader = attributeReader.asCodeAttributeReader();
		int count = attributeReader.readUnsignedShort();
		assert count > 0;
		List<LineNumberTableEntry> entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Instruction startInstruction = codeAttributeReader.readAbsoluteInstruction().orElseThrow();
			int lineNumber = attributeReader.readUnsignedShort();
			var lineNumberEntry = LineNumberTableEntry.of( startInstruction, lineNumber );
			entries.add( lineNumberEntry );
		}
		return of( entries );
	}

	public static LineNumberTableAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static LineNumberTableAttribute of( List<LineNumberTableEntry> entrys )
	{
		return new LineNumberTableAttribute( entrys );
	}

	public final List<LineNumberTableEntry> entrys;

	private LineNumberTableAttribute( List<LineNumberTableEntry> entrys )
	{
		super( tag_LineNumberTable );
		this.entrys = entrys;
	}

	@Deprecated @Override public LineNumberTableAttribute asLineNumberTableAttribute() { return this; }
	@Override public boolean isOptional() { return true; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return entrys.size() + " entries"; }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		CodeConstantWriter codeConstantWriter = constantWriter.asCodeConstantWriter();
		constantWriter.writeUnsignedShort( entrys.size() );
		for( LineNumberTableEntry lineNumber : entrys )
		{
			int location = codeConstantWriter.getLocation( lineNumber.instruction );
			constantWriter.writeUnsignedShort( location );
			constantWriter.writeUnsignedShort( lineNumber.lineNumber );
		}
	}
}
