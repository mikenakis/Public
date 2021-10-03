package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.java_type_model.MethodDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.collections.FlagEnum;
import mikenakis.kit.collections.FlagSet;

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

	public static final FlagEnum<Modifier> modifierEnum = FlagEnum.of( Modifier.class, //
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

	public static ByteCodeMethod of( FlagSet<Modifier> modifiers, MethodPrototype methodPrototype )
	{
		return new ByteCodeMethod( modifiers, Mutf8Constant.of( methodPrototype.methodName ), Mutf8Constant.of( ByteCodeHelpers.descriptorStringFromMethodDescriptor( methodPrototype.descriptor ) ), AttributeSet.of() );
	}

	public static ByteCodeMethod of( FlagSet<Modifier> modifiers, Mutf8Constant methodNameConstant, Mutf8Constant methodDescriptorStringConstant, AttributeSet attributeSet )
	{
		return new ByteCodeMethod( modifiers, methodNameConstant, methodDescriptorStringConstant, attributeSet );
	}

	public final FlagSet<Modifier> modifiers;
	public final Mutf8Constant methodDescriptorStringConstant;

	private ByteCodeMethod( FlagSet<Modifier> modifiers, Mutf8Constant methodNameConstant, Mutf8Constant methodDescriptorStringConstant, AttributeSet attributeSet )
	{
		super( methodNameConstant, attributeSet );
		this.modifiers = modifiers;
		this.methodDescriptorStringConstant = methodDescriptorStringConstant;
	}

	public MethodDescriptor descriptor() { return ByteCodeHelpers.methodDescriptorFromDescriptorString( methodDescriptorStringConstant.stringValue() ); }
	public MethodPrototype prototype() { return MethodPrototype.of( memberNameConstant.stringValue(), descriptor() ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "accessFlags = " + modifiers + ", name = " + memberNameConstant + ", descriptor = " + methodDescriptorStringConstant; }
}
