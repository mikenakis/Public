package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.DirectMethodHandleDesc;
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

	private final MethodHandleConstant methodHandleConstant;
	public final List<Constant> argumentConstants;

	private BootstrapMethod( MethodHandleConstant methodHandleConstant, List<Constant> argumentConstants )
	{
		assert argumentConstants.stream().allMatch( c -> isBootstrapArgumentConstant( c ) );
		this.methodHandleConstant = methodHandleConstant;
		this.argumentConstants = argumentConstants;
	}

	public DirectMethodHandleDesc directMethodHandleDesc() { return ByteCodeHelpers.directMethodHandleDescFromMethodHandleConstant( methodHandleConstant ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "methodHandle = " + methodHandleConstant + ", " + argumentConstants.size() + " arguments"; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof BootstrapMethod kin && equals( kin ); }
	public boolean equals( BootstrapMethod other ) { return methodHandleConstant.equals( other.methodHandleConstant ) && argumentConstants.equals( other.argumentConstants ); }
	@Override public int hashCode() { return Objects.hash( methodHandleConstant, argumentConstants ); }

	private static boolean isBootstrapArgumentConstant( Constant constant )
	{
		return switch( constant.tag )
			{
				case Constant.tag_Class, Constant.tag_MethodType, Constant.tag_String, Constant.tag_MethodHandle -> true;
				default -> false;
			};
	}

	public void intern( Interner interner )
	{
		methodHandleConstant.intern( interner );
		for( Constant constant : argumentConstants )
			constant.intern( interner );
	}

	public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( methodHandleConstant ) );
		constantWriter.writeUnsignedShort( argumentConstants.size() );
		for( Constant argumentConstant : argumentConstants )
			constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( argumentConstant ) );
	}
}
