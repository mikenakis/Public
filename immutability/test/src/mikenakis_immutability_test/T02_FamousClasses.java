package mikenakis_immutability_test;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.mykit.MyKit;
import mikenakis.immutability.type.TypeImmutabilityAssessor;
import mikenakis.immutability.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.type.assessments.TypeAssessment;
import mikenakis.immutability.type.assessments.provisory.ExtensibleAssessment;
import mikenakis.immutability.type.assessments.provisory.ProvisoryContentAssessment;
import org.junit.Test;

import javax.swing.KeyStroke;
import java.awt.MouseInfo;
import java.sql.DriverManager;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

/**
 * Test.
 */
public class T02_FamousClasses
{
	public T02_FamousClasses()
	{
		if( !MyKit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private static TypeImmutabilityAssessor newAssessor()
	{
		return TypeImmutabilityAssessor.create( TestStringizer.instance );
	}

	private static TypeAssessment assess( TypeImmutabilityAssessor assessor, Class<?> type )
	{
		TypeAssessment assessment = assessor.assess( type );
		System.out.println( "assessment for type " + TestStringizer.instance.stringize( type ) + ":" );
		MyKit.<Assessment>tree( assessment, a -> a.children(), a -> a.toString(), s -> System.out.println( "    " + s ) );
		return assessment;
	}

	private final TypeImmutabilityAssessor assessor = newAssessor();

	@Test public void famous_immutable_classes()
	{
		List<Class<?>> classes = List.of( Instant.class, Duration.class, UUID.class, LocalDate.class, LocalDateTime.class, LocalTime.class, //
			MonthDay.class, OffsetDateTime.class, OffsetTime.class, Period.class, Year.class, YearMonth.class, ZoneOffset.class );
		for( Class<?> jvmClass : classes )
		{
			TypeAssessment assessment = assess( assessor, jvmClass );
			assert assessment instanceof ImmutableTypeAssessment;
		}
	}

	@Test public void famous_content_provisory_classes()
	{
		List<Class<?>> classes = List.of( Optional.class, ZonedDateTime.class );
		for( Class<?> jvmClass : classes )
		{
			TypeAssessment assessment = assess( assessor, jvmClass );
			assert assessment instanceof ProvisoryContentAssessment;
		}
	}

	@Test public void famous_extensible_provisory_classes()
	{
		List<Class<?>> classes = List.of( Collections.class, Clock.class, ZoneId.class, MouseInfo.class, DriverManager.class );
		for( Class<?> jvmClass : classes )
		{
			TypeAssessment assessment = assess( assessor, jvmClass );
			assert assessment instanceof ExtensibleAssessment;
		}
	}

	@Test public void famous_mutable_classes()
	{
		List<Class<?>> classes = List.of( ArrayList.class, HashMap.class, Date.class, HashSet.class, LinkedList.class, Properties.class, //
			Random.class, Matcher.class, StringBuilder.class, LinkedHashMap.class, LinkedHashSet.class, SimpleDateFormat.class, StringTokenizer.class, //
			ConcurrentHashMap.class, KeyStroke.class );
		for( Class<?> jvmClass : classes )
		{
			TypeAssessment assessment = assess( assessor, jvmClass );
			assert assessment instanceof MutableTypeAssessment;
		}
	}
}
