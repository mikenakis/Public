package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.DynamicCallSiteDesc;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_InvokeDynamic_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InvokeDynamicConstant extends Constant
{
	public static InvokeDynamicConstant of( BootstrapMethod bootstrapMethod, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		InvokeDynamicConstant invokeDynamicConstant = new InvokeDynamicConstant();
		invokeDynamicConstant.setBootstrapMethod( bootstrapMethod );
		invokeDynamicConstant.setNameAndDescriptorConstant( nameAndDescriptorConstant );
		return invokeDynamicConstant;
	}

	private NameAndDescriptorConstant nameAndDescriptorConstant; // null means that it has not been set yet.
	private BootstrapMethod bootstrapMethod; // null means that it has not been set yet.

	public InvokeDynamicConstant()
	{
		super( tag_InvokeDynamic );
	}

	public BootstrapMethod getBootstrapMethod()
	{
		assert bootstrapMethod != null;
		return bootstrapMethod;
	}

	public void setBootstrapMethod( BootstrapMethod bootstrapMethod )
	{
		assert this.bootstrapMethod == null;
		assert bootstrapMethod != null;
		this.bootstrapMethod = bootstrapMethod;
	}

	public NameAndDescriptorConstant getNameAndDescriptorConstant()
	{
		assert nameAndDescriptorConstant != null;
		return nameAndDescriptorConstant;
	}

	public void setNameAndDescriptorConstant( NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		assert this.nameAndDescriptorConstant == null;
		assert nameAndDescriptorConstant != null;
		this.nameAndDescriptorConstant = nameAndDescriptorConstant;
	}

//	public MethodDescriptor methodDescriptor()
//	{
//		MethodTypeDesc methodTypeDesc = MethodTypeDesc.ofDescriptor( nameAndDescriptorConstant.getDescriptorConstant().stringValue() );
//		ClassDesc returnClassDesc = methodTypeDesc.returnType();
//		TypeDescriptor typeDescriptor = TypeDescriptor.
//		MethodDescriptor.of( methodTypeDesc.)
//	}

	public DynamicCallSiteDesc descriptor()
	{
		DynamicConstantDesc<?> dynamicConstantDescriptor = getBootstrapMethod().constantDescriptor();
		MethodTypeDesc methodTypeDesc = MethodTypeDesc.ofDescriptor( nameAndDescriptorConstant.getDescriptorConstant().stringValue() );
		return DynamicCallSiteDesc.of( dynamicConstantDescriptor.bootstrapMethod(), nameAndDescriptorConstant.getNameConstant().stringValue(), methodTypeDesc, dynamicConstantDescriptor.bootstrapArgs() );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "bootstrapMethod nameAndDescriptor = " + nameAndDescriptorConstant; //TODO add more
	}

	@Deprecated @Override public InvokeDynamicConstant asInvokeDynamicConstant() { return this; }

	@Deprecated @Override public boolean equals( Object other )
	{
		if( other instanceof InvokeDynamicConstant otherInvokeDynamicConstant )
			return equals( otherInvokeDynamicConstant );
		return false;
	}

	public boolean equals( InvokeDynamicConstant other )
	{
		if( !nameAndDescriptorConstant.equalsNameAndDescriptorConstant( other.nameAndDescriptorConstant ) )
			return false;
		if( !getBootstrapMethod().equals( other.getBootstrapMethod() ) )
			return false;
		return true;
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, nameAndDescriptorConstant, getBootstrapMethod() );
	}
}
