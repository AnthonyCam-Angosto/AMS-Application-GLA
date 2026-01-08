/**
 * Module de graphiques du dashboard : calculs techniques et rendu Chart.js.
 */
let priceChart = null;

// --- Calcul SMA ---
/**
 * Calcule la SMA (Simple Moving Average) pour une série.
 * @param {number[]} data - Série de valeurs numériques
 * @param {number} period - Période de la moyenne
 * @returns {Array<number|null>} Tableau aligné avec la série (null pour indices insuffisants)
 */
function calcSMA(data, period) {
    return data.map((_, i) => {
        if (i < period) return null;
        const slice = data.slice(i - period, i);
        const sum = slice.reduce((a, b) => a + b, 0);
        return sum / period;
    });
}

// --- Calcul EMA ---
/**
 * Calcule l'EMA (Exponential Moving Average) pour une série.
 * @param {number[]} data - Série de valeurs numériques
 * @param {number} period - Période de l'EMA
 * @returns {Array<number|null>} Tableau aligné avec la série
 */
function calcEMA(data, period) {
    const k = 2 / (period + 1);
    let emaArray = [null];

    for (let i = 1; i < data.length; i++) {
        if (i < period) {
            emaArray.push(null);
        } else if (i === period) {
            const sma = data.slice(0, period).reduce((a, b) => a + b, 0) / period;
            emaArray.push(sma);
        } else {
            emaArray.push(data[i] * k + emaArray[i - 1] * (1 - k));
        }
    }
    return emaArray;
}

// --- Charger les données OHLC ---
/**
 * Charge les données OHLC depuis l'API et les trie.
 * @returns {Promise<{labels: Date[], closePrices: number[]}>}
 */
async function loadOHLC() {
    const asset = document.getElementById("crypto").value;
    const days = document.getElementById("range").value;

    const url = `/dashboard/ohlc?range=${days}&typeC=${asset}`;
    const response = await fetch(url);
    const ohlc = await response.json();

    //console.log("OHLC reçu :", ohlc);

    const sorted = ohlc.sort((a, b) => { 
      return new Date(a.dateTime) - new Date(b.dateTime); 
    });

    //console.log("sorted reçu :", ohlc);

    const labels = sorted.map(d => new Date(d.dateTime));
    const closePrices = sorted.map(d => d.closePrice);

    return { labels, closePrices };
}

// --- Afficher le graphique en courbes ---
/**
 * Rendu du graphique en courbes (Close + indicateurs SMA/EMA optionnels).
 * Lit les contrôles de la page pour déterminer les options d'affichage.
 */
async function renderLineChart() {
    const { labels, closePrices } = await loadOHLC();

    const showSMA = document.getElementById("sma20").checked;
    const showEMA = document.getElementById("ema50").checked;

    const sma20 = showSMA ? calcSMA(closePrices, 20) : null;
    const ema50 = showEMA ? calcEMA(closePrices, 50) : null;

    // Détruire l'ancien graphique
    if (priceChart) priceChart.destroy();

    const ctx = document.getElementById("priceChart").getContext("2d");

    // --- Construction propre du tableau datasets ---
    const datasets = [
        {
            label: "Prix (Close)",
            data: closePrices,
            borderColor: "#4e79a7",
            borderWidth: 2,
            tension: 0.2,
            pointRadius: 1
        }
    ];

    if (showSMA) {
        datasets.push({
            label: "SMA 20",
            data: sma20,
            borderColor: "#f28e2b",
            borderWidth: 1.5,
            tension: 0.2,
            pointRadius: 0
        });
    }

    if (showEMA) {
        datasets.push({
            label: "EMA 50",
            data: ema50,
            borderColor: "#e15759",
            borderWidth: 1.5,
            tension: 0.2,
            pointRadius: 0
        });
    }

    // --- Création du graphique ---
    priceChart = new Chart(ctx, {
        type: "line",
        data: {
            labels: labels,
            datasets: datasets
        },
        options: {
            responsive: true,
            scales: {
                x: {
                    type: "time",
                    time: { unit: "day" }
                },
                y: {
                    beginAtZero: false
                }
            }
        }
    });
}

