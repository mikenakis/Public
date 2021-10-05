package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the "Source File" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SourceFileAttribute extends KnownAttribute
{
	public static SourceFileAttribute read( AttributeReader attributeReader )
	{
		Mutf8ValueConstant valueConstant = attributeReader.readIndexAndGetConstant().asMutf8ValueConstant();
		return of( valueConstant );
	}

	public static SourceFileAttribute of( String sourceFile )
	{
		Mutf8ValueConstant valueConstant = Mutf8ValueConstant.of( sourceFile );
		return of( valueConstant );
	}

	public static SourceFileAttribute of( Mutf8ValueConstant sourceFileConstant )
	{
		return new SourceFileAttribute( sourceFileConstant );
	}

	private final Mutf8ValueConstant valueConstant;

	private SourceFileAttribute( Mutf8ValueConstant valueConstant )
	{
		super( tag_SourceFile );
		this.valueConstant = valueConstant;
	}

	public String value() { return valueConstant.stringValue(); }
	@Deprecated @Override public SourceFileAttribute asSourceFileAttribute() { return this; }
	@Override public boolean isOptional() { return true; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "value = " + valueConstant.toString(); }

	@Override public void intern( Interner interner )
	{
		valueConstant.intern( interner );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( valueConstant ) );
	}
}
