package test.mikenakis_rumination_test;

import mikenakis.kit.Kit;
import mikenakis.rumination.annotations.Ruminant;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
@Ignore //this depends on rumination. To enable rumination, add "-javaagent:${OUT}/agentclaire-phat.jar=${OUT}/rumination-phat.jar" to the VM options in the run configuration.
public class T01_RuminizerTest
{
	public T01_RuminizerTest()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	/**
	 * Base class for test {@link Ruminant}s.
	 *
	 * NOTE: for some reason, this must be a top-level class.  If it is a nested class, it appears that the JVM does not pass it to the java agent.
	 *
	 * @author Michael Belivanakis (michael.gr)
	 */
	@Ruminant
	public abstract static class BaseRuminant
	{
		public final List<String> memberChanges = new ArrayList<>();

		protected BaseRuminant()
		{
			Ruminant ruminantAnnotation = BaseRuminant.class.getAnnotation( Ruminant.class );
			assert ruminantAnnotation != null;
			assert ruminantAnnotation.processed();
		}

		protected void onRuminationEvent( String fieldName )
		{
			memberChanges.add( fieldName );
		}
	}

	/**
	 * Test class derived from {@link BaseRuminant}.
	 *
	 * @author Michael Belivanakis (michael.gr)
	 */
	public static final class DerivedRuminant extends BaseRuminant
	{
		//@formatter:off
		public boolean   primitiveBooleanMember = false;
		public byte      primitiveByteMember = 1;
		public char      primitiveCharMember = 1;
		public short     primitiveShortMember = 1;
		public int       primitiveIntMember = 1;
		public float     primitiveFloatMember = 1;
		public long      primitiveLongMember = 1;
		public double    primitiveDoubleMember = 1;
		public Boolean   boxedBooleanMember = false;
		public Byte      boxedByteMember = 1;
		public Character boxedCharMember = 1;
		public Short     boxedShortMember = 1;
		public Integer   boxedIntMember = 1;
		public Float     boxedFloatMember = 1.0f;
		public Long      boxedLongMember = 1L;
		public Double    boxedDoubleMember = 1.0;
		public String    stringMember = "abc";

		public DerivedRuminant()
		{
			Ruminant ruminantAnnotation = DerivedRuminant.class.getAnnotation( Ruminant.class );
			assert ruminantAnnotation != null;
			assert ruminantAnnotation.processed();
		}

		public void setPrimitiveBooleanMember ( boolean   primitiveBooleanMember ) { this.primitiveBooleanMember = primitiveBooleanMember; }
		public void setPrimitiveByteMember    ( byte      primitiveByteMember    ) { this.primitiveByteMember    = primitiveByteMember; }
		public void setPrimitiveCharMember    ( char      primitiveCharMember    ) { this.primitiveCharMember    = primitiveCharMember; }
		public void setPrimitiveShortMember   ( short     primitiveShortMember   ) { this.primitiveShortMember   = primitiveShortMember; }
		public void setPrimitiveIntMember     ( int       primitiveIntMember     ) { this.primitiveIntMember     = primitiveIntMember; }
		public void setPrimitiveFloatMember   ( float     primitiveFloatMember   ) { this.primitiveFloatMember   = primitiveFloatMember; }
		public void setPrimitiveLongMember    ( long      primitiveLongMember    ) { this.primitiveLongMember    = primitiveLongMember; }
		public void setPrimitiveDoubleMember  ( double    primitiveDoubleMember  ) { this.primitiveDoubleMember  = primitiveDoubleMember; }
		public void setBoxedBooleanMember     ( Boolean   boxedBooleanMember     ) { this.boxedBooleanMember     = boxedBooleanMember; }
		public void setBoxedByteMember        ( Byte      boxedByteMember        ) { this.boxedByteMember        = boxedByteMember; }
		public void setBoxedCharMember        ( Character boxedCharMember        ) { this.boxedCharMember        = boxedCharMember; }
		public void setBoxedShortMember       ( Short     boxedShortMember       ) { this.boxedShortMember       = boxedShortMember; }
		public void setBoxedIntMember         ( Integer   boxedIntMember         ) { this.boxedIntMember         = boxedIntMember; }
		public void setBoxedFloatMember       ( Float     boxedFloatMember       ) { this.boxedFloatMember       = boxedFloatMember; }
		public void setBoxedLongMember        ( Long      boxedLongMember        ) { this.boxedLongMember        = boxedLongMember; }
		public void setBoxedDoubleMember      ( Double    boxedDoubleMember      ) { this.boxedDoubleMember      = boxedDoubleMember; }
		public void setStringMember           ( String    stringMember           ) { this.stringMember           = stringMember; }
		//@formatter:on

