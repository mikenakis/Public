package io.github.mikenakis.classdump.kit;

import io.github.mikenakis.kit.Kit;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;

/**
 * A Kit of Indispensable Utility Methods.
 *
 * @author michael.gr
 */
@SuppressWarnings( "CharacterComparison" )
public final class Helpers
{
	private Helpers()
	{
	}

	public static String unescapeForJava( String value )
	{
		if( value.length() < 2 )
			throw new RuntimeException();
		char quote = value.charAt( 0 );
		if( quote != '\'' && quote != '\"' )
			throw new RuntimeException();
		if( value.charAt( value.length() - 1 ) != quote )
			throw new RuntimeException();
		value = value.substring( 1, value.length() - 1 );
		var builder = new StringBuilder( value.length() );
		for( int i = 0; i < value.length(); i++ )
		{
			char ch = value.charAt( i );
			if( ch == '\\' )
			{
				char nextChar = (i == value.length() - 1) ? '\\' : value.charAt( i + 1 );
				if( nextChar >= '0' && nextChar <= '7' )
				{
					var code = new StringBuilder();
					code.append( nextChar );
					i++;
					if( (i < value.length() - 1) && value.charAt( i + 1 ) >= '0' && value.charAt( i + 1 ) <= '7' )
					{
						code.append( value.charAt( i + 1 ) );
						i++;
						if( (i < value.length() - 1) && value.charAt( i + 1 ) >= '0' && value.charAt( i + 1 ) <= '7' )
						{
							code.append( value.charAt( i + 1 ) );
							i++;
						}
					}
					builder.append( (char)Integer.parseInt( code.toString(), 8 ) );
					continue;
				}
				switch( nextChar )
				{
					case '\\':
						//ch = '\\';
						break;
					case 'b':
						ch = '\b';
						break;
					case 'f':
						ch = '\f';
						break;
					case 'n':
						ch = '\n';
						break;
					case 'r':
						ch = '\r';
						break;
					case 't':
						ch = '\t';
						break;
					case '\"':
						ch = '\"';
						break;
					case '\'':
						ch = '\'';
						break;
					case 'u':
						if( i >= value.length() - 5 )
						{
							ch = 'u';
							break;
						}
						String number = value.substring( i + 2, i + 6 );
						int code = Integer.parseInt( number, 16 );
						builder.append( Character.toChars( code ) );
						i += 5;
						continue;
					default:
						break;
				}
				i++;
			}
			builder.append( ch );
		}
		return builder.toString();
	}

	public static void forEachFile( Path classFilePathName, String suffix, Consumer<Path> consumer )
	{
		Kit.unchecked( () -> Files.walkFileTree( classFilePathName, new SimpleFileVisitor<>()
		{
			@Override public FileVisitResult visitFile( Path pathName, BasicFileAttributes attrs )
			{
				assert pathName.startsWith( classFilePathName );
				if( attrs.isRegularFile() && pathName.getFileName().toString().endsWith( suffix ) )
					consumer.accept( pathName );
				return FileVisitResult.CONTINUE;
			}
		} ) );
	}
}
