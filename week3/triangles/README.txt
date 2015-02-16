——Testing——

TriangleShape.java, Triangle.java, SafeGenerics.java all have unit testing, which can be ran by executing each class in isolation with parameter uTest (for unit test).  All of these tests can be ran together in a component test by executing Triangle with parameter cTest (Component Test).

I have designed an automated testing tool to test my programmes.  I aim to continue refining this tool with each assignment that we get.  Please note that I have adapted code from the lecture slides for the comparison of items passed to this tool.

the SafeGenerics class handles thrown exception in my program alongside testing.  If the program is not in test mode, a thrown exception will be caught and the program will end.  If the program IS in testing mode, exception will be caught and a false value will be returned - this allows testing of functions with invalid data and avoids the program simply exiting.

——Design——

I have created a multi-class program, aiming to make this assignment as re-usable and extendable as possible.  I Have created a custom exception and several enumerated types.