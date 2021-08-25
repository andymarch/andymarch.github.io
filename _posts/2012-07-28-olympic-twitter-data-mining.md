---
layout: post
title: "Olympic Twitter data mining"
date: "2012-07-28"
---

The London 2012 Olympics kicked off in style last night with a fantastic opening ceremony taking in the history of Britain, music and technology as well as the parade of the teams. This Olympics will be unprecedented in the amount of access the public gets to the athletes, Olympians have been sharing [their training](http://twitter.com/MarkCavendish/status/227778640748892160/photo/1), [their travel](http://twitter.com/NickSymmonds/status/228554045965623296/photo/1) and will no doubt share their success and failure at events as well. But social media is allowing the public to be more involved than ever in spectacle events such as the Olympics, Twitter faced a heavy load last night as people tweeted their way through the opening ceremony giving an amazing back channel to the event.

But what were people talking about for the four hours? [According to Twitter themselves](http://blog.twitter.com/2012/07/onlyontwitter-through-eyes-of-olympians.html) the most talked about moment was Rowan Atkinson during Chariots of Fire. During the London riots last year I was playing with the Twitter API to see if it would be possible to extract key events from the noise of the crowd. Last night I was prompted to turn this on just to see what went by, now the algorithm it currently uses to determine key events is more than a bit flaky and requires training to an event. However what it does do very well is capture all of the messages it processes so they can be played back to train the algorithm. Last night I pulled down around 18,000 public messages from Twitter between 19:54 and 23:56 (I lost the last batch unfortunately as my machine crashed).There is bound to be a lot more traffic out there as with an API key you don't get access to the raw twitter fire hose of tweets just whatever you can search for within your rate limit. However this gives us a good sample size to experiment with. So this morning I wrote a quick and dirty little util to run regular expression parses over these messages to find the talking points based on the key events of the night, here's what I found:
<!--more-->
**After removing duplicates:** 18670 unique messages.

**TeamGB:** 162 messages (1.11%)

**The torch & cauldron:** 169 messages (1.16%)

**Steve Redgrave:** 34 messages (0.23%)

**Paul McCartney:** 177 messages (1.21%)

**Bradley Wiggins:** 25 messages (0.17%)

**The Queen:** 972 messages (6.66%)

**James Bond:** 401 messages (2.74 messages)

**#criticalmass (protest group):** 720 messages (4.93%)

**Mr Bean:** 318 messages (2.18%)

**Lord of the rings:** 131 messages (0.90%)

**Unmatched by any of the above:** 11870 messages

You can see by that last number there a massive amount of data still in there to be categorized. At the moment my solution for identifying events is by  manually training a Bayesian filter (or as in this case picking key events from the running order), there's a wealth of data mining tools available on the net and a range of more scalable methods to process this information. The problem is the speed at which this information needs to be processed, on Thursday night I attended a talk by [Ed Parsons](http://www.edparsons.com/) from Google who said big data isn't a problem when it's static but what we have now is a fire hose of information rushing past us. Twitter is a perfect medium for expressing what is happening right now, that is also the issue with trying to mine that data for geospatial display, it is only relevant for a short period of time.

I've published all the code to extract the matches onto [GitHub](https://github.com/andymarch/HoardProcessor) along with the message set captured.
