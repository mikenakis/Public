package mikenakis.testana.structure.cache;

import mikenakis.kit.Kit;
import mikenakis.testana.discovery.OutputDirectory;
import mikenakis.testana.discovery.OutputFile;
import mikenakis.testana.kit.structured.json.JsonReader;
import mikenakis.testana.kit.structured.json.JsonWriter;
import mikenakis.testana.kit.structured.json.reading.JsonStructuredReader;
import mikenakis.testana.kit.structured.json.writing.JsonStructuredWriter;
import mikenakis.testana.kit.structured.reading.StructuredReader;
import mikenakis.testana.kit.structured.writing.StructuredWriter;
import mikenakis.testana.structure.ProjectModule;
import mikenakis.testana.structure.ProjectStructure;
import mikenakis.testana.structure.ProjectType;

import java.io.BufferedReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

public class Cache
{
	public static Cache empty()
	{
		return new Cache( Instant.MIN, Map.of() );
	}

	public static Cache fromFile( Path cachePathName )
	{
		Map<String,CacheModule> modules = Kit.uncheckedTryGetWith( () -> Files.newBufferedReader( cachePathName, StandardCharsets.UTF_8 ), ( BufferedReader bufferedReader ) -> //
		{
			JsonReader jsonReader = new JsonReader( bufferedReader, true );
			StructuredReader rootReader = new JsonStructuredReader( jsonReader );
			return rootReader.readArray( "modules", moduleArrayReader -> //
			{
				Map<String,CacheModule> modules1 = new LinkedHashMap<>();
				moduleArrayReader.readElements( moduleElementReader -> //
				{
					CacheModule cacheModule = moduleElementReader.readObject( moduleObjectReader -> //
					{
						String moduleName = moduleObjectReader.readMember( "name", StructuredReader::readValue );
						Map<Path,CacheOutputDirectory> outputDirectories = moduleObjectReader.readMember( "outputDirectories", outputDirectoriesMemberReader -> //
						{
							return outputDirectoriesMemberReader.readArray( "outputDirectory", outputDirectoryArrayReader -> //
							{
								Map<Path,CacheOutputDirectory> outputDirectories1 = new LinkedHashMap<>();
								outputDirectoryArrayReader.readElements( outputDirectoryElementReader -> //
								{
									CacheOutputDirectory outputDirectory = outputDirectoryElementReader.readObject( outputDirectoryObjectReader -> //
									{
										Path path = Path.of( outputDirectoryObjectReader.readMember( "path", StructuredReader::readValue ) );
										Map<String,CacheType> types = new LinkedHashMap<>();
										outputDirectoryObjectReader.readMember( "types", typesMemberReader -> //
										{
											typesMemberReader.readArray( "type", typeArrayReader -> //
											{
												typeArrayReader.readElements( typeElementReader -> //
												{
													CacheType type = typeElementReader.readObject( elementObjectReader -> //
													{
														String className = elementObjectReader.readMember( "className", StructuredReader::readValue );
														Optional<String> testEngineName = elementObjectReader.readMember( "testEngineName", StructuredReader::readOptionalValue );
														Collection<String> dependencyNames = elementObjectReader.readMember( "dependencies", dependenciesMemberReader -> //
														{
															return dependenciesMemberReader.readArray( "name", dependencyArrayReader -> //
															{
																Collection<String> names = new LinkedHashSet<>();
																dependencyArrayReader.readElements( dependencyElementReader -> //
																{
																	String name = dependencyElementReader.readValue();
																	Kit.collection.add( names, name );
																} );
																return names;
															} );
														} );
														return new CacheType( className, dependencyNames, testEngineName );
													} );
													Kit.map.add( types, type.className, type );
												} );
											} );
										} );
										return new CacheOutputDirectory( path, types );
									} );
									Kit.map.add( outputDirectories1, outputDirectory.path, outputDirectory );
								} );
								return outputDirectories1;
							} );
						} );
						return new CacheModule( moduleName, outputDirectories );
					} );
					Kit.map.add( modules1, cacheModule.name, cacheModule );
				} );
				return Collections.unmodifiableMap( modules1 );
			} );
		});
		Instant instant = Kit.unchecked( () -> Files.getLastModifiedTime( cachePathName ) ).toInstant();
		return new Cache( instant, modules );
	}

