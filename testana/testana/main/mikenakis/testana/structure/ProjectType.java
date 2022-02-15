package mikenakis.testana.structure;

import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.java_type_model.MethodDescriptor;
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
 * @author michael.gr
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

	public final ProjectModule projectModule;
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

	public String simpleName()
	{
		String fullName = outputFile.className();
		int i = fullName.lastIndexOf( '.' );
		if( i != -1 )
			return fullName.substring( i + 1 );
		return fullName;
	}

	public Collection<String> dependencyNames()
	{
		if( lazyDependencyNames.isEmpty() )
		{
			Collection<String> allDependencyNames = byteCodeInfo().getDependencyNames();
			LinkedHashSet<String> dependencyNames = allDependencyNames.stream() //
				.filter( projectModule::isProjectTypeTransitively ) //
				.sorted() //
				.collect( Collectors.toCollection( LinkedHashSet::new ) );
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
		return _byteCodeInfo.orElseGet( () -> //
		{
			ByteCodeType byteCodeType = projectModule.getProjectByteCodeTypeByNameTransitively( className() );
			ByteCodeInfo byteCodeInfo = new ByteCodeInfo( byteCodeType );
			_byteCodeInfo = Optional.of( byteCodeInfo );
			return byteCodeInfo;
		} );
	}

	public final String getClassSourceLocation()
	{
		return byteCodeInfo().getClassSourceLocation();
	}

	private static MethodDescriptor testMethodDescriptor()
	{
		return MethodDescriptor.of( void.class );
	}

	public String getMethodSourceLocation( String className, String methodName )
	{
		return ByteCodeInfo.getMethodSourceLocation( className, MethodPrototype.of( methodName, testMethodDescriptor() ), projectModule::getProjectByteCodeTypeByNameTransitively );
	}

	public int getMethodIndex( String methodName )
	{
		return byteCodeInfo().getDeclaredMethodIndex( MethodPrototype.of( methodName, testMethodDescriptor() ) );
	}

	public Optional<TestEngine> testEngine()
	{
		return testEngine;
	}
}
