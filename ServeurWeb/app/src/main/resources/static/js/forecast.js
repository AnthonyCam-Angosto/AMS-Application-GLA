// Fonctions de prévision pour crypto (exemples simples)
// Méthodes fournies : SMA forecast, Exponential smoothing (EWMA), Linear regression

// SMA forecast : on calcule la moyenne mobile sur "period" derniers closes
// et on propage cette moyenne pour les "horizon" jours suivants
/**
 * Calcule la prévision par moyenne mobile simple (SMA).
 * @param {number[]} closes - Série des prix de clôture (chronologique)
 * @param {number} [period=20] - Période de la moyenne mobile
 * @param {number} [horizon=7] - Nombre de pas/jours à prévoir
 * @returns {number[]} Valeurs prévues (longueur = `horizon`)
 */
function smaForecast(closes, period = 20, horizon = 7) {
  if (!closes || closes.length === 0) return [];
  const n = closes.length;
  const start = Math.max(0, n - period);
  const slice = closes.slice(start, n);
  const avg = slice.reduce((a, b) => a + b, 0) / slice.length;
  return new Array(horizon).fill(avg);
}

// Exponential smoothing (simple) : forecast evolves from last forecast
// we initialise par la dernière valeur observée et appliquons alpha
/**
 * Prévision par lissage exponentiel simple (EWMA).
 * @param {number[]} closes - Série des prix de clôture
 * @param {number} [alpha=0.3] - Paramètre de lissage (0-1)
 * @param {number} [horizon=7] - Nombre de pas à prévoir
 * @returns {number[]} Valeurs prévues
 */
function exponentialSmoothingForecast(closes, alpha = 0.3, horizon = 7) {
  if (!closes || closes.length === 0) return [];
  // calculer la série lissée (niveau)
  let s = closes[0];
  for (let i = 1; i < closes.length; i++) {
    s = alpha * closes[i] + (1 - alpha) * s;
  }
  // prévoir horizon pas en avant en répétant la dernière valeur lissée
  return new Array(horizon).fill(s);
}

// Régression linéaire simple sur les indices pour projeter la tendance
/**
 * Prévision par régression linéaire simple sur les indices temporels.
 * @param {number[]} closes - Série des prix de clôture
 * @param {number} [horizon=7] - Nombre de pas à prévoir
 * @param {number|null} [window=null] - Taille de fenêtre à utiliser (null = toute la série)
 * @returns {number[]} Valeurs prévues
 */
function linearRegressionForecast(closes, horizon = 7, window = null) {
  if (!closes || closes.length === 0) return [];
  const data = window && window > 0 ? closes.slice(-window) : closes.slice();
  const n = data.length;
  const xs = [];
  for (let i = 0; i < n; i++) xs.push(i);
  const meanX = xs.reduce((a, b) => a + b, 0) / n;
  const meanY = data.reduce((a, b) => a + b, 0) / n;
  let num = 0, den = 0;
  for (let i = 0; i < n; i++) {
    num += (xs[i] - meanX) * (data[i] - meanY);
    den += (xs[i] - meanX) * (xs[i] - meanX);
  }
  const slope = den === 0 ? 0 : num / den;
  const intercept = meanY - slope * meanX;
  // produire forecasts pour les indices n .. n+horizon-1
  const out = [];
  for (let h = 0; h < horizon; h++) {
    const x = n + h;
    out.push(intercept + slope * x);
  }
  return out;
}

// Helper : génération de labels (dates) pour les jours suivants
/**
 * Génère un tableau de dates à partir d'une date de référence.
 * @param {Date|string} lastDate - Date de départ
 * @param {number} horizon - Nombre de jours à générer
 * @returns {Date[]} Tableau de Date pour les jours suivants
 */
function extendLabelsWithDays(lastDate, horizon) {
  const out = [];
  const base = new Date(lastDate);
  for (let i = 1; i <= horizon; i++) {
    const d = new Date(base);
    d.setDate(base.getDate() + i);
    out.push(d);
  }
  return out;
}

// Prépare une série forecast pour Chart.js: valeurs historiques + nulls + prévisions
/**
 * Aligne une série de forecast pour Chart.js en ajoutant des `null` pour la partie historique.
 * @param {number[]} historicalCloses - Valeurs historiques
 * @param {number[]} forecastValues - Valeurs prévues
 * @returns {Array<number|null>} Série alignée
 */
function alignForecastSeries(historicalCloses, forecastValues) {
  // retourne un tableau de la même longueur que (historical + forecast)
  const nulls = new Array(historicalCloses.length).fill(null);
  return nulls.concat(forecastValues);
}

