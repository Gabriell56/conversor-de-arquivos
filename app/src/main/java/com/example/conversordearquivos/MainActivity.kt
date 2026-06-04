package com.example.conversordearquivos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.conversordearquivos.databinding.ActivityMainBinding
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ArrayAdapter
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private fun obterNomeArquivo(uri: Uri): String {
        var nome = "Arquivo selecionado"

        contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )?.use { cursor ->

            val indice = cursor.getColumnIndex(
                android.provider.OpenableColumns.DISPLAY_NAME
            )

            if (cursor.moveToFirst() && indice >= 0) {

                nome = cursor.getString(indice)
            }

        }

        return nome
    }

    private var imagemSelecionada: Uri? = null

    private val selecionarImagem =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                imagemSelecionada = uri

                binding.imgPreview.setImageURI(uri)

                binding.txtArquivo.text = obterNomeArquivo(uri)

                Toast.makeText(
                    this,
                    getString(R.string.confirma_arq),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate((layoutInflater))

        setContentView(binding.root)

        binding.btnEscolher.setOnClickListener {
            selecionarImagem.launch("image/*")
        }

        binding.btnConverter.setOnClickListener {

            val formatoSelecionado = binding.spinnerFormato.selectedItem.toString()

            if (imagemSelecionada != null) {

                if (formatoSelecionado == "PNG") {

                    converter(
                        Bitmap.CompressFormat.PNG,
                        "png"
                    )
                } else if (formatoSelecionado == "JPEG") {

                    converter(
                        Bitmap.CompressFormat.JPEG,
                        "jpeg"
                    )
                }

            } else {

                Toast.makeText(
                    this,
                    getString(R.string.nao_arq),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val formatos = arrayOf(
            "PNG",
            "JPEG"
        )

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            formatos
        )

        adapter.setDropDownViewResource(
            R.layout.spinner_dropdown_item
        )

        binding.spinnerFormato.adapter = adapter
    }

    private fun converter(
        formato: Bitmap.CompressFormat,
        extensao: String
    ) {

        try {

            if (imagemSelecionada == null) {

                Toast.makeText(
                    this,
                    getString((R.string.nao_arq)),
                    Toast.LENGTH_SHORT
                ).show()

                return
            }

            val inputStream = contentResolver.openInputStream((imagemSelecionada!!))

            val bitmap = BitmapFactory.decodeStream(inputStream)

            inputStream?.close()

            val timestamp = System.currentTimeMillis()

            val nomeArquivo = "${getString(R.string.prefixo_arq)}_$timestamp.$extensao"

            val arquivoSaida = File(
                getExternalFilesDir(null),
                nomeArquivo //nao sei se pode
            )

            val outputStream = FileOutputStream(arquivoSaida)

            bitmap.compress(
                formato,
                100,
                outputStream
            )

            outputStream.flush()
            outputStream.close()

            Toast.makeText(
                this,
                getString(R.string.arquivo_salvo, arquivoSaida.name),
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {

            Toast.makeText(
                this,
                getString(R.string.erro),
                Toast.LENGTH_SHORT
            ).show()

            e.printStackTrace()
        }
    }
}