/**
 * Rendu d'un graphique en chandeliers (OHLC) en utilisant Chart.js Financial.
 * @returns {Promise<Array>} Retourne les données triées (utile pour d'autres visuels)
 */
async function renderCandleChart() {
    // Charger les données OHLC complètes
    const asset = document.getElementById("crypto").value;
    const days = document.getElementById("range").value;

    const url = `/dashboard/ohlc?range=${days}&typeC=${asset}`;
    const response = await fetch(url);
    const ohlc = await response.json();

    // Trier par date croissante
    const sorted = ohlc.slice().sort((a, b) => new Date(a.dateTime) - new Date(b.dateTime));

    // Transformer en format Chart.js Financial
    const candleData = sorted.map(d => ({
        x: new Date(d.dateTime),
        o: Number(d.openPrice),
        h: Number(d.highPrice),
        l: Number(d.lowPrice),
        c: Number(d.closePrice)
    }));

    // Détruire l'ancien graphique
    if (priceChart) priceChart.destroy();

    const ctx = document.getElementById("priceChart").getContext("2d");

    // Création du graphique chandeliers
    priceChart = new Chart(ctx, {
        type: "candlestick",
        data: {
            labels: sorted.map(d => new Date(d.dateTime)),
            datasets: [
                {
                    label: "Chandeliers (OHLC)",
                    data: candleData,
                    borderColor: "#000",
                    color: {
                        up: "#26a69a",
                        down: "#ef5350",
                        unchanged: "#999"
                    }
                }
            ]
        },
        options: {
            responsive: true,
            scales: {
                x: {
                    type: "time",
                    time: { unit: "day" }
                },
                y: {
                    beginAtZero: false
                }
            }
        }
    });

    return sorted; // utile pour RSI, heatmap, etc.
}

/**
 * Rendu d'une heatmap (bar chart coloré) des rendements journaliers.
 */
async function renderHeatmapChart() {
    // Charger les données OHLC
    const asset = document.getElementById("crypto").value;
    const days = document.getElementById("range").value;

    const url = `/dashboard/ohlc?range=${days}&typeC=${asset}`;
    const response = await fetch(url);
    const ohlc = await response.json();

    // Trier par date croissante
    const sorted = ohlc.slice().sort((a, b) => new Date(a.dateTime) - new Date(b.dateTime));

    // Labels = dates
    const labels = sorted.map(d => new Date(d.dateTime));

    // Clôtures = base du rendement
    const closePrices = sorted.map(d => Number(d.closePrice));

    // Calcul des rendements
    const returns = calcReturns(closePrices);

    // Couleurs selon rendement
    const colors = returns.map(r => returnToColor(r));

    // Détruire l'ancien graphique heatmap
    if (heatmapChart) heatmapChart.destroy();

    const ctx = document.getElementById("heatmapChart").getContext("2d");

    // Création du graphique heatmap
    heatmapChart = new Chart(ctx, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [
                {
                    label: "Rendements (%)",
                    data: returns,
                    backgroundColor: colors,
                    borderWidth: 0
                }
            ]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: ctx => `${ctx.raw?.toFixed(2)} %`
                    }
                }
            },
            scales: {
                x: { display: false },
                y: { display: false }
            }
        }
    });
}




// --- Bouton "Appliquer" ---
document.getElementById("applyBtn").addEventListener("click", () => {
    const chartType = document.querySelector("input[name='chartType']:checked").value;
    renderRSI();
    renderHeatmap();

    if (chartType === "line") {
      renderLineChart();
    }
    if (chartType === "candlestick") { 
      renderCandleChart();
    }
    if (chartType === "heatmap") { 
      renderHeatmapChart();
    }
});

// --- Charger une première fois ---
document.addEventListener("DOMContentLoaded", () => {
    renderLineChart();
    renderRSI();
    renderHeatmap();
});


/**
 * Calcule l'indicateur RSI sur une série de prix.
 * @param {number[]} data - Série de prix (close)
 * @param {number} [period=14] - Période du RSI
 * @returns {Array<number|null>} Valeurs RSI alignées
 */
