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
	public static InvokeDynamicConstant of( int bootstrapMethodIndex, NameAndTypeConstant nameAndTypeConstant )
	{
		return new InvokeDynamicConstant( bootstrapMethodIndex, nameAndTypeConstant );
	}

	public static final int TAG = 18; // JVMS::CONSTANT_InvokeDynamic_info
	public static final String tagName = "InvokeDynamic";

	private final int bootstrapMethodIndex; //TODO: get rid of the bootstrap method index, reference the actual bootstrap method here.
	private final NameAndTypeConstant nameAndTypeConstant;

	private InvokeDynamicConstant( int bootstrapMethodIndex, NameAndTypeConstant nameAndTypeConstant )
	{
		super( TAG );
		this.bootstrapMethodIndex = bootstrapMethodIndex; //byteCodeType.getIndexOfBootstrapMethod( bootstrapMethod );
		this.nameAndTypeConstant = nameAndTypeConstant;
	}

	public int bootstrapMethodIndex() { return bootstrapMethodIndex; }
	public NameAndTypeConstant nameAndTypeConstant() { return nameAndTypeConstant; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "bootstrapMethod " + bootstrapMethodIndex + ", nameAndType = " + nameAndTypeConstant;
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
			if( !nameAndTypeConstant.equalsNameAndTypeConstant( otherInvokeDynamicConstant.nameAndTypeConstant ) )
				return false;
			return true;
		}
		return false;
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, bootstrapMethodIndex, nameAndTypeConstant );
	}

	public BootstrapMethod getBootstrapMethod( ByteCodeType byteCodeType )
	{
		return byteCodeType.getBootstrapMethodByIndex( bootstrapMethodIndex );
	}
}
