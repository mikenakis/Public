package mikenakis.publishing.test;

import mikenakis.kit.Kit;
import mikenakis.kit.coherence.Coherence;
import mikenakis.kit.coherence.ThreadLocalCoherence;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.Mortal;
import mikenakis.kit.ref.Ref;
import mikenakis.publishing.bespoke.Publisher;
import mikenakis.testkit.TestKit;
import mikenakis.tyraki.exceptions.DuplicateKeyException;
import org.junit.Ignore;
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

	@Ignore
	@Test public void subscribing_twice_fails()
	{
		Mortal.tryWith( Publisher.of( coherence, Procedure0.class ), publisher -> //
		{
			Ref<Integer> issueCount = Ref.of( 0 );
			Procedure0 subscriber = new Subscriber( issueCount );
			Mortal.tryWith( publisher.addSubscription( subscriber ), () -> //
				TestKit.expect( DuplicateKeyException.class, () -> //
					publisher.addSubscription( subscriber ) ) );
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
