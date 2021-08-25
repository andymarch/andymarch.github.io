---
layout: post
title: "Moving Git"
date: "2014-08-02"
---

I've been spending some time over the last month or so migrating our git server at work. Our old environment was starting to show the signs of age and just was not suited to having multiple developers checking in on several large projects at a time meaning we frequently ran into file permissions problems needing one of the team to ssh to the server a manually chmod the directory.

There are a lot of alternatives out there for git hosting including [GitLab](https://about.gitlab.com/), [Bonobo](http://bonobogitserver.com/), [Stash](https://www.atlassian.com/software/stash) and the 800 pound guerilla of [GitHub enterprise](https://enterprise.github.com/). In the end Stash from Atlassian won out, on premise hosting, great Jira integration and finally low license cost for small teams (which is in fact a donation to [Room to Read](http://www.roomtoread.org/)).

Now we have a shiny new server all set up and running how best to migrate all our existing repositories over without losing history, tags, branches or living in a half and half state while we do so? One of the nicest things about distributed version control systems like git is that this process is much simplier than with large centralised version control systems of old, below are a couple of scripts for making this easier still.
<!--more-->
# **Moving to a New Remote**

The sequence below will check out everything from your existing repository and check it into your new server of choice.

Checkout your existing repository:

> git clone <your old repo>

Lets get all the branches (I can't remember where I acquired this branch script from but whoever devised it I owe you a drink):

> #!/bin/bash
> 
> for branch in \`git branch -a | grep remotes | grep -v HEAD | grep -v master \`; do
> 
> git branch --track ${branch#remotes/origin/} $branch
> 
> done

Remove existing remote origin so we have a fall back if all goes wrong:

> git remote rm origin

Create your new remote repository on the server, then its time point your local copy to the remote:

> git remote set-url origin <new repository>

Push that data to your remote:

> git push origin --all

 

# Splitting a Repository

Now we've moved our repositories around we decided it was time for a bit of house keeping. For a reason no-one could remember we had a repository with two codebases in the root. While these two projects are related this decision has caused us repeated problems requiring dubious workarounds in our build scripts to handle this double root. We needed a way to split this directory into two repositories but again without losing and of the history, tags or branches.

Checkout the existing repository:

> git clone <your old repo>

Lets get all the branches by running the script again:

> #!/bin/bash
> 
> for branch in \`git branch -a | grep remotes | grep -v HEAD | grep -v master \`; do
> 
> git branch --track ${branch#remotes/origin/} $branch
> 
> done

Remove existing remote origin so we have a fall back if all goes wrong:

> git remote rm origin

Now we want to split one directory out and make it the root of our new repository:

> git filter-branch --subdirectory-filter <directory> --tag-name-filter cat -- --all

Create a new remote repository for us to push into and add it to our local repository:

> git remote add origin <new remote repository>

Finally push the repository up to the new origin:

> git push origin --all

 

I hope these are useful to someone, I might get round to building these up into complete scripts at some point.
