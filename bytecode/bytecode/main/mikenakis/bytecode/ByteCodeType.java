package mikenakis.bytecode;

import mikenakis.bytecode.attributes.BootstrapMethod;
import mikenakis.bytecode.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.attributes.DeprecatedAttribute;
import mikenakis.bytecode.attributes.EnclosingMethodAttribute;
import mikenakis.bytecode.attributes.InnerClassesAttribute;
import mikenakis.bytecode.attributes.RuntimeInvisibleAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeInvisibleTypeAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeVisibleAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeVisibleTypeAnnotationsAttribute;
import mikenakis.bytecode.attributes.SignatureAttribute;
import mikenakis.bytecode.attributes.SourceFileAttribute;
import mikenakis.bytecode.attributes.SyntheticAttribute;
import mikenakis.bytecode.attributes.UnknownAttribute;
import mikenakis.bytecode.constants.ClassConstant;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.ObservableCollection;
import mikenakis.kit.Kit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
public final class ByteCodeType implements Annotatable
{
	public enum Access
	{
		Public,
		Final,
		Super,
		Interface,
		Abstract,
		Synthetic,
		Annotation,
		Enum
	}

	public static final FlagEnum<Access> accessFlagEnum = FlagEnum.of( Access.class,
		Map.entry( Access.Public, 0x0001 ),     // ACC_PUBLIC = 0x0001; //bit 0 : Declared public; may be accessed from outside its package.
		Map.entry( Access.Final, 0x0010 ),      // ACC_FINAL = 0x0010; //bit 4: Declared final; no subclasses allowed.
		Map.entry( Access.Super, 0x0020 ),      // ACC_SUPER = 0x0020; //bit 5: Treat superclass methods specially when invoked by the invokespecial instruction.
		Map.entry( Access.Interface, 0x0200 ),  // ACC_INTERFACE = 0x0200; //bit 9: Is an interface, not a class.
		Map.entry( Access.Abstract, 0x0400 ),   // ACC_ABSTRACT = 0x0400; //bit 10: Declared abstract; must not be instantiated.
		Map.entry( Access.Synthetic, 0x1000 ),  // ACC_SYNTHETIC = 0x1000; //bit 12: Declared synthetic; not present in the source code.
		Map.entry( Access.Annotation, 0x2000 ), // ACC_ANNOTATION = 0x2000; //bit 13: Declared as an annotation type.
		Map.entry( Access.Enum, 0x4000 ) );     // ACC_ENUM = 0x4000; //bit 14: Declared as an enum type.

	public static ByteCodeType create( EnumSet<Access> access, String className, Optional<String> superClassName )
	{
		int majorVersion = Runtime.version().feature();
		int minorVersion = Runtime.version().interim();
		return new ByteCodeType( minorVersion, majorVersion, access, className, superClassName );
	}

	public static ByteCodeType create( Buffer buffer, Path classFilePathName )
	{
		return create( buffer, Optional.of( classFilePathName ), Optional.empty() );
	}

	public static ByteCodeType create( Buffer buffer, Optional<Path> classFilePathName, Optional<Path> sourcePath )
	{
		return new ByteCodeType( buffer, classFilePathName, sourcePath );
	}

	public static ByteCodeType create( byte[] bytes )
	{
		var buffer = Buffer.create( bytes );
		return create( buffer, Optional.empty(), Optional.empty() );
	}

	public static ByteCodeType create( byte[] bytes, Optional<Path> classFilePathName )
	{
		var buffer = Buffer.create( bytes );
		return create( buffer, classFilePathName, Optional.empty() );
	}

	public static ByteCodeType create( Path classFilePathName )
	{
		return create( classFilePathName, Optional.empty() );
	}

	public static ByteCodeType create( Path classFilePathName, Optional<Path> sourcePath )
	{
		var buffer = Buffer.create( Kit.unchecked( () -> Files.readAllBytes( classFilePathName ) ) );
		return create( buffer, Optional.of( classFilePathName ), sourcePath );
	}

	private static final int MAGIC = 0xCAFEBABE;

	private Buffer buffer;
	private boolean dirty = false;
	public final Optional<Path> classFilePathName;
	public final Optional<Path> sourcePath;
	public final int minorVersion;
	public final int majorVersion;
	public final ConstantPool constantPool;
	public final EnumSet<Access> access;
	public final ClassConstant thisClassConstant;
	public final Optional<ClassConstant> superClassConstant;
	public final Collection<ClassConstant> interfaceClassConstants;
	public final Collection<ByteCodeField> fields;
	public final Collection<ByteCodeMethod> methods;
	public final Attributes attributes;

