---
layout: post
title: "Zune Software 4.2 and the case of disappearing functionality"
date: "2010-02-11"
---

Last week saw the push of the latest release of the Zune desktop software, normally a new release is the indicator of new functionality. Following the update there was a flurry of postings to the Zune community forums complaining about the mysterious disappearance of serveral parts of functionality, namely all of the social functionality and the recently introduced Smart DJ function. As these posts rolled a pattern emerged...they were all from international users.

Now Microsoft has prior for attempting to block international users from accessing the Zune social, which I have discussed at length here before so rather than drag that up again we'll focus on Smart DJ. Smart DJ was introduced with the Zune 4.0 software and greeted with applause, it provided the functionality to automatically build a playlist of similar songs based on a selected album, track or artist ala last.fm. This feature obviously adds value to the product, and competes well against iTunes Genius playlist, there seems to have been no reason to limit this to US users only.

Fortunately EwanD over on the Zune.net forums has posted a cunning work around which re-enables Smart DJ.
<!--more-->
"

- Close Zune software
- \- Run Regedit.exe
- \- Navigate to HKEY\_CURRENT\_USER \\ Software \\ Microsoft \\ Zune
- \- right-click and create a new Key under \\ Zune, called **FeaturesOverride**
- \- Open that key (folder, essentially) and right-click, create a new DWORD (32-bit) value called **QuickMixLocal**
- \- Open that key, set the value to "1"
- \- Start Zune software ... et voila!

"

Under that FeaturesOverride key it is possible to add a number of new values which re-enable all of the functionality which international users have been blocked from.

· SignInAvailable

· QuickPlay

· Marketplace

· Picks

· Videos

· Social

· MusicVideos

· Podcasts

· Channels

· Games

· SubscriptionFreeTracks

· FirstLaunchIntroVideo

· MBRRental

· MBRPurchase

· MBRPreview

Hopefully the sheer number of international users which have expressed their feelings over this direct attack on them will provoke Microsoft to take strides to make the Zune platform available outside of the United States.
