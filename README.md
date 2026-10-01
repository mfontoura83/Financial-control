# FinControl PRO — Gestão Financeira, Controladoria & Tesouraria

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Room Database](https://img.shields.io/badge/Persistence-Room%20KSP-47A248.svg?style=flat&logo=sqlite)](https://developer.android.com/training/data-storage/room)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean%20Flow-blue.svg?style=flat)](https://developer.android.com/topic/architecture)

Sistema corporativo de gestão financeira empresarial de alta performance para Android. Integra em tempo real os pilares de **Controladoria** (DRE, Balanço Patrimonial e DFC), **Tesouraria** (Posição multicarteira consolidada e fluxo de caixa projetado) e **Automação Bancária** com suporte a conciliação automática via Open Finance.

---

## 📊 Módulos e Funcionalidades

### 1. Controladoria & Demonstrações Financeiras Integradas
- **DRE (Demonstração do Resultado do Exercício)**:
  - Estrutura contábil conforme as normas **CPC / IFRS**:
    - Receita Operacional Bruta
    - (-) Deduções e Impostos sobre Vendas (PIS, COFINS, ICMS, ISS)
    - (=) **Receita Operacional Líquida**
    - (-) Custos dos Produtos e Serviços Vendidos (CPV / CMV)
    - (=) **Lucro Bruto** (com Margem Bruta %)
    - (-) Despesas Comerciais e Vendas
    - (-) Despesas Gerais e Administrativas (Folha, Instalações, Honorários)
    - (=) **EBITDA / LAJIDA** (com Margem EBITDA %)
    - (-) Depreciação e Amortização
    - (=) **EBIT / LAJIR** (Resultado Operacional)
    - (+/-) **Resultado Financeiro Líquido** (Receitas Financeiras vs Despesas/Juros)
    - (=) **LAIR** (Lucro Antes do IR)
    - (-) Provisão IRPJ e CSLL
    - (=) **Lucro Líquido do Exercício** (com Margem Líquida %)

- **BP (Balanço Patrimonial)**:
  - **Ativo Total**:
    - *Ativo Circulante*: Caixa e Equivalentes (saldo real das contas bancárias), Contas a Receber, Estoques e Outros Créditos Fiscais.
    - *Ativo Não Circulante*: Imobilizado Bruto deduzido de Depreciação Acumulada e Ativos Intangíveis (Software e Marcas).
  - **Passivo & Patrimônio Líquido**:
    - *Passivo Circulante*: Fornecedores a Pagar, Empréstimos Bancários C/P e Obrigações Fiscais/Trabalhistas.
    - *Passivo Não Circulante*: Financiamentos de Longo Prazo (BNDES/Repasses) e Provisões para Contingências.
    - *Patrimônio Líquido*: Capital Social, Reservas de Capital e Lucro Acumulado do Período (integrado diretamente à DRE).
  - Validação de consistência em tempo real: `Ativo Total == Passivo Total + PL`.

- **DFC (Demonstração dos Fluxos de Caixa)**:
  - Elaborada pelo **Método Direto**:
    - **Atividades Operacionais**: Recebimentos de clientes deduzidos dos pagamentos a fornecedores, folha e tributos.
    - **Atividades de Investimento**: Aquisição de ativos imobilizados (CAPEX) e resgates.
    - **Atividades de Financiamento**: Amortização de financiamentos e distribuição de dividendos.
  - Variação Líquida de Caixa conciliada rigorosamente com o saldo de fechamento das contas da Tesouraria.

---

### 2. Tesouraria & Fluxo de Caixa em Tempo Real
- **Posição Consolidada Multicarteira**:
  - Integração de múltiplas contas PJ (ex: Itaú Empresas, Bradesco Corporate, BTG Pactual Tesouraria CDB 104% CDI, Banco do Brasil).
- **Gestão de Contas a Pagar e a Receber**:
  - Aging schedules de vencimentos (`D+0`, `D+7`, `D+15`, `D+30`, `Vencidos`).
  - Status em tempo real: *Realizado / Liquidado*, *Pendente / A Vencer*, *Em Atraso*.
- **Lançamento Rápido no Caixa**:
  - Registro de transações com classificação contábil automática na DRE, BP e DFC.

---

### 3. Automação Bancária & Conciliação Inteligente
- **Hub Open Finance**:
  - Monitoramento de APIs bancárias, latência de resposta e sincronização de webhooks.
- **Motor de Conciliação Automática**:
  - Algoritmo probabilístico de correspondência por valor exato, janela temporal de liquidação ($\pm 3$ dias úteis) e identificadores (CNPJ, Pix, Protocolo FITID).
  - Execução de conciliação em lote com 1 clique.
- **Geração Contábil a partir do Extrato**:
  - Criação e conciliação instantânea de tarifas bancárias, créditos spot e despesas não provisionadas diretamente do feed bancário.

---

### 4. Dashboards Inteligentes & Indicadores Executivos
- **Indicadores de Liquidez**:
  - *Liquidez Corrente*: $\frac{\text{Ativo Circulante}}{\text{Passivo Circulante}}$
  - *Liquidez Seca*: $\frac{\text{Ativo Circulante} - \text{Estoques}}{\text{Passivo Circulante}}$
  - *Liquidez Imediata*: $\frac{\text{Disponibilidades}}{\text{Passivo Circulante}}$
- **Rentabilidade & Retorno (DuPont)**:
  - Margem Bruta, Margem EBITDA, Margem Líquida.
  - *ROE (Return on Equity)*: $\frac{\text{Lucro Líquido}}{\text{Patrimônio Líquido}} \times 100$
  - *ROA (Return on Assets)*: $\frac{\text{Lucro Líquido}}{\text{Ativo Total}} \times 100$
- **Ciclos e Gestão de Capital de Giro**:
  - Prazo Médio de Recebimento (PMR), Prazo Médio de Pagamento (PMP), Prazo Médio de Estocagem (PME).
  - Ciclo Operacional e Ciclo Financeiro de Caixa.
  - Necessidade de Capital de Giro (NCG): $\text{Ativo Circulante Operacional} - \text{Passivo Circulante Operacional}$.

---

## ⚙️ Parâmetros Técnicos do Aplicativo

| Parâmetro | Configuração |
| :--- | :--- |
| **Namespace** | `com.example` |
| **Application ID** | `com.aistudio.fincontrol.qmvxpz` |
| **Compile SDK** | `Android 36 (minorApiLevel = 1)` |
| **Target SDK** | `36` |
| **Min SDK** | `24 (Android 7.0 Nougat)` |
| **Linguagem** | Kotlin 2.2.10 |
| **Framework de UI** | Jetpack Compose (BOM 2024.09.00) com Material 3 |
| **Banco de Dados Local** | Room Database v2.7.0 (com KSP 2.3.5) |
| **Concorrência** | Coroutines & Kotlin StateFlow / SharedFlow |
| **Injeção de Dependências** | ViewModel Provider / AndroidViewModel |
| **Permissões Declaradas** | `android.permission.INTERNET`, `android.permission.ACCESS_NETWORK_STATE` |

---

## 📂 Estrutura do Projeto

```
app/src/main/
├── AndroidManifest.xml
├── java/com/example/
│   ├── MainActivity.kt
│   ├── data/
│   │   └── local/
│   │       ├── AppDatabase.kt                 # Database Room com TypeConverters
│   │       ├── Converters.kt                  # Serializadores de tipos e enums
│   │       ├── FinanceDatabaseInitializer.kt   # Carga inicial com dados corporativos realistas
│   │       ├── FinanceRepository.kt           # Repositório de dados e regras de conciliação
│   │       ├── dao/
│   │       │   ├── BankAccountDao.kt
│   │       │   ├── BankStatementItemDao.kt
│   │       │   └── CashTransactionDao.kt
│   │       └── model/
│   │           ├── BankAccount.kt
│   │           ├── BankStatementItem.kt
│   │           ├── CashTransaction.kt
│   │           └── FinancialStatementTypes.kt
│   ├── domain/
│   │   └── engine/
│   │       └── FinancialEngine.kt             # Motor de cálculo DRE, BP, DFC, KPIs e Índices
│   └── ui/
│       ├── FinControlApp.kt                   # Scaffold mestre, BottomBar e navegação
│       ├── FinControlViewModel.kt             # ViewModel central com StateFlow reativo
│       ├── components/
│       │   ├── FinancialCharts.kt             # Gráficos de Fluxo de Caixa e composição
│       │   ├── FinancialKpiCard.kt            # Cards de métricas com indicadores de tendência
│       │   └── StatementsTable.kt             # Tabelas estruturadas DRE, BP e DFC
│       ├── dialogs/
│       │   ├── AddTransactionDialog.kt        # Modal de lançamento no caixa
│       │   └── ReconciliationDialog.kt        # Modal de conciliação inteligente do extrato
│       ├── screens/
│       │   ├── DashboardScreen.kt             # Painel executivo e visão geral
│       │   ├── IndicatorsScreen.kt            # Análise de liquidez, rentabilidade e ciclos
│       │   ├── ReconciliationScreen.kt        # Hub Open Finance e conciliação bancária
│       │   ├── StatementsScreen.kt            # Relatórios contábeis DRE, BP e DFC
│       │   └── TreasuryScreen.kt              # Gestão de caixa, contas e vencimentos
│       └── theme/
│           ├── Color.kt                       # Paleta corporativa (Navy, Emerald, Rose, Amber)
│           ├── Theme.kt                       # Configuração de temas Claro / Escuro M3
│           └── Type.kt
└── res/
    ├── drawable/                              # Ícone adaptativo e vetores
    ├── mipmap-*/                              # Ícones de alta densidade (mdpi a xxxhdpi)
    └── values/
        ├── colors.xml
        └── strings.xml
```

---

## 🚀 Como Compilar e Executar

### Pré-requisitos
- **Android Studio Ladybug (ou superior)**
- **JDK 17 ou 21**
- **Android SDK 36**

### Passos de Instalação:
1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/fincontrol-android.git
   cd fincontrol-android
   ```
2. Abra o projeto no Android Studio.
3. Aguarde o Gradle sincronizar as dependências do `gradle/libs.versions.toml`.
4. Execute o build via terminal ou Android Studio:
   ```bash
   ./gradlew assembleDebug
   ```
5. Para rodar os testes unitários e de integração de cálculo:
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 🔒 Segurança e Privacidade
- Os dados financeiros são armazenados localmente e de forma segura com **SQLite / Room Database**.
- As integrações de Open Finance utilizam chaves e tokens protegidos via ambiente seguro (`BuildConfig` / Secrets).
