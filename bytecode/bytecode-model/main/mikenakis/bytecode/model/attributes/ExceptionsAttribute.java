package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "Exceptions" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ExceptionsAttribute extends KnownAttribute
{
	public static ExceptionsAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static ExceptionsAttribute of( List<ClassConstant> exceptionClassConstants )
	{
		return new ExceptionsAttribute( exceptionClassConstants );
	}

	public final List<ClassConstant> exceptionClassConstants;

	private ExceptionsAttribute( List<ClassConstant> exceptionClassConstants )
	{
		super( tag_Exceptions );
		this.exceptionClassConstants = exceptionClassConstants;
	}

	public List<TerminalTypeDescriptor> exceptions() { return exceptionClassConstants.stream().map( c -> ByteCodeHelpers.terminalTypeDescriptorFromInternalName( c.getInternalNameOrDescriptorStringConstant().stringValue() ) ).toList(); }
	@Deprecated @Override public ExceptionsAttribute asExceptionsAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return exceptionClassConstants.size() + " entries"; }
}
