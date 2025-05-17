package com.example.hamburgueriaz

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HamburgueriaZApp()
                }
            }
        }
    }
}

@Composable
fun HamburgueriaZApp() {
    val context = LocalContext.current
    var nome by remember { mutableStateOf("") }
    var bacon by remember { mutableStateOf(false) }
    var queijo by remember { mutableStateOf(false) }
    var onionRings by remember { mutableStateOf(false) }
    var quantidade by remember { mutableStateOf(1) }

    val precoTotal = calcularPreco(quantidade, bacon, queijo, onionRings)

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_hamburgueria),
            contentDescription = "Logo Hamburgueria Z",
            modifier = Modifier
                .height(100.dp)
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "🍔 Faça seu pedido",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do cliente") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Text("Adicionais:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = bacon, onCheckedChange = { bacon = it })
            Text("Bacon")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = queijo, onCheckedChange = { queijo = it })
            Text("Queijo")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = onionRings, onCheckedChange = { onionRings = it })
            Text("Onion Rings")
        }

        Text("Quantidade:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { if (quantidade > 1) quantidade-- }) {
                Text("-")
            }
            Text(
                text = "  $quantidade  ",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 18.sp
            )
            Button(onClick = { quantidade++ }) {
                Text("+")
            }
        }

        Text(
            text = "💰 Preço total: R$ ${precoTotal.format(2)}",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val corpoEmail = gerarResumo(nome, bacon, queijo, onionRings, quantidade, precoTotal)

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Pedido de hambúrguer de $nome")
                        putExtra(Intent.EXTRA_TEXT, corpoEmail)
                    }

                    try {
                        context.startActivity(Intent.createChooser(intent, "Enviar pedido com:"))
                    } catch (e: Exception) {
                        Toast.makeText(context, "Nenhum app disponível para enviar o pedido.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Enviar pedido", color = Color.White)
            }

            Button(
                onClick = {
                    nome = ""
                    bacon = false
                    queijo = false
                    onionRings = false
                    quantidade = 1
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Limpar", color = Color.White)
            }
        }
    }
}

fun calcularPreco(qtd: Int, bacon: Boolean, queijo: Boolean, onion: Boolean): Double {
    var preco = 20.0
    if (bacon) preco += 2.0
    if (queijo) preco += 2.0
    if (onion) preco += 3.0
    return preco * qtd
}

fun gerarResumo(
    nome: String,
    bacon: Boolean,
    queijo: Boolean,
    onion: Boolean,
    qtd: Int,
    preco: Double
): String {
    return """
        Nome: $nome
        Tem Bacon? ${if (bacon) "Sim" else "Não"}
        Tem Queijo? ${if (queijo) "Sim" else "Não"}
        Tem Onion Rings? ${if (onion) "Sim" else "Não"}
        Quantidade: $qtd
        Preço final: R$ ${preco.format(2)}
    """.trimIndent()
}

fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)
