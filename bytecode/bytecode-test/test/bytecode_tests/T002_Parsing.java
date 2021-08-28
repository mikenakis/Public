package bytecode_tests;

import bytecode_tests.model.Class1;
import bytecode_tests.model.Class2WithVariousInstructions;
import mikenakis.bytecode.ByteCodeField;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.kit.Kit;
import org.junit.Test;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class T002_Parsing
{
	public T002_Parsing()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new RuntimeException( "assertions are not enabled!" );
	}

	@Test
	public void Simple_Class_Is_Parsed_Ok()
	{
		Path classFilePathName = Helpers.getPathToClassFile( Class1.class );
		ByteCodeType type = ByteCodeType.create( classFilePathName );
		assert type.access.equals( EnumSet.of( ByteCodeType.Access.Public, ByteCodeType.Access.Super, ByteCodeType.Access.Abstract ) );
		String className = type.getName();
		assert className.equals( Class1.class.getName() );
		Optional<String> superClassName = type.getSuperClassName();
		assert superClassName.isPresent();
		assert superClassName.get().equals( Object.class.getName() );
		assert type.interfaceClassConstants.isEmpty();
		assert type.methods.size() == 3;
		List<ByteCodeMethod> methods = new ArrayList<>( type.methods );
		assert methods.get( 0 ).getName().equals( "<init>" );
		assert methods.get( 1 ).getName().equals( "<init>" );
		assert methods.get( 2 ).getName().equals( "toString" );
		List<ByteCodeField> fields = new ArrayList<>( type.fields );
		assert fields.size() == 12;
		assert fields.get( 0 ).getName().equals( "enum1" );
		assert fields.get( 1 ).getName().equals( "booleanMember" );
		assert fields.get( 2 ).getName().equals( "byteMember" );
		assert fields.get( 3 ).getName().equals( "charMember" );
		assert fields.get( 4 ).getName().equals( "doubleMember" );
		assert fields.get( 5 ).getName().equals( "floatMember" );
		assert fields.get( 6 ).getName().equals( "intMember" );
		assert fields.get( 7 ).getName().equals( "longMember" );
		assert fields.get( 8 ).getName().equals( "shortVar" );
		assert fields.get( 9 ).getName().equals( "stringMember" );
		assert fields.get( 10 ).getName().equals( "arrayOfPrimitiveMember" );
		assert fields.get( 11 ).getName().equals( "arrayOfObjectMember" );
		assert type.tryGetSourceFileName().isPresent(); //yes, according to the JVMS it is an optional attribute, but in practice, it better be there.
		assert type.getSourceFileName().equals( Class1.class.getSimpleName() + ".java" );
	}

	@Test
	public void Class_With_Annotation_With_ArrayValue_Is_Parsed_Ok()
	{
		Path classFilePathName = Helpers.getPathToClassFile( Class2WithVariousInstructions.class );
		ByteCodeType.create( classFilePathName );
	}

	@Test
	public void Multiple_Classes_Enums_And_Interfaces_Are_Parsed_Ok()
	{
		Path rootPath = Helpers.getOutputPath( getClass() ).resolve( "model" );
		List<Path> classFilePathNames = collectResourcePaths( rootPath, ".class" );
		for( Path classFilePathName : classFilePathNames )
			ByteCodeType.create( classFilePathName );
	}

	@Test
	public void Bytecode_Is_Parsed_Ok()
	{
		Path modelPath = Helpers.getOutputPath( getClass() ).resolve( "model" );
		List<Path> classFilePathNames = collectResourcePaths( modelPath, ".class" );
		for( Path classFilePathName : classFilePathNames )
			ByteCodeType.create( classFilePathName );
	}

	private static List<Path> collectResourcePaths( Path rootPath, String suffix )
	{
		List<Path> result = new ArrayList<>();
		collectResourcePathsRecursive( rootPath, suffix, path -> result.add( path ) );
		return result;
	}

	private static void collectResourcePathsRecursive( Path rootPath, String suffix, Consumer<Path> pathConsumer )
	{
		Kit.unchecked( () -> Files.walkFileTree( rootPath, new SimpleFileVisitor<>()
		{
			@Override public FileVisitResult visitFile( Path path, BasicFileAttributes attrs )
			{
				if( path.toString().endsWith( suffix ) )
					pathConsumer.accept( path );
				return FileVisitResult.CONTINUE;
			}
		} ) );
	}
}
