# me.ibrahimsn.lib public API surface - keep it intact for consumers that
# reference these classes from Java/XML or reflectively (e.g. inflating
# SmoothBottomBar from a layout). BottomBarParser is deliberately NOT kept:
# it's `internal`, not part of the public API.
-keep class me.ibrahimsn.lib.SmoothBottomBar { *; }
-keep interface me.ibrahimsn.lib.OnItemSelectedListener { *; }
-keep interface me.ibrahimsn.lib.OnItemReselectedListener { *; }
-keep class me.ibrahimsn.lib.BottomBarItem { *; }
-keep class me.ibrahimsn.lib.NavigationComponentHelper { *; }
