package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.collections.FlagSet;
import mikenakis.kit.collections.FlagEnum;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
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

	public static MethodParameter of( Mutf8ValueConstant nameConstant, FlagSet<Modifier> modifiers )
	{
		return new MethodParameter( nameConstant, modifiers );
	}

	public static final FlagEnum<Modifier> modifierEnum = FlagEnum.of( Modifier.class, //
		Map.entry( Modifier.Final     /**/, 0x0010 ),   // ACC_FINAL      = 0x0010 -- Indicates that the formal parameter was declared final.
		Map.entry( Modifier.Synthetic /**/, 0x1000 ),   // ACC_SYNTHETIC  = 0x1000 -- Indicates that the formal parameter was not explicitly or implicitly declared in source code, according to the specification of the language in which the source code was written (JLS §13.1). (The formal parameter is an implementation artifact of the compiler which produced this class file.)
		Map.entry( Modifier.Mandated  /**/, 0x8000 ) ); // ACC_MANDATED   = 0x8000 -- Indicates that the formal parameter was implicitly declared in source code, according to the specification of the language in which the source code was written (JLS §13.1). (The formal parameter is mandated by a language specification, so all compilers for the language must emit it.)

	private final Mutf8ValueConstant nameConstant;
	public final FlagSet<Modifier> modifiers;

	private MethodParameter( Mutf8ValueConstant nameConstant, FlagSet<Modifier> modifiers )
	{
		this.nameConstant = nameConstant;
		this.modifiers = modifiers;
	}

	public String name() { return nameConstant.stringValue(); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "accessFlags = " + modifiers + ' ' + nameConstant; }

	public void intern( Interner interner )
	{
		nameConstant.intern( interner );
	}

	public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( nameConstant ) );
		constantWriter.writeUnsignedShort( modifiers.getBits() );
	}
}
