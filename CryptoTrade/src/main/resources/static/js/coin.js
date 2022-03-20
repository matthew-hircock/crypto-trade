const ctx = document.getElementById('priceHistory').getContext('2d');
const baseApiUrl = 'http://localhost:8080/coins/'; // change to correct host computer

const chartData = {
	datasets: [{
		label: 'Price',
		data: [],
		backgroundColor: 'rgba(255, 99, 132, 0.2)',
		borderColor: 'rgba(255, 99, 132, 1)',
		borderWidth: 1,
		showLine: true,
		pointRadius: 0
	}]
};

const chartOptions = {
	responsive: true,
	maintainAspectRatio: true,
	animation: false,
	scales: {			
		x: {
			type: 'time',
			time: {
				parser: 'yyyy-MM-ddTHH:mm:ss',
				unit: 'minute',
				displayFormats: {
					'minute': 'dd-MM HH:mm'
				},
			},
			offset: true,
			ticks: {
				beginAtZero: false,
			}
		},
		y: {
			display: true,
			ticks: {
				beginAtZero: true,
			}
		}
	}
};

const priceHistory = new Chart(ctx, {
	type: 'scatter',
	data: chartData,
	options: chartOptions,
});

const fetchData = async (days) => {
	const coinId = document.getElementById('coin-id').innerHTML;
	return await fetch(baseApiUrl + coinId + '/priceHistory?days=' + days)
	.then(response => response.json())
	.then(data => Object.keys(data).map(k => ({'x': parseInt(k), 'y': data[k]})));
}

const refreshChart = async (days) => {
	const newData = await fetchData(days);
	priceHistory.data.datasets[0].data = newData;
	priceHistory.update();
	updateButtonStatus(days);
}

const updateButtonStatus = (days) => {
	var chartButtons = document.querySelectorAll('[id^="chart-refresh"]');
	chartButtons.forEach(chartButton => {
		var buttonDays = chartButton.id.substring(chartButton.id.lastIndexOf('-') + 1);
		console.log(chartButton.id.lastIndexOf('-'))
		console.log(days)
		console.log(buttonDays)
		if (days == buttonDays) {
			chartButton.classList.add('disabled');
		} else {
			chartButton.classList.remove('disabled');
		}
	});
}

refreshChart(1);
