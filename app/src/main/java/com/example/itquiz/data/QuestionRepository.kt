package com.example.itquiz.data

import android.content.Context


class QuestionRepository(private val context: Context) {

    private val questions: List<Question> = listOf(
        Question(1, "Qual processador NÃO existe?", "pergunta1.png", listOf("i5-11400F", "i3-11100F", "i3-8350K", "i7-8086K"), 2),
        Question(2, "Qual especificação do processador é a MENOS relevante na hora da compra?", "pergunta2.png", listOf("Família", "Clock (GHz)", "Geração", "Gráficos integrados"), 2),
        Question(3, "Qual chipset de placa mãe AMD NÃO deve ser utilizado atualmente?", "pergunta3.png", listOf("TRX", "A", "B", "X"), 2),
        Question(4, "Qual tamanho de placa mãe EXISTE?", "pergunta4.png", listOf("L-ATX", "E-ATX", "Micro ITX", "Mini ATX"), 2),
        Question(5, "Qual cooler NÃO deve ser utilizado?", "pergunta5.png", listOf("Air Cooler", "Cooler Box (padrão)", "Water Cooler Custom", "Water Cooler"), 2),
        Question(6, "Qual componente de baixa qualidade MENOS influencia na temperatura do computador?", "pergunta6.png", listOf("Gabinete", "Fan/Ventoinha", "CPU Cooler", "Pasta térmica"), 2),
        Question(7, "Qual combinação de pente(s) é a MELHOR para 32 GB de memória RAM?", "pergunta7.png", listOf("1x32 GB", "2x16 GB", "4x8 GB", "8x4 GB"), 2),
        Question(8, "Qual característica da memória RAM é a MENOS relevante na hora da compra?", "pergunta8.png", listOf("Chip", "Marca", "Latência (CL)", "Frequência"), 2),
        Question(9, "Qual característica da placa de vídeo é a MAIS importante?", "pergunta9.png", listOf("VRAM", "Chip", "Tamanho", "Marca"), 2),
        Question(10, "Qual linha da Nvidia NÃO existe?", "pergunta10.png", listOf("MX", "VX", "GT", "RTX"), 2),
        Question(11, "Qual armazenamento possui a MAIOR temperatura de operação (em média)?", "pergunta11.png", listOf("HD", "SSD NVME", "Pen Drive", "Fita magnética"), 2),
        Question(12, "Qual armazenamento NÃO deve ser o ÚNICO em um computador?", "pergunta12.png", listOf("SSD SATA", "SSD M.2 NVME", "SSD M.2 SATA", "Nenhum"), 2),
        Question(13, "Qual potência de fonte é a MELHOR para um PRIMEIRO computador?", "pergunta13.png", listOf("450W", "650W", "500W", "850W"), 2),
        Question(14, "Qual característica garante MAIOR qualidade em uma fonte?", "pergunta14.png", listOf("Marca", "Certificações", "Potência", "Refrigeração"), 2),
        Question(15, "Qual característica de um gabinete é a MAIS importante?", "pergunta15.png", listOf("Material", "Fluxo de ar", "Marca", "Visual"), 2),
        Question(16, "Qual tamanho de gabinete NÃO existe?", "pergunta16.png", listOf("Mini Tower", "Large Tower", "Mid Tower", "Full Tower"), 2),
        Question(17, "Qual é o nome desse TAMANHO de teclado?", "pergunta17.png", listOf("Pequeno", "TKL", "90%", "Compact"), 2),
        Question(18, "Qual o nome dessa parte do teclado?", "pergunta18.png", listOf("Tecla", "Keycap", "Switch", "Letra"), 2),
        Question(19, "Qual a característica MAIS garante qualidade em um mouse?", "pergunta19.png", listOf("Material", "Sensor", "Tamanho", "DPI"), 2),
        Question(20, "O que NÃO é um COMPONENTE do mouse?", "pergunta20.png", listOf("Switch", "DPI", "Scroll", "Botão"), 2),
        Question(21, "Qual o significado desse barulho emitido pela placa mãe?", "pergunta21.png", listOf("Erro na CPU", "Erro na memória RAM", "Erro na fonte", "Erro na GPU"), 2),
    )

    fun randomizeQuestions(count: Int): List<Question> {
        return questions.shuffled().take(count)
    }

    fun getImageByName(name: String): String {
        return context.assets.list("")?.find { it == name }?.let { name } ?: ""
    }
}
