package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.collections.FlagEnum;
import mikenakis.kit.collections.FlagEnumSet;

import java.lang.constant.ClassDesc;
import java.util.Map;

/**
 * Represents a field.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeField extends ByteCodeMember
{
	public static ByteCodeField of( FlagEnumSet<Modifier> modifierSet, String name, @SuppressWarnings( "TypeMayBeWeakened" ) ClassDesc fieldTypeDescriptor )
	{
		String descriptorString = fieldTypeDescriptor.descriptorString();
		return of( modifierSet, Mutf8Constant.of( name ), Mutf8Constant.of( descriptorString ), AttributeSet.of() );
	}

	public static ByteCodeField of( FlagEnumSet<Modifier> modifierSet, Mutf8Constant nameConstant, Mutf8Constant descriptorConstant, AttributeSet attributeSet )
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

	public final FlagEnumSet<Modifier> modifierSet;
	public final Mutf8Constant descriptorConstant;

	private ByteCodeField( FlagEnumSet<Modifier> modifierSet, Mutf8Constant nameConstant, Mutf8Constant descriptorConstant, AttributeSet attributeSet )
	{
		super( nameConstant, attributeSet );
		this.modifierSet = modifierSet;
		this.descriptorConstant = descriptorConstant;
	}

	public ClassDesc descriptor()
	{
		return ClassDesc.ofDescriptor( descriptorConstant.stringValue() );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "accessFlags = " + modifierSet + ", name = " + nameConstant + ", descriptor = " + descriptorConstant;
	}
}
