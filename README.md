
# SmoothBottomBar
A lightweight Android material bottom navigation bar library

[![API](https://img.shields.io/badge/API-22%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=22)

##  GIF
<img src="https://cdn.dribbble.com/users/1015191/screenshots/6251784/snapp---animation.gif"/>

## Design Credits
All design and inspiration credits belongs to [Alejandro Ausejo](https://dribbble.com/shots/6251784-Navigation-Menu-Animation).

## Usage
-    Create menu.xml under your res/menu/ folder
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <item
        android:id="@+id/item0"
        android:title="@string/menu_dashboard"
        android:icon="@drawable/ic_dashboard_white_24dp"/>

    <item
        android:id="@+id/item1"
        android:title="@string/menu_leaderboard"
        android:icon="@drawable/ic_multiline_chart_white_24dp"/>

    <item
        android:id="@+id/item2"
        android:title="@string/menu_store"
        android:icon="@drawable/ic_store_white_24dp"/>

    <item
        android:id="@+id/item3"
        android:title="@string/menu_profile"
        android:icon="@drawable/ic_person_outline_white_24dp"/>

</menu>
```
- Add view into your layout file
```xml
<me.ibrahimsn.lib.SmoothBottomBar
    android:id="@+id/bottomBar"
    android:layout_width="match_parent"
    android:layout_height="70dp"
    app:backgroundColor="@color/colorPrimary"
    app:menu="@menu/menu_bottom"/>
```
- Use SmoothBottomBar callbacks in your activity
```kotlin
bottomBar.setBottomBarCallback(object: SmoothBottomBar.BottomBarCallback {
    override fun onItemSelect(pos: Int) {

    }

    override fun onItemReselect(pos: Int) {

    }
})
```

## Customization
```xml
<me.ibrahimsn.lib.SmoothBottomBar
        android:id="@+id/bottomBar"
        android:layout_width="match_parent"
        android:layout_height="70dp"
        app:backgroundColor=""
        app:textColor=""
        app:textSize=""
        app:iconSize=""
        app:indicatorColor=""
        app:sideMargins=""
        app:itemPadding=""
        app:iconTint=""
        app:iconTintActive=""
        app:activeItem=""
        app:menu=""/>
```
## Setup
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
	dependencies {
	        implementation 'com.github.ibrahimsn98:SmoothBottomBar:1.0'
	}
```
## License
```
MIT License

Copyright (c) 2019 İbrahim Süren

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
