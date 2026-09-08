# Jekyll Poster

A native Android app for writing and publishing posts to this Jekyll blog
(`andymarch.github.io`) straight from your phone. It talks to GitHub's REST
API directly — there's no server component and no local git checkout on the
device.

## Features

- **GitHub auth** via a fine-grained personal access token, stored in
  `EncryptedSharedPreferences` (AES-256, backed by the Android Keystore).
- **Front matter generation** matching this repo's existing `_posts/*.md`
  conventions (`type`, `layout: post`, `title`, `date`, `tags`), plus the
  matching `_posts/YYYY-MM-DD-slug.md` filename.
- **Image optimisation and upload**: photos are EXIF-oriented, downscaled to a
  configurable max dimension, re-encoded as JPEG, and committed to
  `assets/img/` as soon as they're picked — independent of when you finish
  writing and hit Publish.
- **Drafts** persist locally (a JSON file, not a database — there's no need
  for one at this scale) so you can write on the go and publish later.
- **Recent posts**: browse and re-open existing posts from the repo for
  editing (posts using a front-matter shape this app doesn't recognise are
  left alone rather than risk corrupting them).
- Modern Android stack throughout: Kotlin, Jetpack Compose, Material 3
  (with dynamic color on Android 12+), Navigation-Compose, WorkManager,
  DataStore, and coroutines/Flow — no RxJava, no Hilt (the app is small
  enough that hand-rolled DI is less code than a framework would be).

## Project layout

```
android-app/
  core/    pure-Kotlin/JVM module: front matter + filename/slug logic, unit tested
  app/     the Android application (Compose UI, GitHub client, image pipeline, WorkManager)
```

`core` has no Android dependency, so its logic (front matter rendering, slug
generation, round-tripping an existing post's front matter) is tested with
plain JUnit and runs in any JVM — see `core/src/test`.

## Building

Open the `android-app/` folder in Android Studio (Iguana or newer) and let it
sync — the Gradle wrapper is checked in, so no local Gradle install is
required beyond what Android Studio bundles. `compileSdk`/`targetSdk` 35,
`minSdk` 26.

To run the `core` module's unit tests from the command line:

```
./gradlew :core:test
```

(Building the `:app` module requires the Android SDK components Android
Studio installs — command-line `./gradlew :app:assembleDebug` needs an
`ANDROID_HOME`/`local.properties` pointing at one.)

## First-time setup (in the app)

1. On GitHub, create a **fine-grained personal access token**
   (Settings → Developer settings → Fine-grained tokens) scoped to just this
   repository, with **Contents: Read and write** and nothing else.
2. Open the app, paste the token in, and enter the repository owner
   (`andymarch`) and name (`andymarch.github.io`).
3. The app verifies the token can push to that repo before saving anything,
   and picks up the repo's default branch automatically.

Repository owner/name/branch, an optional commit author name+email, and the
image max-dimension/JPEG-quality settings can all be changed later from the
Settings tab.

## How publishing works

- Tapping "Publish" hands the post off to a `WorkManager` job (`PublishWorker`)
  rather than doing the commit inline in the UI, so backgrounding the app
  mid-publish doesn't lose it. A notification reports success or failure.
- Adding a photo uploads it (via the Contents API) as soon as it's picked;
  the post body gets the `![](/assets/img/...)` markdown appended
  automatically once the upload finishes. Failed uploads show a retry button
  on the thumbnail.
- Editing and republishing an existing post reuses its file's current SHA
  (an update, not a duplicate). If you change the title or date of a
  previously-published post before republishing — which changes its
  filename — the app also deletes the old file so you don't end up with a
  stale duplicate.
