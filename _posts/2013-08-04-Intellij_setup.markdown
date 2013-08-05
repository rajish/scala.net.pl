---
layout: post
title:  "Intellij Setup"
date:   2013-08-04 09:55:11
categories: intellij setup
---

***Prerequisites***

It is required to download <em>Play 2.x Framework</em> and setup system Path.

Also make sure that you have installed <em>Play 2.0 Support</em> plugin in Intellij 

***Basic configuration***

Open <em>scala.net.pl</em> project's workspace in terminal window and launch <em>Play console</em> with command
{% highlight scala %}
play
{% endhighlight %}
Next compile project to make sure target directory is generated, it is important for next steps
{% highlight scala %}
compile
{% endhighlight %}

Finally generate Intellij's configuration files 
{% highlight scala %}
gen-idea sbt-classifiers
{% endhighlight %}
It will download necessary files, do now worry about warnings about missing source or/and javadoc jars.

Run Intellij and open project (do not import it). Despite running <em>Play</em> command project's configuration requires additional adjustments.

Open <em>Project Settings</em> window, locate and remove <em>SBT: scala.2.9.2</em> from <em>Libraries</em> (should be somewhere at the end of libraries list).
Now select (if it is missing) scala compiler library. In the same window select <em>Facetes</em> than <em>Scala(scala\_net\_pl</em>. From drop down list choose <em>
SBT:scala:2.10.0</em>

We will finish configuration by adding target's folders with generated classes and sources to classpath.  
From <em>Project settings</em> select <em>scala\_net\_pl</em> Module. In the <em>Sources</em> tab mark following folder as <em>Sources</em>

{% highlight scala %}
target/scala-2.10/classes_managed
target/scala-2.10/src_managed
{% endhighlight %}

Run test ApplicationSpec.scala, output should be green