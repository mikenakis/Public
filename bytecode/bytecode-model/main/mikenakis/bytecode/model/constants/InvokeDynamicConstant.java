package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.Constant;
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
	public static InvokeDynamicConstant of( int bootstrapMethodIndex, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		return new InvokeDynamicConstant( bootstrapMethodIndex, nameAndDescriptorConstant );
	}

	private final int bootstrapMethodIndex; //TODO: get rid of the bootstrap method index, reference the actual bootstrap method here.
	private final NameAndDescriptorConstant nameAndDescriptorConstant;

	private InvokeDynamicConstant( int bootstrapMethodIndex, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		super( Tag.InvokeDynamic );
		this.bootstrapMethodIndex = bootstrapMethodIndex;
		this.nameAndDescriptorConstant = nameAndDescriptorConstant;
	}

	public int bootstrapMethodIndex() { return bootstrapMethodIndex; }
	public NameAndDescriptorConstant nameAndDescriptorConstant() { return nameAndDescriptorConstant; }

	public DynamicCallSiteDesc descriptor( ByteCodeType byteCodeType ) //TODO: get rid of the ByteCodeType parameter.
	{
		DynamicConstantDesc<?> dynamicConstantDescriptor = byteCodeType.getBootstrapMethodByIndex( bootstrapMethodIndex ).constantDescriptor();
		MethodTypeDesc methodTypeDesc = MethodTypeDesc.ofDescriptor( nameAndDescriptorConstant.descriptorConstant().stringValue() );
		return DynamicCallSiteDesc.of( dynamicConstantDescriptor.bootstrapMethod(), nameAndDescriptorConstant.nameConstant().stringValue(), methodTypeDesc, dynamicConstantDescriptor.bootstrapArgs() );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "bootstrapMethod " + bootstrapMethodIndex + ", nameAndDescriptor = " + nameAndDescriptorConstant;
	}

	@Deprecated @Override public InvokeDynamicConstant asInvokeDynamicConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof InvokeDynamicConstant otherInvokeDynamicConstant )
		{
			if( bootstrapMethodIndex != otherInvokeDynamicConstant.bootstrapMethodIndex )
				return false;
			if( !nameAndDescriptorConstant.equalsNameAndDescriptorConstant( otherInvokeDynamicConstant.nameAndDescriptorConstant ) )
				return false;
			return true;
		}
		return false;
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, bootstrapMethodIndex, nameAndDescriptorConstant );
	}
}
