package mikenakis.swapi;

import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.swapi.modeling.TextRow;
import mikenakis.swapi.modeling.TextTable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Loads data from the swapi service
 *
 * @author michael.gr
 */
public final class SwapiLoader
{
	/**
	 * The original source of information used to be swapi.co, but that one went belly-up.
	 * Then someone created swapi.dev to replace swapi.co.
	 * However, when I last checked (on 2021-05-18) I found that swapi.dev has at least a couple of serious issues:
	 *   1. It has one less film, while it should have one more.
	 *   2. It has lost the contents of the "species" field of several persons. (All these persons appear to be humans; however, the few additional persons
	 *      do have their "species" set correctly, and they are humans.)
	 * Note that I have not checked everything, so there may be more issues.
	 * As a result, I am not downloading files anymore, and instead the files live with the project.
	 * (If the code path that downloads files is reached, an exception will be thrown.)
	 */
	private static final String SOURCE_URL = "http://swapi.co/api/";
	private static final Path filesPath = getFilesPath();
	private final Swapi swapi;

	private static Path getFilesPath()
	{
		Path result = Path.of( System.getProperty( "user.dir" ), "files" );
		Log.debug( "files path: " + result );
		return result;
	}

	/**
	 * Constructor.
	 */
	public SwapiLoader( Swapi swapi )
	{
		this.swapi = swapi;
	}

	public void run()
	{
		for( TextTable textTable : swapi.getTables() )
		{
			Kit.unchecked( () -> loadTable( textTable ) );
		}
	}

	private static void loadTable( TextTable textTable ) throws IOException
	{
		for( JSONObject jsonObject : loadJsonObjects( textTable.name ) )
		{
			int id = getIdFromJsonObject( textTable.name, jsonObject );
			TextRow textRow = new TextRow( id );
			for( String fieldName : JSONObject.getNames( jsonObject ) )
			{
				Object jsonValue = jsonObject.opt( fieldName );
				if( jsonValue instanceof JSONArray jsonArray )
				{
					List<Integer> entityIds = new ArrayList<>();
					for( int i = 0; i < jsonArray.length(); i++ )
					{
						String entityUrl = jsonArray.getString( i );
						Integer entityId = getIdFromUrl( Optional.empty(), entityUrl );
						entityIds.add( entityId );
					}
					textRow.addList( fieldName, entityIds );
				}
				else
				{
					Optional<String> stringValue;
					if( jsonValue == JSONObject.NULL )
						stringValue = Optional.empty();
					else if( jsonValue instanceof Integer )
						stringValue = Optional.of( String.valueOf( jsonValue ) );
					else
						stringValue = Optional.of( (String)jsonValue );
					textRow.addField( fieldName, stringValue );
				}
			}
			textTable.addRow( textRow );
		}
	}

	private static Collection<JSONObject> loadJsonObjects( String entityTypeName ) throws IOException
	{
		Collection<JSONObject> mutableObjects = new ArrayList<>();

		/* NOTE: we do not try downloading data, we read files from resources instead; see header comment. */
		if( Kit.get( true ) )
		{
			ClassLoader classloader = SwapiLoader.class.getClassLoader();
			Kit.uncheckedTryWith( () -> classloader.getResourceAsStream(entityTypeName + ".txt" ), (InputStream inputStream) -> //
				Kit.uncheckedTryWith( () -> new InputStreamReader( inputStream ), (InputStreamReader inputStreamReader) -> //
					readFromFile( inputStreamReader, mutableObjects ) ) );
		}
		else
		{
			Path path = filesPath.resolve( entityTypeName + ".txt" );
			File file = path.toAbsolutePath().toFile();
			if( file.exists() )
			{
				Kit.uncheckedTryWith( () -> new FileReader( file ), (InputStreamReader inputStreamReader) -> //
					readFromFile( inputStreamReader, mutableObjects ) );
			}
			else
			{
				String url = SOURCE_URL + entityTypeName + "/";
				readFromSwapi( url, mutableObjects );
				writeIntoFile( file, "This file is part of the SWAPI project. It was downloaded from " + url, mutableObjects );
			}
		}
		return Collections.unmodifiableCollection( mutableObjects );
	}

	private static void readFromFile( InputStreamReader inputStreamReader, Collection<JSONObject> mutableObjects ) throws IOException
	{
		try( BufferedReader reader = new BufferedReader( inputStreamReader ) )
		{
			for( ; ; )
			{
				String line = reader.readLine();
				if( line == null )
					break;
				line = line.trim();
				if( line.startsWith( "#" ) )
					continue;
				JSONObject jsonObject = new JSONObject( line );
				Kit.collection.add( mutableObjects, jsonObject );
			}
		}
	}

