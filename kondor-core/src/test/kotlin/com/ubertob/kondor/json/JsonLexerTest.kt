package com.ubertob.kondor.json

import com.ubertob.kondor.json.parser.KondorSeparator.*
import com.ubertob.kondor.json.parser.KondorTokenizer
import com.ubertob.kondor.json.parser.Separator
import com.ubertob.kondor.json.parser.Value
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class JsonLexerTest {

    private fun tokenize(jsonStr: String) = KondorTokenizer.tokenize(jsonStr)

    @Test
    fun `single word`() {
        val json = "abc"
        val seq = tokenize(json)

        expectThat(seq.asSequence().toList()).isEqualTo(listOf(Value(json, 1)))
    }

    @Test
    fun `spaces tab and new lines word`() {
        val json = "  abc   def\ngh\tijk\r lmn \n\n opq"
        val seq = tokenize(json)

        expectThat(seq.asSequence().toList()).isEqualTo(
            listOf(
                Value("abc", 3),
                Value("def", 9),
                Value("gh", 13),
                Value("ijk", 16),
                Value("lmn", 21),
                Value("opq", 28)
            )
        )
    }

    @Test
    fun `json special tokens`() {
        val json = "[]{}:, \"\" [a,b,c]  {d:e}"
        val seq = tokenize(json)

        expectThat(seq.asSequence().toList()).isEqualTo(
            listOf(
                Separator(OpeningBracket, 1),
                Separator(ClosingBracket, 2),
                Separator(OpeningCurly, 3),
                Separator(ClosingCurly, 4),
                Separator(Colon, 5),
                Separator(Comma, 6),
                Separator(OpeningQuotes, 8),
                Separator(ClosingQuotes, 9),
                Separator(OpeningBracket, 11),
                Value("a", 12),
                Separator(Comma, 13),
                Value("b", 14),
                Separator(Comma, 15),
                Value("c", 16),
                Separator(ClosingBracket, 17),
                Separator(OpeningCurly, 20),
                Value("d", 21),
                Separator(Colon, 22),
                Value("e", 23),
                Separator(ClosingCurly, 24)
            )
        )
    }

    @Test
    fun `json strings`() {
        val json = """
            { "abc": 123}
        """.trimIndent()
        val seq = tokenize(json)

        expectThat(seq.asSequence().toList()).isEqualTo(
            listOf(
                Separator(OpeningCurly, 1),
                Separator(OpeningQuotes, 3),
                Value("abc", 4),
                Separator(ClosingQuotes, 7),
                Separator(Colon, 8),
                Value("123", 10),
                Separator(ClosingCurly, 13)
            )
        )
    }

    @Test
    fun `json strings with escapes`() {
        val json = """
            {"abc":"abc\"\\ \n}"}
        """.trimIndent()
        val seq = tokenize(json)

        expectThat(seq.asSequence().toList()).isEqualTo(
            listOf(
                Separator(OpeningCurly, 1),
                Separator(OpeningQuotes, 2),
                Value("abc", 3),
                Separator(ClosingQuotes, 6),
                Separator(Colon, 7),
                Separator(OpeningQuotes, 8),
                Value("abc\"\\ \n}", 12),
                Separator(ClosingQuotes, 20),
                Separator(ClosingCurly, 21)
            )
        )
    }

}