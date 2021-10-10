package mikenakis.bytecode.model;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingBootstrapPool;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.Kit;
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
 * @author michael.gr
 */
public final class ByteCodeType
{
	private static final int MAGIC = 0xCAFEBABE;

	public static ByteCodeType read( Class<?> jvmClass )
	{
		String className = jvmClass.getName();
		String prefix = jvmClass.getPackageName();
		assert className.startsWith( prefix + "." );
		String resourceName = className.substring( prefix.length() + 1 ) + ".class";
		byte[] bytes = Kit.uncheckedTryGetWithResources( //
			() -> jvmClass.getResourceAsStream( resourceName ), //
			i -> i.readAllBytes() );
		return read( bytes );
	}

	public static ByteCodeType read( byte[] bytes )
	{
		BufferReader bufferReader = BufferReader.of( bytes, 0, bytes.length );
		ByteCodeType byteCodeType = read( bufferReader );
		assert bufferReader.isAtEnd();
		return byteCodeType;
	}

	public static ByteCodeType read( BufferReader bufferReader )
	{
		int magic = bufferReader.readInt();
		assert magic == MAGIC;
		ByteCodeVersion version = ByteCodeVersion.read( bufferReader );
		ReadingConstantPool constantPool = ReadingConstantPool.read( bufferReader );

		FlagSet<Modifier> modifiers = ByteCodeType.modifierEnum.fromBits( bufferReader.readUnsignedShort() );
		ClassConstant thisClassConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asClassConstant();
		Optional<ClassConstant> superClassConstant = constantPool.tryGetConstant( bufferReader.readUnsignedShort() ).map( Constant::asClassConstant );
		int interfaceCount = bufferReader.readUnsignedShort();
		List<ClassConstant> interfaceClassConstants = new ArrayList<>( interfaceCount );
		for( int i = 0; i < interfaceCount; i++ )
		{
			ClassConstant interfaceClassConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asClassConstant();
			interfaceClassConstants.add( interfaceClassConstant );
		}
		int fieldCount = bufferReader.readUnsignedShort();
		List<ByteCodeField> fields = new ArrayList<>( fieldCount );
		for( int i = 0; i < fieldCount; i++ )
			fields.add( ByteCodeField.read( bufferReader, constantPool ) );
		int methodCount = bufferReader.readUnsignedShort();
		List<ByteCodeMethod> methods = new ArrayList<>( methodCount );
		for( int i = 0; i < methodCount; i++ )
			methods.add( ByteCodeMethod.read( bufferReader, constantPool ) );
		AttributeSet attributes = AttributeSet.read( bufferReader, constantPool, Optional.empty() );

		attributes.tryGetKnownAttributeByTag( KnownAttribute.tag_BootstrapMethods ) //
			.map( attribute -> attribute.asBootstrapMethodsAttribute() ) //
			.ifPresent( bootstrapMethodsAttribute -> //
			{
				constantPool.runBootstrapFixUps( bootstrapMethodsAttribute );
				attributes.removeAttribute( bootstrapMethodsAttribute );
			} );
		Collection<ClassConstant> extraClassReferences = constantPool.getExtraClassReferences();
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

	public byte[] write()
	{
		BufferWriter bufferWriter = new BufferWriter();
		WritingConstantPool constantPool = new WritingConstantPool();
		WritingBootstrapPool bootstrapPool = new WritingBootstrapPool();

		/* first intern all invokeDynamic instructions to collect all bootstrap methods */
		for( ByteCodeMethod method : methods )
		{
			method.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tag_Code ) //
				.map( attribute -> attribute.asCodeAttribute() ) //
				.ifPresent( codeAttribute -> //
				{
					for( Instruction instruction : codeAttribute.instructions.all() )
						if( instruction.groupTag == Instruction.groupTag_InvokeDynamic )
							bootstrapPool.intern( instruction.asInvokeDynamicInstruction().bootstrapMethod() );
				} );
		}

		/* if any bootstrap methods were collected, add the bootstrap methods attribute */
		if( !bootstrapPool.bootstrapMethods().isEmpty() )
		{
			BootstrapMethodsAttribute bootstrapMethodsAttribute = BootstrapMethodsAttribute.of();
			bootstrapMethodsAttribute.bootstrapMethods.addAll( bootstrapPool.bootstrapMethods() ); // TODO perhaps replace the bootstrapPool with the bootstrapMethodsAttribute
			attributeSet.addAttribute( bootstrapMethodsAttribute );
		}

		/* now intern everything, including the attributes, and the newly added bootstrap methods attribute */
		intern( constantPool );

		//TODO: optimize the constant pool by moving the constants most frequently used by the IndirectLoadConstantInstruction to the first 256 entries!
		bufferWriter.writeInt( MAGIC );
		bufferWriter.writeUnsignedShort( version.minor() );
		bufferWriter.writeUnsignedShort( version.major() );
		bufferWriter.writeUnsignedShort( constantPool.size() );
		for( Constant constant : constantPool.constants() )
			constant.write( bufferWriter, constantPool, bootstrapPool );
		bufferWriter.writeUnsignedShort( modifiers.getBits() );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( classConstant ) );
		bufferWriter.writeUnsignedShort( superClassConstant.map( c -> constantPool.getConstantIndex( c ) ).orElse( 0 ) );
		List<ClassConstant> interfaceClassConstants = interfaceConstants;
		bufferWriter.writeUnsignedShort( interfaceClassConstants.size() );
		for( ClassConstant interfaceClassConstant : interfaceClassConstants )
			bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( interfaceClassConstant ) );
		bufferWriter.writeUnsignedShort( fields.size() );
		for( ByteCodeField field : fields )
			field.write( bufferWriter, constantPool, Optional.empty() );
		bufferWriter.writeUnsignedShort( methods.size() );
		for( ByteCodeMethod method : methods )
			method.write( bufferWriter, constantPool, Optional.empty() );
		attributeSet.write( bufferWriter, constantPool, Optional.empty() );
		return bufferWriter.toBytes();
	}
}
