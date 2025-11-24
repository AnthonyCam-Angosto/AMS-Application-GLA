// Utilitaires indicateurs
function sma(values, period) {
  const out = [];
  for (let i = 0; i < values.length; i++) {
    if (i < period - 1) { out.push(null); continue; }
    const slice = values.slice(i - period + 1, i + 1);
    const avg = slice.reduce((a, b) => a + b, 0) / period;
    out.push(avg);
  }
  return out;
}

function ema(values, period) {
  const k = 2 / (period + 1);
  const out = [];
  let prev = null;
  for (let i = 0; i < values.length; i++) {
    const price = values[i];
    if (i === 0) { prev = price; out.push(prev); continue; }
    const e = price * k + prev * (1 - k);
    prev = e;
    out.push(e);
  }
  // align with nulls for initial warm-up
  for (let i = 0; i < period - 1; i++) out[i] = null;
  return out;
}

function rsi(values, period = 14) {
  const gains = [];
  const losses = [];
  for (let i = 1; i < values.length; i++) {
    const diff = values[i] - values[i - 1];
    gains.push(Math.max(diff, 0));
    losses.push(Math.max(-diff, 0));
  }
  const avgGain = sma(gains, period).map(v => (v == null ? null : v));
  const avgLoss = sma(losses, period).map(v => (v == null ? null : v));
  const rsiVals = [null]; // align to same length as prices
  for (let i = 0; i < avgGain.length; i++) {
    const g = avgGain[i], l = avgLoss[i];
    if (g == null || l == null) { rsiVals.push(null); continue; }
    const rs = l === 0 ? 100 : (g / l);
    const rsi = 100 - (100 / (1 + rs));
    rsiVals.push(rsi);
  }
  return rsiVals;
}

async function fetchOHLC(days = 30, asset = "BTC") {
  const response = await fetch(`/dashboard/ohlc?range=${days}&typeC=${asset}`);
  if (!response.ok) {
    throw new Error("Erreur API OHLC");
  }
  return await response.json();
}

function ohlcToClose(ohlc) {
  return ohlc.map(d => d.closePrice);
}

function ohlcToLabels(ohlc) {
  // Convertir dateTime en objet Date pour Chart.js (time scale)
  return ohlc.map(d => new Date(d.dateTime));
}

function computeDailyReturns(ohlc) {
  const returns = [];
  for (let i = 1; i < ohlc.length; i++) {
    const prevClose = ohlc[i - 1].closePrice;
    const currClose = ohlc[i].closePrice;
    const r = (currClose - prevClose) / prevClose;
    returns.push({ 
      t: new Date(ohlc[i].dateTime), 
      r 
    });
  }
  return returns;
}

// Pour le candlestick Chart.js Financial plugin, il faut un objet {x, o, h, l, c}
function formatForCandlestick(ohlc) {
  return ohlc.map(d => ({
    x: new Date(d.dateTime),
    o: d.openPrice,
    h: d.highPrice,
    l: d.lowPrice,
    c: d.closePrice
  }));
}

// Contexte chart
let priceChart, rsiChart, heatmapChart;

function destroyIfExists(chart) {
  if (chart) chart.destroy();
}

// Construction des graphiques
function buildLineChart(ctx, labels, closes, overlays) {
  return new Chart(ctx, {
    type: 'line',
    data: {
      labels,
      datasets: [
        { label: 'Prix (Close)', data: closes, borderColor: '#3b82f6', backgroundColor: 'rgba(59,130,246,0.15)', tension: 0.2 },
        ...(overlays.sma20 ? [{ label: 'SMA 20', data: sma(closes, 20), borderColor: '#22c55e', borderWidth: 1.5 }] : []),
        ...(overlays.ema50 ? [{ label: 'EMA 50', data: ema(closes, 50), borderColor: '#f59e0b', borderWidth: 1.5 }] : []),
      ]
    },
    options: {
      responsive: true,
      interaction: { mode: 'index', intersect: false },
      scales: {
        x: { type: 'time', time: { unit: 'day' } },
        y: { beginAtZero: false }
      },
      plugins: { legend: { display: true } }
    }
  });
}

function buildCandlestickChart(ctx, ohlc) {
  return new Chart(ctx, {
    type: 'candlestick',
    data: {
      datasets: [{
        label: 'OHLC',
        data: ohlc,
        borderColor: { up: '#22c55e', down: '#ef4444', unchanged: '#64748b' },
      }]
    },
    options: {
      responsive: true,
      scales: {
        x: { type: 'time', time: { unit: 'day' } },
        y: { beginAtZero: false }
      }
    }
  });
}

