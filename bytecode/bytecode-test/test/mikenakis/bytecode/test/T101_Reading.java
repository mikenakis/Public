package mikenakis.bytecode.test;

import mikenakis.bytecode.test.kit.TestKit;
import mikenakis.bytecode.test.model.Class1;
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
		Path classFilePathName = Helpers.getPathToClassFile( Class1.class );
		ByteCodeType byteCodeType = create( classFilePathName );
		assert byteCodeType.modifierSet().equals( ByteCodeType.modifierFlagsEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Super, ByteCodeType.Modifier.Abstract ) );
		assert byteCodeType.thisClassConstant.getClassName().equals( Class1.class.getName() );
		assert byteCodeType.superClassConstant.orElseThrow().getClassName().equals( Object.class.getName() );
		assert byteCodeType.interfaceClassConstants().isEmpty();
		assert byteCodeType.methods().size() == 3;
		List<ByteCodeMethod> methods = new ArrayList<>( byteCodeType.methods() );
		assert methods.get( 0 ).nameConstant.stringValue().equals( "<init>" );
		assert methods.get( 1 ).nameConstant.stringValue().equals( "<init>" );
		assert methods.get( 2 ).nameConstant.stringValue().equals( "toString" );
		List<ByteCodeField> fields = new ArrayList<>( byteCodeType.fields() );
		assert fields.size() == 13;
		assert fields.get( 0 ).nameConstant.stringValue().equals( "PublicConstant" );
		assert fields.get( 1 ).nameConstant.stringValue().equals( "enum1" );
		assert fields.get( 2 ).nameConstant.stringValue().equals( "booleanMember" );
		assert fields.get( 3 ).nameConstant.stringValue().equals( "byteMember" );
		assert fields.get( 4 ).nameConstant.stringValue().equals( "charMember" );
		assert fields.get( 5 ).nameConstant.stringValue().equals( "doubleMember" );
		assert fields.get( 6 ).nameConstant.stringValue().equals( "floatMember" );
		assert fields.get( 7 ).nameConstant.stringValue().equals( "intMember" );
		assert fields.get( 8 ).nameConstant.stringValue().equals( "longMember" );
		assert fields.get( 9 ).nameConstant.stringValue().equals( "shortVar" );
		assert fields.get( 10 ).nameConstant.stringValue().equals( "stringMember" );
		assert fields.get( 11 ).nameConstant.stringValue().equals( "arrayOfPrimitiveMember" );
		assert fields.get( 12 ).nameConstant.stringValue().equals( "arrayOfObjectMember" );
		assert byteCodeType.tryGetSourceFileName().isPresent(); //yes, according to the JVMS it is an optional attribute, but in practice, it better be there.
		assert byteCodeType.tryGetSourceFileName().orElseThrow().equals( Class1.class.getSimpleName() + ".java" );
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
