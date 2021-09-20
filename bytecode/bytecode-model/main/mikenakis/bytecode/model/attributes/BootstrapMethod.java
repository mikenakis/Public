package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ConstantDesc;
import java.lang.constant.DynamicConstantDesc;
import java.util.Collections;
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

	private final MethodHandleConstant methodHandleConstant;
	private final List<Constant> argumentConstants;

	private BootstrapMethod( MethodHandleConstant methodHandleConstant, List<Constant> argumentConstants )
	{
		this.methodHandleConstant = methodHandleConstant;
		this.argumentConstants = argumentConstants;
	}

	public MethodHandleConstant methodHandleConstant() { return methodHandleConstant; }
	public List<Constant> argumentConstants() { return Collections.unmodifiableList( argumentConstants ); }

	public DynamicConstantDesc<?> constantDescriptor()
	{
		ConstantDesc[] argumentConstantDescriptors = new ConstantDesc[argumentConstants.size()];
		for( int i = 0; i < argumentConstantDescriptors.length; i++ )
			argumentConstantDescriptors[i] = argumentConstants.get( i ).constantDescriptor();
		return DynamicConstantDesc.of( methodHandleConstant.descriptor(), argumentConstantDescriptors );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "methodHandle = " + methodHandleConstant + ", " + argumentConstants.size() + " arguments";
	}
}
