---
layout: post
title: "PBTrainer on GitHub"
date: "2012-08-08"
---

I was playing with Window Phone 7 development for a proof of concept today at work which got me thinking about the PBTrainer project I wrote last year. Having a quick look on [create.msdn.com](http://create.msdn.com) told me that it is nearing the 600 download mark, not bad for an abandoned application. However there were lots of things I wasn't happy with in v1.1; no infinite repetition sessions, poor about/versioning info and pretty ropey tombstone handling.

So having got a bit of a buzz today writing a quick app I dredged out the codebase and had a look. Ah issue number one it does not compile, looks like I was halfway through the v1.2 changes and got interrupted. Okay no issue I know what the changes I wanted to make were and making a few bug fixes would be an ideal way to get up to speed again. So lets just roll back to the v1.1 code base and start making changes. Issue number two I broke the cardinal rule, no source control. It'd only a little side project, only I will ever use it, I'll probably rewrite most of this, all of these are fine reasons we tell ourselves not to bother with source control. Judging by the code comments and TODOs its living somewhere between v1.1 and v1.2 (probably where life intervened and cut the project short) so I'll have to pick through the code and hope that I wrote good comments (and didn't use the same excuse not to do those). So the first stage of the revival is complete I have corrected my mistake and committed the project to an [public GitHub repository](https://github.com/andymarch/PBTrainer). Hopefully over the coming weeks I'll be working my way towards v1.2 of PBTrainer, meanwhile I have learnt my lesson: whatever you're writing source control first.
