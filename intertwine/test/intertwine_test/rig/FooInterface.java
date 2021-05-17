package intertwine_test.rig;

public interface FooInterface //NOTE: Intertwine requires that this be public!
{
	Alpha getAlpha( int index );

	void setAlpha( int index, Alpha alpha );
}
