package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.java_type_model.MethodDescriptor;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;
import java.util.Objects;

/**
 * An entry of the {@link BootstrapMethodsAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class BootstrapMethod
{
	public static BootstrapMethod of( MethodHandleConstant methodHandleConstant, List<Constant> argumentConstants )
	{
		return new BootstrapMethod( methodHandleConstant, argumentConstants );
	}

	public final MethodHandleConstant methodHandleConstant;
	public final List<Constant> argumentConstants;

	private BootstrapMethod( MethodHandleConstant methodHandleConstant, List<Constant> argumentConstants )
	{
		assert argumentConstants.stream().allMatch( c -> isBootstrapArgumentConstant( c ) );
		this.methodHandleConstant = methodHandleConstant;
		this.argumentConstants = argumentConstants;
	}

	public MethodDescriptor invocationMethodDescriptor() { return ByteCodeHelpers.methodDescriptorFromDescriptorString( ByteCodeHelpers.descriptorStringFromMethodHandleConstantInvocation( methodHandleConstant ) ); }
	public TypeDescriptor ownerTypeDescriptor() { return ByteCodeHelpers.typeDescriptorFromDescriptorString( ByteCodeHelpers.descriptorStringFromMethodHandleConstantOwner( methodHandleConstant ) ); }

	private static boolean isBootstrapArgumentConstant( Constant constant )
	{
		return switch( constant.tag )
			{
				case Constant.tag_Class, Constant.tag_MethodType, Constant.tag_String, Constant.tag_MethodHandle -> true;
				default -> false;
			};
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "methodHandle = " + methodHandleConstant + ", " + argumentConstants.size() + " arguments"; }

	@Deprecated @Override public boolean equals( Object other )
	{
		if( other instanceof BootstrapMethod otherBootstrapMethod )
			return equals( otherBootstrapMethod );
		return false;
	}

	public boolean equals( BootstrapMethod other )
	{
		return methodHandleConstant.equals( other.methodHandleConstant ) && argumentConstants.equals( other.argumentConstants );
	}

	@Override public int hashCode()
	{
		return Objects.hash( methodHandleConstant, argumentConstants );
	}
}
