package mikenakis.bytecode.test;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.test.kit.TestKit;
import mikenakis.bytecode.test.model.Class1WithFields;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.reading.ByteCodeReader;
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
		return ByteCodeReader.read( bytes );
	}

	@Test public void Simple_Class_Is_Parsed_Ok()
	{
		Path classFilePathName = Helpers.getPathToClassFile( Class1WithFields.class );
		ByteCodeType byteCodeType = create( classFilePathName );
		assert byteCodeType.modifierSet().equals( ByteCodeType.modifierFlagsEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Super, ByteCodeType.Modifier.Abstract ) );
		assert byteCodeType.descriptor().equals( Class1WithFields.class.describeConstable().orElseThrow() );
		assert byteCodeType.superClassDescriptor().orElseThrow().equals( Object.class.describeConstable().orElseThrow() );
		assert ByteCodeHelpers.typeNameFromClassDesc( byteCodeType.descriptor() ).equals( Class1WithFields.class.getTypeName() );
		assert byteCodeType.superClassDescriptor().map( c -> ByteCodeHelpers.typeNameFromClassDesc( c ) ).orElseThrow().equals( Object.class.getTypeName() );
		assert byteCodeType.interfaceClassConstants().isEmpty();
		assert byteCodeType.methods().size() == 3;
		List<ByteCodeMethod> methods = new ArrayList<>( byteCodeType.methods() );
		assert methods.get( 0 ).name().equals( "<init>" );
		assert methods.get( 1 ).name().equals( "<init>" );
		assert methods.get( 2 ).name().equals( "toString" );
		List<ByteCodeField> fields = new ArrayList<>( byteCodeType.fields() );
		assert fields.size() == 13;
		assert fields.get( 0 ).name().equals( "PublicConstant" );
		assert fields.get( 1 ).name().equals( "enumMember" );
		assert fields.get( 2 ).name().equals( "booleanMember" );
		assert fields.get( 3 ).name().equals( "byteMember" );
		assert fields.get( 4 ).name().equals( "charMember" );
		assert fields.get( 5 ).name().equals( "doubleMember" );
		assert fields.get( 6 ).name().equals( "floatMember" );
		assert fields.get( 7 ).name().equals( "intMember" );
		assert fields.get( 8 ).name().equals( "longMember" );
		assert fields.get( 9 ).name().equals( "shortVar" );
		assert fields.get( 10 ).name().equals( "stringMember" );
		assert fields.get( 11 ).name().equals( "arrayOfPrimitiveMember" );
		assert fields.get( 12 ).name().equals( "arrayOfObjectMember" );
		assert byteCodeType.tryGetSourceFileName().isPresent(); //yes, according to the JVMS it is an optional attribute, but in practice, it better be there.
		assert byteCodeType.tryGetSourceFileName().orElseThrow().equals( Class1WithFields.class.getSimpleName() + ".java" );
	}

	@Test public void Multiple_Classes_With_Enums_And_Interfaces_Are_Parsed_Ok()
	{
		Path modelPath = Helpers.getOutputPath( getClass() ).resolve( "model" );
		List<Path> classFilePathNames = TestKit.collectResourcePaths( modelPath, false, ".class" );
		List<byte[]> bytesList = classFilePathNames.stream().map( path -> Kit.unchecked( () -> Files.readAllBytes( path ) ) ).toList();
		for( var bytes : bytesList )
			ByteCodeReader.read( bytes );
	}
}