// Requête OHLC (copie légère de dashboard.fetchOHLC)
/**
 * Récupère les OHLC depuis l'API côté serveur.
 * @param {number} [days=30] - Nombre de jours d'historique à récupérer
 * @param {string} [asset='BTC'] - Identifiant de l'actif
 * @returns {Promise<Object[]>} Tableau d'objets OHLC triés par date
 */
async function fetchOHLC(days = 30, asset = 'BTC') {
  const response = await fetch(`/dashboard/ohlc?range=${days}&typeC=${asset}`);
  if (!response.ok) throw new Error('Erreur API OHLC');
  const ohlc = await response.json();
  return ohlc.sort((a, b) => { 
      return new Date(a.dateTime) - new Date(b.dateTime); 
    });
}

// Fonction utilitaire qui renvoie historique + forecasts prêt à être tracé
// method: 'sma'|'ewma'|'lr' ; params: objet options propres à la méthode
/**
 * Prépare l'historique et les prévisions prêtes pour le tracé.
 * @param {Object} options - Options de récupération et de prévision
 * @param {number} [options.historyDays=90]
 * @param {string} [options.asset='BTC']
 * @param {number} [options.horizon=7]
 * @param {string} [options.method='sma'] - 'sma'|'ewma'|'lr'|'linear'
 * @param {Object} [options.params={}] - Paramètres spécifiques à la méthode
 * @returns {Promise<Object>} Objet contenant `labels`, `historical`, `forecast`, et `raw`
 */
async function fetchOHLCForecast({historyDays = 90, asset = 'BTC', horizon = 7, method = 'sma', params = {}} = {}) {
  const ohlc = await fetchOHLC(historyDays, asset);
  const closes = ohlc.map(d => d.closePrice);
  const lastDate = ohlc.length ? new Date(ohlc[ohlc.length - 1].dateTime) : new Date();

  let forecastValues = [];
  if (method === 'sma') {
    const period = params.period || 20;
    forecastValues = smaForecast(closes, period, horizon);
  } else if (method === 'ewma' || method === 'exp') {
    const alpha = params.alpha ?? 0.3;
    forecastValues = exponentialSmoothingForecast(closes, alpha, horizon);
  } else if (method === 'lr' || method === 'linear') {
    const window = params.window || null;
    forecastValues = linearRegressionForecast(closes, horizon, window);
  } else {
    throw new Error('Méthode de forecast inconnue: ' + method);
  }

  const futureLabels = extendLabelsWithDays(lastDate, horizon);
  const labels = ohlc.map(d => new Date(d.dateTime)).concat(futureLabels);

  const historicalDataset = closes.slice();
  const forecastDataset = alignForecastSeries(closes, forecastValues);

  return {
    labels,
    historical: historicalDataset,
    forecast: forecastDataset,
    raw: { ohlc, closes, forecastValues }
  };
}

// Exposer les fonctions pour usage dans la page
/**
 * API publique exposée côté page pour réutiliser les utilitaires de forecast.
 */
globalThis.Forecast = {
  smaForecast,
  exponentialSmoothingForecast,
  linearRegressionForecast,
  extendLabelsWithDays,
  alignForecastSeries,
  fetchOHLCForecast,
  fetchOHLC
};

function destroyChart(c) { if (c) c.destroy(); }

/**
 * Détruit un chart Chart.js si présent.
 * @param {Chart|null} c - Instance Chart.js
 */


/**
 * Construit un Chart.js montrant l'historique et plusieurs prévisions.
 * @param {CanvasRenderingContext2D} ctx - Contexte du canvas
 * @param {Date[]|string[]} labels - Labels (dates)
 * @param {number[]} historical - Valeurs historiques
 * @param {Array<{name:string,data:Array<number|null>}>} forecasts - Tableaux de prévisions
 * @returns {Chart} Instance Chart.js
 */
