package mikenakis.bytecode.model;

import mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.descriptors.MethodDescriptor;
import mikenakis.bytecode.model.descriptors.TerminalTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.collections.FlagEnum;
import mikenakis.kit.collections.FlagEnumSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a Java Type (Class, Interface, or Enum) in raw form.
 * <p>
 * Source of information: The Java Virtual Machine Specification (JVMS) Chapter 4: The class File Format
 * <p>
 * https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html
 * <p>
 * For emitting bytecode and creating class files, see:
 * <p>
 * https://github.com/mozilla/rhino/tree/master/src/org/mozilla/classfile
 * <p>
 * https://github.com/mit-cml/ai2-kawa/tree/master/gnu/bytecode
 * <p>
 * http://sourceware.org/viewvc/kawa/trunk/gnu/bytecode/
 * <p>
 * For a comparison of the above, see:
 * <p>
 * http://elliotth.blogspot.nl/2008/04/generating-jvm-bytecode-3.html
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeType
{
	public static record Version( int major, int minor ) {}

	public static final int MAGIC = 0xCAFEBABE;

	public enum Modifier
	{
		Public, Final, Super, Interface, Abstract, Synthetic, Annotation, Enum
	}

	public static final FlagEnum<Modifier> modifierFlagsEnum = FlagEnum.of( Modifier.class, //
		Map.entry( Modifier.Public, 0x0001 ),     // ACC_PUBLIC = 0x0001; //bit 0 : Declared public; may be accessed from outside its package.
		Map.entry( Modifier.Final, 0x0010 ),      // ACC_FINAL = 0x0010; //bit 4: Declared final; no subclasses allowed.
		Map.entry( Modifier.Super, 0x0020 ),      // ACC_SUPER = 0x0020; //bit 5: Treat superclass methods specially when invoked by the invokespecial instruction.
		Map.entry( Modifier.Interface, 0x0200 ),  // ACC_INTERFACE = 0x0200; //bit 9: Is an interface, not a class.
		Map.entry( Modifier.Abstract, 0x0400 ),   // ACC_ABSTRACT = 0x0400; //bit 10: Declared abstract; must not be instantiated.
		Map.entry( Modifier.Synthetic, 0x1000 ),  // ACC_SYNTHETIC = 0x1000; //bit 12: Declared synthetic; not present in the source code.
		Map.entry( Modifier.Annotation, 0x2000 ), // ACC_ANNOTATION = 0x2000; //bit 13: Declared as an annotation type.
		Map.entry( Modifier.Enum, 0x4000 ) );     // ACC_ENUM = 0x4000; //bit 14: Declared as an enum type.

	public static ByteCodeType of( FlagEnumSet<Modifier> modifierSet, String typeName, Optional<String> superName )
	{
		TerminalTypeDescriptor typeDescriptor = TerminalTypeDescriptor.ofTypeName( typeName );
		Optional<TerminalTypeDescriptor> superDescriptor = superName.map( TerminalTypeDescriptor::ofTypeName );
		return of( modifierSet, typeDescriptor, superDescriptor );
	}

	public static ByteCodeType of( FlagEnumSet<Modifier> modifierSet, TerminalTypeDescriptor typeDescriptor, //
		Optional<TerminalTypeDescriptor> superTypeDescriptor )
	{
		Version version = new Version( 60, 0 ); //TODO: add full support for this version!
		return of( version, modifierSet, ByteCodeHelpers.classConstantFromTerminalTypeDescriptor( typeDescriptor ), //
			superTypeDescriptor.map( c -> ByteCodeHelpers.classConstantFromTerminalTypeDescriptor( c ) ), //
			new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), AttributeSet.of(), new ArrayList<>() );
	}

	public static ByteCodeType of( Version version, FlagEnumSet<Modifier> modifierSet, ClassConstant classConstant, //
		Optional<ClassConstant> superClassConstant, List<ClassConstant> interfaceConstants, List<ByteCodeField> fields, //
		List<ByteCodeMethod> methods, AttributeSet attributeSet, Collection<Constant> extraConstants )
	{
		return new ByteCodeType( version, modifierSet, classConstant, superClassConstant, interfaceConstants, fields, methods, //
			attributeSet, extraConstants );
	}

	public final Version version;
	public final FlagEnumSet<Modifier> modifierSet;
	private final ClassConstant classConstant;
	private final Optional<ClassConstant> superClassConstant;
	private final List<ClassConstant> interfaceConstants;
	public final List<ByteCodeField> fields;
	public final List<ByteCodeMethod> methods;
	public final AttributeSet attributeSet;
	public final Collection<Constant> extraConstants;

	private ByteCodeType( Version version, FlagEnumSet<Modifier> modifierSet, ClassConstant classConstant, //
		Optional<ClassConstant> superClassConstant, List<ClassConstant> interfaceConstants, List<ByteCodeField> fields, //
		List<ByteCodeMethod> methods, AttributeSet attributeSet, Collection<Constant> extraConstants )
	{
		this.version = version;
		this.modifierSet = modifierSet;
		this.classConstant = classConstant;
		this.superClassConstant = superClassConstant;
		this.interfaceConstants = interfaceConstants;
		this.fields = fields;
		this.methods = methods;
		this.attributeSet = attributeSet;
		this.extraConstants = extraConstants;
	}

	public ClassConstant classConstant() { return classConstant; }
	public TerminalTypeDescriptor typeDescriptor() { return TerminalTypeDescriptor.of( classConstant ); }
	public String typeName() { return typeDescriptor().name(); }
	public Optional<ClassConstant> superClassConstant() { return superClassConstant; }
	public Optional<TerminalTypeDescriptor> superTypeDescriptor() { return superClassConstant.map( c -> TerminalTypeDescriptor.of( c ) ); }
	public Optional<String> superTypeName() { return superTypeDescriptor().map( d -> d.name() ); }
	public List<ClassConstant> interfaceClassConstants() { return interfaceConstants; }
	public List<TerminalTypeDescriptor> interfaceTypeDescriptors() { return interfaceConstants.stream().map( c -> TerminalTypeDescriptor.of( c ) ).toList(); }
	public List<String> interfaceTypeNames() { return interfaceConstants.stream().map( c -> TerminalTypeDescriptor.of( c ).name() ).toList(); }

	public Optional<String> tryGetSourceFileName()
	{
		return attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tag_SourceFile ) //
			.map( a -> a.asSourceFileAttribute() ) //
			.map( a -> a.value() );
	}

	public BootstrapMethodsAttribute createOrGetBootstrapMethodsAttribute()
	{
		return attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tag_BootstrapMethods ) //
			.map( a -> a.asBootstrapMethodsAttribute() ) //
			.orElseGet( () -> {
				BootstrapMethodsAttribute bootstrapMethodsAttribute = BootstrapMethodsAttribute.of();
				attributeSet.addAttribute( bootstrapMethodsAttribute );
				return bootstrapMethodsAttribute;
			} );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return classConstant.typeName();
	}

	public int findDeclaredMethodByNameAndDescriptor( String name, MethodDescriptor descriptor )
	{
		int index = 0;
		for( ByteCodeMethod method : methods )
		{
			if( match( method, name, descriptor ) )
				return index;
			index++;
		}
		return -1;
	}

	public Optional<ByteCodeMethod> getMethodByNameAndDescriptor( String name, MethodDescriptor descriptor, Function<String,ByteCodeType> byteCodeTypeByName )
	{
		for( ByteCodeType byteCodeType = this; ; )
		{
			int index = byteCodeType.findDeclaredMethodByNameAndDescriptor( name, descriptor );
			if( index != -1 )
				return Optional.of( byteCodeType.methods.get( index ) );
			Optional<ByteCodeType> superType = byteCodeType.superTypeName().map( byteCodeTypeByName );
			if( superType.isEmpty() )
				break;
			byteCodeType = superType.get();
		}
		return Optional.empty();
	}

	private static boolean match( ByteCodeMethod method, String name, MethodDescriptor descriptor )
	{
		if( !method.name().equals( name ) )
			return false;
		return method.getMethodDescriptor().equals( descriptor );
	}
}