function calcRSI(data, period = 14) {
    const rsi = new Array(data.length).fill(null);

    if (data.length < period) return rsi;

    let gains = 0;
    let losses = 0;

    // Initial average gain/loss
    for (let i = 1; i <= period; i++) {
        const diff = data[i] - data[i - 1];
        if (diff >= 0) gains += diff;
        else losses -= diff;
    }

    let avgGain = gains / period;
    let avgLoss = losses / period;

    rsi[period] = 100 - (100 / (1 + (avgGain / avgLoss)));

    // Remaining RSI values
    for (let i = period + 1; i < data.length; i++) {
        const diff = data[i] - data[i - 1];

        if (diff >= 0) {
            avgGain = (avgGain * (period - 1) + diff) / period;
            avgLoss = (avgLoss * (period - 1)) / period;
        } else {
            avgGain = (avgGain * (period - 1)) / period;
            avgLoss = (avgLoss * (period - 1) - diff) / period;
        }

        const rs = avgLoss === 0 ? 100 : avgGain / avgLoss;
        rsi[i] = 100 - (100 / (1 + rs));
    }

    return rsi;
}


let rsiChart = null;

/**
 * Rendu du graphique RSI (utilise `calcRSI`).
 */
async function renderRSI() {
    const { labels, closePrices } = await loadOHLC();

    console.log("closePrices",closePrices);

    const rsi = calcRSI(closePrices, 14);

    if (rsiChart) rsiChart.destroy();

    const ctx = document.getElementById("rsiChart").getContext("2d");

    console.log("rsi",rsi);

    rsiChart = new Chart(ctx, {
        type: "line",
        data: {
            labels: labels,
            datasets: [
                {
                    label: "RSI 14",
                    data: rsi,
                    borderColor: "#8e44ad",
                    borderWidth: 1.5,
                    tension: 0.2,
                    pointRadius: 0
                },
                {
                    label: "Zone 70",
                    data: new Array(rsi.length).fill(70),
                    borderColor: "#e74c3c",
                    borderWidth: 1,
                    borderDash: [5, 5],
                    pointRadius: 0
                },
                {
                    label: "Zone 30",
                    data: new Array(rsi.length).fill(30),
                    borderColor: "#3498db",
                    borderWidth: 1,
                    borderDash: [5, 5],
                    pointRadius: 0
                }
            ]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    min: 0,
                    max: 100
                },
                x: {
                    type: "time",
                    time: { unit: "day" }
                }
            }
        }
    });
}

/**
 * Calcule les rendements (%) entre closes consécutifs.
 * @param {number[]} closePrices
 * @returns {Array<number|null>} Rendements (%) (premier élément = null)
 */
function calcReturns(closePrices) {
    const returns = [null]; // premier rendement impossible

    for (let i = 1; i < closePrices.length; i++) {
        const r = ((closePrices[i] - closePrices[i - 1]) / closePrices[i - 1]) * 100;
        returns.push(r);
    }

    return returns;
}

/**
 * Convertit un rendement en couleur RGBA pour la heatmap.
 * @param {number|null} r - Rendement en % ou null
 * @returns {string} Couleur CSS
 */
function returnToColor(r) {
    if (r === null) return "rgba(0,0,0,0)";

    const intensity = Math.min(Math.abs(r) / 5, 1); // normalisation

    if (r >= 0) {
        return `rgba(0, 200, 0, ${0.2 + intensity * 0.8})`; // vert
    } else {
        return `rgba(200, 0, 0, ${0.2 + intensity * 0.8})`; // rouge
    }
}
let heatmapChart = null;

async function renderHeatmap() {
    const { labels, closePrices } = await loadOHLC();

    const returns = calcReturns(closePrices);

    const colors = returns.map(r => returnToColor(r));

    if (heatmapChart) heatmapChart.destroy();

    const ctx = document.getElementById("heatmapChart").getContext("2d");

    heatmapChart = new Chart(ctx, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                label: "Rendements (%)",
                data: returns,
                backgroundColor: colors,
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: ctx => `${ctx.raw.toFixed(2)} %`
                    }
                }
            },
            scales: {
                x: { display: false },
                y: { display: false }
            }
        }
    });
}