		public void setStringMember2( String stringMember )
		{
			assert this.stringMember != null;
			this.stringMember = stringMember;
		}

		public void setStringMember3( String stringMember )
		{
			assert this.stringMember != null;
			if( !Objects.equals( this.stringMember, stringMember ) )
			{
				this.stringMember = stringMember;
				onRuminationEvent( "stringMember" );
			}
		}
	}

	@Test
	public void Test1()
	{
		DerivedRuminant ruminant = new DerivedRuminant();

		//@formatter:off
		ruminant.setPrimitiveBooleanMember ( false );    assert ruminant.memberChanges.isEmpty();
		ruminant.setPrimitiveByteMember    ( (byte)1 );  assert ruminant.memberChanges.isEmpty();
		ruminant.setPrimitiveCharMember    ( (char)1 );  assert ruminant.memberChanges.isEmpty();
		ruminant.setPrimitiveShortMember   ( (short)1 ); assert ruminant.memberChanges.isEmpty();
		ruminant.setPrimitiveIntMember     ( 1 );        assert ruminant.memberChanges.isEmpty();
		ruminant.setPrimitiveFloatMember   ( 1 );        assert ruminant.memberChanges.isEmpty();
		ruminant.setPrimitiveLongMember    ( 1 );        assert ruminant.memberChanges.isEmpty();
		ruminant.setPrimitiveDoubleMember  ( 1 );        assert ruminant.memberChanges.isEmpty();
		ruminant.setBoxedBooleanMember     ( false );    assert ruminant.memberChanges.isEmpty();
		ruminant.setStringMember           ( "abc" );    assert ruminant.memberChanges.isEmpty();
		ruminant.setPrimitiveBooleanMember ( true );
		//noinspection ConstantConditions
		assert ruminant.memberChanges.size() == 1;
		ruminant.setPrimitiveByteMember    ( (byte)2 );  assert ruminant.memberChanges.size() == 2;
		ruminant.setPrimitiveCharMember    ( (char)2 );  assert ruminant.memberChanges.size() == 3;
		ruminant.setPrimitiveShortMember   ( (short)2 ); assert ruminant.memberChanges.size() == 4;
		ruminant.setPrimitiveIntMember     ( 2 );        assert ruminant.memberChanges.size() == 5;
		ruminant.setPrimitiveFloatMember   ( 2 );        assert ruminant.memberChanges.size() == 6;
		ruminant.setPrimitiveLongMember    ( 2 );        assert ruminant.memberChanges.size() == 7;
		ruminant.setPrimitiveDoubleMember  ( 2 );        assert ruminant.memberChanges.size() == 8;
		ruminant.setBoxedBooleanMember     ( true );     assert ruminant.memberChanges.size() == 9;
		ruminant.setBoxedByteMember        ( (byte)2 );  assert ruminant.memberChanges.size() == 10;
		ruminant.setBoxedCharMember        ( (char)2 );  assert ruminant.memberChanges.size() == 11;
		ruminant.setBoxedShortMember       ( (short)2 ); assert ruminant.memberChanges.size() == 12;
		ruminant.setBoxedIntMember         ( 2 );        assert ruminant.memberChanges.size() == 13;
		ruminant.setBoxedFloatMember       ( 2.0f );     assert ruminant.memberChanges.size() == 14;
		ruminant.setBoxedLongMember        ( 2L );       assert ruminant.memberChanges.size() == 15;
		ruminant.setBoxedDoubleMember      ( 2.0 );      assert ruminant.memberChanges.size() == 16;
		ruminant.setStringMember           ( "def" );    assert ruminant.memberChanges.size() == 17;
		//@formatter:on

		assert ruminant.memberChanges.get( 0 ).equals( "primitiveBooleanMember" );
		assert ruminant.memberChanges.get( 1 ).equals( "primitiveByteMember" );
		assert ruminant.memberChanges.get( 2 ).equals( "primitiveCharMember" );
		assert ruminant.memberChanges.get( 3 ).equals( "primitiveShortMember" );
		assert ruminant.memberChanges.get( 4 ).equals( "primitiveIntMember" );
		assert ruminant.memberChanges.get( 5 ).equals( "primitiveFloatMember" );
		assert ruminant.memberChanges.get( 6 ).equals( "primitiveLongMember" );
		assert ruminant.memberChanges.get( 7 ).equals( "primitiveDoubleMember" );
		assert ruminant.memberChanges.get( 8 ).equals( "boxedBooleanMember" );
		assert ruminant.memberChanges.get( 9 ).equals( "boxedByteMember" );
		assert ruminant.memberChanges.get( 10 ).equals( "boxedCharMember" );
		assert ruminant.memberChanges.get( 11 ).equals( "boxedShortMember" );
		assert ruminant.memberChanges.get( 12 ).equals( "boxedIntMember" );
		assert ruminant.memberChanges.get( 13 ).equals( "boxedFloatMember" );
		assert ruminant.memberChanges.get( 14 ).equals( "boxedLongMember" );
		assert ruminant.memberChanges.get( 15 ).equals( "boxedDoubleMember" );
		assert ruminant.memberChanges.get( 16 ).equals( "stringMember" );
	}

