# Changelog

This file starts tracking changes from this point forward; prior releases (1.0 through 1.8.0) are only recorded as git tags.

## Unreleased

- Fixed a Kotlin stdlib version conflict across the `lib` and `app` modules that could crash the compiler.
- Removed unused `androidx.legacy:legacy-support-v4` and `androidx.multidex:multidex` dependencies.
- Removed dead SDK-version checks now that `minSdkVersion` is 24.
- Fixed `setSelectedItem` swallowing the real cause of failures.
- Removed a half-finished, unused `needsRecalculation` optimization flag.
- Fixed a broken `OnItemSelectedListener` sample in the README that didn't compile.
- Documented `itemSpacing`, `iconBackgroundColor`, `iconBackgroundPadding`, and `iconMargin` attributes.
- Added CI (GitHub Actions) running a build and lint check on every push and pull request.
- Removed unmodified boilerplate test stubs.
- Removed committed Kotlin-compiler crash logs from the repository.
