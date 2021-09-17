package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.FlagSet;
import mikenakis.bytecode.model.FlagEnum;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.Utf8Constant;
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
	public static InnerClass of( ClassConstant innerClassConstant, Optional<ClassConstant> outerClassConstant, Optional<Utf8Constant> innerNameConstant, //
		FlagSet<InnerClassModifier> modifierSet )
	{
		return new InnerClass( innerClassConstant, outerClassConstant, innerNameConstant, modifierSet );
	}

	//FIXME XXX TODO use FlagsEnum for the following!
	//@formatter:off
	public static final int ACC_PUBLIC	    = 0x0001; // Marked or implicitly public in source.
	public static final int ACC_PRIVATE	    = 0x0002; // Marked private in source.
	public static final int ACC_PROTECTED   = 0x0004; // Marked protected in source.
	public static final int ACC_STATIC	    = 0x0008; // Marked or implicitly static in source.
	public static final int ACC_FINAL	    = 0x0010; // Marked final in source.
	public static final int ACC_INTERFACE   = 0x0200; // Was an interface in source.
	public static final int ACC_ABSTRACT    = 0x0400; // Marked or implicitly abstract in source.
	public static final int ACC_SYNTHETIC   = 0x1000; // Declared synthetic; not present in the source code.
	public static final int ACC_ANNOTATION  = 0x2000; // Declared as an annotation type.
	public static final int ACC_ENUM	    = 0x4000; // Declared as an enum type.
	//@formatter:on

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
		Map.entry( InnerClassModifier.Public    , 0x0001 ),   // ACC_PUBLIC       = 0x0001
		Map.entry( InnerClassModifier.Private   , 0x0002 ),   // ACC_PRIVATE      = 0x0002
		Map.entry( InnerClassModifier.Protected , 0x0004 ),   // ACC_PROTECTED    = 0x0004
		Map.entry( InnerClassModifier.Static    , 0x0008 ),   // ACC_STATIC       = 0x0008
		Map.entry( InnerClassModifier.Final     , 0x0010 ),   // ACC_FINAL        = 0x0010
		Map.entry( InnerClassModifier.Interface , 0x0200 ),   // ACC_INTERFACE    = 0x0200
		Map.entry( InnerClassModifier.Abstract  , 0x0400 ),   // ACC_ABSTRACT     = 0x0400
		Map.entry( InnerClassModifier.Synthetic , 0x1000 ),   // ACC_SYNTHETIC    = 0x1000
		Map.entry( InnerClassModifier.Annotation, 0x2000 ),   // ACC_ANNOTATION   = 0x2000
		Map.entry( InnerClassModifier.Enum      , 0x4000 ) ); // ACC_ENUM         = 0x4000

	private final ClassConstant innerClassConstant;
	private final Optional<ClassConstant> outerClassConstant;
	private final Optional<Utf8Constant> innerNameConstant;
	private final FlagSet<InnerClassModifier> modifierSet;

	private InnerClass( ClassConstant innerClassConstant, Optional<ClassConstant> outerClassConstant, Optional<Utf8Constant> innerNameConstant, //
		FlagSet<InnerClassModifier> modifierSet )
	{
		this.innerClassConstant = innerClassConstant;
		this.outerClassConstant = outerClassConstant;
		this.innerNameConstant = innerNameConstant;
		this.modifierSet = modifierSet;
	}

	public ClassConstant innerClassConstant()
	{
		return innerClassConstant;
	}

	public Optional<ClassConstant> outerClassConstant()
	{
		return outerClassConstant;
	}

	public Optional<Utf8Constant> innerNameConstant()
	{
		return innerNameConstant;
	}

	public FlagSet<InnerClassModifier> modifierSet()
	{
		return modifierSet;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "outerClass = " + outerClassConstant + " accessFlags = " + modifierSet + " innerClass = " + innerClassConstant + " innerName = " + innerNameConstant;
	}
}
