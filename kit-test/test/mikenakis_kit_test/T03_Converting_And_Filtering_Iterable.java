package mikenakis_kit_test;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function1;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

public class T03_Converting_And_Filtering_Iterable
{
	public T03_Converting_And_Filtering_Iterable()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private static final Function1<Optional<String>,Integer> converterAndFilterer = i -> //
	{
		if( (i % 2) == 1 )
			return Optional.empty();
		return Optional.of( String.valueOf( i ) );
	};

	private static void check( List<Integer> source, List<String> expectedResult )
	{
		Iterable<String> convertingAndFilteringIterable = Kit.iterable.convertedAndFiltered( source, converterAndFilterer );
		List<String> actualResult = Kit.iterable.toList( convertingAndFilteringIterable );
		assert actualResult.equals( expectedResult );
	}

	@Test public void Empty_Iterable_Checks_Out()
	{
		check( List.of(), List.of() );
	}

	@Test public void Single_Unfiltered_Item_Iterable_Checks_Out()
	{
		check( List.of( 2 ), List.of( "2" ) );
	}

	@Test public void Two_Unfiltered_Items_Iterable_Checks_Out()
	{
		check( List.of( 2, 4 ), List.of( "2", "4" ) );
	}

	@Test public void Single_Filtered_Item_Iterable_Checks_Out()
	{
		check( List.of( 3 ), List.of() );
	}

	@Test public void Two_Filtered_Items_Iterable_Checks_Out()
	{
		check( List.of( 3, 5 ), List.of() );
	}

	@Test public void One_Filtered_And_One_Unfiltered_Items_Iterable_Checks_Out()
	{
		check( List.of( 3, 4 ), List.of( "4" ) );
	}

	@Test public void One_Unfiltered_And_One_Filtered_Items_Iterable_Checks_Out()
	{
		check( List.of( 2, 3 ), List.of( "2" ) );
	}

	@Test public void One_Unfiltered_Between_Two_Filtered_Items_Iterable_Checks_Out()
	{
		check( List.of( 1, 2, 3 ), List.of( "2" ) );
	}

	@Test public void One_Filtered_Between_Two_Unfiltered_Items_Iterable_Checks_Out()
	{
		check( List.of( 2, 3, 4 ), List.of( "2", "4" ) );
	}
}
