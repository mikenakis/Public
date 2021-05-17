# mikenakis-kit-test

#### Tests for `mikenakis-kit`

The tests for `mikenakis-kit` are in a separate maven project, and in a separate git repository, because:

- The tests of `mikenakis-kit` depend on `mikenakis-benchmark`.
- `mikenakis-benchmark` depends on `mikenakis-kit`.

So, if `mikenakis-kit` was to contain its own tests, then there would be a circular dependency.

## Discussion

When it comes to the general question of whether production code and tests should be kept together in the same maven project and git repository or in separate maven projects and
git repositories, my observations are as follows:

- The choice followed by the entire Java world, with no known exceptions, is to always keep them together.
- I have never actually come across a situation where keeping them together is necessary.
- I have never even come across a situation where keeping them together offers any convenience worth mentioning.
- I have several times come across the situation where they have to be kept separate in order to avoid a circular dependency.
- In the interest of keeping things simple, it is best to have only one way of accomplishing any given thing.
- If there are two ways of accomplishing a given thing, and one of them is never necessary, while the other is sometimes necessary, then the second way should be chosen as the only
  way of accomplishing that thing.

From the above I suspect that build systems (such as maven) that offer the ability to keep both production code and test code in the same project essentially achieve nothing but
the following:

- They complicate their inner workings with an unnecessary feature.
- To support this feature, their interface is more complex than it needs to be.
- This added complexity must be dealt with by us programmers, on a daily basis.
- So they are unnecessarily complicating our life.

(And as usual, what I am essentially suspecting here is that the entire Java world is doing it wrong.)
