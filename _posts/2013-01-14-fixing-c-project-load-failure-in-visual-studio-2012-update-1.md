---
layout: post
title: "Fixing C++ project load failure in Visual Studio 2012 Update 1"
date: "2013-01-14"
---

Visual Studio 2012 marks a change in cycle for the development tools team as they are changing pace to incorporate regular updates to the product rather than the big bang of a new release every couple of years. Update 1 came out back in November (read more about that [here](http://www.microsoft.com/visualstudio/eng/visual-studio-update#story-update-1)) and I had been running it in the RC without issue. However with my fresh install of Express  I had forgotten this update, launching VS this morning to make a couple of changes I saw the update available reminder. Couple of quick clicks later the download is on its way. Code change done I fired off the update and all seems fine, progress bars, coffee, reboot. Reopen Visual Studio, open solution, none of the projects will load on hitting reload  for a single project and you are greeted with an alert with the text:

> The composition produced a single composition error. The root cause is provided below. Review the CompositionException.Errors property for more detailed information.
> 
> 1) No exports were found that match the constraint: ContractName    Microsoft.VisualStudio.Shell.Interop.IVsHierarchy RequiredTypeIdentity    Microsoft.VisualStudio.Shell.Interop.IVsHierarchy RequiredCreationPolicy    Shared
> 
> Resulting in: Cannot set import 'Microsoft.VisualStudio.Project.VS.Implementation.VSUnconfiguredProjectIntegrationService.IVsHierarchy (ContractName="Microsoft.VisualStudio.Shell.Interop.IVsHierarchy")' on part 'Microsoft.VisualStudio.Project.VS.Implementation.VSUnconfiguredProjectIntegrationService'.
> 
> Element: Microsoft.VisualStudio.Project.VS.Implementation.VSUnconfiguredProjectIntegrationService.IVsHierarchy (ContractName="Microsoft.VisualStudio.Shell.Interop.IVsHierarchy") --> Microsoft.VisualStudio.Project.VS.Implementation.VSUnconfiguredProjectIntegrationService
> 
> Resulting in: Cannot get export 'Microsoft.VisualStudio.Project.VS.Implementation.VSUnconfiguredProjectIntegrationService (ContractName="Microsoft.VisualStudio.Project.Designers.IVsUnconfiguredProjectIntegrationService")' from part 'Microsoft.VisualStudio.Project.VS.Implementation.VSUnconfiguredProjectIntegrationService'.
> 
> Element: Microsoft.VisualStudio.Project.VS.Implementation.VSUnconfiguredProjectIntegrationService (ContractName="Microsoft.VisualStudio.Project.Designers.IVsUnconfiguredProjectIntegrationService") --> CachedAssemblyCatalog
> 
> Resulting in: Cannot set import 'Microsoft.VisualStudio.Project.UnconfiguredProjectServices.vsUnconfiguredProjectIntegrationService (ContractName="Microsoft.VisualStudio.Project.Designers.IVsUnconfiguredProjectIntegrationService")' on part 'Microsoft.VisualStudio.Project.UnconfiguredProjectServices'.
> 
> Element: Microsoft.VisualStudio.Project.UnconfiguredProjectServices.vsUnconfiguredProjectIntegrationService (ContractName="Microsoft.VisualStudio.Project.Designers.IVsUnconfiguredProjectIntegrationService") --> Microsoft.VisualStudio.Project.UnconfiguredProjectServices
> 
> Resulting in: Cannot get export 'Microsoft.VisualStudio.Project.UnconfiguredProjectServices (ContractName="Microsoft.VisualStudio.Project.IUnconfiguredProjectServices")' from part 'Microsoft.VisualStudio.Project.UnconfiguredProjectServices'.
> 
> Element: Microsoft.VisualStudio.Project.UnconfiguredProjectServices (ContractName="Microsoft.VisualStudio.Project.IUnconfiguredProjectServices") --> CachedAssemblyCatalog

That's one very large dialog with just the less than helpful OK button. Visual Studio does occasionally glitch out but its been a while since I've seen one quite so spectacular and consistent.  A quick search for the issue above yields pages and pages of connect.microsoft.com reports of people facing the exact same issue, always with C++ projects always after update 1.

[Visual Studio 2012 update 1 ctp 3 install has destroyed ability to load or create any project](http://connect.microsoft.com/VisualStudio/feedback/details/766831/visual-studio-2012-update-1-ctp-3-install-has-destroyed-ability-to-load-or-create-any-project)

[Error on creating C class library in VS 2012 for desktop](http://connect.microsoft.com/VisualStudio/feedback/details/763473/error-on-creating-c-class-library-in-vs-2012-for-desktop)

[Error when creating C class library project](http://connect.microsoft.com/VisualStudio/feedback/details/763715/error-when-creating-c-class-library-project "Error when creating C class library project")

So I'm not alone that's always a good start, less good is that all are closed and none contain any answers. At this point I thought I'd check for more updates in Visual Studio, no joy automatic update checking is on but no update. Okay how about a repair? More progress bars, more coffee and another reboot later still the same error. Digging through the bug reports I found one report of user with a fix, wipe and reinstall OS.

After spending the best part of an hour searching, repairing and patching, Windows update kicked in on our schedule and prompted to check for yet more updates. Sitting right at the top of the list was [KB2781514](http://support.microsoft.com/kb/2781514). Now that looks a little more helpful than a disk format.

[![KB2781514](/assets/img/bugfix-300x128.png)](http://andymarch.co.uk/wp-content/uploads/2013/01/bugfix.png)

The additional information doesn't hint at a cause or even mention the issue shown in the short description above, instead it only states that it improves the stability of Visual Studio. With the patch applied normality is restored and projects once more load successfully.

It seems an unusual choice to offer the update pack of software via one mechanism but to offer patches by a different approach, especially one which may be tightly controlled within a corporate environment. It is a shame the sheer volume of reports on connect.microsoft.com prevent them from updating the report with the resolution when a cause has been found.
