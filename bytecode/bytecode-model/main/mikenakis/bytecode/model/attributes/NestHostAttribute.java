package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the "NestHost" {@link Attribute} of a java class file.
 * <p>
 * See JVMS 4.7.28 "The NestHost Attribute", https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.7.28
 * <p>
 * This attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * See JVMS Table 4.7-C, "Predefined class file attributes (by location)" https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.7-320
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class NestHostAttribute extends KnownAttribute
{
	public static NestHostAttribute of( ClassConstant classConstant )
	{
		return new NestHostAttribute( classConstant );
	}

	public final ClassConstant hostClassConstant;

	private NestHostAttribute( ClassConstant hostClassConstant )
	{
		super( tag_NestHost );
		this.hostClassConstant = hostClassConstant;
	}

	public TerminalTypeDescriptor hostClass() { return hostClassConstant.terminalTypeDescriptor(); }

	@Deprecated @Override public NestHostAttribute asNestHostAttribute() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return " -> " + hostClassConstant;
	}
}
