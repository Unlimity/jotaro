package com.unlimity.jotaro.sample

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import com.unlimity.jotaro.asDeferred
import kotlinx.coroutines.*

class SampleActivity : Activity() {
    private val value by lazy { findViewById<EditText>(R.id.valueEdit) }
    private val type by lazy { findViewById<RadioGroup>(R.id.typeGroup) }
    private val put by lazy { findViewById<Button>(R.id.putButton) }
    private val get by lazy { findViewById<Button>(R.id.getButton) }
    private val getDeferred by lazy { findViewById<Button>(R.id.getDeferredButton) }
    private val putDeferred by lazy { findViewById<Button>(R.id.putDeferredButton) }

    lateinit var preferences: JotaroPreferences
    lateinit var gsonPreferences: JotaroGsonPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        preferences = JotaroPreferences(getSharedPreferences("jotaro", Context.MODE_PRIVATE))
        gsonPreferences = JotaroGsonPreferences(getSharedPreferences("jotaro-gson", Context.MODE_PRIVATE))

        put.setOnClickListener {
            val text = value.text.toString()

            when(type.checkedRadioButtonId) {
                R.id.typeInt -> preferences.int = text.toInt()
                R.id.typeLong -> preferences.long = text.toLong()
                R.id.typeFloat -> preferences.float = text.toFloat()
                R.id.typeBoolean -> preferences.boolean = text.toBoolean()
                R.id.typeString -> preferences.string = text
                R.id.typeCustom -> preferences.custom = text.split('|').let {
                    JotaroPreferences.Custom(
                        it[0].toInt(),
                        it[1]
                    )
                }
                R.id.typeCustomGson -> gsonPreferences.custom = text.split('|').let {
                    JotaroGsonPreferences.Custom(
                        it[0].toInt(),
                        it[1],
                        it[2].toFloat()
                    )
                }
            }
        }

        get.setOnClickListener {
            val text = when(type.checkedRadioButtonId) {
                R.id.typeInt -> preferences.int.toString()
                R.id.typeLong -> preferences.long.toString()
                R.id.typeFloat -> preferences.float.toString()
                R.id.typeBoolean -> preferences.boolean.toString()
                R.id.typeString -> preferences.string
                R.id.typeCustom -> preferences.custom.let { "${it.id}|${it.name}" }
                R.id.typeCustomGson -> gsonPreferences.custom.let { "${it.id}|${it.name}|${it.height}" }
                else -> ""
            }

            value.setText(text)
        }

        getDeferred.setOnClickListener {
            MainScope().launch {
                val text = withContext(Dispatchers.IO) {
                    when (type.checkedRadioButtonId) {
                        R.id.typeInt -> asDeferred(preferences::int).await().toString()
                        R.id.typeLong -> asDeferred(preferences::long).await().toString()
                        R.id.typeFloat -> asDeferred(preferences::float).await().toString()
                        R.id.typeBoolean -> asDeferred(preferences::boolean).await().toString()
                        R.id.typeString -> asDeferred(preferences::string).await()
                        R.id.typeCustom -> asDeferred(preferences::custom).await().let { "${it.id}|${it.name}" }
                        R.id.typeCustomGson -> asDeferred(gsonPreferences::custom).await().let { "${it.id}|${it.name}|${it.height}" }
                        else -> ""
                    }
                }

                value.setText(text)
            }
        }

        putDeferred.setOnClickListener {
            val text = value.text.toString()

            GlobalScope.launch(Dispatchers.IO) {
                when (type.checkedRadioButtonId) {
                    R.id.typeInt -> asDeferred(preferences::int, text.toInt()).await()
                    R.id.typeLong -> asDeferred(preferences::long, text.toLong()).await()
                    R.id.typeFloat -> asDeferred(preferences::float, text.toFloat()).await()
                    R.id.typeBoolean -> asDeferred(preferences::boolean, text.toBoolean()).await()
                    R.id.typeString -> asDeferred(preferences::string, text).await()
                    R.id.typeCustom -> asDeferred(preferences::custom, text.split('|').let {
                        JotaroPreferences.Custom(
                            it[0].toInt(),
                            it[1]
                        )
                    }).await()
                    R.id.typeCustomGson -> asDeferred(gsonPreferences::custom, text.split('|').let {
                        JotaroGsonPreferences.Custom(
                            it[0].toInt(),
                            it[1],
                            it[2].toFloat()
                        )
                    }).await()
                }
            }
        }
    }
}
