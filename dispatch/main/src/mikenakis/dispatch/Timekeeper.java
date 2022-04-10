package mikenakis.dispatch;

import java.time.Clock;
import java.time.Duration;

/**
 * Companion interface which provides means of creating timers.
 */
public interface Timekeeper
{
	Clock clock();
	Timer newTimer( Duration period, Timer.Tick timerTick );
}
