package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the "Signature" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SignatureAttribute extends KnownAttribute
{
	public static SignatureAttribute read( AttributeReader attributeReader )
	{
		Mutf8ValueConstant signatureConstant = attributeReader.readIndexAndGetConstant().asMutf8ValueConstant();
		return of( signatureConstant );
	}

	public static SignatureAttribute of( String signature )
	{
		return of( Mutf8ValueConstant.of( signature ) );
	}

	public static SignatureAttribute of( Mutf8ValueConstant signatureConstant )
	{
		return new SignatureAttribute( signatureConstant );
	}

	private final Mutf8ValueConstant signatureConstant;

	private SignatureAttribute( Mutf8ValueConstant signatureConstant )
	{
		super( tag_Signature );
		this.signatureConstant = signatureConstant;
	}

	public String signatureString() { return signatureConstant.stringValue(); }
	@Deprecated @Override public SignatureAttribute asSignatureAttribute() { return this; }
	@Override public boolean isOptional() { return true; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "signature = " + signatureConstant.stringValue(); }

	@Override public void intern( Interner interner )
	{
		signatureConstant.intern( interner );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( signatureConstant ) );
	}
}
