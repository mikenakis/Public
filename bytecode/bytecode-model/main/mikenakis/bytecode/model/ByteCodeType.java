package mikenakis.bytecode.model;

import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.model.attributes.SourceFileAttribute;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
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

	public static ByteCodeType of( FlagSet<Modifier> modifierSet, ClassConstant thisClassConstant, Optional<ClassConstant> superClassConstant )
	{
		int majorVersion = Runtime.version().feature();
		int minorVersion = Runtime.version().interim();
		return of( minorVersion, majorVersion, modifierSet, thisClassConstant, superClassConstant );
	}

	public static ByteCodeType of( int minorVersion, int majorVersion, FlagSet<Modifier> modifierSet, ClassConstant thisClassConstant, //
		Optional<ClassConstant> superClassConstant )
	{
		return of( minorVersion, majorVersion, modifierSet, thisClassConstant, superClassConstant, new LinkedHashSet<>( 0 ), //
			new LinkedHashSet<>( 0 ), new LinkedHashSet<>( 0 ), AttributeSet.of(), new ArrayList<>() );
	}

	public static ByteCodeType of( int minorVersion, int majorVersion, FlagSet<Modifier> modifierSet, ClassConstant thisClassConstant, //
		Optional<ClassConstant> superClassConstant, Collection<ClassConstant> interfaceClassConstants, Collection<ByteCodeField> fields, //
		Collection<ByteCodeMethod> methods, AttributeSet attributeSet, Collection<Constant> extraConstants )
	{
		return new ByteCodeType( minorVersion, majorVersion, modifierSet, thisClassConstant, superClassConstant, interfaceClassConstants, fields, methods, //
			attributeSet, extraConstants );
	}

	public final int minorVersion;
	public final int majorVersion;
	private final FlagSet<Modifier> modifierSet;
	public final ClassConstant thisClassConstant;
	public final Optional<ClassConstant> superClassConstant;
	private final Collection<ClassConstant> interfaceClassConstants;
	private final Collection<ByteCodeField> fields;
	private final Collection<ByteCodeMethod> methods;
	private final AttributeSet attributeSet;
	private final Collection<Constant> extraConstants;

	private ByteCodeType( int minorVersion, int majorVersion, FlagSet<Modifier> modifierSet, ClassConstant thisClassConstant, //
		Optional<ClassConstant> superClassConstant, Collection<ClassConstant> interfaceClassConstants, Collection<ByteCodeField> fields, //
		Collection<ByteCodeMethod> methods, AttributeSet attributeSet, Collection<Constant> extraConstants )
	{
		this.minorVersion = minorVersion;
		this.majorVersion = majorVersion;
		this.modifierSet = modifierSet;
		this.thisClassConstant = thisClassConstant;
		this.superClassConstant = superClassConstant;
		this.interfaceClassConstants = interfaceClassConstants;
		this.fields = fields;
		this.methods = methods;
		this.attributeSet = attributeSet;
		this.extraConstants = extraConstants;
	}

	public FlagSet<Modifier> modifierSet()
	{
		return modifierSet;
	}

	public Collection<ClassConstant> interfaceClassConstants()
	{
		return Collections.unmodifiableCollection( interfaceClassConstants );
	}

	public Collection<ByteCodeField> fields()
	{
		return Collections.unmodifiableCollection( fields );
	}

	public void addField( ByteCodeField field )
	{
		fields.add( field );
	}

	public Collection<ByteCodeMethod> methods()
	{
		return Collections.unmodifiableCollection( methods );
	}

	public void addMethod( ByteCodeMethod method )
	{
		methods.add( method );
	}

	public Optional<String> tryGetSourceFileName()
	{
		return attributeSet.tryGetAttributeByName( SourceFileAttribute.kind.mutf8Name ) //
			.map( a -> a.asSourceFileAttribute() ) //
			.map( a -> a.valueConstant().stringValue() );
	}

	public boolean isInterface()
	{
		return modifierSet.contains( Modifier.Interface );
	}

	public boolean isEnum()
	{
		return modifierSet.contains( Modifier.Enum );
	}

	public BootstrapMethodsAttribute createOrGetBootstrapMethodsAttribute()
	{
		return attributeSet.tryGetAttributeByName( BootstrapMethodsAttribute.kind.mutf8Name ) //
			.map( a -> a.asBootstrapMethodsAttribute() ) //
			.orElseGet( () ->
			{
				BootstrapMethodsAttribute bootstrapMethodsAttribute = BootstrapMethodsAttribute.of();
				attributeSet.addAttribute( bootstrapMethodsAttribute );
				return bootstrapMethodsAttribute;
			} );
	}

	public int getIndexOfBootstrapMethod( BootstrapMethod bootstrapMethod )
	{
		var bootstrapMethodsAttribute = createOrGetBootstrapMethodsAttribute();
		return bootstrapMethodsAttribute.getIndexOfBootstrapMethod( bootstrapMethod );
	}

	public BootstrapMethod getBootstrapMethodByIndex( int index )
	{
		var bootstrapMethodsAttribute = createOrGetBootstrapMethodsAttribute();
		return bootstrapMethodsAttribute.getBootstrapMethodByIndex( index );
	}

	public AttributeSet attributeSet()
	{
		return attributeSet;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return thisClassConstant.nameConstant().stringValue();
	}

	public ClassDesc descriptor()
	{
		return thisClassConstant.classDescriptor();
	}

	public String name()
	{
		return ByteCodeHelpers.typeNameFromClassDesc( thisClassConstant.classDescriptor() );
	}

	public Optional<ClassDesc> superClassDescriptor()
	{
		return superClassConstant.map( c -> c.classDescriptor() );
	}

	public Optional<String> superClassName()
	{
		return superClassDescriptor().map( c -> ByteCodeHelpers.typeNameFromClassDesc( c ) );
	}

	public boolean isAnnotation()
	{
		return modifierSet.contains( Modifier.Annotation );
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

	public ByteCodeMethod getMethodByNameAndDescriptor( String name, MethodTypeDesc descriptor, Function<String,ByteCodeType> byteCodeTypeByName )
	{
		for( ByteCodeType byteCodeType = this; ; byteCodeType = byteCodeType.superClassName().map( byteCodeTypeByName ).orElseThrow() )
			for( ByteCodeMethod method : byteCodeType.methods )
				if( match( method, name, descriptor ) )
					return method;
	}

	private static boolean match( ByteCodeMethod method, String name, MethodTypeDesc descriptor )
	{
		if( !method.name().equals( name ) )
			return false;
		if( !method.descriptor().equals( descriptor ) )
			return false;
		return true;
	}

	public Collection<Constant> extraConstants()
	{
		return extraConstants;
	}
}
