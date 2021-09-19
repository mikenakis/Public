package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_MethodType_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodTypeConstant extends Constant
{
	public static MethodTypeConstant of( Mutf8Constant descriptorConstant )
	{
		return new MethodTypeConstant( descriptorConstant );
	}

	public static MethodTypeConstant of( String descriptor )
	{
		return of( Mutf8Constant.of( descriptor ) );
	}

	public static final int TAG = 16; // JVMS::CONSTANT_MethodType_info

	public final Mutf8Constant descriptorConstant;

	private MethodTypeConstant( Mutf8Constant descriptorConstant )
	{
		super( TAG );
		this.descriptorConstant = descriptorConstant;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "descriptor = " + descriptorConstant;
	}

	@Deprecated @Override public MethodTypeConstant asMethodTypeConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof MethodTypeConstant otherMethodTypeConstant )
			return descriptorConstant.equalsMutf8Constant( otherMethodTypeConstant.descriptorConstant );
		return false;
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, descriptorConstant );
	}
}
