package com.koaladev.recycly.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.koaladev.recycly.R
import android.content.SharedPreferences

class PointsWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.points_widget)
    
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val points = sharedPref.getInt("POINTS", 0)
    
    views.setTextViewText(R.id.tv_points_value, points.toString())
    
    appWidgetManager.updateAppWidget(appWidgetId, views)
}