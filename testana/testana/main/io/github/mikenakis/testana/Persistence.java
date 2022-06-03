package io.github.mikenakis.testana;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.logging.Log;
import io.github.mikenakis.testana.kit.structured.json.JsonReader;
import io.github.mikenakis.testana.kit.structured.json.JsonWriter;
import io.github.mikenakis.testana.kit.structured.json.reading.JsonStructuredReader;
import io.github.mikenakis.testana.kit.structured.json.writing.JsonStructuredWriter;
import io.github.mikenakis.testana.kit.structured.reading.StructuredReader;
import io.github.mikenakis.testana.kit.structured.writing.StructuredWriter;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Persistence.
 *
 * @author michael.gr
 */
public final class Persistence
{
	private static class TestClassInfo
	{
		final String testClassName;
		Instant timeOfLastRun;
		boolean used;

		TestClassInfo( String testClassName, Instant timeOfLastRun )
		{
			this.testClassName = testClassName;
			this.timeOfLastRun = timeOfLastRun;
		}

		@Override public String toString()
		{
			return "timeStamp = " + timeOfLastRun + "; used = " + used;
		}
	}

	private final Path persistencePathName;
	private final boolean autoSave;
	private final Map<String,TestClassInfo> entryFromNameMap = new LinkedHashMap<>();
	private boolean dirty;

	public Persistence( Path persistencePathName, boolean skipLoad, boolean autoSave )
	{
		this.persistencePathName = persistencePathName;
		this.autoSave = autoSave;
		if( skipLoad )
		{
			dirty = true;
		}
		else
		{
			if( !Files.exists( persistencePathName ) )
				return;
			try
			{
				load( persistencePathName );
			}
			catch( Throwable exception )
			{
				Log.warning( "Failed to load persistence due to " + exception.getClass() + (exception.getMessage() == null ? "" : ": " + exception.getMessage()) );
			}
		}
	}

	private void load( Path persistencePathName )
	{
		Kit.uncheckedTryWith( () -> Files.newBufferedReader( persistencePathName ), bufferedReader -> //
		{
			JsonReader jsonReader = new JsonReader( bufferedReader, true );
			StructuredReader rootReader = new JsonStructuredReader( jsonReader );
			rootReader.readArray( "elementName", arrayReader -> //
			{
				Map<String,TestClassInfo> mutableMap = new LinkedHashMap<>();
				arrayReader.readElements( elementReader -> //
				{
					TestClassInfo entry = elementReader.readObject( elementObjectReader -> //
					{
						String name = elementObjectReader.readMember( "name", StructuredReader::readValue );
						Instant timeOfLastRun = Instant.parse( elementObjectReader.readMember( "timeOfLastRun", StructuredReader::readValue ) );
						return new TestClassInfo( name, timeOfLastRun );
					} );
					Kit.map.add( mutableMap, entry.testClassName, entry );
				} );
				entryFromNameMap.putAll( mutableMap );
			} );
		} );
	}

	public boolean isDirty()
	{
		return dirty;
	}

	public void save()
	{
		/**
		 * PEARL: Files.newBufferedWriter() disregards the fact that BufferedWriter's close() method does not close the contained OutputStreamWriter!
		 *        Files.newBufferedWriter() returns a new BufferedWriter which writes to a new OutputStreamWriter which writes to a new OutputStream.
		 *        The OutputStreamWriter's close() method contains a call to the contained OutputStream's close() method; however, BufferedWriter's close()
		 *        method does not contain a call to the contained OutputStreamWriter's close() method!  Therefore, when the BufferedWriter gets closed, neither
		 *        the contained OutputStreamWriter nor the contained OutputStream get closed, so the OutputStream remains open until it gets garbage-collected!
		 *        (This has been observed in JDK 17.)
		 *        Thus, we have to refrain from invoking Files.newBufferedWriter() and instead create the BufferedWriter and the OutputStreamWriter ourselves,
		 *        so that we can make sure they both get closed.
		 */
		Kit.uncheckedTryWith( () -> new OutputStreamWriter( Files.newOutputStream( persistencePathName ), StandardCharsets.UTF_8 ), outputStreamWriter -> //
			Kit.uncheckedTryWith( () -> new BufferedWriter( outputStreamWriter, 32 * 1024 ), writer -> //
			{
				JsonWriter jsonWriter = new JsonWriter( writer, true, true );
				StructuredWriter rootWriter = new JsonStructuredWriter( jsonWriter, JsonWriter.Mode.Object );
				rootWriter.writeArray( "elementName", arrayWriter -> //
				{
					List<String> names = new ArrayList<>( entryFromNameMap.keySet() );
					names.sort( Comparator.naturalOrder() );
					for( String name : names )
					{
						TestClassInfo entry = Kit.map.get( entryFromNameMap, name );
						if( !entry.used )
						{
							Log.info( "Persistence entry for " + name + " not used, forgetting..." );
							Kit.map.remove( entryFromNameMap, name );
							continue;
						}
						arrayWriter.writeElement( elementWriter -> elementWriter.writeObject( elementObjectWriter -> //
						{
							elementObjectWriter.writeMember( "name", memberWriter -> memberWriter.writeValue( name ) );
							elementObjectWriter.writeMember( "timeOfLastRun", memberWriter -> memberWriter.writeValue( entry.timeOfLastRun.toString() ) );
						} ) );
					}
				} );
			} ) );
		dirty = false;
	}

	public Optional<Instant> tryGetTimeOfLastRun( String name )
	{
		Optional<TestClassInfo> entry = Optional.ofNullable( Kit.map.tryGet( entryFromNameMap, name ) );
		entry.ifPresent( e -> e.used = true );
		return entry.map( e -> e.timeOfLastRun );
	}

	public void setTimeOfLastRun( String name, Instant instant )
	{
		TestClassInfo entry = Kit.map.getOptional( entryFromNameMap, name ).orElseGet( () -> //
		{
			TestClassInfo e = new TestClassInfo( name, Instant.EPOCH );
			Kit.map.add( entryFromNameMap, name, e );
			return e;
		} );
		entry.timeOfLastRun = instant;
		entry.used = true;
		if( autoSave )
			save();
		else
			dirty = true;
	}
}
