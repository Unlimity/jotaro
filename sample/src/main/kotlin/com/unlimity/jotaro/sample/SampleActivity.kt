package com.unlimity.jotaro.sample

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import com.unlimity.jotaro.asDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SampleActivity : Activity() {
    private val value by lazy { findViewById<EditText>(R.id.valueEdit) }
    private val type by lazy { findViewById<RadioGroup>(R.id.typeGroup) }
    private val put by lazy { findViewById<Button>(R.id.putButton) }
    private val get by lazy { findViewById<Button>(R.id.getButton) }
    private val getDeferred by lazy { findViewById<Button>(R.id.getDeferredButton) }

    lateinit var preferences: JotaroPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        preferences = JotaroPreferences(getSharedPreferences("jotaro", Context.MODE_PRIVATE))

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
                        else -> ""
                    }
                }

                value.setText(text)
            }
        }
    }
}