	private static void writeIntoFile( File file, String comment, Collection<JSONObject> jsonObjects ) throws IOException
	{
		//noinspection ResultOfMethodCallIgnored
		file.getParentFile().mkdirs();
		try( FileWriter fileWriter = new FileWriter( file ); Writer writer = new BufferedWriter( fileWriter ) )
		{
			writer.write( "# " + comment + "\n" );
			for( JSONObject jsonObject : jsonObjects )
			{
				String line = jsonObject.toString();
				writer.write( line );
				writer.write( "\n" );
			}
		}
	}

	private static void readFromSwapi( String url, Collection<JSONObject> mutableJsonObjects ) throws IOException
	{
		//DO NOT USE! see header comment.
		if( Kit.get( true ) )
			throw new AssertionError();
		for( ; ; )
		{
			System.out.println( "Sending SWAPI request: " + url );
			String text = getHyperText( url );
			JSONObject page = new JSONObject( text );
			JSONArray results = page.getJSONArray( "results" );
			int n = results.length();
			for( int i = 0; i < n; i++ )
			{
				Object stupidObject = results.get( i );
				JSONObject jsonObject = (JSONObject)stupidObject;
				Kit.collection.add( mutableJsonObjects, jsonObject );
			}
			Optional<String> nextUrl = getStringOrNull( page, "next" );
			if( nextUrl.isEmpty() )
				break;
			url = nextUrl.get();
		}
	}

	private static Optional<String> getStringOrNull( JSONObject jsonObject, String key )
	{
		Object stupidObject = jsonObject.get( key );
		if( stupidObject == JSONObject.NULL )
			return Optional.empty();
		return Optional.of( (String)stupidObject );
	}

	private static HttpURLConnection handleRedirects( HttpURLConnection connection )
	{
		int status = Kit.unchecked( () -> connection.getResponseCode() );
		if( status == HttpURLConnection.HTTP_OK )
			return connection;
		if( status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER )
		{
			URL newUrl = Kit.unchecked( () -> new URL( connection.getHeaderField( "Location" ) ) );
			HttpURLConnection newConnection = (HttpURLConnection)Kit.unchecked( () -> newUrl.openConnection() );
			newConnection.setRequestProperty( "Cookie", connection.getHeaderField( "Set-Cookie" ) );
			Kit.unchecked( () -> newConnection.setRequestMethod( connection.getRequestMethod() ) );
			newConnection.setDoInput( connection.getDoInput() );
			newConnection.setRequestProperty( "Accept-Language", connection.getRequestProperty( "Accept-Language" ) );
			newConnection.setRequestProperty( "User-Agent", connection.getRequestProperty( "User-Agent" ) );
			newConnection.setRequestProperty( "Accept", connection.getRequestProperty( "Accept" ) );
			return handleRedirects( newConnection );
			//			connection.addRequestProperty( "Accept-Language", "en-US,en;q=0.8" );
			//			connection.addRequestProperty( "User-Agent", "Mozilla" );
			//			connection.addRequestProperty( "Referer", "google.com" );
			//			System.out.println( "Redirect to URL : " + newUrl );
		}
		throw new RuntimeException();
	}

	private static String getHyperText( String input ) throws IOException
	{
		//input = input.replace( "http:", "https:" );
		URL url = new URL( input );
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setInstanceFollowRedirects( true ); //PEARL: this has absolutely no effect.
		connection.setRequestMethod( "GET" );
		connection.setDoInput( true );
		connection.setRequestProperty( "User-Agent", "java-test" );
		connection.setRequestProperty( "Accept", "application/json" );
		connection.addRequestProperty( "Accept-Language", "en-US,en;q=0.8" );
		connection = handleRedirects( connection );
		try( DataInputStream dataInputStream = new DataInputStream( connection.getInputStream() ) )
		{
			try( BufferedReader buffer = new BufferedReader( new InputStreamReader( dataInputStream ) ) )
			{
				var builder = new StringBuilder();
				for( ; ; )
				{
					String line = buffer.readLine();
					if( line == null )
						break;
					builder.append( line );
					builder.append( '\r' );
				}
				return builder.toString();
			}
		}
	}

	private static int getIdFromJsonObject( String entityTypeName, JSONObject jsonObject )
	{
		String url = jsonObject.getString( "url" );
		//url = url.replace( "http:", "https:" );
		return getIdFromUrl( Optional.of( entityTypeName ), url );
	}

	static int getIdFromUrl( Optional<String> entityTypeName, String url )
	{
		assert url.startsWith( SOURCE_URL );
		url = url.substring( SOURCE_URL.length() );
		if( entityTypeName.isPresent() )
		{
			assert url.startsWith( entityTypeName.get() );
			url = url.substring( entityTypeName.get().length() );
		}
		else
		{
			int index = url.indexOf( '/' );
			url = url.substring( index );
		}
		assert url.startsWith( "/" );
		url = url.substring( 1 );
		assert url.endsWith( "/" );
		url = url.substring( 0, url.length() - 1 );
		return Integer.parseInt( url );
	}
}
