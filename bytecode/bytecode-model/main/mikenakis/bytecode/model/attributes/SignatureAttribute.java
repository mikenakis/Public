package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.Mutf8Constant;
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
	public static SignatureAttribute of( String signature )
	{
		return of( Mutf8Constant.of( signature ) );
	}

	public static SignatureAttribute of( Mutf8Constant signatureConstant )
	{
		return new SignatureAttribute( signatureConstant );
	}

	public final Mutf8Constant signatureConstant;

	private SignatureAttribute( Mutf8Constant signatureConstant )
	{
		super( tag_Signature );
		this.signatureConstant = signatureConstant;
	}

	public String signatureString() { return signatureConstant.stringValue(); }
	@Deprecated @Override public SignatureAttribute asSignatureAttribute() { return this; }
	@Override public boolean isOptional() { return true; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "signature = " + signatureConstant.stringValue(); }
}
