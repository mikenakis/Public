package mikenakis_immutability_test;

import mikenakis.immutability.ObjectImmutabilityAssessor;
import mikenakis.immutability.exceptions.ObjectMustBeImmutableException;
import mikenakis.immutability.internal.mykit.MyKit;
import org.junit.Test;

import javax.swing.KeyStroke;
import java.lang.reflect.InaccessibleObjectException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Test.
 */
public class T02_FamousObjects
{
	public T02_FamousObjects()
	{
		if( !MyKit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test public void famous_immutable_objects_are_immutable()
	{
		List<Object> objects = List.of( Instant.EPOCH, Duration.ZERO, UUID.randomUUID(), LocalDate.EPOCH, LocalDateTime.now(), LocalTime.MIDNIGHT, //
			MonthDay.now(), OffsetDateTime.now(), OffsetTime.now(), Period.ZERO, Year.now(), YearMonth.now(), ZoneOffset.UTC, ZonedDateTime.now() );
		for( Object object : objects )
			assert ObjectImmutabilityAssessor.instance.mustBeImmutableAssertion( object );
	}

	@Test public void optional_is_mutable_or_immutable_depending_on_payload()
	{
		ObjectImmutabilityAssessor.instance.mustBeImmutableAssertion( Optional.empty() );
		ObjectImmutabilityAssessor.instance.mustBeImmutableAssertion( Optional.of( 1 ) );
		MyTestKit.expect( ObjectMustBeImmutableException.class, () -> ObjectImmutabilityAssessor.instance.mustBeImmutableAssertion( Optional.of( new StringBuilder() ) ) );
	}

	@Test public void certain_classes_are_messed_up()
	{
		/* PEARL: ZoneId.systemDefault() returns an instance of ZoneRegion, which is mutable! And since ZoneRegion is jdk-internal, we cannot preassess it! */
		MyTestKit.expect( ObjectMustBeImmutableException.class, () -> ObjectImmutabilityAssessor.instance.mustBeImmutableAssertion( ZoneId.systemDefault() ) );

		/* PEARL: Clock.systemUTC() returns an instance of SystemClock, which is inaccessible, so we cannot assess it! */
		MyTestKit.expect( InaccessibleObjectException.class, () -> ObjectImmutabilityAssessor.instance.mustBeImmutableAssertion( Clock.systemUTC() ) );
	}

	@Test public void famous_mutable_objects_are_mutable()
	{
		List<Object> objects = List.of( new ArrayList<>(), new HashMap<>(), new Date(), new HashSet<>(), new LinkedList<>(), new Properties(), //
			new Random(), Pattern.compile("" ).matcher( "" ), new StringBuilder(), new LinkedHashMap<>(), new LinkedHashSet<>(), //
			new SimpleDateFormat( "", Locale.ROOT ), new StringTokenizer( "" ), new ConcurrentHashMap<>(), KeyStroke.getKeyStroke( 'c' ) );
		for( Object object : objects )
			MyTestKit.expect( ObjectMustBeImmutableException.class, () -> ObjectImmutabilityAssessor.instance.mustBeImmutableAssertion( object ) );
	}
}
