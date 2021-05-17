package mikenakis.testana.structure;

import mikenakis.bytecode.ByteCodeHelpers;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.testana.TestEngine;
import mikenakis.testana.discovery.OutputFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Contains a {@link ByteCodeType} and additional information about it.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class ProjectType
{
	private final ProjectModule projectModule;
	private final OutputFile outputFile;
	private Instant lastModifiedTime;
	private Optional<Collection<String>> dependencyNames;
	private Optional<Class<?>> javaClass = Optional.empty();
	private Optional<ByteCodeType> byteCodeType = Optional.empty();
	private final Optional<TestEngine> testEngine;

	ProjectType( ProjectModule projectModule, OutputFile outputFile, Optional<Collection<String>> dependencyNames, Optional<TestEngine> testEngine )
	{
		this.projectModule = projectModule;
		this.outputFile = outputFile;
		lastModifiedTime = outputFile.lastModifiedTime;
		this.dependencyNames = dependencyNames;
		this.testEngine = testEngine;
	}

	public String className()
	{
		return outputFile.className();
	}

	public Collection<String> dependencyNames()
	{
		if( dependencyNames.isEmpty() )
		{
			ensureClassAndByteCodeAreLoaded();
			String className = className();
			dependencyNames = Optional.of( byteCodeType.orElseThrow().getDependencyNames().stream().filter( t -> !t.equals( className ) && projectModule.isProjectTypeTransitively( t ) ).collect( Collectors.toList() ) );
		}
		return dependencyNames.orElseThrow();
	}

	public Collection<ProjectType> dependencies()
	{
		List<ProjectType> result = new ArrayList<>();
		for( String dependencyName : dependencyNames() )
		{
			ProjectType projectType = projectModule.tryGetProjectTypeByNameTransitively( dependencyName ).orElseThrow();
			result.add( projectType );
		}
		return result;
	}

	public Instant getLastModifiedTime()
	{
		return lastModifiedTime;
	}

	public void setLastModifiedTime( Instant instant )
	{
		lastModifiedTime = instant;
	}

	@Override public String toString()
	{
		return className();
	}

	private void ensureClassAndByteCodeAreLoaded()
	{
		if( javaClass.isEmpty() )
		{
			javaClass = Optional.of( projectModule.getProjectClassByNameTransitively( className() ) );
			byteCodeType = Optional.of( projectModule.getProjectByteCodeTypeByNameTransitively( className() ) );
		}
	}

	public Class<?> javaClass()
	{
		ensureClassAndByteCodeAreLoaded();
		return javaClass.orElseThrow();
	}

	public final String getClassSourceLocation()
	{
		return ByteCodeHelpers.getClassSourceLocation( byteCodeType.orElseThrow() );
	}

	public String getMethodSourceLocation( String methodName )
	{
		ensureClassAndByteCodeAreLoaded();
		ByteCodeMethod byteCodeMethod = byteCodeType.orElseThrow().getMethodByNameAndDescriptor( methodName, "()V", projectModule::getProjectByteCodeTypeByNameTransitively );
		return ByteCodeHelpers.getMethodSourceLocation( byteCodeMethod );
	}

	public int getDeclaredMethodIndex( String methodName )
	{
		ensureClassAndByteCodeAreLoaded();
		return byteCodeType.orElseThrow().findDeclaredMethodByNameAndDescriptor( methodName, "()V" );
	}

	public Optional<TestEngine> testEngine()
	{
		return testEngine;
	}
}
