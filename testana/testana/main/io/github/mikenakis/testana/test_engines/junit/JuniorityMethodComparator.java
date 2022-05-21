package io.github.mikenakis.testana.test_engines.junit;

import java.util.Comparator;

class JuniorityMethodComparator implements Comparator<JunitTestMethod>
{
	JuniorityMethodComparator()
	{
	}

	@Override public int compare( JunitTestMethod method1, JunitTestMethod method2 )
	{
		return Integer.compare( method1.derivationDepth, method2.derivationDepth );
	}
}
