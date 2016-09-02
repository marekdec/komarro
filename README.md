Current release: 1.0, available in maven central

![logo](https://github.com/marekdec/komarro/blob/master/komarro_logo.png)

# News
Release notes:

1. The *given(type).isRequested* feature (with no support for generic collaborators guaranteed or collaborators that inherit from generic classes).
1. Unit under test initialization routine.
1. Mock collaborator generator and injection mechanism. 

Maven users can get it from:

```xml
<dependencies>
  ...
  <dependency>
    <groupId>com.googlecode.komarro</groupId>
    <artifactId>komarro</artifactId>
    <version>1.0</version>
    <scope>test</scope>
  </dependency>
  ...
</dependencies>
```

The non-maven users can download Komarro from the Downloads page.

# Introduction
Komarro is a mocking framework based on a great Mockito framework. It provides a way to specify the indirect inputs to a method under test in a way that the test stays agnostic of many details of the System Under Test collaborators. Komarro is intended to be used to aid the unit testing process in applications that make use of Dependency Injection. Thanks to Komarro's given(type).isRequested idiom, the test setup can be greatly simplified. Also, Komarro is supposed to decrease the level of test vulnerability to System Under Test refactoring.
See the Examples of usage and the motivation section to get started.


# Examples of usage and blog posts

* [An example on how to use Komarro in TDD](https://marekdec.wordpress.com/2012/01/30/mockarro-tdd-example/)
* [An example on how to use Komarro and Mockito together](https://marekdec.wordpress.com/2012/02/13/mockarro-changes-name-to-komarro/)
* [Johan Haleby's blog post about Komarro](https://blog.jayway.com/2012/02/13/komarro-a-new-interesting-mock-framework-for-java/)
* [An example project that uses Komarro](https://github.com/marekdec/komarro-example-pizza-shop)
* [Another example project that uses Komarro](https://github.com/marekdec/planetary-system)

# Motivation
The intention of the Komarro initiative is to prove that unit testing can be performed without any knowledge of the collaborators implementation.
It is based on the great Mockito library and it is intended to facilitate the way of creating java unit tests in systems that extensively use dependency injection.
According to the Komarro way of doing things, a test of a method should not distinguish between method's formal parameters (direct input) and the input data provided by its collaborators (indirect input). Also, it should treat equally both the return parameters and the side effects that affect the collaborators.
In its first attempt, the Komarro library concentrates on the input parameters.
Let's consider a following method:

```java
   public int getNumberOfChildrenBySSN(String socialSecurityNumber) {
      Person person = personDao.getPerson(socialSecurityNumber);
      return person.getChildren().size();
   }
```

Sticking to a pure Mockito approach, a basic unit test of such a method would look like:


```java
   @BeforeMethod
   public void init() {
      initMocks(this);

      personService = new PersonService();
      personService.personDao = personDao;
   }

   @Test
   public void countChildrenOfZeus() {
       //given
       Person zeus = createPersonWithNumberOfChildrenEqualTo(71);
       when(personDao.getPerson("123-45-6789")).thenReturn(zeus);

       //when
       int numOfChildren = personService.getNumberOfChildrenBySSN("123-45-6789");
 
       //then
       assertThat(numOfChildren).isEqualTo(71);
   }
```

What Komarro is trying to prove is that personDao.getPerson("123-45-6789") is completely irrelevant for the test and it should be removed. Over past few years the unit testing methodology advanced rapidly and it brought to the Java world some great inventions like !JMock, EasyMock and then Mockito. Notably, it was not really clear how to deal with the unit's collaborators until Mockito was released. Mockito was the one to bring a clear definition of what is to be verified and what is to be ignored when testing a method. It came with a clear message: explicitly verify the important interactions and do not fear of the unexpected. Arguably, one may think that verification is applied only to the methods that were intentionally specified using the Mockito verify(mock).method() syntax. This seem to save us from the EasyMock's merciless expect, replay and verify idiom.
But does it really? What happens if the method under test changes to following:

```java
   public int getNumberOfChildrenBySSN(String socialSecurityNumber) {
      Person person = socialSecurityRepository.getCitizen(socialSecurityNumber);
      return person.getChildren().size();
   }
```

Will the test still pass? And why wouldn't it, if it is only the type of interaction with collaborators that changed and no interaction is verified? Komarro forms a following hypothesis: Mockito's when idiom is an implicit and unwanted verification of an interaction with a collaborator (of course this also applies to other similar method stubbing techniques)
And also, Komarro brings a solution to this problem. Consider following:


```java
   @BeforeMethod
   public void init() {
      personService = instanceForTesting(PersonService.class);
   }

   @Test
   public void countChildrenOfZeus() {
       //given
       Person zeus = createPersonWithNumberOfChildrenEqualTo(71);
       given(Person.class).isRequested().thenReturn(zeus);

       //when
       int numOfChildren = personService.getNumberOfChildrenBySSN("123-45-6789");
 
       //then
       assertThat(numOfChildren).isEqualTo(71);
   }
```

A test defined in such a way does not verify any interaction any longer. It is bounded only to its input parameters which are formed by its implicit input parameters (an empty set in this case) and the input parameters provided by the collaborators.
Surely this is another way of trading test precision for its durability. 
