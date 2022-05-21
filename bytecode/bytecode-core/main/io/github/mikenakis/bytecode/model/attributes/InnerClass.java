package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.constants.ClassConstant;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.java_type_model.TerminalTypeDescriptor;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.kit.collections.FlagEnum;
import io.github.mikenakis.kit.collections.FlagSet;

import java.util.Map;
import java.util.Optional;

/**
 * Represents an entry of {@link InnerClassesAttribute}.
 *
 * @author michael.gr
 */
public final class InnerClass
{
	public static InnerClass of( ClassConstant innerClassConstant, Optional<ClassConstant> outerClassConstant, Optional<Mutf8ValueConstant> innerNameConstant, //
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

	private final ClassConstant innerClassConstant;
	private final Optional<ClassConstant> outerClassConstant;
	private final Optional<Mutf8ValueConstant> innerNameConstant;
	public final FlagSet<InnerClassModifier> modifiers;

	private InnerClass( ClassConstant innerClassConstant, Optional<ClassConstant> outerClassConstant, Optional<Mutf8ValueConstant> innerNameConstant, //
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
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "outerClass = " + outerClassConstant + " accessFlags = " + modifiers + " innerClass = " + innerClassConstant + " innerName = " + innerNameConstant; }

	public void intern( Interner interner )
	{
		innerClassConstant.intern( interner );
		outerClassConstant.ifPresent( c -> c.intern( interner ) );
		innerNameConstant.ifPresent( c -> c.intern( interner ) );
	}

	public void write( BufferWriter bufferWriter, WritingConstantPool constantPool )
	{
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( innerClassConstant ) );
		bufferWriter.writeUnsignedShort( outerClassConstant.map( c -> constantPool.getConstantIndex( c ) ).orElse( 0 ) );
		bufferWriter.writeUnsignedShort( innerNameConstant.map( c -> constantPool.getConstantIndex( c ) ).orElse( 0 ) );
		bufferWriter.writeUnsignedShort( modifiers.getBits() );
	}
}
