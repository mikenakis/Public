package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.FlagSet;
import mikenakis.bytecode.model.FlagEnum;
import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Map;

/**
 * Represents an entry of {@link MethodParametersAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodParameter
{
	public enum Modifier
	{
		Final, Synthetic, Mandated
	}

	public static MethodParameter of( Utf8Constant nameConstant, FlagSet<Modifier> modifierSet )
	{
		return new MethodParameter( nameConstant, modifierSet );
	}

	public static final FlagEnum<Modifier> modifierFlagEnum = FlagEnum.of( Modifier.class, //
		Map.entry( Modifier.Final     /**/, 0x0010 ),   // ACC_FINAL      = 0x0010
		Map.entry( Modifier.Synthetic /**/, 0x1000 ),   // ACC_SYNTHETIC  = 0x1000
		Map.entry( Modifier.Mandated  /**/, 0x8000 ) ); // ACC_MANDATED   = 0x8000

	public final Utf8Constant nameConstant;
	public final FlagSet<Modifier> modifierSet;

	private MethodParameter( Utf8Constant nameConstant, FlagSet<Modifier> modifierSet )
	{
		this.nameConstant = nameConstant;
		this.modifierSet = modifierSet;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "accessFlags = " + modifierSet + ' ' + nameConstant;
	}
}
