package mikenakis.bytecode.model;

import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.descriptors.TerminalTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.collections.FlagEnum;
import mikenakis.kit.collections.FlagEnumSet;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
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

	public static ByteCodeType of( FlagEnumSet<Modifier> modifierSet, String className, Optional<String> superClassName )
	{
		TerminalTypeDescriptor thisClassDescriptor = TerminalTypeDescriptor.of( className );
		Optional<TerminalTypeDescriptor> superClassDescriptor = superClassName.map( TerminalTypeDescriptor::of );
		int majorVersion = Runtime.version().feature();
		int minorVersion = Runtime.version().interim();
		return of( minorVersion, majorVersion, modifierSet, thisClassDescriptor, superClassDescriptor );
	}

	public static ByteCodeType of( int minorVersion, int majorVersion, FlagEnumSet<Modifier> modifierSet, TerminalTypeDescriptor thisClassDescriptor, //
		Optional<TerminalTypeDescriptor> superClassDescriptor )
	{
		return of( minorVersion, majorVersion, modifierSet, thisClassDescriptor, superClassDescriptor, new ArrayList<>( 0 ), //
			new ArrayList<>( 0 ), new ArrayList<>( 0 ), AttributeSet.of(), new ArrayList<>() );
	}

	public static ByteCodeType of( int minorVersion, int majorVersion, FlagEnumSet<Modifier> modifierSet, TerminalTypeDescriptor thisClassDescriptor, //
		Optional<TerminalTypeDescriptor> superClassDescriptor, List<TerminalTypeDescriptor> interfaces, List<ByteCodeField> fields, //
		List<ByteCodeMethod> methods, AttributeSet attributeSet, Collection<Constant> extraConstants )
	{
		return new ByteCodeType( minorVersion, majorVersion, modifierSet, thisClassDescriptor, superClassDescriptor, interfaces, fields, methods, //
			attributeSet, extraConstants );
	}

	public final int minorVersion;
	public final int majorVersion;
	public final FlagEnumSet<Modifier> modifierSet;
	public final TerminalTypeDescriptor thisClassDescriptor;
	public final Optional<TerminalTypeDescriptor> superClassDescriptor;
	public final List<TerminalTypeDescriptor> interfaces;
	public final List<ByteCodeField> fields;
	public final List<ByteCodeMethod> methods;
	public final AttributeSet attributeSet;
	public final Collection<Constant> extraConstants;

	private ByteCodeType( int minorVersion, int majorVersion, FlagEnumSet<Modifier> modifierSet, TerminalTypeDescriptor thisClassDescriptor, //
		Optional<TerminalTypeDescriptor> superClassDescriptor, List<TerminalTypeDescriptor> interfaces, List<ByteCodeField> fields, //
		List<ByteCodeMethod> methods, AttributeSet attributeSet, Collection<Constant> extraConstants )
	{
		this.minorVersion = minorVersion;
		this.majorVersion = majorVersion;
		this.modifierSet = modifierSet;
		this.thisClassDescriptor = thisClassDescriptor;
		this.superClassDescriptor = superClassDescriptor;
		this.interfaces = interfaces;
		this.fields = fields;
		this.methods = methods;
		this.attributeSet = attributeSet;
		this.extraConstants = extraConstants;
	}

	public Optional<String> tryGetSourceFileName()
	{
		return attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tagSourceFile ) //
			.map( a -> a.asSourceFileAttribute() ) //
			.map( a -> a.valueConstant().stringValue() );
	}

	public BootstrapMethodsAttribute createOrGetBootstrapMethodsAttribute()
	{
		return attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tagBootstrapMethods ) //
			.map( a -> a.asBootstrapMethodsAttribute() ) //
			.orElseGet( () ->
			{
				BootstrapMethodsAttribute bootstrapMethodsAttribute = BootstrapMethodsAttribute.of();
				attributeSet.addAttribute( bootstrapMethodsAttribute );
				return bootstrapMethodsAttribute;
			} );
	}

	public BootstrapMethod getBootstrapMethodByIndex( int index )
	{
		var bootstrapMethodsAttribute = createOrGetBootstrapMethodsAttribute();
		return bootstrapMethodsAttribute.getBootstrapMethodByIndex( index );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return thisClassDescriptor.name();
	}

	public ClassDesc descriptor()
	{
		return thisClassDescriptor.classDesc;
	}

	public Optional<ClassDesc> superClassDescriptor()
	{
		return superClassDescriptor.map( c -> c.classDesc );
	}

	public int findDeclaredMethodByNameAndDescriptor( String name, MethodTypeDesc descriptor )
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

	public Optional<ByteCodeMethod> getMethodByNameAndDescriptor( String name, MethodTypeDesc descriptor, Function<String,ByteCodeType> byteCodeTypeByName )
	{
		for( ByteCodeType byteCodeType = this; ;  )
		{
			int index = byteCodeType.findDeclaredMethodByNameAndDescriptor( name, descriptor );
			if( index != -1 )
				return Optional.of( byteCodeType.methods.get( index ) );
			Optional<ByteCodeType> superType = byteCodeType.superClassDescriptor.map( c -> c.name() ).map( byteCodeTypeByName );
			if( superType.isEmpty() )
				break;
			byteCodeType = superType.get();
		}
		return Optional.empty();
	}

	private static boolean match( ByteCodeMethod method, String name, MethodTypeDesc descriptor )
	{
		if( !method.name().equals( name ) )
			return false;
		return method.descriptor().equals( descriptor );
	}
}