function buildForecastChart(ctx, labels, historical, forecasts) {
  // Construire des jeux de points explicites {x: date, y: value} pour éviter
  // tout problème d'indexation/ordre des labels qui ferait "retourner" le tracé.
  const colors = ['#3b82f6', '#f59e0b','#22c55e','#a855f7','#ef4444'];

  // Dataset historique : mappe chaque label à une valeur y (ou null si hors historique)
  const historicalPoints = labels.map((lab, idx) => ({ x: lab, y: idx < historical.length ? historical[idx] : null }));
  // s'assurer que les points sont triés par date (Chart.js dessine dans l'ordre du tableau)
  historicalPoints.sort((a, b) => new Date(a.x) - new Date(b.x));

  const datasets = [
    { label: 'Historique', data: historicalPoints, borderColor: colors[0], tension: 0.2, spanGaps: false }
  ];

  let i = 1;
  for (const f of forecasts) {
    const pts = labels.map((lab, idx) => ({ x: lab, y: (f.data && f.data[idx] != null) ? f.data[idx] : null }));
    pts.sort((a, b) => new Date(a.x) - new Date(b.x));
    datasets.push({ label: f.name, data: pts, borderColor: colors[i % colors.length], borderDash: [6,4], fill: false, tension: 0.2, spanGaps: false, parsing: false });
    i++;
  }

  try {
    return new Chart(ctx, {
      type: 'line',
      data: { datasets },
      options: {
        responsive: true,
        interaction: { mode: 'index', intersect: false },
        scales: {
          x: { type: 'time', time: { unit: 'day' } },
          y: { beginAtZero: false }
        }
      }
    });
  } catch (e) {
    console.error('Chart creation failed (object points). Falling back to array format:', e);
    // Fallback: build arrays aligned with labels (legacy format)
    const histArr = historical.slice();
    const labelsArr = labels.slice();
    const legacyDatasets = [ { label: 'Historique', data: histArr, borderColor: colors[0], tension: 0.2 } ];
    let j = 0;
    for (const f of forecasts) {
      legacyDatasets.push({ label: f.name, data: f.data, borderColor: colors[(j+1) % colors.length], borderDash: [6,4], fill: false, tension: 0.2 });
      j++;
    }
    return new Chart(ctx, { type: 'line', data: { labels: labelsArr, datasets: legacyDatasets }, options: { responsive: true } });
  }
}

/**
 * Construit un chart d'erreurs (MAE / MAPE) pour comparer modèles.
 * @param {CanvasRenderingContext2D} ctx
 * @param {string[]} labels - Noms des modèles
 * @param {number[]} maes
 * @param {number[]} mapes
 * @returns {Chart}
 */
function buildErrorChart(ctx, labels, maes, mapes) {
  return new Chart(ctx, {
    type: 'bar',
    data: { labels, datasets: [ { label: 'MAE', data: maes, backgroundColor: '#60a5fa' }, { label: 'MAPE (%)', data: mapes, backgroundColor: '#fb7185' } ] },
    options: { responsive: true }
  });
}

/**
 * Calcule MAE et MAPE entre une série de test et les prévisions.
 * @param {number[]} trainCloses - Série d'entraînement (non utilisée ici sauf pour signature)
 * @param {number[]} testCloses - Valeurs réelles pour le backtest
 * @param {number[]} forecastValues - Prévisions correspondantes
 * @returns {{mae:number,mape:number}} Erreurs calculées
 */
function computeErrors(trainCloses, testCloses, forecastValues) {
  const n = testCloses.length;
  let sumAbs = 0, sumPct = 0;
  for (let i = 0; i < n; i++) {
    const pred = forecastValues[i];
    const actual = testCloses[i];
    const err = Math.abs(pred - actual);
    sumAbs += err;
    sumPct += actual === 0 ? 0 : (err / Math.abs(actual));
  }
  const mae = sumAbs / n;
  const mape = (sumPct / n) * 100;
  return { mae, mape };
}

