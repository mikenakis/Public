package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.descriptors.TerminalTypeDescriptor;
import mikenakis.kit.collections.FlagSet;
import mikenakis.kit.collections.FlagEnum;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Map;
import java.util.Optional;

/**
 * Represents an entry of {@link InnerClassesAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InnerClass
{
	public static InnerClass of( ClassConstant innerClassConstant, Optional<ClassConstant> outerClassConstant, Optional<Mutf8Constant> innerNameConstant, //
		FlagSet<InnerClassModifier> modifiers )
	{
		return new InnerClass( innerClassConstant, outerClassConstant, innerNameConstant, modifiers );
	}

	public enum InnerClassModifier
	{
		Public,     // Marked or implicitly public in source.
		Private,    // Marked private in source.
		Protected,  // Marked protected in source.
		Static,     // Marked or implicitly static in source.
		Final,      // Marked final in source.
		Interface,  // Was an interface in source.
		Abstract,   // Marked or implicitly abstract in source.
		Synthetic,  // Declared synthetic; not present in the source code.
		Annotation, // Declared as an annotation type.
		Enum,       // Declared as an enum type.
	}

	public static final FlagEnum<InnerClassModifier> innerClassModifierFlagsEnum = FlagEnum.of( InnerClassModifier.class, //
		Map.entry( InnerClassModifier.Public    , 0x0001 ),   // ACC_PUBLIC
		Map.entry( InnerClassModifier.Private   , 0x0002 ),   // ACC_PRIVATE
		Map.entry( InnerClassModifier.Protected , 0x0004 ),   // ACC_PROTECTED
		Map.entry( InnerClassModifier.Static    , 0x0008 ),   // ACC_STATIC
		Map.entry( InnerClassModifier.Final     , 0x0010 ),   // ACC_FINAL
		Map.entry( InnerClassModifier.Interface , 0x0200 ),   // ACC_INTERFACE
		Map.entry( InnerClassModifier.Abstract  , 0x0400 ),   // ACC_ABSTRACT
		Map.entry( InnerClassModifier.Synthetic , 0x1000 ),   // ACC_SYNTHETIC
		Map.entry( InnerClassModifier.Annotation, 0x2000 ),   // ACC_
		Map.entry( InnerClassModifier.Enum      , 0x4000 ) ); // ACC_ENUM

	public final ClassConstant innerClassConstant;
	public final Optional<ClassConstant> outerClassConstant;
	public final Optional<Mutf8Constant> innerNameConstant;
	public final FlagSet<InnerClassModifier> modifiers;

	private InnerClass( ClassConstant innerClassConstant, Optional<ClassConstant> outerClassConstant, Optional<Mutf8Constant> innerNameConstant, //
		FlagSet<InnerClassModifier> modifiers )
	{
		this.innerClassConstant = innerClassConstant;
		this.outerClassConstant = outerClassConstant;
		this.innerNameConstant = innerNameConstant;
		this.modifiers = modifiers;
	}

	public TerminalTypeDescriptor innerType() { return innerClassConstant.terminalTypeDescriptor(); }
	public Optional<TerminalTypeDescriptor> outerType() { return outerClassConstant.map( c -> c.terminalTypeDescriptor() ); }
	public Optional<String> innerName() { return innerNameConstant.map( c -> c.stringValue() ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "outerClass = " + outerClassConstant + " accessFlags = " + modifiers + " innerClass = " + innerClassConstant + " innerName = " + innerNameConstant;
	}
}
