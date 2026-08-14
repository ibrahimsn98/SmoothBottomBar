# Changelog

This file starts tracking changes from this point forward; prior releases (1.0 through 1.8.0) are only recorded as git tags.

## Unreleased

### Breaking

- **Renamed packages**: the View library's public API moved from `me.ibrahimsn.lib` to `me.ibrahimsn.smoothbottombar`; the demo app moved to `me.ibrahimsn.app`. Update imports and any XML-referenced fully-qualified class names (e.g. `<me.ibrahimsn.smoothbottombar.SmoothBottomBar>`).
- **Raised `minSdk` from 24 to 32** (`targetSdk`/`compileSdk` are now 37).

### Added

- New **`:compose` module**: a fully Jetpack Compose-native `SmoothBottomBar` composable alongside the existing View-based one, with matching visuals and behavior (animated selection, badges, full RTL support). Controlled component (`selectedIndex` owned by the caller). Not yet published to JitPack - include it as a Gradle module from source for now. See the README for setup, a usage sample, and the full parameter reference.
- New `badgeRadius` XML attribute, making the badge dot's size configurable (previously a hardcoded, non-dp-scaled value).
- Badge state is now announced to TalkBack/accessibility services, and refreshes immediately on a menu change instead of only on the next tap.
- A GitHub Actions workflow that runs an on-device Maestro end-to-end test suite (tab switching, badge behavior, cross-navigation, both demo flavors) on every push/PR, with results uploaded to Qualflare for failure analysis.
- Documented the new Compose module in the README, and fixed several stale README claims: the API-level badge, a JitPack install snippet pinned to a version that predates the package rename, and the previously-undocumented `badgeRadius` attribute.

### Fixed

- Fixed a crash (`IndexOutOfBoundsException`) when the bar's menu is set programmatically after construction (e.g. `itemMenuRes = ...` inside `onCreate()`) rather than only via the XML `app:menu` attribute - the accessibility helper held a stale, often-empty snapshot of the item list instead of reading it live. (#102)
- Fixed `android:elevation` having no visible effect - the bar paints its own background entirely by hand in `onDraw` and never set a real `background` Drawable, so the default shadow outline had nothing to derive a shape from. (#109)
- Fixed a crash when `itemMenuRes` was left unset (the `menu` property was `lateinit` and never initialized).
- Fixed `IndexOutOfBoundsException` crashes in `onSizeChanged`/`onDraw`/`applyItemActiveIndex` from unchecked `items[itemActiveIndex]` access on an empty item list.
- Fixed the tab-switch animation being able to race itself on rapid re-tapping - the underlying `ValueAnimator` wasn't tracked/cancelled between taps, or on `onDetachedFromWindow`.
- Fixed the active tab's indicator being sized to all leftover bar width instead of its own content's width, and centered on the raw item rect instead of the icon+label's true visual center (they aren't symmetric around it).
- Fixed the outgoing tab's icon visibly jumping mid-animation instead of animating smoothly with the rest of the transition.
- Fixed RTL layouts: badges weren't drawn at all in the RTL `onDraw` branch (LTR-only), and the accessibility helper's hit-testing/bounds used a naive uniform grid that ignored side margins, the active item's non-uniform width, and RTL mirroring entirely.
- Fixed a leak in `NavigationComponentHelper`: calling `setupWithNavController()` more than once on the same bar accumulated `OnDestinationChangedListener`s on the `NavController` instead of replacing the previous one.
- Fixed `XmlResourceParser` never being closed in `BottomBarParser`, and a missing menu-item title silently rendering as the literal string `"null"` instead of failing loudly.
- Fixed `paintBackground` being seeded with the wrong color constant.
- Fixed a Kotlin stdlib version conflict across the `lib` and `app` modules that could crash the compiler.
- Fixed `setSelectedItem` swallowing the real cause of failures.
- Fixed a broken `OnItemSelectedListener` sample in the README that didn't compile.

### Changed

- Unified the tab-switch animation (indicator position/size, icon tint, label alpha) onto one shared progress curve instead of three separately-interpolated animators.
- Wired up `consumerProguardFiles` (`consumer-rules.pro`) - it was declared but never actually packaged into the published AAR - and scoped its keep rules to the real public API.
- Removed `LAYER_TYPE_HARDWARE`, which was counterproductive on a view that invalidates almost every frame during animation.
- Performance: cache per-item title width and last-applied icon tint instead of recomputing every `onDraw()` frame; avoid a per-frame allocation in the draw loop; replace `ArgbEvaluator` boxing with a hand-rolled color lerp; move icon `mutate()` to parse time instead of every frame.
- Bumped the Android Gradle Plugin and Gradle wrapper to their current major versions, and dependency versions (appcompat, core-ktx, navigation) across both modules.
- Removed unused `androidx.legacy:legacy-support-v4` and `androidx.multidex:multidex` dependencies, and dead SDK-version checks now that `minSdkVersion` is 32.
- Removed a half-finished, unused `needsRecalculation` optimization flag.
- Added CI (GitHub Actions) running a build and lint check on every push and pull request.
- Removed unmodified boilerplate test stubs and committed Kotlin-compiler crash logs from the repository.
