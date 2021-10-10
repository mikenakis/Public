package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents the "Synthetic" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author michael.gr
 */
public final class SyntheticAttribute extends KnownAttribute
{
	public static SyntheticAttribute of()
	{
		return new SyntheticAttribute();
	}

	private SyntheticAttribute()
	{
		super( tag_Synthetic );
	}

	@Deprecated @Override public SyntheticAttribute asSyntheticAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "synthetic"; }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		// nothing to do
	}
}
