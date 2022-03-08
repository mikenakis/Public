package mikenakis.dispatch;

import mikenakis.kit.lifetime.Closeable;

import java.time.Duration;

public interface Timer extends Closeable
{
	Duration period();

	interface Tick
	{
		void tick( Duration deltaTime );
	}
}