	private ByteCodeType( int minorVersion, int majorVersion, EnumSet<Access> access, String className, Optional<String> superClassName )
	{
		buffer = Buffer.EMPTY;
		classFilePathName = Optional.empty();
		sourcePath = Optional.empty();
		this.minorVersion = minorVersion;
		this.majorVersion = majorVersion;
		this.access = access;
		constantPool = new ConstantPool( this::markAsDirty, this );
		thisClassConstant = new ClassConstant( className );
		superClassConstant = superClassName.map( s -> new ClassConstant( s ) );
		interfaceClassConstants = newSet( 0 );
		fields = newSet( 0 );
		methods = newSet( 0 );
		attributes = new Attributes( this::markAsDirty );
	}

	private ByteCodeType( Buffer buffer, Optional<Path> classFilePathName, Optional<Path> sourcePath )
	{
		this.buffer = buffer;
		var bufferReader = new BufferReader( buffer );
		this.classFilePathName = classFilePathName;
		this.sourcePath = sourcePath;
		int magic = bufferReader.readInt();
		assert magic == MAGIC;
		minorVersion = bufferReader.readUnsignedShort();
		majorVersion = bufferReader.readUnsignedShort();
		constantPool = new ConstantPool( this::markAsDirty, this, bufferReader );
		int accessFlags = bufferReader.readUnsignedShort();
		access = accessFlagEnum.fromInt( accessFlags );
		thisClassConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
		superClassConstant = constantPool.tryReadIndexAndGetConstant( bufferReader ).map( constant -> constant.asClassConstant() );
		int interfaceCount = bufferReader.readUnsignedShort();
		interfaceClassConstants = newSet( interfaceCount );
		for( int i = 0; i < interfaceCount; i++ )
		{
			ClassConstant interfaceClassConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
			Kit.collection.add( interfaceClassConstants, interfaceClassConstant );
		}
		int fieldCount = bufferReader.readUnsignedShort();
		fields = newSet( fieldCount );
		for( int i = 0; i < fieldCount; i++ )
		{
			ByteCodeField byteCodeField = ByteCodeField.read( this::markAsDirty, this, bufferReader );
			Kit.collection.add( fields, byteCodeField );
		}
		int methodCount = bufferReader.readUnsignedShort();
		methods = newSet( methodCount );
		for( int i = 0; i < methodCount; i++ )
		{
			ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( this::markAsDirty, this, bufferReader );
			Kit.collection.add( methods, byteCodeMethod );
		}
		attributes = Attributes.read( this::markAsDirty, constantPool, this::newAttribute, bufferReader );
		assert sourcePathIsOkAssertion();
		assert bufferReader.isAtEnd();
	}

	private boolean sourcePathIsOkAssertion()
	{
		if( sourcePath.isPresent() )
		{
			String sourceFileName = getSourceFileName();
			Path sourcePathName = sourcePath.get().resolve( sourceFileName );
			assert Files.isRegularFile( sourcePathName );
		}
		return true;
	}

	public void rebuild()
	{
		constantPool.clear();
		intern();
	}

	private void write( BufferWriter bufferWriter )
	{
		intern();
		bufferWriter.writeInt( MAGIC );
		bufferWriter.writeUnsignedShort( minorVersion );
		bufferWriter.writeUnsignedShort( majorVersion );
		constantPool.write( bufferWriter );
		bufferWriter.writeUnsignedShort( accessFlagEnum.toInt( access ) );
		thisClassConstant.writeIndex( constantPool, bufferWriter );
		bufferWriter.writeUnsignedShort( superClassConstant.isEmpty() ? 0 : constantPool.getIndex( superClassConstant.get() ) );
		bufferWriter.writeUnsignedShort( interfaceClassConstants.size() );
		for( var interfaceClassConstant : interfaceClassConstants )
			interfaceClassConstant.writeIndex( constantPool, bufferWriter );
		bufferWriter.writeUnsignedShort( fields.size() );
		for( var field : fields )
			field.write( bufferWriter );
		bufferWriter.writeUnsignedShort( methods.size() );
		for( var method : methods )
			method.write( bufferWriter );
		attributes.write( constantPool, bufferWriter );
	}

	private void intern()
	{
		thisClassConstant.intern( constantPool );
		superClassConstant.ifPresent( classConstant -> classConstant.intern( constantPool ) );
		for( var interfaceClassConstant : interfaceClassConstants )
			interfaceClassConstant.intern( constantPool );
		for( var field : fields )
			field.intern();
		for( var method : methods )
			method.intern();
		attributes.intern( constantPool );
	}

	public String getName()
	{
		return thisClassConstant.getClassName();
	}

