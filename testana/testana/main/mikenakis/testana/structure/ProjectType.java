package mikenakis.testana.structure;

import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.descriptors.MethodDescriptor;
import mikenakis.testana.TestEngine;
import mikenakis.testana.discovery.OutputFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Contains a {@link ByteCodeType} and additional information about it.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class ProjectType
{
	public static ProjectType of( ProjectModule projectModule, OutputFile outputFile, Optional<TestEngine> testEngine, Collection<String> dependencyNames )
	{
		return new ProjectType( projectModule, outputFile, testEngine, Optional.of( dependencyNames ) );
	}

	public static ProjectType of( ProjectModule projectModule, OutputFile outputFile, Optional<TestEngine> testEngine )
	{
		return new ProjectType( projectModule, outputFile, testEngine, Optional.empty() );
	}

	private final ProjectModule projectModule;
	private final OutputFile outputFile;
	private Instant lastModifiedTime;
	private Optional<Collection<String>> lazyDependencyNames;
	@SuppressWarnings( "FieldNamingConvention" ) private Optional<Class<?>> _javaClass = Optional.empty();
	@SuppressWarnings( "FieldNamingConvention" ) private Optional<ByteCodeInfo> _byteCodeInfo = Optional.empty();
	private final Optional<TestEngine> testEngine;

	private ProjectType( ProjectModule projectModule, OutputFile outputFile, Optional<TestEngine> testEngine, Optional<Collection<String>> lazyDependencyNames )
	{
		this.projectModule = projectModule;
		this.outputFile = outputFile;
		lastModifiedTime = outputFile.lastModifiedTime;
		this.lazyDependencyNames = lazyDependencyNames;
		this.testEngine = testEngine;
	}

	public String className()
	{
		return outputFile.className();
	}

	public Collection<String> dependencyNames()
	{
		if( lazyDependencyNames.isEmpty() )
		{
			Collection<String> allDependencyNames = byteCodeInfo().getDependencyNames();
			LinkedHashSet<String> dependencyNames = allDependencyNames.stream() //
				.filter( t -> projectModule.isProjectTypeTransitively( t ) ) //
				.sorted() //
				.collect( Collectors.toCollection( () -> new LinkedHashSet<>() ) );
			lazyDependencyNames = Optional.of( dependencyNames );
		}
		return lazyDependencyNames.orElseThrow();
	}

	public Collection<ProjectType> dependencies()
	{
		Collection<ProjectType> result = new ArrayList<>();
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

	public Class<?> javaClass()
	{
		if( _javaClass.isEmpty() )
			_javaClass = Optional.of( projectModule.getProjectClassByNameTransitively( className() ) );
		return _javaClass.orElseThrow();
	}

	private ByteCodeInfo byteCodeInfo()
	{
		if( _byteCodeInfo.isEmpty() )
		{
			ByteCodeType byteCodeType = projectModule.getProjectByteCodeTypeByNameTransitively( className() );
			_byteCodeInfo = Optional.of( new ByteCodeInfo( byteCodeType ) );
		}
		return _byteCodeInfo.orElseThrow();
	}

	public final String getClassSourceLocation()
	{
		return byteCodeInfo().getClassSourceLocation();
	}

	private static MethodDescriptor testMethodDescriptor()
	{
		return MethodDescriptor.ofDescriptorString( "()V" );
	}

	public String getMethodSourceLocation( String methodName )
	{
		return byteCodeInfo().getMethodSourceLocation( methodName, testMethodDescriptor(), projectModule::getProjectByteCodeTypeByNameTransitively );
	}

	public int getDeclaredMethodIndex( String methodName )
	{
		return byteCodeInfo().getDeclaredMethodIndex( methodName, testMethodDescriptor() );
	}

	public Optional<TestEngine> testEngine()
	{
		return testEngine;
	}
}
