---
layout: post
title: "Firesheep, making Facebook hi-jacking even easier"
date: "2010-10-25"
---

There's been a lot of [press](http://www.guardian.co.uk/technology/blog/2010/oct/25/firefox-extension-firesheep-wi-fi) floating about today about a new Firefox extension called [Firesheep](http://codebutler.com/firesheep). This extension sniffs networks for HTTP cookies for a number of popular social network services and captures them ready for one click session highjacks. Thanks to the publicly available APIs for these networks Firesheep performs a lookup to see exactly whose's cookie you have allowing greater refinement of attack.

A lot of people have responded to this by advocating MiFis and VPN solutions to prevent ever using public WiFi in the first place. However this requires extra kit and therefore extra expense, you could recommend users only use the SSL encrypted pages but really how many people while remember to do this for ever site they visit?

In the mind of fighting fire with fire you can fight extensions with extensions, [KB SSL Enforcer](https://chrome.google.com/extensions/detail/flcpelgcagfhfoegekianiofphddckof) for Google Chrome provides a near zero effort solution to the problem. This extension will automatically redirect the user to a SSL encrypted copy of the page if one is available. All the security with much less hassle.
