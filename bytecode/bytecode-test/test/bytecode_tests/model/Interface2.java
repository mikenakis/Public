package bytecode_tests.model;

/**
 * test interface.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface Interface2 extends Interface1
{
	Interface1 getInterface1();

	long getLong( Class3ImplementingInterface class3ImplementingInterface );

	void setDouble( double d );
}
