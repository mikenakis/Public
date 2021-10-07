package mikenakis.bytecode.model;

import mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.bytecode.reading.ByteCodeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.collections.FlagEnum;
import mikenakis.kit.collections.FlagSet;

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

	public static ByteCodeType read( ByteCodeReader byteCodeReader )
	{
		assert byteCodeReader.getMagic() == MAGIC;
		ByteCodeVersion version = byteCodeReader.getVersion();
		FlagSet<Modifier> modifiers = ByteCodeType.modifierEnum.fromBits( byteCodeReader.readUnsignedShort() );
		ClassConstant thisClassConstant = byteCodeReader.readIndexAndGetConstant().asClassConstant();
		Optional<ClassConstant> superClassConstant = byteCodeReader.tryReadIndexAndGetConstant().map( Constant::asClassConstant );
		int interfaceCount = byteCodeReader.readUnsignedShort();
		List<ClassConstant> interfaceClassConstants = new ArrayList<>( interfaceCount );
		for( int i = 0; i < interfaceCount; i++ )
		{
			ClassConstant interfaceClassConstant = byteCodeReader.readIndexAndGetConstant().asClassConstant();
			interfaceClassConstants.add( interfaceClassConstant );
		}
		int fieldCount = byteCodeReader.readUnsignedShort();
		List<ByteCodeField> fields = new ArrayList<>( fieldCount );
		for( int i = 0; i < fieldCount; i++ )
		{
			FlagSet<ByteCodeField.Modifier> fieldModifiers = ByteCodeField.modifierEnum.fromBits( byteCodeReader.readUnsignedShort() );
			Mutf8ValueConstant nameConstant = byteCodeReader.readIndexAndGetConstant().asMutf8ValueConstant();
			Mutf8ValueConstant descriptorConstant = byteCodeReader.readIndexAndGetConstant().asMutf8ValueConstant();
			AttributeSet attributes = AttributeSet.read( byteCodeReader.getAttributeReader() );
			ByteCodeField byteCodeField = ByteCodeField.of( fieldModifiers, nameConstant, descriptorConstant, attributes );
			fields.add( byteCodeField );
		}
		int methodCount = byteCodeReader.readUnsignedShort();
		List<ByteCodeMethod> methods = new ArrayList<>( methodCount );
		for( int i = 0; i < methodCount; i++ )
		{
			FlagSet<ByteCodeMethod.Modifier> methodModifiers = ByteCodeMethod.modifierEnum.fromBits( byteCodeReader.readUnsignedShort() );
			Mutf8ValueConstant nameConstant = byteCodeReader.readIndexAndGetConstant().asMutf8ValueConstant();
			Mutf8ValueConstant descriptorConstant = byteCodeReader.readIndexAndGetConstant().asMutf8ValueConstant();
			AttributeSet attributes = AttributeSet.read( byteCodeReader.getAttributeReader() );
			ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( methodModifiers, nameConstant, descriptorConstant, attributes );
			methods.add( byteCodeMethod );
		}
		AttributeSet attributes = AttributeSet.read( byteCodeReader.getAttributeReader() );

		attributes.tryGetKnownAttributeByTag( KnownAttribute.tag_BootstrapMethods ) //
			.map( attribute -> attribute.asBootstrapMethodsAttribute() ) //
			.ifPresent( bootstrapMethodsAttribute -> //
			{
				byteCodeReader.applyFixUps( bootstrapMethodsAttribute );
				attributes.removeAttribute( bootstrapMethodsAttribute );
			} );
		Collection<ClassConstant> extraClassReferences = byteCodeReader.getExtraClassReferences();
		return of( version, modifiers, thisClassConstant, superClassConstant, interfaceClassConstants, fields, methods, //
			attributes, extraClassReferences );
	}

	public enum Modifier
	{
		Public, Final, Super, Interface, Abstract, Synthetic, Annotation, Enum
	}

	public static final FlagEnum<Modifier> modifierEnum = FlagEnum.of( Modifier.class, //
		Map.entry( Modifier.Public     /**/, 0x0001 ),   // ACC_PUBLIC     -- bit 0: Declared public; may be accessed from outside its package.
		Map.entry( Modifier.Final      /**/, 0x0010 ),   // ACC_FINAL      -- bit 4: Declared final; no subclasses allowed.
		Map.entry( Modifier.Super      /**/, 0x0020 ),   // ACC_SUPER      -- bit 5: Treat superclass methods specially when invoked by the invokespecial instruction.
		Map.entry( Modifier.Interface  /**/, 0x0200 ),   // ACC_INTERFACE  -- bit 9: Is an interface, not a class.
		Map.entry( Modifier.Abstract   /**/, 0x0400 ),   // ACC_ABSTRACT   -- bit 10: Declared abstract; must not be instantiated.
		Map.entry( Modifier.Synthetic  /**/, 0x1000 ),   // ACC_SYNTHETIC  -- bit 12: Declared synthetic; not present in the source code.
		Map.entry( Modifier.Annotation /**/, 0x2000 ),   // ACC_ANNOTATION -- bit 13: Declared as an annotation type.
		Map.entry( Modifier.Enum       /**/, 0x4000 ) ); // ACC_ENUM       -- bit 14: Declared as an enum type.

	public static ByteCodeType of( FlagSet<Modifier> modifiers, TerminalTypeDescriptor typeDescriptor, //
		Optional<TerminalTypeDescriptor> superTypeDescriptor, Collection<TerminalTypeDescriptor> interfaces )
	{
		ByteCodeVersion version = new ByteCodeVersion( 60, 0 ); //TODO: add full support for this version!
		return of( version, modifiers, ClassConstant.of( typeDescriptor ), //
			superTypeDescriptor.map( c -> ClassConstant.of( c ) ), //
			interfaces.stream().map( i -> ClassConstant.of( i ) ).toList(),
			new ArrayList<>(), new ArrayList<>(), AttributeSet.of(), new ArrayList<>() );
	}

	public static ByteCodeType of( ByteCodeVersion version, FlagSet<Modifier> modifiers, ClassConstant classConstant, //
		Optional<ClassConstant> superClassConstant, List<ClassConstant> interfaceConstants, List<ByteCodeField> fields, //
		List<ByteCodeMethod> methods, AttributeSet attributeSet, Collection<ClassConstant> extraClassConstants )
	{
		return new ByteCodeType( version, modifiers, classConstant, superClassConstant, interfaceConstants, fields, methods, //
			attributeSet, extraClassConstants );
	}

	public final ByteCodeVersion version;
	public final FlagSet<Modifier> modifiers;
	private final ClassConstant classConstant;
	private final Optional<ClassConstant> superClassConstant;
	private final List<ClassConstant> interfaceConstants;
	public final List<ByteCodeField> fields;
	public final List<ByteCodeMethod> methods;
	public final AttributeSet attributeSet;
	private final Collection<ClassConstant> extraClassConstants;

	private ByteCodeType( ByteCodeVersion version, FlagSet<Modifier> modifiers, ClassConstant classConstant, //
		Optional<ClassConstant> superClassConstant, List<ClassConstant> interfaceConstants, List<ByteCodeField> fields, //
		List<ByteCodeMethod> methods, AttributeSet attributeSet, Collection<ClassConstant> extraClassConstants )
	{
		this.version = version;
		this.modifiers = modifiers;
		this.classConstant = classConstant;
		this.superClassConstant = superClassConstant;
		this.interfaceConstants = interfaceConstants;
		this.fields = fields;
		this.methods = methods;
		this.attributeSet = attributeSet;
		this.extraClassConstants = extraClassConstants;
	}

	//public ClassConstant classConstant() { return classConstant; }
	public TerminalTypeDescriptor typeDescriptor() { return classConstant.terminalTypeDescriptor(); }
	//public Optional<ClassConstant> superClassConstant() { return superClassConstant; }
	public Optional<TerminalTypeDescriptor> superTypeDescriptor() { return superClassConstant.map( c -> c.terminalTypeDescriptor() ); }
	//public List<ClassConstant> interfaceClassConstants() { return interfaceConstants; }
	public List<TerminalTypeDescriptor> interfaces() { return interfaceConstants.stream().map( c -> c.terminalTypeDescriptor() ).toList(); }
	public List<TerminalTypeDescriptor> extraTypes() { return extraClassConstants.stream().map( c -> c.terminalTypeDescriptor() ).toList(); }

	public ByteCodeMethod addMethod( ByteCodeMethod method )
	{
		methods.add( method );
		return method;
	}

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

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return classConstant.toString(); }

	public int findDeclaredMethod( MethodPrototype methodPrototype )
	{
		int index = 0;
		for( ByteCodeMethod method : methods )
		{
			if( method.isMatch( methodPrototype ) )
				return index;
			index++;
		}
		return -1;
	}

	public Optional<ByteCodeMethod> getMethod( MethodPrototype methodPrototype, Function<String,ByteCodeType> byteCodeTypeByName )
	{
		for( ByteCodeType byteCodeType = this; ; )
		{
			int index = byteCodeType.findDeclaredMethod( methodPrototype );
			if( index != -1 )
				return Optional.of( byteCodeType.methods.get( index ) );
			Optional<ByteCodeType> superType = byteCodeType.superTypeDescriptor().map( d -> byteCodeTypeByName.apply( d.typeName ) );
			if( superType.isEmpty() )
				break;
			byteCodeType = superType.get();
		}
		return Optional.empty();
	}

	public void intern( Interner interner )
	{
		classConstant.intern( interner );
		superClassConstant.ifPresent( c -> c.intern( interner ) );
		for( ClassConstant classConstant : interfaceConstants )
			classConstant.intern( interner );
		for( ByteCodeField field : fields )
			field.intern( interner );
		for( ByteCodeMethod method : methods )
			method.intern( interner );

		attributeSet.intern( interner );

		for( Constant extraConstant : extraClassConstants )
			extraConstant.intern( interner );
	}

	public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( modifiers.getBits() );
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( classConstant ) );
		constantWriter.writeUnsignedShort( superClassConstant.map( c -> constantWriter.getConstantIndex( c ) ).orElse( 0 ) );
		List<ClassConstant> interfaceClassConstants = interfaceConstants;
		constantWriter.writeUnsignedShort( interfaceClassConstants.size() );
		for( ClassConstant interfaceClassConstant : interfaceClassConstants )
			constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( interfaceClassConstant ) );
		constantWriter.writeUnsignedShort( fields.size() );
		for( ByteCodeField field : fields )
			field.write( constantWriter );
		constantWriter.writeUnsignedShort( methods.size() );
		for( ByteCodeMethod method : methods )
			method.write( constantWriter );
		attributeSet.write( constantWriter );
	}
}
