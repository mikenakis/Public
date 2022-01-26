package mikenakis.test.intertwine.comparisons.rig;

public interface FooInterface //NOTE: Intertwine requires that this be public!
{
	void voidMethod();

	Alpha getAlpha( int index );

	void setAlpha( int index, Alpha alpha );
}
