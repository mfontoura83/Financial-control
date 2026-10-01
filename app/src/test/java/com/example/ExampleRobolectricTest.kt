package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.FinanceDatabaseInitializer
import com.example.domain.engine.FinancialEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("FinControl", appName)
    }

    @Test
    fun `test financial engine DRE and BP balancing`() {
        val accounts = FinanceDatabaseInitializer.getInitialAccounts()
        val txs = FinanceDatabaseInitializer.getInitialTransactions()

        val dre = FinancialEngine.calculateDre(txs)
        assertTrue("Receita bruta deve ser positiva", dre.receitaBruta > 0)
        assertTrue("Lucro bruto deve ser positivo", dre.lucroBruto > 0)
        assertTrue("EBITDA deve ser positivo", dre.ebitda > 0)

        val bp = FinancialEngine.calculateBalanceSheet(accounts, txs, dre)
        assertTrue("Ativo deve ser igual a Passivo + PL", bp.isBalanced)
        assertTrue("Caixa total positivo", bp.caixaBancos > 0)

        val dfc = FinancialEngine.calculateDfc(accounts, txs)
        assertTrue("Fluxo operacional deve gerar caixa", dfc.fluxoOperacionalLiquido > 0)
    }
}
