package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.NameAndTypeConstant;
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
public final class EnclosingMethodAttribute extends Attribute
{
	public static EnclosingMethodAttribute of( ClassConstant classConstant, Optional<NameAndTypeConstant> methodNameAndTypeConstant )
	{
		return new EnclosingMethodAttribute( classConstant, methodNameAndTypeConstant );
	}

	public static final String name = "EnclosingMethod";
	public static final Kind kind = new Kind( name );

	private final ClassConstant classConstant;
	private final Optional<NameAndTypeConstant> methodNameAndTypeConstant;

	private EnclosingMethodAttribute( ClassConstant classConstant, Optional<NameAndTypeConstant> methodNameAndTypeConstant )
	{
		super( kind );
		this.classConstant = classConstant;
		this.methodNameAndTypeConstant = methodNameAndTypeConstant;
	}

	public ClassConstant classConstant() { return classConstant; }
	public Optional<NameAndTypeConstant> methodNameAndTypeConstant() { return methodNameAndTypeConstant; }

	@Deprecated @Override public EnclosingMethodAttribute asEnclosingMethodAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "class = " + classConstant + ", methodNameAndType = { " + methodNameAndTypeConstant + " }";
	}
}
