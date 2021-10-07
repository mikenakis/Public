package mikenakis.bytecode.test;

import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.test.kit.TestKit;
import mikenakis.bytecode.test.model.Class1WithFields;
import mikenakis.bytecode.test.model.Model;
import mikenakis.kit.Kit;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class T101_Reading
{
	public T101_Reading()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new RuntimeException( "assertions are not enabled!" );
	}

	private static ByteCodeType create( Path classFilePathName )
	{
		byte[] bytes = Kit.unchecked( () -> Files.readAllBytes( classFilePathName ) );
		return ByteCodeType.read( bytes );
	}

	@Test public void Simple_Class_Is_Parsed_Ok()
	{
		Path classFilePathName = TestKit.getPathToClassFile( Class1WithFields.class );
		ByteCodeType byteCodeType = create( classFilePathName );
		assert byteCodeType.modifiers.equals( ByteCodeType.modifierEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Super, ByteCodeType.Modifier.Abstract ) );
		assert ByteCodeHelpers.descriptorStringFromTypeDescriptor( byteCodeType.typeDescriptor() ).equals( Class1WithFields.class.describeConstable().orElseThrow().descriptorString() );
		assert byteCodeType.typeDescriptor().typeName.equals( Class1WithFields.class.getTypeName() );
		assert ByteCodeHelpers.descriptorStringFromTypeDescriptor( byteCodeType.superTypeDescriptor().orElseThrow() ).equals( Object.class.describeConstable().orElseThrow().descriptorString() );
		assert byteCodeType.superTypeDescriptor().orElseThrow().typeName.equals( Object.class.getTypeName() );
		assert byteCodeType.interfaces().isEmpty();
		assert byteCodeType.methods.size() == 3;
		List<ByteCodeMethod> methods = new ArrayList<>( byteCodeType.methods );
		assert methods.get( 0 ).name().equals( "<init>" );
		assert methods.get( 1 ).name().equals( "<init>" );
		assert methods.get( 2 ).name().equals( "toString" );
		List<ByteCodeField> fields = new ArrayList<>( byteCodeType.fields );
		assert fields.size() == 17;
		assert fields.get( 0 ).name().equals( "PublicStringConstant" );
		assert fields.get( 1 ).name().equals( "PublicDoubleConstant" );
		assert fields.get( 2 ).name().equals( "PublicFloatConstant" );
		assert fields.get( 3 ).name().equals( "PublicLongConstant" );
		assert fields.get( 4 ).name().equals( "PublicIntConstant" );
		assert fields.get( 5 ).name().equals( "enumMember" );
		assert fields.get( 6 ).name().equals( "booleanMember" );
		assert fields.get( 7 ).name().equals( "byteMember" );
		assert fields.get( 8 ).name().equals( "charMember" );
		assert fields.get( 9 ).name().equals( "doubleMember" );
		assert fields.get( 10 ).name().equals( "floatMember" );
		assert fields.get( 11 ).name().equals( "intMember" );
		assert fields.get( 12 ).name().equals( "longMember" );
		assert fields.get( 13 ).name().equals( "shortVar" );
		assert fields.get( 14 ).name().equals( "stringMember" );
		assert fields.get( 15 ).name().equals( "arrayOfPrimitiveMember" );
		assert fields.get( 16 ).name().equals( "arrayOfObjectMember" );
		assert byteCodeType.tryGetSourceFileName().isPresent(); //yes, according to the JVMS it is an optional attribute, but in practice, it better be there.
		assert byteCodeType.tryGetSourceFileName().orElseThrow().equals( Class1WithFields.class.getSimpleName() + ".java" );
	}

	@Test public void Multiple_Classes_With_Enums_And_Interfaces_Are_Parsed_Ok()
	{
		List<byte[]> bytesList = Model.getClassFilePathNames().stream() //
			.map( path -> Kit.unchecked( () -> Files.readAllBytes( path ) ) ) //
			.toList();
		for( var bytes : bytesList )
			ByteCodeType.read( bytes );
	}
}
