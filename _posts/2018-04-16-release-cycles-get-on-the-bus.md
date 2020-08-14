---
layout: post
title: "Release Cycles - get on the bus"
date: "2018-04-16"
---

I've spent a lot of time recently looking at how to manage multiple supported code streams. One key question we've been discussing is how frequently do you patch a product depending on where in its lifecycle it is? One of the positions was that this discussion has to happen on an ad-hoc basis so that a release is made when sufficient changes have occurred to constitute a patch or an urgent release is needed. While this makes sense for products approaching end of life when changes are few and necessitated by the requests of existing customers, however patching this way for products under active development or even maintenance leads to problems with resource scheduling and awareness.

The solution to this is the release bus. The bus has a fixed cadence, once release per year/half/quarter/month (based on your release tolerance) any fix that is ready to go when that bus is scheduled to released with that bus and anything else must wait for the next one. Buses are easy to schedule, (should) run on time and everyone knows when the bus is going. This sounds fantastic in theory but there will always be an expected change that just needs to be released now, so what gets there faster than the bus? The release taxi, you can go where you want, when you want but it costs more and if we're paying you will be asked why.

By splitting the patch releases into these two categories its easier to classify scheduled maintenance from one-off changes. If you start to see too many release taxis questions can be made raised as to why these changes are not making it onto the schedule.
