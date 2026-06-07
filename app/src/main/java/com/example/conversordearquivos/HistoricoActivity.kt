package com.example.conversordearquivos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class HistoricoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        window.statusBarColor = getColor(R.color.black)

        setContentView(R.layout.activity_historico)

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)

        btnVoltar.setOnClickListener {

            finish()
        }

        val lista = findViewById<ListView>(R.id.listHistorico)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            HistoricoManager.historico
        )

        lista.adapter = adapter

        }
    }