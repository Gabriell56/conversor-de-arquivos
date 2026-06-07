package com.example.conversordearquivos

import android.content.Intent
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
import android.widget.PopupMenu

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

        supportActionBar?.hide()

        window.statusBarColor = getColor(R.color.black)

        binding = ActivityMainBinding.inflate((layoutInflater))

        setContentView(binding.root)

        binding.btnMenu.setOnClickListener {
            val popup = PopupMenu(
                this,
                binding.btnMenu
            )

            popup.menu.add(getString(R.string.hist))
            popup.menu.add(getString(R.string.limpa_hist))
            popup.menu.add(getString(R.string.sobre))

            popup.setOnMenuItemClickListener {

                when (it.title) {

                    "Histórico" -> {

                        val intent = Intent(
                            this,
                            HistoricoActivity::class.java
                        )

                        startActivity(intent)
                    }

                    "Limpar Histórico" -> {
                        HistoricoManager.historico.clear()

                        Toast.makeText(
                            this,
                            getString(R.string.hist_limpo),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    "Sobre" -> {

                        Toast.makeText(
                            this,
                            getString(R.string.sobre_info),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                true
            }

            popup.show()
        }

        binding.btnEscolher.setOnClickListener {
            selecionarImagem.launch("image/*")
        }

        binding.btnConverter.setOnClickListener {

            if (imagemSelecionada == null) {

                Toast.makeText(
                    this,
                    getString(R.string.nao_arq),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val formatoSelecionado = binding.spinnerFormato.selectedItem.toString()

            val nomeArquivoOriginal = obterNomeArquivo(imagemSelecionada!!)

            if (
                nomeArquivoOriginal.endsWith(
                    ".png",
                    ignoreCase = true
                )
                &&
                formatoSelecionado == "PNG"
            ) {

                Toast.makeText(
                    this,
                    getString(R.string.ja_png),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if(
                (
                        nomeArquivoOriginal.endsWith(
                            ".jpg",
                            ignoreCase = true
                        )
                                ||
                                nomeArquivoOriginal.endsWith(
                                    ".jpeg",
                                    ignoreCase = true
                                )
                        )
                &&
                formatoSelecionado == "JPEG"
            ) {

                Toast.makeText(
                    this,
                    getString(R.string.ja_jpeg),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (
                nomeArquivoOriginal.endsWith(
                    ".webp",
                    ignoreCase = true
                )
                &&
                formatoSelecionado == "WEBP"
            ) {

                Toast.makeText(
                    this,
                    getString(R.string.ja_webp),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            when(formatoSelecionado) {

                "PNG" -> converter(
                    Bitmap.CompressFormat.PNG,
                    "png"
                )

                "JPEG" -> converter(
                    Bitmap.CompressFormat.JPEG,
                    "jpeg"
                )

                "WEBP" -> converter(
                    Bitmap.CompressFormat.WEBP,
                    "webp"
                )

                else -> {

                    Toast.makeText(
                        this,
                        getString(R.string.f_invalido),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        val formatos = arrayOf(
            "PNG",
            "JPEG",
            "WEBP"
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

            val pastaPictures = getExternalFilesDir(
                android.os.Environment.DIRECTORY_PICTURES
            )
            val arquivoSaida = File(
                pastaPictures,
                nomeArquivo
            )

            val outputStream = FileOutputStream(arquivoSaida)

            bitmap.compress(
                formato,
                100,
                outputStream
            )

            outputStream.flush()
            outputStream.close()

            HistoricoManager.historico.add(nomeArquivo)

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