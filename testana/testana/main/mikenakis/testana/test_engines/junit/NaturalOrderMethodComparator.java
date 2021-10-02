package mikenakis.testana.test_engines.junit;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class NaturalOrderMethodComparator implements Comparator<JunitTestMethod>
{
	/*private final Function<JunitTestMethod,Integer> methodIndexResolver;*/
	/*private final Map<JunitTestMethod,Integer> indexFromJavaMethodMap = new HashMap<>();*/

	NaturalOrderMethodComparator( /*Function<JunitTestMethod,Integer> methodIndexResolver*/ )
	{
		/*this.methodIndexResolver = methodIndexResolver;*/
	}

	@Override public int compare( JunitTestMethod javaMethod1, JunitTestMethod javaMethod2 )
	{
		int index1 = getMethodIndex( javaMethod1 );
		int index2 = getMethodIndex( javaMethod2 );
		return Integer.compare( index1, index2 );
	}

	private int getMethodIndex( JunitTestMethod javaMethod )
	{
		return javaMethod.methodIndex;
		/*return indexFromJavaMethodMap.computeIfAbsent( javaMethod, methodIndexResolver );*/
	}
}