function buildRsiChart(ctx, labels, closes, showRsi) {
  return new Chart(ctx, {
    type: 'line',
    data: {
      labels,
      datasets: showRsi ? [{
        label: 'RSI 14',
        data: rsi(closes, 14),
        borderColor: '#a855f7',
        tension: 0.2
      }] : []
    },
    options: {
      responsive: true,
      scales: {
        x: { type: 'time', time: { unit: 'day' } },
        y: { min: 0, max: 100, grid: { color: '#e5e7eb' } }
      },
      plugins: {
        legend: { display: true },
        annotation: {
          annotations: {
            overbought: { type: 'line', yMin: 70, yMax: 70, borderColor: '#ef4444' },
            oversold: { type: 'line', yMin: 30, yMax: 30, borderColor: '#22c55e' }
          }
        }
      }
    }
  });
}

function buildHeatmapChart(ctx, returns) {
  // Exemple: matrice jour x semaine avec intensité = rendement
  const cells = returns.map((d) => {
    const day = d.t.getDate();
    const week = Math.floor((d.t.getDate() - 1) / 7); // 0..4
    return { x: day, y: week, v: d.r };
  });

  return new Chart(ctx, {
    type: 'matrix',
    data: {
      datasets: [{
        label: 'Rendements journaliers',
        data: cells,
        backgroundColor(ctx) {
          const v = ctx.raw.v;
          const base = v >= 0 ? 'rgba(34,197,94,' : 'rgba(239,68,68,';
          const alpha = Math.min(1, Math.abs(v) * 8);
          return base + alpha + ')';
        },
        // ✅ Vérification que chartArea existe avant d’y accéder
        width: (ctx) => {
          const area = ctx.chart.chartArea;
          return area ? (area.width / 31) - 2 : 0;
        },
        height: (ctx) => {
          const area = ctx.chart.chartArea;
          return area ? (area.height / 5) - 2 : 0;
        },
        borderWidth: 0
      }]
    },
    options: {
      responsive: true,
      scales: {
        x: { type: 'linear', min: 1, max: 31, ticks: { stepSize: 1 } },
        y: { type: 'linear', min: 0, max: 4, ticks: { callback: v => `Semaine ${v + 1}` } }
      },
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: (ctx) => `Jour ${ctx.raw.x}, Semaine ${ctx.raw.y + 1}: ${(ctx.raw.v*100).toFixed(2)}%`
          }
        }
      }
    }
  });
}


// Initialisation
document.addEventListener('DOMContentLoaded', async () => {
  const priceCtx = document.getElementById('priceChart').getContext('2d');
  const rsiCtx = document.getElementById('rsiChart').getContext('2d');
  const heatCtx = document.getElementById('heatmapChart').getContext('2d');

  let ohlc = await fetchOHLC(30, "BTC");
  let closes = ohlcToClose(ohlc);
  let labels = ohlcToLabels(ohlc);

  priceChart = buildLineChart(priceCtx, labels, closes, { sma20: true, ema50: true });
  rsiChart = buildRsiChart(rsiCtx, labels, closes, true);
  heatmapChart = buildHeatmapChart(heatCtx, computeDailyReturns(ohlc));

  const applyBtn = document.getElementById('applyBtn');
  applyBtn.addEventListener('click', async () => {
    const range = parseInt(document.getElementById('range').value, 10);
    const chartType = document.querySelector('input[name="chartType"]:checked').value;
    const showSMA = document.getElementById('sma20').checked;
    const showEMA = document.getElementById('ema50').checked;
    const showRSI = document.getElementById('rsi14').checked;
    const crypto = document.getElementById('crypto').value;

    ohlc = await fetchOHLC(range, crypto);
    closes = ohlcToClose(ohlc);
    labels = ohlcToLabels(ohlc);

    destroyIfExists(priceChart);
    destroyIfExists(rsiChart);
    destroyIfExists(heatmapChart);

    if (chartType === 'line') {
      priceChart = buildLineChart(priceCtx, labels, closes, { sma20: showSMA, ema50: showEMA });
    } else if (chartType === 'candlestick') {
      priceChart = buildCandlestickChart(priceCtx, formatForCandlestick(ohlc));
    } else if (chartType === 'heatmap') {
      priceChart = buildHeatmapChart(priceCtx, computeDailyReturns(ohlc));
    }

    rsiChart = buildRsiChart(rsiCtx, labels, closes, showRSI);
    heatmapChart = buildHeatmapChart(heatCtx, computeDailyReturns(ohlc));
  });
});