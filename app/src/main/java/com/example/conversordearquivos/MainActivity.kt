package com.example.conversordearquivos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.conversordearquivos.databinding.ActivityMainBinding
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var imagemSelecionada: Uri? = null

    private val selecionarImagem =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                imagemSelecionada = uri

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

        binding.btnConverterPNG.setOnClickListener {

            if (imagemSelecionada != null) {

                converterParaPNG()

            } else {

                Toast.makeText(
                    this,
                    getString(R.string.nao_arq),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnConverterJPEG.setOnClickListener {

            if (imagemSelecionada != null) {

                converterParaJPEG()

            } else {

                Toast.makeText(
                    this,
                    getString(R.string.nao_arq),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun converterParaPNG() {

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

            val arquivoSaida = File(
                getExternalFilesDir(null),
                getString(R.string.png_salvo) //nao sei se pode
            )

            val outputStream = FileOutputStream(arquivoSaida)

            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                outputStream
            )

            outputStream.flush()
            outputStream.close()

            Toast.makeText(
                this,
                getString(R.string.arq_convertido),
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

    private fun converterParaJPEG() {

        try{

            if (imagemSelecionada == null) {

                Toast.makeText(
                    this,
                    getString(R.string.nao_arq),
                    Toast.LENGTH_SHORT
                ).show()

                return
            }

            val inputStream = contentResolver.openInputStream(imagemSelecionada!!)

            val bitmap = BitmapFactory.decodeStream(inputStream)

            inputStream?.close()

            val arquivoSaida = File(
                getExternalFilesDir(null),
                getString(R.string.jpeg_salvo) //nao sei se pode
            )

            val outputStream = FileOutputStream(arquivoSaida)

            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                100,
                outputStream
            )

            outputStream.flush()
            outputStream.close()

            Toast.makeText(
                this,
                getString(R.string.arq_convertido),
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