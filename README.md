 ReactiveFlix is an application written using the Java Spring 5 framework. It demonstrates the use of reactive streams. Demostrasting the two kinds of publishers: Flux and Mono.
The are added dependencies from project reactor( https://projectreactor.io ); which is a reactive streams library; which closely resembles RxJava 2. Where a flux is equivalent of Observable in RxJava 2 and Mono is equivalent of Single or MayBe.

Mono: returns 0 or 1 items.

Flux: returns a stream of items.

Example: Mono <Movie> byId() returns only 1 movie and 
Flux <Movie> all() return many movies.

Reactive stream solve back presure problem. 

This application runs on top of a reactive server: netty(https://netty.io), you can optionaly use Undertow: http://undertow.io




 
