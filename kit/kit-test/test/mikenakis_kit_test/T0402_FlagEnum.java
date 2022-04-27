package mikenakis_kit_test;

import mikenakis.debug.Debug;
import mikenakis.kit.Kit;
import mikenakis.kit.collections.FlagEnum;
import mikenakis.kit.collections.FlagSet;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test.
 */
public class T0402_FlagEnum
{
	public T0402_FlagEnum()
	{
		if( !Debug.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private enum E
	{
		A, B, C
	}

	@Test public void FlagEnum_Checks_Out()
	{
		FlagEnum<E> flagEnum = FlagEnum.of( E.class, //
			Map.entry( E.A, 0x0001 ), //
			Map.entry( E.B, 0x0002 ), //
			Map.entry( E.C, 0x0004 ) );
		FlagSet<E> empty = flagEnum.of();
		assert empty.isEmpty();
		assert empty.getBits() == 0;
		FlagSet<E> ab = flagEnum.of( E.A, E.B );
		assert !ab.isEmpty();
		assert ab.getBits() == 0x0003;
		FlagSet<E> abc = ab.tryWith( E.C );
		assert !abc.isEmpty();
		assert abc.getBits() == 0x0007;
		assert abc.equals( flagEnum.of( E.A, E.B, E.C ) );
		FlagSet<E> b = ab.tryWithout( E.A );
		assert !b.isEmpty();
		assert b.getBits() == 0x0002;
		assert b.equals( flagEnum.of( E.B ) );
		assert ab.tryWith( flagEnum.of( E.B, E.C ) ).equals( abc );
		assert ab.tryWithout( flagEnum.of( E.A, E.C ) ).equals( b );
		Map<FlagSet<E>,E> map = new HashMap<>();
		Kit.map.add( map, flagEnum.of( E.A ), E.A );
		Kit.map.add( map, flagEnum.of( E.B ), E.B );
		Kit.map.add( map, flagEnum.of( E.C ), E.C );
		assert Kit.map.get( map, flagEnum.of( E.A ) ) == E.A;
		assert Kit.map.get( map, flagEnum.of( E.B ) ) == E.B;
		assert Kit.map.get( map, flagEnum.of( E.C ) ) == E.C;
	}
}
