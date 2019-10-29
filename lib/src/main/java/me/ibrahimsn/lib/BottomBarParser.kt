package me.ibrahimsn.lib

import android.content.Context
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import me.ibrahimsn.lib.Constants.ICON_ATTRIBUTE
import me.ibrahimsn.lib.Constants.ITEM_TAG
import me.ibrahimsn.lib.Constants.TITLE_ATTRIBUTE

class BottomBarParser(private val context: Context, res: Int) {

    private val parser: XmlResourceParser = context.resources.getXml(res)

    fun parse(): List<BottomBarItem> {
        val items: MutableList<BottomBarItem> = mutableListOf()
        var eventType: Int?
        do {
            eventType = parser.next()
            if (eventType == XmlResourceParser.START_TAG && parser.name == ITEM_TAG) {
                items.add(getTabConfig(parser))
            }
        } while (eventType != XmlResourceParser.END_DOCUMENT)
        return items.toList()
    }

    private fun getTabConfig(parser: XmlResourceParser): BottomBarItem {
        val attributeCount = parser.attributeCount
        var itemText: String? = null
        var itemDrawable: Drawable? = null

        for (i in 0..attributeCount) {
            when (parser.getAttributeName(i)) {
                ICON_ATTRIBUTE -> itemDrawable =
                    ContextCompat.getDrawable(context, parser.getAttributeResourceValue(i, 0))
                TITLE_ATTRIBUTE -> {
                    itemText = try {
                        context.getString(parser.getAttributeResourceValue(i, 0))
                    } catch (e: Exception) {
                        parser.getAttributeValue(i)
                    }
                }
            }
        }
        return BottomBarItem(itemText ?: "", itemDrawable!!, alpha = 0)
    }
}