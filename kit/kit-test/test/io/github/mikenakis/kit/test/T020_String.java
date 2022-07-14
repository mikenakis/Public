package io.github.mikenakis.kit.test;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.testkit.TestKit;
import org.junit.Test;

import java.util.List;

/**
 * Test.
 */
public class T020_String
{
	public T020_String()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test
	public void replaceAll_Works()
	{
		TestKit.expect( AssertionError.class, () -> //
			Kit.string.replaceAll( "", "", "!" ) );
		assert Kit.string.replaceAll( "", "/", "!" ).equals( "" );
		assert Kit.string.replaceAll( "a", "/", "!" ).equals( "a" );
		assert Kit.string.replaceAll( "a/b", "/", "!" ).equals( "a!b" );
		assert Kit.string.replaceAll( "a/b/c", "/", "!" ).equals( "a!b!c" );
		assert Kit.string.replaceAll( "/a/", "/", "!" ).equals( "!a!" );
		assert Kit.string.replaceAll( "a/b", "/", "" ).equals( "ab" );
		assert Kit.string.replaceAll( "/a/", "/", "" ).equals( "a" );
		assert Kit.string.replaceAll( "a///b", "//", "!" ).equals( "a!b" );
		assert Kit.string.replaceAll( "a///b///c", "//", "!" ).equals( "a!b!c" );
		assert Kit.string.replaceAll( "///a///", "//", "!" ).equals( "!a!" );
		assert Kit.string.replaceAll( "a///b", "//", "" ).equals( "ab" );
		assert Kit.string.replaceAll( "///a///", "//", "" ).equals( "a" );
	}

	@Test
	public void split_works()
	{
		assert List.of( Kit.string.split( "", '-' ) ).equals( List.of() );
		assert List.of( Kit.string.split( "-", '-' ) ).equals( List.of( "" ) ); //equals( List.of( "", "" ) );
		assert List.of( Kit.string.split( "a", '-' ) ).equals( List.of( "a" ) );
		assert List.of( Kit.string.split( "a-", '-' ) ).equals( List.of( "a" ) ); //List.of( "a", "" ) );
		assert List.of( Kit.string.split( "-b", '-' ) ).equals( List.of( "", "b" ) );
		assert List.of( Kit.string.split( "a-b", '-' ) ).equals( List.of( "a", "b" ) );
		assert List.of( Kit.string.split( " ", '-', true ) ).equals( List.of( "" ) );
		assert List.of( Kit.string.split( " - ", '-', true ) ).equals( List.of( "", "" ) );
		assert List.of( Kit.string.split( " a ", '-', true ) ).equals( List.of( "a" ) );
		assert List.of( Kit.string.split( " a - ", '-', true ) ).equals( List.of( "a", "" ) );
		assert List.of( Kit.string.split( " - b ", '-', true ) ).equals( List.of( "", "b" ) );
		assert List.of( Kit.string.split( " a - b ", '-', true ) ).equals( List.of( "a", "b" ) );
		assert List.of( Kit.string.split( "a-b-c", '-', 2 ) ).equals( List.of( "a", "b-c" ) );
	}
}
