package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.java_type_model.MethodDescriptor;
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

	public MethodDescriptor methodDescriptor() { return ByteCodeHelpers.methodDescriptorFromDescriptorString( descriptorConstant.stringValue() ); }
	@Deprecated @Override public MethodTypeConstant asMethodTypeConstant() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "descriptor = " + descriptorConstant; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof MethodTypeConstant kin && equals( kin ); }
	public boolean equals( MethodTypeConstant other ) { return descriptorConstant.equalsMutf8Constant( other.descriptorConstant ); }
	@Override public int hashCode() { return Objects.hash( tag, descriptorConstant ); }
}
