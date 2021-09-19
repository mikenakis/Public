package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

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

	public static final int TAG = 18; // JVMS::CONSTANT_InvokeDynamic_info

	private final int bootstrapMethodIndex; //TODO: get rid of the bootstrap method index, reference the actual bootstrap method here.
	private final NameAndDescriptorConstant nameAndDescriptorConstant;

	private InvokeDynamicConstant( int bootstrapMethodIndex, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		super( TAG );
		this.bootstrapMethodIndex = bootstrapMethodIndex; //byteCodeType.getIndexOfBootstrapMethod( bootstrapMethod );
		this.nameAndDescriptorConstant = nameAndDescriptorConstant;
	}

	public int bootstrapMethodIndex() { return bootstrapMethodIndex; }
	public NameAndDescriptorConstant nameAndDescriptorConstant() { return nameAndDescriptorConstant; }

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

	public BootstrapMethod getBootstrapMethod( ByteCodeType byteCodeType )
	{
		return byteCodeType.getBootstrapMethodByIndex( bootstrapMethodIndex );
	}
}
