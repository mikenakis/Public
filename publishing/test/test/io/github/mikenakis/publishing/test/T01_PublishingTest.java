package io.github.mikenakis.publishing.test;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.implementation.ThreadLocalCoherence;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.functional.Procedure0;
import io.github.mikenakis.kit.ref.Ref;
import io.github.mikenakis.lifetime.Mortal;
import io.github.mikenakis.publishing.bespoke.Publisher;
import org.junit.Test;

/**
 * {@link Publisher} test.
 *
 * @author michael.gr
 */
public class T01_PublishingTest
{
	private final Coherence coherence = ThreadLocalCoherence.instance();

	public T01_PublishingTest()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private static class Subscriber implements Procedure0
	{
		private final Ref<Integer> issueCountRef;

		Subscriber( Ref<Integer> issueCountRef )
		{
			this.issueCountRef = issueCountRef;
		}

		@Override public void invoke()
		{
			issueCountRef.value++;
		}
	}

	@Test public void subscriber_receives_no_issues_published_while_not_subscribed()
	{
		Mortal.tryWith( Publisher.of( coherence, Procedure0.class ), publisher -> //
		{
			publisher.allSubscribers().invoke();
			Ref<Integer> issueCount = Ref.of( 0 );
			Procedure0 subscriber = new Subscriber( issueCount );
			Mortal.tryWith( publisher.addSubscription( subscriber ), subscription -> { } );
			publisher.allSubscribers().invoke();
			assert issueCount.value == 0;
		} );
	}

	@Test public void subscriber_receives_one_issue_for_one_publication()
	{
		Mortal.tryWith( Publisher.of( coherence, Procedure0.class ), publisher -> //
		{
			Ref<Integer> issueCount = Ref.of( 0 );
			Procedure0 subscriber = new Subscriber( issueCount );
			Mortal.tryWith( publisher.addSubscription( subscriber ), () -> //
				publisher.allSubscribers().invoke() );
			assert issueCount.value == 1;
		} );
	}

	@Test public void subscriber_receives_two_issues_for_two_publications()
	{
		Mortal.tryWith( Publisher.of( coherence, Procedure0.class ), publisher -> //
		{
			Ref<Integer> issueCount = Ref.of( 0 );
			Procedure0 subscriber = new Subscriber( issueCount );
			Mortal.tryWith( publisher.addSubscription( subscriber ), () -> //
			{
				publisher.allSubscribers().invoke(); //1
				publisher.allSubscribers().invoke(); //2
			} );
			assert issueCount.value == 2;
		} );
	}

	@Test public void subscribing_twice_causes_two_invocations_per_publication()
	{
		Mortal.tryWith( Publisher.of( coherence, Procedure0.class ), publisher -> //
		{
			Ref<Integer> issueCount = Ref.of( 0 );
			Procedure0 subscriber = new Subscriber( issueCount );
			Mortal.tryWith( publisher.addSubscription( subscriber ), () -> //
				Mortal.tryWith( publisher.addSubscription( subscriber ), () -> //
					publisher.allSubscribers().invoke() ) );
			assert issueCount.value == 2;
		} );
	}

	@Test public void subscriber_survives_garbage_collection()
	{
		Mortal.tryWith( Publisher.of( coherence, Procedure0.class ), publisher -> //
		{
			Ref<Integer> issueCount = Ref.of( 0 );
			Procedure0 subscriber = new Subscriber( issueCount );
			Mortal.tryWith( publisher.addSubscription( subscriber ), () -> //
			{
				publisher.allSubscribers().invoke(); //1
				Kit.runGarbageCollection();
				publisher.allSubscribers().invoke(); //2
				assert issueCount.value == 2;
			} );
		} );
	}
}
