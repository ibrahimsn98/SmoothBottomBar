# me.ibrahimsn.smoothbottombar public API surface - keep it intact for consumers that
# reference these classes from Java/XML or reflectively (e.g. inflating
# SmoothBottomBar from a layout). BottomBarParser is deliberately NOT kept:
# it's `internal`, not part of the public API.
-keep class me.ibrahimsn.smoothbottombar.SmoothBottomBar { *; }
-keep interface me.ibrahimsn.smoothbottombar.OnItemSelectedListener { *; }
-keep interface me.ibrahimsn.smoothbottombar.OnItemReselectedListener { *; }
-keep class me.ibrahimsn.smoothbottombar.BottomBarItem { *; }
-keep class me.ibrahimsn.smoothbottombar.NavigationComponentHelper { *; }
