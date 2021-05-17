package mikenakis.kit.events;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

abstract class DeadlockGuard
{
	static final DeadlockGuard instance = Kit.areAssertionsEnabled() ? new DevelopmentDeadlockGuard() : new ProductionDeadlockGuard();

	public abstract void guard( Thread targetThread, Procedure0 procedure );

	private static class ProductionDeadlockGuard extends DeadlockGuard
	{
		@Override public void guard( Thread targetThread, Procedure0 procedure )
		{
			procedure.invoke();
		}
	}

	private static class DevelopmentDeadlockGuard extends DeadlockGuard
	{
		private final ReentrantLock reentrantLock = new ReentrantLock();
		private final Map<Thread,Thread> entries = new LinkedHashMap<>();

		private DevelopmentDeadlockGuard()
		{
		}

		//TODO: move to Kit?
		private static void synchronize( ReentrantLock lock, Procedure0 procedure )
		{
			if( Kit.areAssertionsEnabled() )
			{
				lock.lock();
				procedure.invoke();
				lock.unlock();
			}
			else
			{
				lock.lock();
				try
				{
					procedure.invoke();
				}
				finally
				{
					lock.unlock();
				}
			}
		}

		@Override public void guard( Thread targetThread, Procedure0 procedure )
		{
			Thread callingThread = Thread.currentThread();
			synchronize( reentrantLock, () -> {
				if( makesCycle( callingThread, targetThread ) )
				{
					StackTraceElement[] targetThreadStackTrace = targetThread.getStackTrace();
					throw new DeadlockException( callingThread, targetThread, targetThreadStackTrace );
				}
				Kit.map.add( entries, callingThread, targetThread );
			} );
			procedure.invoke();
			synchronize( reentrantLock, () -> Kit.map.remove( entries, callingThread ) );
		}

		private boolean makesCycle( Thread callingThread, Thread targetThread )
		{
			for( ; targetThread != null; targetThread = Kit.map.tryGet( entries, targetThread ) )
				if( targetThread == callingThread )
					return true;
			return false;
		}
	}
}