	@Test
	public void Test2()
	{
		@Ruminant( ruminatorMethodName = "ruminationMethodWithCustomName" )
		class TestRuminant
		{
			public final List<String> memberChanges = new ArrayList<>();

			private int primitiveIntMember = 1;

			TestRuminant()
			{
				assert TestRuminant.class.getAnnotation( Ruminant.class ).processed();
			}

			public void setPrimitiveIntMember( int primitiveIntMember )
			{
				this.primitiveIntMember = primitiveIntMember;
			}

			public int getPrimitiveIntMember()
			{
				return primitiveIntMember;
			}

			protected void ruminationMethodWithCustomName( String fieldName )
			{
				memberChanges.add( fieldName );
			}
		}

		TestRuminant ruminant = new TestRuminant();
		ruminant.setPrimitiveIntMember( 1 );
		assert ruminant.memberChanges.isEmpty();
		assert ruminant.getPrimitiveIntMember() == 1;
		ruminant.setPrimitiveIntMember( 2 );
		//noinspection ConstantConditions
		assert ruminant.memberChanges.size() == 1;
		assert ruminant.memberChanges.get( 0 ).equals( "primitiveIntMember" );
		assert ruminant.getPrimitiveIntMember() == 2;
	}

	@Test
	public void Test3()
	{
		@Ruminant
		class TestRuminant
		{
			public final List<String> memberChanges = new ArrayList<>();

			private int primitiveIntMember = 1;

			TestRuminant()
			{
				assert TestRuminant.class.getAnnotation( Ruminant.class ).processed();
			}

			public void setPrimitiveIntMember( int primitiveIntMember )
			{
				this.primitiveIntMember = primitiveIntMember;
			}

			public int getPrimitiveIntMember()
			{
				return primitiveIntMember;
			}

			protected void ruminationMethodWithIncorrectName( String fieldName )
			{
				memberChanges.add( fieldName );
			}
		}

		TestRuminant ruminant = new TestRuminant();
		ruminant.setPrimitiveIntMember( 1 );
		assert ruminant.memberChanges.isEmpty();
		Kit.testing.expectException( NoSuchMethodError.class, () -> ruminant.setPrimitiveIntMember( 2 ) );
	}
}
