package io.github.mikenakis.bytecode.test.model;

import io.github.mikenakis.testkit.TestKit;

import java.nio.file.Path;
import java.util.List;

public class Model
{
	private static final List<Class<?>> allClasses = List.of(
		Class0HelloWorld.class,
		Class1WithFields.class,
		Class2WithTableswitch.class,
		Class2WithVariousInstructions.class,
		Class3ImplementingInterface.class,
		Class4ExtendingClass3.class,
		Class5ExtendingClass3AndImplementingInterface2.class,
		Class6WithAnnotations.class,
		Class6WithTypeAnnotations.class,
		Class7WithInnerClass.class,
		Class8WithAnonymousClass.class,
		Class9WithCode.class,
		Enum1.class,
		Interface1.class,
		Interface2.class,
		RuntimeInvisibleAnnotation1.class,
		RuntimeInvisibleTypeAnnotation1.class,
		RuntimeVisibleAnnotation1.class,
		RuntimeVisibleAnnotation2.class,
		RuntimeVisibleTypeAnnotation1.class
	);

	public static List<Path> getClassFilePathNames()
	{
		Path modelClassPathName = TestKit.getPathToClassFile( Model.class );
		List<Path> classFilePathNames = TestKit.collectResourcePaths( modelClassPathName.getParent(), false, ".class" )
			.stream().filter( c -> !c.equals( modelClassPathName ) ).toList();
		//If this assertion fails, it probably means that a class was just added to the model. The class needs to also be added to the list of all classes.
		assert classFilePathNames.stream().filter( c -> !c.toString().contains( "$" ) ).toList().size() == allClasses.size();
		return classFilePathNames;
	}
}
