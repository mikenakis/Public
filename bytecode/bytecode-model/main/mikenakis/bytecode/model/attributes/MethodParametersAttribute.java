package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the "MethodParameters" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodParametersAttribute extends KnownAttribute
{
	public static MethodParametersAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static MethodParametersAttribute of( List<MethodParameter> methodParameters )
	{
		return new MethodParametersAttribute( methodParameters );
	}

	public final List<MethodParameter> methodParameters;

	private MethodParametersAttribute( List<MethodParameter> methodParameters )
	{
		super( tagMethodParameters );
		this.methodParameters = methodParameters;
	}

	@Deprecated @Override public MethodParametersAttribute asMethodParametersAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return methodParameters.size() + " entries";
	}
}