document.addEventListener('DOMContentLoaded', async () => {
  const runBtn = document.getElementById('runForecast');
  const assetEl = document.getElementById('asset');
  const rangeEl = document.getElementById('range');
  const horizonEl = document.getElementById('horizon');
  const smaEl = document.getElementById('sma');
  const emaEl = document.getElementById('ema');
  const linregEl = document.getElementById('linreg');
  const arimaEl = document.getElementById('arima');

  const forecastCanvas = document.getElementById('forecastChart');
  const errorCanvas = document.getElementById('errorChart');
  if (!forecastCanvas) console.error('forecast.js: canvas #forecastChart introuvable');
  if (!errorCanvas) console.error('forecast.js: canvas #errorChart introuvable');
  const forecastCtx = forecastCanvas ? forecastCanvas.getContext('2d') : null;
  const errorCtx = errorCanvas ? errorCanvas.getContext('2d') : null;

  console.debug('forecast.js: elements', { forecastCanvas, errorCanvas, forecastCtx, errorCtx });
  try {
    console.debug('forecast.js: Chart object', window.Chart ? (window.Chart.version || 'Chart present') : 'Chart MISSING');
  } catch (e) { console.debug('forecast.js: Chart check error', e); }
  if (forecastCanvas) {
    const s = window.getComputedStyle(forecastCanvas);
    console.debug('forecast.js: forecastCanvas size', { w: forecastCanvas.clientWidth, h: forecastCanvas.clientHeight, display: s.display });
    if (forecastCanvas.clientWidth === 0 || forecastCanvas.clientHeight === 0) forecastCanvas.style.outline = '2px dashed orange';
  }
  if (errorCanvas) {
    const s2 = window.getComputedStyle(errorCanvas);
    console.debug('forecast.js: errorCanvas size', { w: errorCanvas.clientWidth, h: errorCanvas.clientHeight, display: s2.display });
    if (errorCanvas.clientWidth === 0 || errorCanvas.clientHeight === 0) errorCanvas.style.outline = '2px dashed orange';
  }
  const smaPeriodEl = document.getElementById('smaPeriod');
  const emaAlphaEl = document.getElementById('emaAlpha');
  const loaderEl = document.getElementById('loader');

  let forecastChart = null;
  let errorChart = null;

  runBtn.addEventListener('click', async () => {
    const asset = assetEl.value;
    const historyDays = Number.parseInt(rangeEl.value, 10);
    const horizon = Number.parseInt(horizonEl.value, 10);
    const smaPeriod = Number.parseInt(smaPeriodEl.value, 10) || 20;
    let emaAlpha = Number.parseFloat(emaAlphaEl.value);
    if (Number.isNaN(emaAlpha)) emaAlpha = 0.25;
    const models = [];
    if (smaEl.checked) models.push('sma');
    if (emaEl.checked) models.push('ewma');
    if (linregEl.checked) models.push('lr');
    if (arimaEl.checked) models.push('arima');

    // Récupérer données complètes
    loaderEl.style.display = 'block';
    runBtn.disabled = true;
    try {
      const data = await fetchOHLCForecast({ historyDays, asset, horizon, method: 'sma' });
    const labels = data.labels;
    const historical = data.historical;
    console.debug('forecast.js: fetched', { labelsLen: labels.length, historicalLen: historical.length, horizon });

    // Préparer forecasts pour chaque modèle et backtest simple
    const forecastSeries = [];
    const errorLabels = [];
    const maes = [];
    const mapes = [];

    // backtest: séparer train/test
    const closesAll = data.raw.closes;
    const train = closesAll.slice(0, Math.max(1, closesAll.length - horizon));
    const test = closesAll.slice(-horizon);

    for (const m of models) {
      let fv = [];
      if (m === 'sma') fv = smaForecast(closesAll, smaPeriod, horizon);
      else if (m === 'ewma') fv = exponentialSmoothingForecast(closesAll, emaAlpha, horizon);
      else if (m === 'lr') fv = linearRegressionForecast(closesAll, horizon, 60);
      else if (m === 'arima') {
        // simulation simple: lr + noise
        const base = linearRegressionForecast(closesAll, horizon, 30);
        fv = base.map(v => v * (1 + (Math.random() - 0.5) * 0.02));
      }

      // align forecast for plotting
      const aligned = alignForecastSeries(historical, fv);
      forecastSeries.push({ name: m.toUpperCase(), data: aligned });

      // backtest using train -> forecast and compare to test
      let backForecast = [];
      if (m === 'sma') backForecast = smaForecast(train, smaPeriod, horizon);
      else if (m === 'ewma') backForecast = exponentialSmoothingForecast(train, emaAlpha, horizon);
      else if (m === 'lr') backForecast = linearRegressionForecast(train, horizon, 60);
      else if (m === 'arima') {
        const base2 = linearRegressionForecast(train, horizon, 30);
        backForecast = base2.map(v => v * (1 + (Math.random() - 0.5) * 0.02));
      }

      const errs = computeErrors(train, test, backForecast);
      errorLabels.push(m.toUpperCase());
      maes.push(Number(errs.mae.toFixed(6)));
      mapes.push(Number(errs.mape.toFixed(3)));
    }

    destroyChart(forecastChart);
    destroyChart(errorChart);
    if (!forecastCtx) { console.error('forecast.js: impossible de créer forecastChart, ctx absent'); }
    else {
      forecastChart = buildForecastChart(forecastCtx, labels, historical, forecastSeries);
      console.debug('forecast.js: forecastChart created', forecastChart);
      try { if (forecastChart) { forecastChart.resize(); forecastChart.update(); } } catch(e) { console.debug('forecast.js: forecastChart resize/update failed', e); }
      if (!forecastChart && forecastCanvas) forecastCanvas.style.outline = '3px solid red';
    }
    if (!errorCtx) { console.error('forecast.js: impossible de créer errorChart, ctx absent'); }
    else {
      errorChart = buildErrorChart(errorCtx, errorLabels, maes, mapes);
      console.debug('forecast.js: errorChart created', errorChart);
      try { if (errorChart) { errorChart.resize(); errorChart.update(); } } catch(e) { console.debug('forecast.js: errorChart resize/update failed', e); }
      if (!errorChart && errorCanvas) errorCanvas.style.outline = '3px solid red';
    }
    } finally {
      loaderEl.style.display = 'none';
      runBtn.disabled = false;
    }
  });
});
