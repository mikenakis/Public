package mikenakis.bytecode.model.attributes.stackmap.verification;

import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;

/**
 * Verification Type.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class VerificationType
{
	public enum Tag
	{
		Top               /**/( 0 ), //
		Integer           /**/( 1 ), //
		Float             /**/( 2 ), //
		Double            /**/( 3 ), //
		Long              /**/( 4 ), //
		Null              /**/( 5 ), //
		UninitializedThis /**/( 6 ), //
		Object            /**/( 7 ), //
		Uninitialized     /**/( 8 );

		private static final List<Tag> values = List.of( values() );

		public static Tag fromNumber( int number )
		{
			return values.get( number );
		}

		Tag( int tagNumber )
		{
			assert tagNumber == ordinal();
		}

		public int number()
		{
			return ordinal();
		}
	}

	public interface Switcher<T>
	{
		T caseSimpleVerificationType();
		T caseObjectVerificationType();
		T caseUninitializedVerificationType();
	}

	public static <T> T doSwitch( Tag tag, Switcher<T> switcher )
	{
		return switch( tag )
			{
				case Top, Integer, Float, Double, Long, Null, UninitializedThis -> //
					switcher.caseSimpleVerificationType();        //
				case Object ->                                    //
					switcher.caseObjectVerificationType();        //
				case Uninitialized ->                             //
					switcher.caseUninitializedVerificationType(); //
			};
	}

	public final Tag tag;

	protected VerificationType( Tag tag )
	{
		this.tag = tag;
	}

	public interface Visitor<T>
	{
		T visit( SimpleVerificationType simpleVerificationType );
		T visit( ObjectVerificationType objectVerificationType );
		T visit( UninitializedVerificationType uninitializedVerificationType );
	}

	public <T> T visit( Visitor<T> visitor )
	{
		return doSwitch( tag, new Switcher<>()
		{
			@Override public T caseSimpleVerificationType()
			{
				return visitor.visit( asSimpleVerificationType() );
			}
			@Override public T caseObjectVerificationType()
			{
				return visitor.visit( asObjectVerificationType() );
			}
			@Override public T caseUninitializedVerificationType()
			{
				return visitor.visit( asUninitializedVerificationType() );
			}
		} );
	}

	@ExcludeFromJacocoGeneratedReport protected ObjectVerificationType asObjectVerificationType() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport protected SimpleVerificationType asSimpleVerificationType() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport protected UninitializedVerificationType asUninitializedVerificationType() { return Kit.fail(); }

	@ExcludeFromJacocoGeneratedReport @Override @OverridingMethodsMustInvokeSuper public String toString()
	{
		return "tag = " + tag.name();
	}
}
