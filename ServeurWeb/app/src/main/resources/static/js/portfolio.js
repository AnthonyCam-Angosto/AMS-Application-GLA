// ===============================
// API
// ===============================

// Liste des portefeuilles de l'utilisateur
async function fetchPortfolios() {
  const res = await fetch("/portfolio/list");
  return await res.json();
}

// Liste des cryptos disponibles
async function fetchCryptos() {
  const res = await fetch("/cryptos");
  return await res.json();
}

// Prix actuel d'une crypto
async function fetchPrice(cryptoId) {
  const res = await fetch(`/price?id=${cryptoId}`);
  return await res.json(); // { price: 42000 }
}

// Comptes du portefeuille
async function fetchAccounts(portfolioId) {
  const res = await fetch(`/portfolio/${portfolioId}/comptes`);
  return await res.json();
}

// Transactions du portefeuille
async function fetchTransactions(portfolioId) {
  const res = await fetch(`/portfolio/${portfolioId}/transactions`);
  return await res.json();
}

// Ajouter une transaction
async function addTransactionAPI(data) {
  await fetch("/portfolio/transaction", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  });
}

// Créer un portefeuille
async function createPortfolioAPI(data) {
  const res = await fetch("/portfolio/create", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  });
  return await res.json();
}


// ===============================
// LOGIQUE
// ===============================

let currentPortfolio = null;
let cryptos = [];
let accounts = [];
let transactions = [];

let portfolioChart;

// Remplit les selects
async function initSelectors() {
  const portfolios = await fetchPortfolios();
  cryptos = await fetchCryptos();

  const portfolioSelect = document.getElementById("portfolioSelect");
  const cryptoSelect = document.getElementById("cryptoSelect");

  portfolios.forEach(p => {
    portfolioSelect.innerHTML += `<option value="${p.id_portefeuille}">${p.nom}</option>`;
  });

  cryptos.forEach(c => {
    cryptoSelect.innerHTML += `<option value="${c.crypto_id}">${c.nom}</option>`;
  });

  currentPortfolio = portfolios[0].id_portefeuille;
  portfolioSelect.value = currentPortfolio;

  portfolioSelect.addEventListener("change", async () => {
    currentPortfolio = portfolioSelect.value;
    await refreshPortfolio();
  });
}

// Rafraîchit toutes les données
async function refreshPortfolio() {
  accounts = await fetchAccounts(currentPortfolio);
  transactions = await fetchTransactions(currentPortfolio);

  updatePositionsTable();
  updateHistoryTable();
  updateSummary();
  updatePortfolioChart();
}

function initCreatePortfolioPopup() {
  const modal = document.getElementById("createPortfolioModal");
  const openBtn = document.getElementById("openCreatePortfolio");
  const closeBtn = document.getElementById("closeCreatePortfolio");

  openBtn.addEventListener("click", () => modal.classList.remove("hidden"));
  closeBtn.addEventListener("click", () => modal.classList.add("hidden"));
}


// ===============================
// AFFICHAGE
// ===============================

async function updatePositionsTable() {
  const tbody = document.getElementById("positionsTable");
  tbody.innerHTML = "";

  for (const acc of accounts) {
    const price = (await fetchPrice(acc.crypto_id)).price;
    const value = acc.solde * price;

    tbody.innerHTML += `
      <tr>
        <td>${acc.crypto_nom}</td>
        <td>${acc.solde}</td>
        <td>${value.toFixed(2)} €</td>
      </tr>
    `;
  }
}

function updateHistoryTable() {
  const tbody = document.getElementById("historyTable");
  tbody.innerHTML = "";

  transactions.forEach(t => {
    tbody.innerHTML += `
      <tr>
        <td>${new Date(t.date_transaction).toLocaleString()}</td>
        <td>${t.crypto_nom}</td>
        <td>${t.type}</td>
        <td>${t.montant}</td>
      </tr>
    `;
  });
}

async function updateSummary() {
  let total = 0;

  for (const acc of accounts) {
    const price = (await fetchPrice(acc.crypto_id)).price;
    total += acc.solde * price;
  }

  document.getElementById("totalValue").textContent = total.toFixed(2) + " €";
  document.getElementById("totalPNL").textContent = "N/A";
  document.getElementById("totalROI").textContent = "N/A";
}

function updatePortfolioChart() {
  const ctx = document.getElementById("portfolioChart").getContext("2d");

  if (portfolioChart) portfolioChart.destroy();

  portfolioChart = new Chart(ctx, {
    type: "line",
    data: {
      labels: transactions.map(t => new Date(t.date_transaction)),
      datasets: [{
        label: "Transactions",
        data: transactions.map(t => t.montant),
        borderColor: "#3b82f6",
        tension: 0.2
      }]
    }
  });
}

// ===============================
// AJOUT TRANSACTION
// ===============================

async function addTransaction() {
  const cryptoId = document.getElementById("cryptoSelect").value;
  const type = document.getElementById("type").value;
  const quantity = parseFloat(document.getElementById("quantity").value);

  if (!quantity || quantity <= 0) {
    alert("Quantité invalide");
    return;
  }

  await addTransactionAPI({
    id_portefeuille: currentPortfolio,
    crypto_id: cryptoId,
    type,
    montant: quantity
  });

  await refreshPortfolio();
}

// ===============================
// INIT
// ===============================

document.addEventListener("DOMContentLoaded", async () => {
  await initSelectors();
  await refreshPortfolio();

  initCreatePortfolioPopup();

  // Remplir la liste des cryptos dans la popup
  const cryptoSelectPopup = document.getElementById("newPortfolioCrypto");
  cryptos = await fetchCryptos();
  cryptos.forEach(c => {
    cryptoSelectPopup.innerHTML += `<option value="${c.crypto_id}">${c.nom}</option>`;
  });

  document.getElementById("createPortfolioBtn").addEventListener("click", createPortfolio);


  document.getElementById("addTrade").addEventListener("click", addTransaction);
});
