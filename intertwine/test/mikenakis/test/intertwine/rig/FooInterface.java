package mikenakis.test.intertwine.rig;

public interface FooInterface //NOTE: Intertwine requires that this be public!
{
	void voidMethod();

	Alpha getAlpha( int index );

	void setAlpha( int index, Alpha alpha );
}
