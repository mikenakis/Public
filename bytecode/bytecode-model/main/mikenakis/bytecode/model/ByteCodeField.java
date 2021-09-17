package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Map;

/**
 * Represents a field.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeField extends ByteCodeMember
{
	public static ByteCodeField of( FlagSet<Modifier> modifierSet, String name, String descriptor )
	{
		return of( modifierSet, Utf8Constant.of( name ), Utf8Constant.of( descriptor ), AttributeSet.of() );
	}

	public static ByteCodeField of( FlagSet<Modifier> modifierSet, Utf8Constant nameConstant, Utf8Constant descriptorConstant, AttributeSet attributeSet )
	{
		return new ByteCodeField( modifierSet, nameConstant, descriptorConstant, attributeSet );
	}

	public enum Modifier
	{
		Public, Private, Protected, Static, Final, Volatile, Transient, Synthetic, Enum
	}

	public static final FlagEnum<Modifier> modifierFlagsEnum = FlagEnum.of( Modifier.class, //
		Map.entry( Modifier.Public    /**/, 0x0001 ),   // ACC_PUBLIC    = 0x0001;
		Map.entry( Modifier.Private   /**/, 0x0002 ),   // ACC_PRIVATE   = 0x0002;
		Map.entry( Modifier.Protected /**/, 0x0004 ),   // ACC_PROTECTED = 0x0004;
		Map.entry( Modifier.Static    /**/, 0x0008 ),   // ACC_STATIC    = 0x0008;
		Map.entry( Modifier.Final     /**/, 0x0010 ),   // ACC_FINAL     = 0x0010;
		Map.entry( Modifier.Volatile  /**/, 0x0040 ),   // ACC_VOLATILE  = 0x0040;
		Map.entry( Modifier.Transient /**/, 0x0080 ),   // ACC_TRANSIENT = 0x0080;
		Map.entry( Modifier.Synthetic /**/, 0x1000 ),   // ACC_SYNTHETIC = 0x1000;
		Map.entry( Modifier.Enum      /**/, 0x4000 ) ); // ACC_ENUM      = 0x4000;

	private final FlagSet<Modifier> modifierSet;

	private ByteCodeField( FlagSet<Modifier> modifierSet, Utf8Constant nameConstant, Utf8Constant descriptorConstant, AttributeSet attributeSet )
	{
		super( nameConstant, descriptorConstant, attributeSet );
		this.modifierSet = modifierSet;
	}

	@Override public FlagSet<?> modifierSet()
	{
		return modifierSet;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "accessFlags = " + modifierSet + ", name = " + nameConstant + ", descriptor = " + descriptorConstant;
	}
}