	public static Cache fromProjectStructure( ProjectStructure projectStructure )
	{
		Map<String,CacheModule> cacheModules = new LinkedHashMap<>();
		for( ProjectModule projectModule : projectStructure.projectModules() )
		{
			String name = projectModule.name();
			Map<Path,CacheOutputDirectory> outputDirectories = new LinkedHashMap<>();
			for( OutputDirectory outputDirectory : projectModule.outputDirectories() )
			{
				Map<String,CacheType> cacheTypes = new LinkedHashMap<>();
				for( OutputFile projectFile : outputDirectory.files() )
				{
					if( projectFile.type != OutputFile.Type.Class )
						continue;
					Optional<ProjectType> projectTypeOpt = projectModule.tryGetProjectTypeByOutputFile( projectFile );
					if( projectTypeOpt.isEmpty() )
						continue;
					ProjectType projectType = projectTypeOpt.get();
					Collection<String> dependencyNames = projectType.dependencyNames();
					Optional<String> testEngineName = projectType.testEngine().map( e -> e.name() );
					CacheType cacheType = new CacheType( projectType.className(), dependencyNames, testEngineName );
					Kit.map.add( cacheTypes, projectType.className(), cacheType );
				}
				CacheOutputDirectory cacheOutputDirectory = new CacheOutputDirectory( outputDirectory.path, cacheTypes );
				Kit.map.add( outputDirectories, outputDirectory.path, cacheOutputDirectory );
			}
			CacheModule cacheModule = new CacheModule( name, outputDirectories );
			Kit.map.add( cacheModules, name, cacheModule );
		}
		return new Cache( Instant.MIN, cacheModules );
	}

	private final Instant timestamp;
	private final Map<String,CacheModule> modules;
	private int hits;
	private int misses;

	private Cache( Instant timestamp, Map<String,CacheModule> modules )
	{
		this.timestamp = timestamp;
		this.modules = modules;
	}

	public int hits()
	{
		return hits;
	}

	public int misses()
	{
		return misses;
	}

	public void save( Path path )
	{
		Kit.uncheckedTryWith( () -> Files.newBufferedWriter( path ), ( Writer writer ) -> //
		{
			JsonWriter jsonWriter = new JsonWriter( writer, true, true );
			StructuredWriter rootWriter = new JsonStructuredWriter( jsonWriter, JsonWriter.Mode.Object );
			rootWriter.writeArray( "modules", moduleArrayWriter -> //
			{
				for( CacheModule module : modules.values() )
				{
					moduleArrayWriter.writeElement( moduleElementWriter -> //
					{
						moduleElementWriter.writeObject( moduleObjectWriter -> //
						{
							moduleObjectWriter.writeMember( "name", moduleMemberWriter -> moduleMemberWriter.writeValue( module.name ) );
							moduleObjectWriter.writeMember( "outputDirectories", moduleMemberWriter -> //
								moduleMemberWriter.writeArray( "outputDirectory", outputDirectoryArrayWriter -> //
								{
									for( CacheOutputDirectory outputDirectory : module.outputDirectories() )
										outputDirectoryArrayWriter.writeElement( outputDirectoryElementWriter -> //
										{
											outputDirectoryElementWriter.writeObject( outputDirectoryObjectWriter -> //
											{
												outputDirectoryObjectWriter.writeMember( "path", memberWriter -> memberWriter.writeValue( outputDirectory.path.toString() ) );
												outputDirectoryObjectWriter.writeMember( "types", typesMemberWriter -> //
													typesMemberWriter.writeArray( "type", typesArrayWriter -> //
													{
														for( CacheType type : outputDirectory.types() )
														{
															typesArrayWriter.writeElement( typesElementWriter -> //
															{
																typesElementWriter.writeObject( typeObjectWriter -> //
																{
																	typeObjectWriter.writeMember( "className", memberWriter -> memberWriter.writeValue( type.className ) );
																	typeObjectWriter.writeMember( "testEngineName", memberWriter -> memberWriter.writeOptionalValue( type.testEngineName ) );
																	typeObjectWriter.writeMember( "dependencies", dependenciesMemberWriter -> //
																		dependenciesMemberWriter.writeArray( "name", dependencyArrayWriter -> //
																		{
																			for( String name : type.dependencyNames )
																				dependencyArrayWriter.writeElement( dependencyElementWriter -> dependencyElementWriter.writeValue( name ) );
																		} ) );
																} );
															} );
														}
													} ) );
											} );
										} );
								} ) );
						} );
					} );
				}
			} );
		} );
	}

	public Optional<CacheType> tryGetType( String moduleName, OutputDirectory outputDirectory, String className, Instant lastModifiedTime )
	{
		if( lastModifiedTime.isAfter( timestamp ) )
			return Optional.empty();
		Optional<CacheType> cacheType = Optional.ofNullable( Kit.map.tryGet( modules, moduleName ) ).flatMap( m -> m.tryGetType( outputDirectory, className ) );
		if( cacheType.isPresent() )
			hits++;
		else
			misses++;
		return cacheType;
	}

	public int moduleCount()
	{
		return modules.size();
	}

	public int typeCount()
	{
		return modules.values().stream().map( m -> m.typeCount() ).reduce( 0, ( a, b ) -> a + b );
	}
}
