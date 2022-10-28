# SmoothBottomBar

A lightweight Android material bottom navigation bar library

[![](https://jitpack.io/v/ibrahimsn98/SmoothBottomBar.svg)](https://jitpack.io/#ibrahimsn98/SmoothBottomBar)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-SmoothBottomBar-green.svg?style=flat )]( https://android-arsenal.com/details/1/7932 )
[![Android Weekly](https://androidweekly.net/issues/issue-385/badge)](https://androidweekly.net/issues/issue-385)

## GIF

<img src="https://cdn.dribbble.com/users/1015191/screenshots/6251784/snapp---animation.gif"/>

## Design Credits

All design and inspiration credits belong to [Alejandro Ausejo](https://dribbble.com/shots/6251784-Navigation-Menu-Animation).

## Usage

-   Create menu.xml under your res/menu/ folder
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

-   Add view into your layout file
```xml
<me.ibrahimsn.lib.SmoothBottomBar
    android:id="@+id/bottomBar"
    android:layout_width="match_parent"
    android:layout_height="70dp"
    app:backgroundColor="@color/colorPrimary"
    app:badgeColor="@color/colorBadge"
    app:menu="@menu/menu_bottom"/>
```

-   Use SmoothBottomBar callbacks in your activity
```kotlin
bottomBar.onItemSelected = {
    status.text = "Item $it selected"
}

bottomBar.onItemReselected = {
    status.text = "Item $it re-selected"
}
```

OR

```kotlin
bottomBar.setOnItemSelectedListener(object: OnItemSelectedListener {
    override fun onItemSelect(pos: Int) {
        status.text = "Item $pos selected"
    }
})

bottomBar.setOnItemReselectedListener(object: OnItemReselectedListener {
    override fun onItemReselect(pos: Int) {
        status.text = "Item $pos re-selected"
    }
})
```

 >**Note:** For projects without kotlin, you may need to add `org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion` to your dependencies since this is a Kotlin library.

## **Use SmoothBottomBar with [Navigation Components](https://developer.android.com/guide/navigation/).**

Coupled with the Navigation Component from the [Android Jetpack](https://developer.android.com/jetpack), SmoothBottomBar offers easier navigation within your application by designating navigation to the Navigation Component. This works best when using fragments, as the Navigation component helps to handle your fragment transactions.

- Setup Navigation Component i.e. Add dependenccy to your project, create a Navigation Graph etc.
- For each Fragment in your Navigation Graph, ensure that the Fragment's `id` is the same as the MenuItems in your Menu i.e res/menu/ folder
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <item
        android:id="@+id/first_fragment"
        android:title="@string/menu_dashboard"
        android:icon="@drawable/ic_dashboard_white_24dp"/>

    <item
        android:id="@+id/second_fragment"
        android:title="@string/menu_leaderboard"
        android:icon="@drawable/ic_multiline_chart_white_24dp"/>

    <item
        android:id="@+id/third_fragment"
        android:title="@string/menu_store"
        android:icon="@drawable/ic_store_white_24dp"/>

    <item
        android:id="@+id/fourth_fragment"
        android:title="@string/menu_profile"
        android:icon="@drawable/ic_person_outline_white_24dp"/>

</menu>
```

Navigation Graph i.e res/navigation/ folder
```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/first_fragment">

    <fragment
        android:id="@+id/first_fragment"
        android:name="me.ibrahimsn.smoothbottombar.FirstFragment"
        android:label="Dashboard"
        tools:layout="@layout/fragment_first" >
        <action
            android:id="@+id/action_firstFragment_to_secondFragment"
            app:destination="@id/second_fragment" />
    </fragment>
    <fragment
        android:id="@+id/second_fragment"
        android:name="me.ibrahimsn.smoothbottombar.SecondFragment"
        android:label="Leaderboard"
        tools:layout="@layout/fragment_second" >
        <action
            android:id="@+id/action_secondFragment_to_thirdFragment"
            app:destination="@id/third_fragment" />
    </fragment>
    <fragment
        android:id="@+id/third_fragment"
        android:name="me.ibrahimsn.smoothbottombar.ThirdFragment"
        android:label="Store"
        tools:layout="@layout/fragment_third" >
        <action
            android:id="@+id/action_thirdFragment_to_fourthFragment"
            app:destination="@id/fourth_fragment" />
    </fragment>
    <fragment
        android:id="@+id/fourth_fragment"
        android:name="me.ibrahimsn.smoothbottombar.FourthFragment"
        android:label="Profile"
        tools:layout="@layout/fragment_fourth" />
</navigation>
```

- In your activity i.e `MainActivity`, create an instance of `PopupMenu` which takes a `context` and an `anchor`(pass in null) and then inflate this `PopupMenu` with the layout menu for the `SmoothBottomBar`.  
- Get a reference to your `SmoothBottomBar` and call `setupWithNavController()` which takes in a `Menu` and `NavController`, pass in the menu of the previously instantiated `PopupMenu` i.e.(`popUpMenu.menu`)  and your `NavController`. 
- Preferably set this up in a function as shown below and call this function i.e. (`setupSmoothBottomMenu()`) in the `onCreate` method of your activity. 

**N.B:**  Sample app makes use of [ViewBinding](https://developer.android.com/topic/libraries/view-binding) to get reference to views in the layout.

```kotlin
   private fun setupSmoothBottomMenu() {
        binding.bottomBar.setupWithNavController(navController)
   }
```


### ActionBar
You can also setup your `ActionBar` with the Navigation Component by calling `setupActionBarWithNavController` and pass in your `NavController`. 

**N.B:** Your app needs to have a `Toolbar` for `setupActionBarWithNavController` to work. If your app theme doesn't have a `Toolbar` i.e. (Theme.AppCompat.Light.NoActionBar) you would need to: 

- Add one to your layout i.e.

```xml
 <com.google.android.material.appbar.AppBarLayout
        android:id="@+id/app_bar_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">
        <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolBar"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />
    </com.google.android.material.appbar.AppBarLayout>
```
- Set the support action bar to your `Toolbar` using `setSupportActionBar(your_toolbar_id)` in this case `setSupportActionBar(binding.toolBar)`

We now have something like this:

```kotlin
 private lateinit var navController: NavController
 private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.main_fragment)
        setupActionBarWithNavController(navController)
        setupSmoothBottomMenu()
    }
    
    private fun setupSmoothBottomMenu() {
        binding.bottomBar.setupWithNavController(navController)
    }

    //set an active fragment programmatically
    fun setSelectedItem(pos:Int){
        binding.bottomBar.setSelectedItem(pos)
    }
    //set badge indicator
    fun setBadge(pos:Int){
        binding.bottomBar.setBadge(pos)
    }
    //remove badge indicator
    fun removeBadge(pos:Int){
        binding.bottomBar.removeBadge(pos)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
```

### Result Demo:

<p align="center"><a><img src="https://user-images.githubusercontent.com/29807085/117545623-78f8a280-b01e-11eb-9a2a-06d610bd6f95.gif" width="300"></a></p>

### Update [8th May, 2021]
Prior to the [initial addition of this feature](https://github.com/ibrahimsn98/SmoothBottomBar/pull/33), you can now inflate separate menu items for the `SmoothBottomBar` and your `Toolbar`. 
- Create the menu item you want to inflate in the `Toolbar` i.e.

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/another_item_1"
        android:title="Another Item 1" />
    <item
        android:id="@+id/another_item_2"
        android:title="Another Item 2" />
    <item
        android:id="@+id/another_item_3"
        android:title="Another Item 3" />
</menu>
```
- Override `OnCreateOptionsMenu` and `onOptionsItemSelected` (ensure it returns `super.onOptionsItemSelected(item)`). Now we have:

```kotlin
  class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.main_fragment)
        setupActionBarWithNavController(navController)
        setupSmoothBottomMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.another_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.another_item_1 -> {
                showToast("Another Menu Item 1 Selected")
            }

            R.id.another_item_2 -> {
                showToast("Another Menu Item 2 Selected")
            }

            R.id.another_item_3 -> {
                showToast("Another Menu Item 3 Selected")
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun setupSmoothBottomMenu() {
        binding.bottomBar.setupWithNavController(navController)
    }

    //set an active fragment programmatically
    fun setSelectedItem(pos:Int){
        binding.bottomBar.setSelectedItem(pos)
    }
    //set badge indicator
    fun setBadge(pos:Int){
        binding.bottomBar.setBadge(pos)
    }
    //remove badge indicator
    fun removeBadge(pos:Int){
        binding.bottomBar.removeBadge(pos)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
```

### Select Bottom Item from any fragment
```kotlin
    buttonId.setOnClickListener {
        (requireActivity() as MainActivity).setSelectedItem(2)
    }
```


### Result Demo:

<p align="center"><a><img src="https://user-images.githubusercontent.com/29807085/117545665-a6dde700-b01e-11eb-8ede-bb9263826814.gif" width="300"></a></p>


## Customization

```xml
<me.ibrahimsn.lib.SmoothBottomBar
        android:id="@+id/bottomBar"
        android:layout_width="match_parent"
        android:layout_height="70dp"
        app:menu=""
        app:backgroundColor=""
        app:indicatorColor=""
        app:indicatorRadius=""
        app:cornerRadius=""
        app:corners=""
        app:sideMargins=""
        app:itemPadding=""
        app:textColor=""
        app:badgeColor=""
        app:itemFontFamily=""
        app:textSize=""
        app:iconSize=""
        app:iconTint=""
        app:iconTintActive=""
        app:activeItem=""
        app:duration="" />
```

## Setup

> Follow me on Twitter [@ibrahimsn98](https://twitter.com/ibrahimsn98)

```gradle
//project label build.gradle
buildscript {
    repositories {
         ....
        maven { url 'https://jitpack.io' }
    }
}

allprojects {
    repositories {
     .......
        maven { url 'https://www.jitpack.io' }
    }
}
//app label build.gradle
dependencies {
        implementation 'com.github.ibrahimsn98:SmoothBottomBar:1.7.9'
}
```

## Contributors ✨
<table>
    <tr>
        <td align="center">
            <a href="https://github.com/brookmg">
                <img src="https://avatars3.githubusercontent.com/u/7487888?s=460&u=7ba52b250536b301987aaa350f605bbaf3ffa8ed&v=4" width="100px;" alt=""/><br />
                <sub><b>brookmg</b></sub>
            </a>
        </td>
        <td align="center">
            <a href="https://github.com/rezaepsilon0">
                <img src="https://avatars0.githubusercontent.com/u/18679475?s=460&u=a5e771cc32bff987aceccafa55edab06a8aa804f&v=4" width="100px;" alt=""/><br />
                <sub><b>rezaepsilon0</b></sub>
            </a>
        </td>
        <td align="center">
            <a href="https://github.com/amitdash291">
                <img src="https://avatars2.githubusercontent.com/u/18529185?s=460&v=4" width="100px;" alt=""/><br />
                <sub><b>amitdash291</b></sub>
            </a>
        </td>
        <td align="center">
            <a href="https://github.com/tobiasschuerg">
                <img src="https://avatars2.githubusercontent.com/u/1324555?s=460&v=4" width="100px;" alt=""/><br />
                <sub><b>tobiasschuerg</b></sub>
            </a>
        </td>
        <td align="center">
            <a href="https://github.com/mayokunthefirst">
                <img src="https://avatars0.githubusercontent.com/u/29807085?s=460&u=493fd9b143dd96eecea56749f57d20e002e246e4&v=4" width="100px;" alt=""/><br />
                <sub><b>mayokunthefirst</b></sub>
            </a>
        </td>
        <td align="center">
            <a href="https://github.com/FannyDemey">
                <img src="https://avatars.githubusercontent.com/u/3006608?v=4" width="100px;" alt=""/><br />
                <sub><b>FannyDemey</b></sub>
            </a>
        </td>
    </tr>
    <tr>
    	 <td align="center">
            <a href="https://github.com/Milad-Akarie">
                <img src="https://avatars.githubusercontent.com/u/55059449?v=4" width="100px;" alt=""/><br />
                <sub><b>Milad akarie</b></sub>
            </a>
        </td>
        <td align="center">
            <a href="https://github.com/milon27">
                <img src="https://avatars.githubusercontent.com/u/44096479?v=4" width="100px;" alt=""/><br />
                <sub><b>Milon27</b></sub>
            </a>
        </td>
    </tr>
</table>


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
