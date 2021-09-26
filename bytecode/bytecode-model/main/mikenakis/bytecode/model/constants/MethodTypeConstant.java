package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ConstantDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_MethodType_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodTypeConstant extends Constant
{
//	public static MethodTypeConstant of( String descriptor )
//	{
//		return of( Mutf8Constant.of( descriptor ) );
//	}

	private Mutf8Constant descriptorConstant; //null means that it has not been set yet.

	public MethodTypeConstant()
	{
		super( tag_MethodType );
	}

	public Mutf8Constant getDescriptorConstant()
	{
		assert descriptorConstant != null;
		return descriptorConstant;
	}

	public void setDescriptorConstant( Mutf8Constant descriptorConstant )
	{
		assert this.descriptorConstant == null;
		assert descriptorConstant != null;
		this.descriptorConstant = descriptorConstant;
	}

	public ConstantDesc constantDescriptor()
	{
		return MethodTypeDesc.ofDescriptor( descriptorConstant.stringValue() );
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
