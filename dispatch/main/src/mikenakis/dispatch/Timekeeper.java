package mikenakis.dispatch;

import java.time.Clock;
import java.time.Duration;

public interface Timekeeper
{
	Clock clock();
	Timer newTimer( Duration period, Timer.Tick timerTick );
}
