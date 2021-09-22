package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ConstantDesc;
import java.lang.constant.DynamicConstantDesc;
import java.util.List;

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

	private static boolean isBootstrapArgumentConstant( Constant constant )
	{
		return switch( constant.tag )
			{
				case Constant.tagClass, Constant.tagMethodType, Constant.tagString, Constant.tagMethodHandle -> true;
				default -> false;
			};
	}

	public DynamicConstantDesc<?> constantDescriptor()
	{
		ConstantDesc[] argumentConstantDescriptors = new ConstantDesc[argumentConstants.size()];
		for( int i = 0; i < argumentConstantDescriptors.length; i++ )
		{
			Constant constant = argumentConstants.get( i );
			argumentConstantDescriptors[i] = switch( constant.tag )
				{
					case Constant.tagClass -> constant.asClassConstant().constantDescriptor();
					case Constant.tagMethodType -> constant.asMethodTypeConstant().constantDescriptor();
					case Constant.tagString -> constant.asStringConstant().constantDescriptor();
					case Constant.tagMethodHandle -> constant.asMethodHandleConstant().constantDescriptor();
					default -> throw new AssertionError( constant );
				};
		}
		return DynamicConstantDesc.of( methodHandleConstant.descriptor(), argumentConstantDescriptors );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "methodHandle = " + methodHandleConstant + ", " + argumentConstants.size() + " arguments";
	}
}
