package com.example.a17422_final_project

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

/// TODO: strat is get the entire thing working in memory first. then persist it
/// TODO: once the api is good and working.

/// TODO: arraylist to jsonarray
/// TODO: timeLimit for task
/// TODO: on timelimit expire, drawOverOtherApps for another ring then dismiss button.
/// TODO: showTimer in top corner
/// TODO: use alarm name in the PendingIntent maybe so handler can lookup alarm and call ring()
/// TODO: schedule/rearm the timeLimit alarm

/// TODO LATER: deal with multiple alarms attempting to ring at same time? queue?

class Task(val type : TaskType, val timeLimit : Int, val params : JSONObject) {
    fun makeIntent(ctx : Context): Intent {
        val intent = when (type) {
            TaskType.STEPS  -> Intent(ctx, StepActivity::class.java)
            TaskType.SPEECH -> Intent(ctx, SpeechTask::class.java)
        }
        intent.putExtra("timeLimit", timeLimit)
        return intent.putExtra("params", params.toString())
    }

    fun toJSON() : JSONObject {
        val obj = JSONObject()
        obj.put("type", type.key)
        obj.put("timeLimit", timeLimit)
        obj.put("params", params)
        return obj
    }

    companion object { fun fromJSON(obj : JSONObject) : Task {
        val otype = TaskType[obj.getInt("type")]
        val limit = obj.getInt("timeLimit")
        val params = obj.getJSONObject("params")
        return Task(otype, limit, params)
    }}

}

/// TODO: make this numSeconds offset by default?
/// TODO: timeLimit in seconds
class Check(val task : Task) {
    var hour : Int = 0
    var minute : Int = 0
    var isExact : Boolean = false  // if false, is offset from dismissal of alarm. else is time

    fun toJSON() : JSONObject {
        var obj = JSONObject()
        obj.put("task", task.toJSON())
        obj.put("hour", hour)
        obj.put("minute", minute)
        obj.put("isExact", isExact)
        return obj
    }

    companion object { fun fromJSON(obj : JSONObject) : Check {
        val t = Task.fromJSON(obj.getJSONObject("task"))
        val c = Check(t)
        c.hour = obj.getInt("hour")
        c.minute = obj.getInt("minute")
        c.isExact = obj.getBoolean("isExact")
        return c
    }}
}

class Timer {

    var go = {} as Runnable

    fun start(ctx : Context, timeLimit : Int) {
        go = {
            ForegroundService.start(ctx)
            start(ctx, timeLimit)
        } as Runnable

        Handler(Looper.getMainLooper()).postDelayed(go, timeLimit * 1000L)
    }
    fun stop() {
        Handler(Looper.getMainLooper()).removeCallbacks(go)
    }
}

class Alarm {
    // write data
    var name    : String = ""
    var mask    : Int = 0
    var hour    : Int = 0
    var minute  : Int = 0
    var armed   : Boolean = true
    var oneShot : Boolean = false     // if true, disarm on ring
    var tasks   = ArrayList<Task>()   // complete all to dismiss alarm
    var checks  = ArrayList<Check>()  // complete each or else ring again but with offset on?

    // runtime state
    var alarmIntent : Intent? = null  // the armed alarm. cancel later

    /// TODO: add all new alarm props to the I/O
    fun toJSON() : JSONObject {
        var obj = JSONObject()
        obj.put("name", name)
        obj.put("mask", mask)
        obj.put("hour", hour)
        obj.put("minute", minute)
        obj.put("armed", armed)
        obj.put("checks", checks.map { it.toJSON() })
        Log.d("Alarm.toJSON()", obj.toString())
        return obj
    }

    fun arm(ctx : Context) {
        /// TODO:
        /// set alarmintent
        armed = true
    }

    fun disarm(ctx : Context) {
        /// TODO:
        /// if (intent valid) then cancel intent
        armed = false
    }

    fun ring(ctx : Context) {
        /// TODO:
        /// play sound?
        startTaskStack(ctx, tasks)
        disarm(ctx)
        if (!oneShot)
            arm(ctx)
    }

    companion object { fun fromJSON(obj : JSONObject) : Alarm {
        var a = Alarm()
        a.name = obj.getString("name")
        a.mask = obj.getInt("mask")
        a.hour = obj.getInt("hour")
        a.minute = obj.getInt("minute")
        a.armed = obj.getBoolean("armed")
        return a
    }}
}

interface TaskActivity {
    val timer: Timer
    fun init(ctx : Content, intent : Intent) {
        timer.start(intent.getIntExtra("timeLimit"))

    }

    fun
}