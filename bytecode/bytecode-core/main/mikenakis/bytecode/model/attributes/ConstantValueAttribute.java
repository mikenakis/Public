package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the "ConstantValue" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeField}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ConstantValueAttribute extends KnownAttribute
{
	public static ConstantValueAttribute read( AttributeReader attributeReader )
	{
		ValueConstant valueConstant = attributeReader.readIndexAndGetConstant().asValueConstant();
		return of( valueConstant );
	}

	public static ConstantValueAttribute of( ValueConstant valueConstant )
	{
		return new ConstantValueAttribute( valueConstant );
	}

	public final ValueConstant valueConstant;

	private ConstantValueAttribute( ValueConstant valueConstant )
	{
		super( tag_ConstantValue );
		this.valueConstant = valueConstant;
	}

	@Deprecated @Override public ConstantValueAttribute asConstantValueAttribute() { return this; }
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
