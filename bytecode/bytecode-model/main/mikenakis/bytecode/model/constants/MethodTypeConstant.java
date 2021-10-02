package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.descriptors.MethodDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_MethodType_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodTypeConstant extends Constant
{
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

	public MethodDescriptor methodDescriptor() { return MethodDescriptor.ofDescriptorString( descriptorConstant.stringValue() ); }
	@Deprecated @Override public MethodTypeConstant asMethodTypeConstant() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "descriptor = " + descriptorConstant;
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
