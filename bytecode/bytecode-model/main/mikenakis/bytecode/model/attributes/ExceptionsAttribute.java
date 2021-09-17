package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.Collections;
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
public final class ExceptionsAttribute extends Attribute
{
	public static ExceptionsAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static ExceptionsAttribute of( List<ClassConstant> exceptionClassConstants )
	{
		return new ExceptionsAttribute( exceptionClassConstants );
	}

	public static final String name = "Exceptions";
	public static final Kind kind = new Kind( name );

	private final List<ClassConstant> exceptionClassConstants;

	private ExceptionsAttribute( List<ClassConstant> exceptionClassConstants )
	{
		super( kind );
		this.exceptionClassConstants = exceptionClassConstants;
	}

	public List<ClassConstant> exceptionClassConstants()
	{
		return Collections.unmodifiableList( exceptionClassConstants );
	}

	@Deprecated @Override public ExceptionsAttribute asExceptionsAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return exceptionClassConstants.size() + " entries";
	}
}
