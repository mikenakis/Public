package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents the "EnclosingMethod" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class EnclosingMethodAttribute extends KnownAttribute
{
	public static EnclosingMethodAttribute of( ClassConstant enclosingClassConstant, Optional<NameAndDescriptorConstant> enclosingMethodNameAndDescriptorConstant )
	{
		return new EnclosingMethodAttribute( enclosingClassConstant, enclosingMethodNameAndDescriptorConstant );
	}

	public final ClassConstant enclosingClassConstant;
	public final Optional<NameAndDescriptorConstant> enclosingMethodNameAndDescriptorConstant;

	private EnclosingMethodAttribute( ClassConstant enclosingClassConstant, Optional<NameAndDescriptorConstant> enclosingMethodNameAndDescriptorConstant )
	{
		super( tag_EnclosingMethod );
		this.enclosingClassConstant = enclosingClassConstant;
		this.enclosingMethodNameAndDescriptorConstant = enclosingMethodNameAndDescriptorConstant;
	}

	public String enclosingClassTypeName() { return enclosingClassConstant.typeName(); }
	@Deprecated @Override public EnclosingMethodAttribute asEnclosingMethodAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "class = " + enclosingClassConstant + ", methodNameAndDescriptor = { " + enclosingMethodNameAndDescriptorConstant + " }"; }
}
