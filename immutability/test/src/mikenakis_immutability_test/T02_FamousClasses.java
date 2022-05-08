package mikenakis_immutability_test;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.type.TypeImmutabilityAssessor;
import mikenakis.immutability.type.assessments.ImmutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.MutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.TypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsExtensibleProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsCompositeProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.MultiReasonProvisoryTypeImmutabilityAssessment;
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

	private static TypeImmutabilityAssessment assess( TypeImmutabilityAssessor assessor, Class<?> type )
	{
		TypeImmutabilityAssessment assessment = assessor.assess( type );
		System.out.println( "assessment for type " + TestStringizer.instance.stringizeClassName( type ) + ":" );
		MyKit.<Assessment>tree( assessment, a -> a.children(), a -> a.toString(), s -> System.out.println( "    " + s ) );
		return assessment;
	}

	private final TypeImmutabilityAssessor assessor = TypeImmutabilityAssessor.create( TestStringizer.instance );

	@Test public void famous_immutable_classes_are_immutable()
	{
		List<Class<?>> classes = List.of( Instant.class, Duration.class, UUID.class, LocalDate.class, LocalDateTime.class, LocalTime.class, //
			MonthDay.class, OffsetDateTime.class, OffsetTime.class, Period.class, Year.class, YearMonth.class, ZoneOffset.class );
		for( Class<?> jvmClass : classes )
		{
			TypeImmutabilityAssessment assessment = assess( assessor, jvmClass );
			assert assessment instanceof ImmutableTypeImmutabilityAssessment;
		}
	}

	@Test public void famous_multi_reason_provisory_classes_are_multi_reason_provisory()
	{
		List<Class<?>> classes = List.of( ZonedDateTime.class );
		for( Class<?> jvmClass : classes )
		{
			TypeImmutabilityAssessment assessment = assess( assessor, jvmClass );
			assert assessment instanceof MultiReasonProvisoryTypeImmutabilityAssessment;
		}
	}

	@Test public void famous_composite_provisory_classes_are_composite_provisory()
	{
		List<Class<?>> classes = List.of( Optional.class );
		for( Class<?> jvmClass : classes )
		{
			TypeImmutabilityAssessment assessment = assess( assessor, jvmClass );
			assert assessment instanceof IsCompositeProvisoryTypeImmutabilityAssessment;
		}
	}

	@Test public void famous_extensible_provisory_classes_are_extensible_provisory()
	{
		List<Class<?>> classes = List.of( Collections.class, Clock.class, ZoneId.class, MouseInfo.class, DriverManager.class );
		for( Class<?> jvmClass : classes )
		{
			TypeImmutabilityAssessment assessment = assess( assessor, jvmClass );
			assert assessment instanceof IsExtensibleProvisoryTypeImmutabilityAssessment;
		}
	}

	@Test public void famous_mutable_classes_are_mutable()
	{
		List<Class<?>> classes = List.of( ArrayList.class, HashMap.class, Date.class, HashSet.class, LinkedList.class, Properties.class, //
			Random.class, Matcher.class, StringBuilder.class, LinkedHashMap.class, LinkedHashSet.class, SimpleDateFormat.class, StringTokenizer.class, //
			ConcurrentHashMap.class, KeyStroke.class );
		for( Class<?> jvmClass : classes )
		{
			TypeImmutabilityAssessment assessment = assess( assessor, jvmClass );
			assert assessment instanceof MutableTypeImmutabilityAssessment;
		}
	}
}
