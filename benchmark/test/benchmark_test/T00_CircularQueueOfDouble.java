package benchmark_test;

import mikenakis.benchmark.util.CircularQueueOfDouble;
import mikenakis.benchmark.util.IntRef;
import mikenakis.debug.Debug;
import mikenakis.kit.Kit;
import mikenakis.testkit.TestKit;
import org.junit.Test;

import java.util.Arrays;

public class T00_CircularQueueOfDouble
{
	public T00_CircularQueueOfDouble()
	{
		if( !Debug.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test public void T0_Capacity_0_Fails()
	{
		TestKit.expect( AssertionError.class, () -> new CircularQueueOfDouble( 0 ) );
	}

	@Test public void T1_Capacity_1_Works()
	{
		CircularQueueOfDouble queue = new CircularQueueOfDouble( 1 );
		assert queue.capacity() == 1;
		assertEquals( queue, new double[] {} );
		assertEnqueueWorks( queue, 1.0, new double[] { 1.0 } );
		assertEnqueueFails( queue );
		assertDequeueWorks( queue, 1.0, new double[] {} );
		assertDequeueFails( queue );
		assertEnqueueWorks( queue, 2.0, new double[] { 2.0 } );
		assertEnqueueFails( queue );
		assertDequeueWorks( queue, 2.0, new double[] {} );
		assertDequeueFails( queue );
	}

	@Test public void T2_Capacity_2_Works()
	{
		CircularQueueOfDouble queue = new CircularQueueOfDouble( 2 );
		assert queue.capacity() == 2;
		assertEquals( queue, new double[] {} );
		assertEnqueueWorks( queue, 1.0, new double[] { 1.0 } );
		assertEnqueueWorks( queue, 2.0, new double[] { 1.0, 2.0 } );
		assertEnqueueFails( queue );
		assertDequeueWorks( queue, 1.0, new double[] { 2.0 } );
		assertDequeueWorks( queue, 2.0, new double[] {} );
		assertDequeueFails( queue );
		assertEnqueueWorks( queue, 3.0, new double[] { 3.0 } );
		assertDequeueWorks( queue, 3.0, new double[] {} );
		assertDequeueFails( queue );
		assertEnqueueWorks( queue, 4.0, new double[] { 4.0 } );
		assertEnqueueWorks( queue, 5.0, new double[] { 4.0, 5.0 } );
		assertEnqueueFails( queue );
		assertDequeueWorks( queue, 4.0, new double[] { 5.0 } );
		assertEnqueueWorks( queue, 6.0, new double[] { 5.0, 6.0 } );
		assertEnqueueFails( queue );
		assertDequeueWorks( queue, 5.0, new double[] { 6.0 } );
		assertDequeueWorks( queue, 6.0, new double[] {} );
		assertDequeueFails( queue );
	}

	private static void assertEnqueueWorks( CircularQueueOfDouble queue, double value, double[] expectedElements )
	{
		queue.enqueue( value );
		assertEquals( queue, expectedElements );
	}

	private static void assertDequeueWorks( CircularQueueOfDouble queue, double value, double[] expectedElements )
	{
		double actualValue = queue.dequeue();
		assert Kit.math.eq( actualValue, value, 1e-9 );
		assertEquals( queue, expectedElements );
	}

	private static void assertEquals( CircularQueueOfDouble queue, double[] expectedElements )
	{
		assert queue.size() == expectedElements.length;
		double[] existingElements = queue.toArray();
		assert Arrays.equals( existingElements, expectedElements );
		IntRef indexRef = new IntRef( 0 );
		queue.foreach( value -> //
		{
			assert Kit.math.eq( value, existingElements[indexRef.value++], 1e-9 );
		} );
	}

	private static void assertEnqueueFails( CircularQueueOfDouble queue )
	{
		double[] elements = queue.toArray();
		TestKit.expect( AssertionError.class, () -> queue.enqueue( 99.0 ) );
		assertEquals( queue, elements );
	}

	private static void assertDequeueFails( CircularQueueOfDouble queue )
	{
		double[] elements = queue.toArray();
		TestKit.expect( AssertionError.class, () -> queue.dequeue() );
		assertEquals( queue, elements );
	}
}
