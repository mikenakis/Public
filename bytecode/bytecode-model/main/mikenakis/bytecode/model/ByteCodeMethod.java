package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Map;

/**
 * Represents a method.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeMethod extends ByteCodeMember
{
	public enum Modifier
	{
		Public, Private, Protected, Static, Final, Synchronized, Bridge, Varargs, Native, Abstract, Strict, Synthetic
	}

	public static final FlagEnum<Modifier> modifierFlagsEnum = FlagEnum.of( Modifier.class, //
		Map.entry( Modifier.Public       /**/, 0x0001 ), // ACC_PUBLIC       = 0x0001
		Map.entry( Modifier.Private      /**/, 0x0002 ), // ACC_PRIVATE      = 0x0002
		Map.entry( Modifier.Protected    /**/, 0x0004 ), // ACC_PROTECTED    = 0x0004
		Map.entry( Modifier.Static       /**/, 0x0008 ), // ACC_STATIC       = 0x0008
		Map.entry( Modifier.Final        /**/, 0x0010 ), // ACC_FINAL        = 0x0010
		Map.entry( Modifier.Synchronized /**/, 0x0020 ), // ACC_SYNCHRONIZED = 0x0020
		Map.entry( Modifier.Bridge       /**/, 0x0040 ), // ACC_BRIDGE       = 0x0040
		Map.entry( Modifier.Varargs      /**/, 0x0080 ), // ACC_VARARGS      = 0x0080
		Map.entry( Modifier.Native       /**/, 0x0100 ), // ACC_NATIVE       = 0x0100
		Map.entry( Modifier.Abstract     /**/, 0x0400 ), // ACC_ABSTRACT     = 0x0400
		Map.entry( Modifier.Strict       /**/, 0x0800 ), // ACC_STRICT       = 0x0800
		Map.entry( Modifier.Synthetic    /**/, 0x1000 )  // ACC_SYNTHETIC    = 0x1000
	);

	public static ByteCodeMethod of( FlagSet<Modifier> modifierSet, Utf8Constant nameConstant, Utf8Constant descriptorConstant, AttributeSet attributeSet )
	{
		return new ByteCodeMethod( modifierSet, nameConstant, descriptorConstant, attributeSet );
	}

	private final FlagSet<Modifier> modifierSet;

	private ByteCodeMethod( FlagSet<Modifier> modifierSet, Utf8Constant nameConstant, Utf8Constant descriptorConstant, AttributeSet attributeSet )
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
