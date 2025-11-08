const ctx = document.getElementById('cryptoChart').getContext('2d');
    new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['Lun','Mar','Mer','Jeu','Ven','Sam','Dim'],
        datasets: [{
          label: 'BTC (USD)',
          data: [35000,36000,35500,37000,36500,38000,37500],
          borderColor: '#6ee7f9',
          backgroundColor: 'rgba(110,231,249,0.2)',
          fill: true,
          tension: 0.3
        }]
      },
      options: { responsive: true }
    });