	public Optional<String> getSuperClassName()
	{
		return superClassConstant.map( c -> c.getClassName() );
	}

	public Optional<String> tryGetSourceFileName()
	{
		return SourceFileAttribute.tryFrom( attributes ).map( a -> a.getValue() );
	}

	public String getSourceFileName()
	{
		return tryGetSourceFileName().orElseThrow();
	}

	public boolean isInterface()
	{
		return access.contains( Access.Interface );
	}

	public Buffer getBuffer()
	{
		if( dirty )
		{
			var bufferWriter = new BufferWriter();
			write( bufferWriter );
			buffer = bufferWriter.toBuffer();
			dirty = false;
		}
		return buffer;
	}

	private Attribute newAttribute( String attributeName, BufferReader bufferReader )
	{
		switch( attributeName )
		{
			//@formatter:off
			case BootstrapMethodsAttribute.NAME:                return new BootstrapMethodsAttribute               ( this::markAsDirty, constantPool, bufferReader );
			case DeprecatedAttribute.NAME:                      return new DeprecatedAttribute                     ( this::markAsDirty );
			case EnclosingMethodAttribute.NAME:                 return new EnclosingMethodAttribute                ( this::markAsDirty, constantPool, bufferReader );
			case InnerClassesAttribute.NAME:                    return new InnerClassesAttribute                   ( this::markAsDirty, constantPool, bufferReader );
			case RuntimeInvisibleAnnotationsAttribute.NAME:     return new RuntimeInvisibleAnnotationsAttribute    ( this::markAsDirty, constantPool, bufferReader );
			case RuntimeVisibleAnnotationsAttribute.NAME:       return new RuntimeVisibleAnnotationsAttribute      ( this::markAsDirty, constantPool, bufferReader );
			case RuntimeInvisibleTypeAnnotationsAttribute.NAME: return new RuntimeInvisibleTypeAnnotationsAttribute( this::markAsDirty, constantPool, bufferReader );
			case RuntimeVisibleTypeAnnotationsAttribute.NAME:   return new RuntimeVisibleTypeAnnotationsAttribute  ( this::markAsDirty, constantPool, bufferReader );
			case SignatureAttribute.NAME:                       return new SignatureAttribute                      ( this::markAsDirty, constantPool, bufferReader );
			case SourceFileAttribute.NAME:                      return new SourceFileAttribute                     ( this::markAsDirty, constantPool, bufferReader );
			case SyntheticAttribute.NAME:                       return new SyntheticAttribute                      ( this::markAsDirty );
			case "SourceDebugExtension":
			default:                                            return new UnknownAttribute                        ( this::markAsDirty, attributeName, bufferReader );
			//@formatter:on
		}
	}

	public BootstrapMethodsAttribute createOrGetBootstrapMethodsAttribute()
	{
		Optional<BootstrapMethodsAttribute> existingAttribute = BootstrapMethodsAttribute.tryFrom( attributes );
		if( existingAttribute.isPresent() )
			return existingAttribute.get();
		BootstrapMethodsAttribute attribute = new BootstrapMethodsAttribute( this::markAsDirty, constantPool );
		attributes.addAttribute( attribute );
		return attribute;
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

	@Override public Attributes getAttributes()
	{
		return attributes;
	}

	@Override public String toString()
	{
		return getName();
	}

	public boolean isAnnotation()
	{
		return access.contains( Access.Annotation );
	}

	private <E> Collection<E> newSet( int capacity )
	{
		Set<E> set = new LinkedHashSet<>( capacity );
		return new ObservableCollection<>( set, this::markAsDirty );
	}

	@Override public void markAsDirty()
	{
		dirty = true;
	}

	public int findDeclaredMethodByNameAndDescriptor( String name, String descriptor )
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

	public ByteCodeMethod getMethodByNameAndDescriptor( String name, String descriptor, Function<String,ByteCodeType> byteCodeTypeByName )
	{
		for( ByteCodeType byteCodeType = this; ; )
		{
			for( ByteCodeMethod method : byteCodeType.methods )
				if( match( method, name, descriptor ) )
					return method;
			if( byteCodeType.superClassConstant.isEmpty() )
				break;
			String ancestorName = byteCodeType.superClassConstant.get().getClassName();
			byteCodeType = byteCodeTypeByName.apply( ancestorName );
		}
		throw new AssertionError();
	}

	private static boolean match( ByteCodeMethod method, String name, String descriptor )
	{
		if( !method.getName().equals( name ) )
			return false;
		if( !method.getDescriptorString().equals( descriptor ) )
			return false;
		return true;
	}

	public Collection<String> getDependencyNames()
	{
		return DependencyExtractor.getDependencyNames( this );
	}
}
