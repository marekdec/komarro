**Current release: 1.0, available in [maven](MavenInstructions.md) central**

![http://komarro.googlecode.com/git/src/img/logo.png](http://komarro.googlecode.com/git/src/img/logo.png)
<br><sup>Previously known as <a href='http://code.google.com/p/mockarro/'>mockarro</a></sup>

<h1>Index</h1>
<ul><li><h3><a href='#News.md'>News</a></h3>
</li><li><h3><a href='http://komarro.googlecode.com/git/docs/apidocs/com/googlecode/komarro/Komarro.html'>Komarro API</a></h3>
</li><li><h3><a href='http://code.google.com/p/komarro/downloads/list'>Download</a> | <a href='MavenInstructions.md'>Instructions for Maven users</a></h3>
</li><li><h3><a href='#Introduction.md'>Introduction</a></h3>
</li><li><h3><a href='#Examples_of_usage_and_blog_posts.md'>Examples and blog posts</a></h3>
</li><li><h3><a href='#Motivation.md'>Motivation</a></h3>
</li><li><h3><a href='LogoDescription.md'>see info about the logo</a></h3></li></ul>

<hr />
<h1>News</h1>

After 8 months in limbo and 1 name change, Komarro got released!<br>
<br>
Release notes:<br>
<ol><li>The <i>given(type).isRequested</i> feature (with no support for generic collaborators guaranteed or collaborators that inherit from generic classes).<br>
</li><li>Unit under test initialization routine.<br>
</li><li>Mock collaborator generator and injection mechanism.</li></ol>


Maven users can get it from:<br>
<br>
<pre><code>&lt;dependencies&gt;<br>
  ...<br>
  &lt;dependency&gt;<br>
    &lt;groupId&gt;com.googlecode.komarro&lt;/groupId&gt;<br>
    &lt;artifactId&gt;komarro&lt;/artifactId&gt;<br>
    &lt;version&gt;1.0&lt;/version&gt;<br>
    &lt;scope&gt;test&lt;/scope&gt;<br>
  &lt;/dependency&gt;<br>
  ...<br>
&lt;/dependencies&gt;<br>
</code></pre>

The non-maven users can download Komarro from the <a href='http://code.google.com/p/komarro/downloads/list'>Downloads</a> page.<br>
<br>
<hr />
<h1>Introduction</h1>

Komarro is a mocking framework based on a great <a href='http://mockito.org'>Mockito</a> framework. It provides a way to specify the indirect inputs to a method under test in a way that the test stays agnostic of many details of the System Under Test collaborators.<br>
Komarro is intended to be used to aid the unit testing process in applications that make use of Dependency Injection. Thanks to Komarro's <i>given(type).isRequested</i> idiom, the test setup can be greatly simplified. Also, Komarro is supposed to decrease the level of test vulnerability to System Under Test refactoring.<br>
<br>
See the <a href='#Examples_of_usage_and_blog_posts.md'>Examples of usage</a> and the <a href='#Motivation.md'>motivation</a> section to get started.<br>
<br>
<hr />
<h1>Examples of usage and blog posts</h1>

<ul><li><a href='http://marekdec.wordpress.com/2012/01/30/mockarro-tdd-example/'>An example on how to use Komarro in TDD</a>
</li><li><a href='http://marekdec.wordpress.com/2012/02/13/mockarro-changes-name-to-komarro/'>An example on how to use Komarro and Mockito together</a>
</li><li><a href='http://blog.jayway.com/2012/02/13/komarro-a-new-interesting-mock-framework-for-java/'>Johan Haleby's blog post about Komarro</a>
</li><li><a href='https://github.com/marekdec/komarro-example-pizza-shop'>An example project that uses Komarro</a>
</li><li><a href='https://github.com/marekdec/planetary-system'>Another example project that uses Komarro</a></li></ul>


<hr />
<h1>Motivation</h1>

The intention of the Komarro initiative is to prove that unit testing can be performed without any knowledge of the collaborators implementation.<br>
<br>
It is based on the great Mockito library and it is intended to facilitate the way of creating java unit tests in systems that extensively use dependency injection.<br>
<br>
According to the Komarro way of doing things, a test of a method should not distinguish between method's formal parameters (direct input) and the input data provided by its collaborators (indirect input). Also, it should treat equally both the return parameters and the side effects that affect the collaborators.<br>
<br>
In its first attempt, the Komarro library concentrates on the input parameters.<br>
<br>
Let's consider a following method:<br>
<pre><code>   public int getNumberOfChildrenBySSN(String socialSecurityNumber) {<br>
      Person person = personDao.getPerson(socialSecurityNumber);<br>
      return person.getChildren().size();<br>
   }<br>
</code></pre>

Sticking to a pure Mockito approach, a basic unit test of such a method would look like:<br>
<pre><code>   @BeforeMethod<br>
   public void init() {<br>
      initMocks(this);<br>
<br>
      personService = new PersonService();<br>
      personService.personDao = personDao;<br>
   }<br>
<br>
   @Test<br>
   public void countChildrenOfZeus() {<br>
       //given<br>
       Person zeus = createPersonWithNumberOfChildrenEqualTo(71);<br>
       when(personDao.getPerson("123-45-6789")).thenReturn(zeus);<br>
<br>
       //when<br>
       int numOfChildren = personService.getNumberOfChildrenBySSN("123-45-6789");<br>
 <br>
       //then<br>
       assertThat(numOfChildren).isEqualTo(71);<br>
   }<br>
</code></pre>

What Komarro is trying to prove is that <code>personDao.getPerson("123-45-6789")</code> is completely irrelevant for the test and it should be removed.<br>
Over past few years the unit testing methodology advanced rapidly and it brought to the Java world some great inventions like !JMock, EasyMock and then Mockito.<br>
Notably, it was not really clear how to deal with the unit's collaborators until Mockito was released.<br>
Mockito was the one to bring a clear definition of what is to be verified and what is to be ignored when testing a method.<br>
It came with a clear message: <b>explicitly verify the important interactions and do not fear of the unexpected</b>.<br>
Arguably, one may think that verification is applied only to the methods that were intentionally specified using the Mockito <code>verify(mock).method()</code> syntax.<br>
This seem to save us from the EasyMock's merciless <i>expect, replay and verify</i> idiom.<br>
<br>
But does it really?<br>
What happens if the method under test changes to following:<br>
<pre><code>   public int getNumberOfChildrenBySSN(String socialSecurityNumber) {<br>
      Person person = socialSecurityRepository.getCitizen(socialSecurityNumber);<br>
      return person.getChildren().size();<br>
   }<br>
</code></pre>

Will the test still pass? And why wouldn't it, if it is only the type of interaction with collaborators that changed and no interaction is verified?<br>
Komarro forms a following hypothesis:<br>
<b>Mockito's <i>when</i> idiom is an implicit and unwanted verification of an interaction with a collaborator</b> (of course this also applies to other similar method stubbing techniques)<br>
<br>
And also, Komarro brings a solution to this problem.<br>
Consider following:<br>
<br>
<pre><code>   @BeforeMethod<br>
   public void init() {<br>
      personService = instanceForTesting(PersonService.class);<br>
   }<br>
<br>
   @Test<br>
   public void countChildrenOfZeus() {<br>
       //given<br>
       Person zeus = createPersonWithNumberOfChildrenEqualTo(71);<br>
       given(Person.class).isRequested().thenReturn(zeus);<br>
<br>
       //when<br>
       int numOfChildren = personService.getNumberOfChildrenBySSN("123-45-6789");<br>
 <br>
       //then<br>
       assertThat(numOfChildren).isEqualTo(71);<br>
   }<br>
</code></pre>

A test defined in such a way does not verify any interaction any longer. It is bounded only to its input parameters which are formed by its implicit input parameters (an empty set in this case) and the input parameters provided by the collaborators.<br>
<br>
Surely this is another way of trading test precision for its durability. But isn't it the direction we are all going?